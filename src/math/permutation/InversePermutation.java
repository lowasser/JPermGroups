package math.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
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

  @Override public int sign() {
    return sigma.sign();
  }

  @Override Collection<List<E>> createCycleDecomposition() {
    return Collections2.transform(sigma.createCycleDecomposition(),
        new Function<List<E>, List<E>>() {
          @Override public List<E> apply(List<E> input) {
            return Lists.reverse(input);
          }
        });
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj instanceof InversePermutation) {
      InversePermutation<?> tau = (InversePermutation<?>) obj;
      return inverse().equals(tau.inverse());
    }
    return super.equals(obj);
  }
}
