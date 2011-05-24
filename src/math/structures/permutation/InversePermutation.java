package math.structures.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import java.util.Set;

import javax.annotation.Nullable;

final class InversePermutation<E> extends AbstractPermutation<E> {
  private final Permutation<E> forward;

  InversePermutation(Permutation<E> forward) {
    this.forward = checkNotNull(forward);
  }

  @Override public E apply(E e) {
    return forward.preimage(e);
  }

  @Override public boolean equals(@Nullable Object obj) {
    return (obj instanceof InversePermutation) ? Objects.equal(forward,
        ((InversePermutation<?>) obj).forward) : super.equals(obj);
  }

  @Override public int hashCode() {
    return forward.hashCode();
  }

  @Override public Permutation<E> inverse() {
    return forward;
  }

  @Override public Parity parity() {
    return forward.parity();
  }

  @Override public E preimage(E e) {
    return forward.apply(e);
  }

  @Override public boolean stabilizes(E e) {
    return forward.stabilizes(e);
  }

  @Override public boolean stabilizes(Set<E> s) {
    return forward.stabilizes(s);
  }

  @Override protected Set<E> createSupport() {
    return forward.support();
  }

  @Override public boolean isIdentity() {
    return forward.isIdentity();
  }
}
