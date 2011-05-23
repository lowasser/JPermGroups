package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import math.structures.Pair;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

final class CosetTables<E> extends ForwardingList<Collection<Permutation<E>>> {
  private interface TableInsertionListener<E> {
    public void tableModified(Permutation<E> sigma, int table);
  }

  public static <E> CosetTables<E> trivial(Set<E> domain) {
    return immutable(new CosetTables<E>(domain));
  }

  static <E> CosetTables<E> build(Set<E> domain,
      Collection<Permutation<E>> generators,
      List<Predicate<Permutation<E>>> filters) {
    CosetTables<E> table = new CosetTables<E>(domain, filters);
    for (Permutation<E> g : generators) {
      table.filter(g, table.generatorInsertionListener);
    }
    return immutable(table);
  }

  static <A, B> CosetTables<Pair<A, B>> directProduct(Set<A> domainA,
      CosetTables<A> tablesA, Set<B> domainB, CosetTables<B> tablesB) {
    ImmutableList.Builder<Collection<Permutation<Pair<A, B>>>> tables =
        ImmutableList.builder();
    ImmutableList.Builder<Predicate<Permutation<Pair<A, B>>>> filters =
        ImmutableList.builder();

    Function<Permutation<Pair<A, B>>, Permutation<A>> inducedA =
        Project1st.projectDown(domainA, domainB);
    Function<Permutation<Pair<A, B>>, Permutation<B>> inducedB =
        Project2nd.projectDown(domainA, domainB);
    for (int i = 0; i < tablesA.size(); i++) {
      filters.add(Predicates.compose(tablesA.filters.get(i), inducedA));
    }
    tables.addAll(Lists.transform(tablesA,
        CollectionMap.forFunction(Project1st.projectUp(domainA, domainB))));

    for (int i = 0; i < tablesB.size(); i++) {
      filters.add(Predicates.compose(tablesB.filters.get(i), inducedB));
    }
    tables.addAll(Lists.transform(tablesB,
        CollectionMap.forFunction(Project2nd.projectUp(domainA, domainB))));
    return new CosetTables<Pair<A, B>>(tables.build(), filters.build());
  }

  static <E> CosetTables<E> immutable(CosetTables<E> tables) {
    ImmutableList.Builder<Set<Permutation<E>>> builder =
        ImmutableList.builder();
    for (Collection<Permutation<E>> table : tables) {
      builder.add(ImmutableSet.copyOf(table));
    }
    return new CosetTables<E>(builder.build(),
        ImmutableList.copyOf(tables.filters));
  }

  static <E> CosetTables<E> mutableCopy(CosetTables<E> copyTables) {
    return new CosetTables<E>(copyTables);
  }

  static <E> CosetTables<E> newMutableCosetTables() {
    return new CosetTables<E>();
  }

  static <E> CosetTables<E> normalClosure(CosetTables<E> tables,
      final Collection<Permutation<E>> generators) {
    List<Permutation<E>> todo = Lists.newArrayList(Iterables.concat(tables));
    final CosetTables<E> closedTables = mutableCopy(tables);
    TableInsertionListener<E> normalizingListener =
        new TableInsertionListener<E>() {
          @Override public void tableModified(Permutation<E> sigma, int table) {
            for (Permutation<E> g : generators) {
              closedTables.filter(sigma.conjugate(g), this);
            }
          }
        };
    for (Permutation<E> sigma : todo) {
      for (Permutation<E> g : generators) {
        closedTables.filter(sigma.conjugate(g), normalizingListener);
      }
    }
    return immutable(closedTables);
  }

  private final List<Collection<Permutation<E>>> tables;

  final List<Predicate<Permutation<E>>> filters;

  private transient Collection<Permutation<E>> generatedPermutations;

  private transient final TableInsertionListener<E> generatorInsertionListener =
      new TableInsertionListener<E>() {
        @Override public void tableModified(Permutation<E> sigma, int table) {
          Set<Permutation<E>> newSiftees = Sets.newHashSet();
          for (int j = 0; j <= table; j++) {
            for (Permutation<E> tau : tables.get(j)) {
              newSiftees.add(tau.compose(sigma));
            }
          }
          for (int j = table + 1; j < tables.size(); j++) {
            for (Permutation<E> tau : tables.get(j)) {
              newSiftees.add(sigma.compose(tau));
            }
          }
          newSiftees.remove(sigma);
          for (Permutation<E> tau : newSiftees)
            filter(tau, this);
        }
      };

  /**
   * Initializes mutable, empty coset tables.
   */
  private CosetTables() {
    this.tables = Lists.newArrayList();
    this.filters = Lists.newArrayList();
  }

