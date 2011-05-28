package math.structures.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

final class InversePermutation<E> extends AbstractPermutation<E> {
  private final AbstractPermutation<E> forward;

  InversePermutation(AbstractPermutation<E> forward) {
    this.forward = checkNotNull(forward);
  }

  @Override public E apply(E e) {
    return forward.preimage(e);
  }

  @Override public Permutation<E> compose(List<Permutation<E>> taus) {
    return forward.inverseCompose(taus);
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

  @Override public boolean isIdentity() {
    return forward.isIdentity();
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

  @Override protected int computeOrder() {
    return forward.order();
  }

  @Override protected Set<E> createDomain() {
    return forward.domain();
  }

  @Override protected Permutation<E> inverseCompose(List<Permutation<E>> taus) {
    return forward.compose(taus);
  }
}
