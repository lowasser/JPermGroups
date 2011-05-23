package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;

public final class LeftCoset<E> extends AbstractSet<Permutation<E>> {
  private final Permutation<E> sigma;
  private final Collection<Permutation<E>> generators;
  private transient PermutationGroup<E> group;

  public static <E> LeftCoset<E> coset(Permutation<E> sigma,
      Collection<Permutation<E>> generators) {
    return new LeftCoset<E>(sigma, generators);
  }

  public static <E> LeftCoset<E> coset(Permutation<E> sigma,
      PermutationGroup<E> group) {
    return new LeftCoset<E>(sigma, group);
  }

  public Collection<Permutation<E>> getGenerators() {
    return generators;
  }

  private LeftCoset(Permutation<E> sigma, Collection<Permutation<E>> generators) {
    this.sigma = sigma;
    this.generators = generators;
  }

  private LeftCoset(Permutation<E> sigma,
      Collection<Permutation<E>> generators, PermutationGroup<E> group) {
    this.sigma = checkNotNull(sigma);
    this.generators = ImmutableList.copyOf(generators);
    this.group = checkNotNull(group);
    checkArgument(group.containsAll(generators));
    checkArgument(Objects.equal(sigma.domain(), group.domain()));
  }

  private LeftCoset(Permutation<E> sigma, PermutationGroup<E> group) {
    this(sigma, group.generators(), group);
  }

  public Permutation<E> getRepresentative() {
    return sigma;
  }

  public PermutationGroup<E> getGroup() {
    return (group == null) ? group =
        Groups.generateGroup(sigma.domain(), generators) : group;
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

  public LeftCoset<E> compose(Permutation<E> tau) {
    return new LeftCoset<E>(tau.compose(sigma), group);
  }
}
