package math.algebra.permgroup;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;

public abstract class PermutationGroup<E> extends AbstractSet<Permutation<E>> {
  @Override public boolean equals(@Nullable Object o) {
    if (o instanceof RegularPermutationGroup) {
      @SuppressWarnings("unchecked")
      PermutationGroup<E> g = (PermutationGroup) o;
      return size() == g.size() && g.containsAll(generators());
    }
    return super.equals(o);
  }

  /**
   * Returns the permutation group generated by this group and the specified
   * generators.
   */
  public PermutationGroup<E> extend(Iterable<Permutation<E>> newGenerators) {
    List<Permutation<E>> newGs = Lists.newArrayList(generators());
    for (Permutation<E> g : newGenerators) {
      if (!contains(g)) {
        newGs.add(g);
      }
    }
    if (newGs.size() == generators().size()) {
      return this;
    }
    return new RegularPermutationGroup<E>(newGs, CosetTables.create(newGs));
  }

  public abstract Collection<Permutation<E>> generators();

  @Override public boolean isEmpty() {
    return false;
  }

  public boolean isSubgroupOf(PermutationGroup<E> g) {
    return size() <= g.size() && g.containsAll(generators());
  }

  /**
   * Returns the subgroup of elements satisfying all of the specified filters.
   */
  public PermutationGroup<E> subgroup(
      List<? extends Predicate<? super Permutation<E>>> filters) {
    if (filters.isEmpty()) {
      return this;
    }
    CosetTables<E> subgroupTables = CosetTables.subgroupTables(
        generators(), filters);
    return new RegularPermutationGroup<E>(subgroupTables);
  }

  /**
   * Returns the subgroup of elements satisfying the specified filter.
   */
  public PermutationGroup<E> subgroup(Predicate<? super Permutation<E>> filter) {
    return subgroup(Collections.singletonList(filter));
  }

  public abstract Set<E> support();

  @Override public String toString() {
    Collection<Permutation<E>> generators = generators();
    StringBuilder builder = new StringBuilder(generators.size() * 10);
    builder.append('<');
    Joiner.on(", ").appendTo(builder, generators);
    builder.append('>');
    return builder.toString();
  }
  
  public boolean stabilizes(Set<E> set) {
    for(Permutation<E> g:generators()) {
      if(!g.stabilizes(set))
        return false;
    }
    return true;
  }

}