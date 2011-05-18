package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

final class ExtendedPermutation<E> extends Permutation<E> {
  private Set<E> domain;
  private Permutation<E> sigma;

  @Override public Set<E> domain() {
    return domain;
  }

  @Override Permutation<E> createInverse() {
    return new ExtendedPermutation<E>(domain, sigma.inverse());
  }

  static <E> Permutation<E> extend(Set<E> domain, Permutation<E> sigma){
    return new ExtendedPermutation<E>(domain, sigma);
  }
  
  private ExtendedPermutation(Set<E> domain, Permutation<E> sigma) {
    this.domain = checkNotNull(domain);
    this.sigma = checkNotNull(sigma);
    assert domain.containsAll(sigma.domain());
  }

  @Override public E image(E e) {
    return sigma.domain().contains(e) ? sigma.image(e) : e;
  }
}
