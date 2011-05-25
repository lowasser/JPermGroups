package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.Set;

import math.structures.Subsets;

public class SetAction<E> extends AbstractPermutation<Set<E>> {
  private final Permutation<E> sigma;
  private final int k;

  SetAction(Permutation<E> sigma, int k) {
    this.sigma = sigma;
    this.k = k;
  }

  private transient final Function<Set<E>, Set<E>> inverseAction =
      new Function<Set<E>, Set<E>>() {
        @Override public Set<E> apply(Set<E> set) {
          checkArgument(set.size() <= k);
          ImmutableSet.Builder<E> builder = ImmutableSet.builder();
          for (E e : set) {
            builder.add(sigma.preimage(e));
          }
          return builder.build();
        }
      };
  private transient final Function<Set<E>, Set<E>> action =
      new Function<Set<E>, Set<E>>() {
        @Override public Set<E> apply(Set<E> set) {
          checkArgument(set.size() <= k);
          ImmutableSet.Builder<E> builder = ImmutableSet.builder();
          for (E e : set) {
            builder.add(sigma.apply(e));
          }
          return builder.build();
        }
      };

  private transient final Map<Set<E>, Set<E>> imageCache = new MapMaker()
    .maximumSize(100).makeComputingMap(action);
  private transient final Map<Set<E>, Set<E>> preImageCache = new MapMaker()
    .maximumSize(100).softKeys().makeComputingMap(inverseAction);

  @Override public Set<E> preimage(Set<E> set) {
    return preImageCache.get(set);
  }

  @Override public Set<E> apply(Set<E> set) {
    return imageCache.get(set);
  }

  @Override protected Set<Set<E>> createSupport() {
    return ImmutableSet.copyOf(Iterables.filter(
        Subsets.subsetsOfSizeAtMost(sigma.support(), k),
        new Predicate<Set<E>>() {
          @Override public boolean apply(Set<E> set) {
            return !Objects.equal(SetAction.this.apply(set), set);
          }
        }));
  }
}