  /**
   * Initializes a mutable copy of the specified CosetTables.
   */
  private CosetTables(CosetTables<E> copyTables) {
    this.filters = copyTables.filters;
    this.tables = Lists.newArrayListWithCapacity(copyTables.size());
    for (Collection<Permutation<E>> table : copyTables) {
      tables.add(Sets.newLinkedHashSet(table));
    }
  }

  /**
   * Initializes mutable coset tables to the identity group on the specified
   * domain.
   */
  private CosetTables(ImmutableSet<E> domain) {
    this(domain, new StabilizingFilterList<E>(domain));
  }

  /**
   * Initializes an immutable CosetTables with the specified tables and filters.
   */
  private CosetTables(List<? extends Collection<Permutation<E>>> tables,
      List<Predicate<Permutation<E>>> filters) {
    this.tables = ImmutableList.copyOf(tables);
    this.filters = filters;
  }

  private CosetTables(Set<E> domain) {
    this(ImmutableSet.copyOf(domain));
  }

  private CosetTables(Set<E> domain, List<Predicate<Permutation<E>>> filters) {
    this.filters = filters;
    this.tables = Lists.newArrayListWithCapacity(filters.size());
    Set<Permutation<E>> init =
        Collections.singleton(Permutations.identity(domain));
    for (int i = 0; i < filters.size(); i++) {
      tables.add(Sets.newHashSet(init));
    }
  }

  /**
   * Use with caution, only for direct products.
   */
  public boolean addAll(CosetTables<E> t2) {
    return this.tables.addAll(t2.tables) && this.filters.addAll(t2.filters);
  }

  public CosetTables<E> extend(Collection<Permutation<E>> newGenerators) {
    CosetTables<E> tables2 = mutableCopy(this);
    for (Permutation<E> g : newGenerators) {
      tables2.filter(g, tables2.generatorInsertionListener);
    }
    return immutable(tables2);
  }

  public CosetTables<E> extend(Set<E> domain) {
    return new CosetTables<E>(Lists.transform(tables,
        CollectionMap.forFunction(DomainExtension.forDomain(domain))), filters);
  }

  public Collection<Permutation<E>> generatedPermutations() {
    if (generatedPermutations == null) {
      Set<List<Permutation<E>>> factors = Sets.cartesianProduct(setTables());
      return generatedPermutations =
          Collections2.transform(factors,
              new Function<List<Permutation<E>>, Permutation<E>>() {
                @Override public Permutation<E>
                    apply(List<Permutation<E>> input) {
                  return Permutations.compose(input);
                }
              });
    }
    return generatedPermutations;
  }

  public boolean generates(Permutation<E> alpha) {
    for (int i = 0; i < tables.size(); i++) {
      if (Permutations.isIdentity(alpha)) {
        return true;
      }
      Predicate<Permutation<E>> filter = filters.get(i);
      Collection<Permutation<E>> table = tables.get(i);
      Permutation<E> found = null;
      for (Permutation<E> gamma : table) {
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (filter.apply(p)) {
          found = p;
          break;
        }
      }
      if (found == null)
        return false;
      alpha = found;
    }
    return Permutations.isIdentity(alpha);
  }

  @Override public CosetTables<E> subList(int fromIndex, int toIndex) {
    return new CosetTables<E>(tables.subList(fromIndex, toIndex),
        filters.subList(fromIndex, toIndex));
  }

  @Override protected List<Collection<Permutation<E>>> delegate() {
    return tables;
  }

  boolean isValid() {
    if (isEmpty())
      return true;
    Iterator<Permutation<E>> iterator = Iterables.concat(this).iterator();
    Set<E> domain = iterator.next().domain();
    boolean good = true;
    while (good && iterator.hasNext()) {
      good &= iterator.next().domain().equals(domain);
    }
    Iterator<Collection<Permutation<E>>> tableIterator = iterator();
    while (good && tableIterator.hasNext()) {
      good &= !tableIterator.next().isEmpty();
    }
    return good;
  }

  private void filter(Permutation<E> alpha, TableInsertionListener<E> listener) {
    for (int i = 0; i < tables.size(); i++) {
      if (Permutations.isIdentity(alpha))
        return;

      Permutation<E> found = null;
      Predicate<Permutation<E>> filter = filters.get(i);
      for (Permutation<E> gamma : tables.get(i)) {
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (filter.apply(p)) {
          found = p;
          break;
        }
      }
      if (found == null) {
        tables.get(i).add(alpha);
        listener.tableModified(alpha, i);
        return;
      }
      alpha = found;
    }
  }

  private List<Set<Permutation<E>>> setTables() {
    ImmutableList.Builder<Set<Permutation<E>>> builder =
        ImmutableList.builder();
    for (Collection<Permutation<E>> table : this) {
      builder.add(ImmutableSet.copyOf(table));
    }
    return builder.build();
  }
}
