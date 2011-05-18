package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkNotNull;
import algorithms.CartesianProduct;
import algorithms.Pair;

import java.util.Set;

final class PairPermutation<E> extends Permutation<Pair<E, E>> {
  private final Permutation<E> sigma;
  private transient Set<Pair<E, E>> domain = null;

  PairPermutation(Permutation<E> sigma) {
    this.sigma = checkNotNull(sigma);
  }

  @Override public Set<Pair<E, E>> domain() {
    return (domain == null) ? domain =
        CartesianProduct.of(sigma.domain(), sigma.domain()) : domain;
  }

  @Override public Pair<E, E> image(Pair<E, E> pair) {
    return Pair.of(sigma.image(pair.getFirst()), sigma.image(pair.getSecond()));
  }
}
