package math.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

final class ExtendedPermutation<E> extends Permutation<E> {
  private final ImmutableSet<E> domain;
  private final Permutation<E> sigma;

  @Override public Set<E> domain() {
    return domain;
  }

  @Override Permutation<E> createInverse() {
    return new ExtendedPermutation<E>(domain, sigma.inverse());
  }

  ExtendedPermutation(Set<E> domain, Permutation<E> sigma) {
    this.domain = ImmutableSet.copyOf(domain);
    this.sigma = checkNotNull(sigma);
  }

  @Override public E image(E e) {
    return sigma.domain().contains(e) ? sigma.image(e) : e;
  }

  @Override public E preimage(E e) {
    return sigma.domain().contains(e) ? sigma.preimage(e) : e;
  }

  @Override public Permutation<E> extend(Set<E> newDomain) {
    checkArgument(newDomain.containsAll(domain));
    return new ExtendedPermutation<E>(newDomain, sigma);
  }
}
