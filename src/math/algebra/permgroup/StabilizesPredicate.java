package math.algebra.permgroup;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import math.structures.permutation.Permutation;

final class StabilizesPredicate<E> implements Predicate<Permutation<E>> {
  public static <E> StabilizesPredicate<E> on(E e) {
    return new StabilizesPredicate<E>(ImmutableSet.of(e));
  }

  public static <E> StabilizesPredicate<E> on(E... elements) {
    return new StabilizesPredicate<E>(ImmutableSet.copyOf(elements));
  }

  public static <E> StabilizesPredicate<E> on(Set<E> elements) {
    return new StabilizesPredicate<E>(ImmutableSet.copyOf(elements));
  }

  private final ImmutableSet<E> set;

  private StabilizesPredicate(ImmutableSet<E> set) {
    this.set = set;
  }

  @Override public boolean apply(Permutation<E> sigma) {
    return sigma.stabilizes(set);
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof StabilizesPredicate) {
      return ((StabilizesPredicate) obj).set.equals(this.set);
    }
    return false;
  }

  @Override public int hashCode() {
    return set.hashCode();
  }

  @Override public String toString() {
    return "Stabilizes" + set;
  }
}