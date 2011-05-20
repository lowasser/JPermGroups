package math.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nullable;
class InversePermutation<E> extends Permutation<E> {
  private final Permutation<E> sigma;

  InversePermutation(Permutation<E> sigma) {
    this.sigma = checkNotNull(sigma);
  }

  @Override public E image(E e) {
    return sigma.preimage(e);
  }

  @Override public E preimage(E e) {
    return sigma.image(e);
  }

  @Override public Set<E> domain() {
    return sigma.domain();
  }

  @Override public Permutation<E> inverse() {
    return sigma;
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj instanceof InversePermutation) {
      InversePermutation<?> tau = (InversePermutation<?>) obj;
      return inverse().equals(tau.inverse());
    }
    return super.equals(obj);
  }
}
