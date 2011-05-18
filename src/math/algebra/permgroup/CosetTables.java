package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

final class CosetTables<E> extends ForwardingList<Collection<Permutation<E>>> {
  public static <E> CosetTables<E> build(Set<E> domain,
      Collection<Permutation<E>> generators,
      List<Predicate<Permutation<E>>> filters) {
    return immutable(new CosetTables<E>(domain, generators, filters));
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

  private final List<Collection<Permutation<E>>> tables;

  final List<Predicate<Permutation<E>>> filters;

  CosetTables(List<? extends Collection<Permutation<E>>> tables,
      List<Predicate<Permutation<E>>> filters) {
    this.tables = ImmutableList.copyOf(tables);
    this.filters = filters;
  }

  CosetTables() {
    this.tables = Lists.newArrayList();
    this.filters = Lists.newArrayList();
  }

  private CosetTables(Set<E> domain, Collection<Permutation<E>> generators,
      List<Predicate<Permutation<E>>> filters) {
    Permutation<E> id = Permutations.identity(domain);
    this.filters = filters;
    tables = Lists.newArrayListWithCapacity(filters.size());
    for (int i = 0; i < filters.size(); i++) {
      Set<Permutation<E>> table = Sets.newLinkedHashSet();
      table.add(id);
      tables.add(table);
    }

    Queue<Permutation<E>> todo = Lists.newLinkedList(generators);
    while (!todo.isEmpty()) {
      Permutation<E> sigma = todo.poll();
      todo.addAll(filter(sigma));
    }
  }

  @Override protected List<Collection<Permutation<E>>> delegate() {
    return tables;
  }

  private Set<Permutation<E>> filter(Permutation<E> alpha) {
    for (int i = 0; i < tables.size(); i++) {
      if (Permutations.isIdentity(alpha)) {
        return ImmutableSet.of();
      }

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
        Set<Permutation<E>> newFilters = Sets.newHashSet();
        for (int j = 0; j <= i; j++) {
          for (Permutation<E> sigma : tables.get(j)) {
            newFilters.add(sigma.compose(alpha));
          }
        }
        for (int j = i + 1; j < tables.size(); j++) {
          for (Permutation<E> sigma : tables.get(j)) {
            newFilters.add(alpha.compose(sigma));
          }
        }
        newFilters.remove(alpha);
        return newFilters;
      }
      alpha = found;
    }
    return ImmutableSet.of();
  }

  @Override public CosetTables<E> subList(int fromIndex, int toIndex) {
    return new CosetTables<E>(tables.subList(fromIndex, toIndex),
        filters.subList(fromIndex, toIndex));
  }

  public boolean addAll(CosetTables<E> t2) {
    return this.tables.addAll(t2.tables) && this.filters.addAll(t2.filters);
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
    return true;
  }

  private transient Collection<Permutation<E>> generatedPermutations;

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

  private List<Set<Permutation<E>>> setTables() {
    ImmutableList.Builder<Set<Permutation<E>>> builder =
        ImmutableList.builder();
    for (Collection<Permutation<E>> table : this) {
      builder.add(ImmutableSet.copyOf(table));
    }
    return builder.build();
  }

  public CosetTables<E> extend(Set<E> domain) {
    return new CosetTables<E>(Lists.transform(tables,
        CollectionMap.forFunction(DomainExtension.forDomain(domain))), filters);
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
}
