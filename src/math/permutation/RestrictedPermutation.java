package math.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import math.algebra.permgroup.Orbit;

final class RestrictedPermutation<E> extends Permutation<E> {
  private final Permutation<E> sigma;
  private final Orbit<E> orbit;

  RestrictedPermutation(Permutation<E> sigma, Orbit<E> orbit) {
    this.sigma = checkNotNull(sigma);
    this.orbit = checkNotNull(orbit);
    assert isValid();
  }

  @Override public Set<E> domain() {
    return orbit;
  }

  @Override public E image(E e) {
    assert orbit.contains(e);
    return sigma.image(e);
  }

  @Override public E preimage(E e) {
    assert orbit.contains(e);
    return sigma.preimage(e);
  }

  @Override Permutation<E> createInverse() {
    return new RestrictedPermutation<E>(sigma, orbit);
  }

  private boolean isValid() {
    for (E e : orbit)
      if (!orbit.contains(sigma.image(e)))
        return false;
    return true;
  }
}
