package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkNotNull;
import static math.structures.permutation.Permutations.compose;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;

public final class LCoset<E> extends AbstractSet<Permutation<E>> {
  public static <E> LCoset<E> precompose(Permutation<E> sigma, LCoset<E> coset) {
    return new LCoset<E>(compose(sigma, coset.getRepresentative()),
        coset.getGroup());
  }

  private final Permutation<E> sigma;

  private final Collection<Permutation<E>> generators;

  private transient PermGroup<E> group;

  public LCoset(Permutation<E> sigma, Collection<Permutation<E>> generators) {
    this.sigma = sigma;
    this.generators = generators;
  }

  public LCoset(Permutation<E> sigma, PermGroup<E> group) {
    this.sigma = checkNotNull(sigma);
    this.generators = ImmutableList.copyOf(group.generators());
    this.group = checkNotNull(group);
  }

  @SuppressWarnings("unchecked") @Override public boolean contains(
      @Nullable Object o) {
    if (o instanceof Permutation) {
      Permutation tau = (Permutation) o;
      return group.contains(compose(sigma.inverse(), tau));
    }
    return false;
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(
      @Nullable Object o) {
    if (o instanceof LCoset) {
      LCoset<?> coset = (LCoset<?>) o;
      return Objects.equal(group, coset.group)
          && group
            .contains(compose(sigma.inverse(), (Permutation) coset.sigma));
    } else if (o instanceof RegularPermGroup) {
      RegularPermGroup<?> g = (RegularPermGroup<?>) o;
      return Objects.equal(group, g) && group.contains(sigma);
    } else {
      return super.equals(o);
    }
  }

  public Collection<Permutation<E>> getGenerators() {
    return generators;
  }

  public PermGroup<E> getGroup() {
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
            return compose(sigma, tau);
          }
        });
  }

  @Override public int size() {
    return group.size();
  }
}
