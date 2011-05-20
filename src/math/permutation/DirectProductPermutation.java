package math.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

import java.util.Set;

import javax.annotation.Nullable;

import math.structures.CartesianProduct;
import math.structures.Pair;

class DirectProductPermutation<A, B> extends Permutation<Pair<A, B>> {
  private final Permutation<A> sigmaA;
  private final Permutation<B> sigmaB;

  DirectProductPermutation(Permutation<A> sigmaA, Permutation<B> sigmaB) {
    this.sigmaA = checkNotNull(sigmaA);
    this.sigmaB = checkNotNull(sigmaB);
  }

  @Override public Set<Pair<A, B>> domain() {
    return CartesianProduct.of(sigmaA.domain(), sigmaB.domain());
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj instanceof DirectProductPermutation) {
      DirectProductPermutation<?, ?> tau = (DirectProductPermutation<?, ?>) obj;
      return Objects.equal(sigmaA, tau.sigmaA)
          && Objects.equal(sigmaB, tau.sigmaB);
    }
    return super.equals(obj);
  }

  @Override public Pair<A, B> image(Pair<A, B> p) {
    return Pair.of(sigmaA.image(p.getFirst()), sigmaB.image(p.getSecond()));
  }

  @Override public Pair<A, B> preimage(Pair<A, B> p) {
    return Pair.of(sigmaA.preimage(p.getFirst()),
        sigmaB.preimage(p.getSecond()));
  }

  @Override Permutation<Pair<A, B>> createInverse() {
    return new DirectProductPermutation<A, B>(sigmaA.inverse(),
        sigmaB.inverse());
  }
}
