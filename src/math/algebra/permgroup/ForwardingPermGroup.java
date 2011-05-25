package math.algebra.permgroup;

import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingCollection;

import java.util.Collection;
import java.util.Set;

import math.structures.permutation.Permutation;

abstract class ForwardingPermGroup<E> extends
    ForwardingCollection<Permutation<E>> implements PermGroup<E> {

  @Override public PermGroup<E> extend(Iterable<Permutation<E>> newGenerators) {
    return delegate().extend(newGenerators);
  }

  @Override public PermGroup<E> extend(PermGroup<E> h) {
    return delegate().extend(h);
  }

  @Override public Collection<Permutation<E>> generators() {
    return delegate().generators();
  }

  @Override public boolean isSubgroupOf(PermGroup<E> g) {
    return delegate().isSubgroupOf(g);
  }

  @Override public boolean stabilizes(Set<E> set) {
    return delegate().stabilizes(set);
  }

  @Override public PermSubgroup<E> subgroup(
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    return delegate().subgroup(filters);
  }

  @Override public PermGroup<E> subgroup(
      Predicate<? super Permutation<E>> filter) {
    return delegate().subgroup(filter);
  }

  @Override protected abstract PermGroup<E> delegate();
}
