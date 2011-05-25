package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkPositionIndex;
import static math.structures.permutation.Permutations.compose;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

final class CosetTables<E> {
  interface CosetTablesListener<E> {
    public void tableModified(Permutation<E> sigma, CosetTable table);
  }

  public static <E> CosetTables<E> create() {
    return new CosetTables<E>();
  }

  public static <E> CosetTables<E>
      create(Collection<Permutation<E>> generators) {
    CosetTables<E> tables = new CosetTables<E>();
    for (Permutation<E> g : generators) {
      tables.addGenerator(g);
    }
    return immutable(tables);
  }

  public static <E> CosetTables<E> subgroupTables(
      Iterable<? extends Permutation<E>> generators,
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    CosetTables<E> tables = new CosetTables<E>();
    for (Predicate<? super Permutation<E>> filter : filters)
      tables.addTable(filter);
    for (Permutation<E> g : generators) {
      tables.addGenerator(g);
    }
    return immutable(tables);
  }

  public CosetTables<E> drop(int k) {
    checkPositionIndex(k, tables.size());
    return new CosetTables<E>(support, tables.subList(k, tables.size()));
  }

  public CosetTables<E> take(int k) {
    checkPositionIndex(k, tables.size());
    return new CosetTables<E>(support, tables.subList(0, k));
  }

  public static <E> CosetTables<E> immutable(CosetTables<E> cTables) {
    ImmutableList.Builder<CosetTable<E>> builder = ImmutableList.builder();
    for (CosetTable<E> table : cTables.tables) {
      builder.add(CosetTable.immutable(table));
    }
    return new CosetTables<E>(ImmutableSet.copyOf(cTables.support),
        builder.build(), ImmutableList.copyOf(cTables.generators));
  }

  private final Set<E> support;

  private final List<CosetTable<E>> tables;

  private transient final CosetTablesListener<E> generatorListener =
      new CosetTablesListener<E>() {
        @Override public void tableModified(Permutation<E> sigma,
            CosetTable table) {
          Set<Permutation<E>> news = Sets.newHashSet();
          for (Permutation<E> tau : Iterables.concat(tables.subList(0,
              table.index + 1))) {
            news.add(compose(sigma, tau));
          }
          for (Permutation<E> tau : Iterables.concat(tables.subList(
              table.index + 1, tables.size()))) {
            news.add(compose(tau, sigma));
          }
          for (Permutation<E> tau : news) {
            filter(tau, this);
          }
        }
      };

  private transient Collection<Permutation<E>> generated;
  private final Collection<Permutation<E>> generators;

  private CosetTables() {
    this(Sets.<E> newHashSet(), Lists.<CosetTable<E>> newArrayList(), Lists
      .<Permutation<E>> newArrayList());
  }

  private CosetTables(Set<E> support, List<CosetTable<E>> tables,
      Collection<Permutation<E>> generators) {
    this.support = support;
    this.tables = tables;
    this.generators = generators;
  }

  private CosetTables(Set<E> support, List<CosetTable<E>> tables) {
    this.support = support;
    this.tables = tables;
    this.generators = Sets.newHashSet(Iterables.concat(tables));
  }

  public CosetTables<E> extend(Collection<Permutation<E>> newGenerators) {
    List<Permutation<E>> gens =
        Lists.newArrayListWithCapacity(newGenerators.size());
    for (Permutation<E> g : newGenerators) {
      if (!generates(g))
        gens.add(g);
    }
    if (gens.isEmpty()) {
      return this;
    }
    List<CosetTable<E>> newTables =
        Lists.newArrayListWithCapacity(tables.size());
    for (CosetTable<E> table : this.tables) {
      newTables.add(CosetTable.mutableCopy(table));
    }
    CosetTables<E> result =
        new CosetTables<E>(Sets.newHashSet(support), newTables, generators);
    for (Permutation<E> g : gens)
      result.addGenerator(g);
    return immutable(result);
  }

  public Iterator<Permutation<E>> generatedIterator() {
    return generated().iterator();
  }

  public boolean generates(Permutation<E> sigma) {
    for (CosetTable<E> table : tables) {
      if (sigma.isIdentity()) {
        return true;
      }
      sigma = table.filter(sigma);
      if (sigma == null)
        return false;
    }
    return sigma.isIdentity();
  }

  public Set<E> getSupport() {
    return support;
  }

  public int size() {
    return generated().size();
  }

  public List<CosetTable<E>> getTables() {
    return tables;
  }

  public Collection<Permutation<E>> getGenerators() {
    return generators;
  }

  boolean addGenerator(Permutation<E> sigma) {
    if (filter(sigma, generatorListener)) {
      generators.add(sigma);
      return true;
    }
    return false;
  }

  boolean filter(Permutation<E> sigma, CosetTablesListener<E> listener) {
    for (E e : sigma.support()) {
      if (support.add(e)) {
        addStabilizingTable(e);
      }
    }

    for (CosetTable<E> table : tables) {
      if (sigma.isIdentity()) {
        return false;
      }
      Permutation<E> sigmaPrime = table.filter(sigma);
      if (sigmaPrime == null) {
        table.add(sigma);
        listener.tableModified(sigma, table);
        return true;
      }
      sigma = sigmaPrime;
    }
    return false;
  }

  private void addStabilizingTable(E e) {
    addTable(StabilizesPredicate.on(e));
  }

  private void addTable(Predicate<? super Permutation<E>> filter) {
    tables.add(CosetTable.table(tables.size(), filter));
  }

  Collection<Permutation<E>> generated() {
    if (generated == null) {
      Set<List<Permutation<E>>> factors = Sets.cartesianProduct(tables);
      Function<List<Permutation<E>>, Permutation<E>> composer =
          new Function<List<Permutation<E>>, Permutation<E>>() {
            @Override public Permutation<E> apply(List<Permutation<E>> input) {
              return Permutations.compose(input);
            }
          };
      return generated = Collections2.transform(factors, composer);
    }
    return generated;
  }
}
