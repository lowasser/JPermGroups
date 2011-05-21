package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Iterators;

import java.util.AbstractSet;
import java.util.Iterator;

import javax.annotation.Nullable;

import math.permutation.Permutation;

public final class LeftCoset<E> extends AbstractSet<Permutation<E>> {
  private final Permutation<E> sigma;
  private final PermutationGroup<E> group;

  public static <E> LeftCoset<E> coset(Permutation<E> sigma,
      PermutationGroup<E> group) {
    assert Objects.equal(sigma.domain(), group.domain());
    return new LeftCoset<E>(sigma, group);
  }

  private LeftCoset(Permutation<E> sigma, PermutationGroup<E> group) {
    this.sigma = sigma;
    this.group = group;
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(
      @Nullable Object o) {
    if (o instanceof LeftCoset) {
      LeftCoset<?> coset = (LeftCoset<?>) o;
      return Objects.equal(group, coset.group)
          && group.contains(sigma.inverse().compose((Permutation) coset.sigma));
    } else if (o instanceof PermutationGroup) {
      PermutationGroup<?> g = (PermutationGroup<?>) o;
      return Objects.equal(group, g) && group.contains(sigma);
    } else {
      return super.equals(o);
    }
  }

  @Override public boolean isEmpty() {
    return group.isEmpty();
  }

  @SuppressWarnings("unchecked") @Override public boolean contains(
      @Nullable Object o) {
    if (o instanceof Permutation) {
      Permutation tau = (Permutation) o;
      return group.contains(sigma.inverse().compose(tau));
    }
    return false;
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return Iterators.transform(group.iterator(),
        new Function<Permutation<E>, Permutation<E>>() {
          @Override public Permutation<E> apply(Permutation<E> input) {
            return sigma.compose(input);
          }
        });
  }

  @Override public int size() {
    return group.size();
  }
}
