package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

final class RestrictedPermutation<E> extends AbstractPermutation<E> {
  private final Permutation<E> sigma;
  private final Set<E> support;

  RestrictedPermutation(Permutation<E> sigma, Set<E> support) {
    this.support = support = ImmutableSet.copyOf(support);
    this.sigma = checkNotNull(sigma);
    checkArgument(sigma.stabilizes(support));
    for (E e : support)
      checkArgument(!sigma.stabilizes(e));
  }

  @Override public E preimage(E e) {
    return support.contains(e) ? sigma.preimage(e) : e;
  }

  @Override public E apply(E e) {
    return support.contains(e) ? sigma.apply(e) : e;
  }

  @Override protected Set<E> createSupport() {
    return support;
  }

}
