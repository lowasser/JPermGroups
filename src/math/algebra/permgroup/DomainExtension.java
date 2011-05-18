package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;

final class DomainExtension<E> implements
    Function<Permutation<E>, Permutation<E>> {
  public static <E> DomainExtension<E> forDomain(Set<E> domain) {
    return new DomainExtension<E>(domain);
  }

  private final ImmutableSet<E> domain;

  private DomainExtension(Set<E> domain) {
    this.domain = ImmutableSet.copyOf(domain);
  }

  @Override public Permutation<E> apply(Permutation<E> sigma) {
    return sigma.extend(domain);
  }
}