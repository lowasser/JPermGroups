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
import math.structures.permutation.Permutations;

public final class LeftCoset<E> extends AbstractSet<Permutation<E>> {
  public static <E> LeftCoset<E> coset(Permutation<E> sigma,
      Collection<Permutation<E>> generators) {
    return new LeftCoset<E>(sigma, generators);
  }

  public static <E> LeftCoset<E> coset(Permutation<E> sigma,
      AbstractPermutationGroup<E> group) {
    return new LeftCoset<E>(sigma, group);
  }

  public static <E> LeftCoset<E> compose(Permutation<E> sigma,
      LeftCoset<E> coset) {
    return coset(Permutations.compose(sigma, coset.getRepresentative()),
        coset.getGroup());
  }

  private final Permutation<E> sigma;

  private final Collection<Permutation<E>> generators;

  private transient PermutationGroup<E> group;

  private LeftCoset(Permutation<E> sigma, Collection<Permutation<E>> generators) {
    this.sigma = sigma;
    this.generators = generators;
  }

  private LeftCoset(Permutation<E> sigma,
      Collection<Permutation<E>> generators, RegularPermutationGroup<E> group) {
    this.sigma = checkNotNull(sigma);
    this.generators = ImmutableList.copyOf(generators);
    this.group = checkNotNull(group);
    checkArgument(group.containsAll(generators));
  }

  private LeftCoset(Permutation<E> sigma, RegularPermutationGroup<E> group) {
    this(sigma, group.generators(), group);
  }

  public LeftCoset<E> compose(Permutation<E> tau) {
    return new LeftCoset<E>(Permutations.compose(tau, sigma), group);
  }

  @SuppressWarnings("unchecked") @Override public boolean contains(
      @Nullable Object o) {
    if (o instanceof Permutation) {
      Permutation tau = (Permutation) o;
      return group.contains(Permutations.compose(sigma.inverse(), tau));
    }
    return false;
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(
      @Nullable Object o) {
    if (o instanceof LeftCoset) {
      LeftCoset<?> coset = (LeftCoset<?>) o;
      return Objects.equal(group, coset.group)
          && group.contains(Permutations.compose(sigma.inverse(),
              (Permutation) coset.sigma));
    } else if (o instanceof RegularPermutationGroup) {
      RegularPermutationGroup<?> g = (RegularPermutationGroup<?>) o;
      return Objects.equal(group, g) && group.contains(sigma);
    } else {
      return super.equals(o);
    }
  }

  public Collection<Permutation<E>> getGenerators() {
    return generators;
  }

  public PermutationGroup<E> getGroup() {
    return (group == null) ? group = Groups.generateGroup(generators) : group;
  }

  public Permutation<E> getRepresentative() {
    return sigma;
  }

  @Override public boolean isEmpty() {
    return group.isEmpty();
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return Iterators.transform(group.iterator(),
        new Function<Permutation<E>, Permutation<E>>() {
          @Override public Permutation<E> apply(Permutation<E> tau) {
            return Permutations.compose(sigma, tau);
          }
        });
  }

  @Override public int size() {
    return group.size();
  }
}
