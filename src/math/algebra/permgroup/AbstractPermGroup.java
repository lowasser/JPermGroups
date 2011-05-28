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

public abstract class AbstractPermGroup<E> extends AbstractSet<Permutation<E>>
    implements PermGroup<E> {
  @Override public boolean equals(@Nullable Object o) {
    if (o instanceof AbstractPermGroup) {
      @SuppressWarnings("unchecked")
      AbstractPermGroup<E> g = (AbstractPermGroup) o;
      return size() == g.size() && g.containsAll(generators());
    }
    return super.equals(o);
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#extend(java.lang.Iterable)
   */
  @Override public PermGroup<E> extend(Iterable<Permutation<E>> newGenerators) {
    List<Permutation<E>> newGs = Lists.newArrayList(generators());
    for (Permutation<E> g : newGenerators) {
      if (!contains(g)) {
        newGs.add(g);
      }
    }
    if (newGs.size() == generators().size()) {
      return this;
    }
    return new RegularPermGroup<E>(newGs, CosetTables.create(newGs));
  }

  @Override public PermGroup<E> extend(PermGroup<E> h) {
    return extend(h.generators());
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#generators()
   */
  @Override public abstract Collection<Permutation<E>> generators();

  @Override public boolean isEmpty() {
    return false;
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#isSubgroupOf(math.algebra.permgroup.AbstractPermutationGroup)
   */
  @Override public boolean isSubgroupOf(PermGroup<E> g) {
    return size() <= g.size() && g.containsAll(generators());
  }

  @Override public boolean stabilizes(Collection<Set<E>> collection) {
    for (Set<E> set : collection) {
      for (Permutation<E> sigma : generators()) {
        if (!collection.contains(sigma.apply(set))) {
          return false;
        }
      }
    }
    return true;
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#stabilizes(java.util.Set)
   */
  @Override public boolean stabilizes(Set<E> set) {
    for (Permutation<E> g : generators()) {
      if (!g.stabilizes(set)) {
        return false;
      }
    }
    return true;
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#subgroup(java.util.List)
   */
  @Override public PermSubgroup<E> subgroup(
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    CosetTables<E> tables = CosetTables.subgroupTables(this, filters);
    PermGroup<E> subgroup =
        new RegularPermGroup<E>(tables.drop(filters.size()));
    Collection<Permutation<E>> reps = tables.take(filters.size()).generated();
    return new SubgroupView<E>(reps, subgroup, this);
  }

  /* (non-Javadoc)
   * @see math.algebra.permgroup.PermutationGroup#subgroup(com.google.common.base.Predicate)
   */
  @Override public PermGroup<E> subgroup(
      Predicate<? super Permutation<E>> filter) {
    return subgroup(Collections.singletonList(filter));
  }

  @Override public String toString() {
    Collection<Permutation<E>> generators = generators();
    StringBuilder builder = new StringBuilder(generators.size() * 10);
    builder.append('<');
    Joiner.on(", ").appendTo(builder, generators);
    builder.append('>');
    return builder.toString();
  }

}