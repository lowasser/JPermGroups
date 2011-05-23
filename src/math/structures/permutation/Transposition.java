package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

final class Transposition<E> extends AbstractPermutation<E> {
  private final E a;
  private final E b;

  Transposition(E a, E b) {
    this.a = checkNotNull(a);
    this.b = checkNotNull(b);
    checkArgument(!Objects.equal(a, b));
  }

  @Override public E apply(E e) {
    checkNotNull(e);
    return Objects.equal(e, a) ? b : Objects.equal(e, b) ? a : e;
  }

  @Override public Permutation<E> inverse() {
    return this;
  }

  @Override public E preimage(E e) {
    return apply(e);
  }

  @Override public boolean stabilizes(E e) {
    return !(Objects.equal(e, a) || Objects.equal(e, b));
  }

  @Override public boolean stabilizes(Set<E> s) {
    return s.isEmpty() || (s.contains(a) && s.contains(b));
  }

  @Override protected Set<E> createSupport() {
    return ImmutableSet.of(a, b);
  }
}
