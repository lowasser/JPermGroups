package math.structures.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

final class InversePermutation<E> implements Permutation<E> {
  private final Permutation<E> forward;

  InversePermutation(Permutation<E> forward) {
    this.forward = checkNotNull(forward);
  }

  @Override public E apply(E e) {
    return forward.preimage(e);
  }

  @Override public Set<E> support() {
    return forward.support();
  }

  @Override public Permutation<E> inverse() {
    return forward;
  }

  @Override public E preimage(E e) {
    return forward.apply(e);
  }

  @Override public Parity parity() {
    return forward.parity();
  }

  @Override public int hashCode() {
    return forward.hashCode();
  }

  @Override public boolean stabilizes(E e) {
    return forward.stabilizes(e);
  }

  @Override public boolean stabilizes(Set<E> s) {
    return forward.stabilizes(s);
  }
}
