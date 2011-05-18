package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

final class CosetTables<E> extends ForwardingList<Set<Permutation<E>>> {
  public static <E> CosetTables<E> build(Set<E> domain,
      Collection<Permutation<E>> generators,
      List<Predicate<Permutation<E>>> filters) {
    return immutable(new CosetTables<E>(domain, generators, filters));
  }

  private static <E> CosetTables<E> immutable(CosetTables<E> tables) {
    ImmutableList.Builder<Set<Permutation<E>>> builder = ImmutableList
      .builder();
    for (Set<Permutation<E>> table : tables) {
      builder.add(ImmutableSet.copyOf(table));
    }
    return new CosetTables<E>(builder.build(),
        ImmutableList.copyOf(tables.filters));
  }

  private final List<Set<Permutation<E>>> tables;

  final List<Predicate<Permutation<E>>> filters;

  private CosetTables(List<Set<Permutation<E>>> tables,
      List<Predicate<Permutation<E>>> filters) {
    this.tables = tables;
    this.filters = filters;
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

  @Override protected List<Set<Permutation<E>>> delegate() {
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

  public boolean generates(Permutation<E> alpha) {
    for (int i = 0; i < tables.size(); i++) {
      if (Permutations.isIdentity(alpha)) {
        return true;
      }
      Predicate<Permutation<E>> filter = filters.get(i);
      Set<Permutation<E>> table = tables.get(i);
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
      Set<List<Permutation<E>>> factors = Sets.cartesianProduct(this);
      return generatedPermutations = Collections2.transform(factors,
          new Function<List<Permutation<E>>, Permutation<E>>() {
            @Override public Permutation<E> apply(List<Permutation<E>> input) {
              return Permutations.compose(input);
            }
          });
    }
    return generatedPermutations;
  }
}
