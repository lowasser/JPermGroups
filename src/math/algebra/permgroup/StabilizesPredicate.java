package math.algebra.permgroup;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

final class StabilizesPredicate<E> implements Predicate<Permutation<E>> {
  private final ImmutableSet<E> set;

  private StabilizesPredicate(ImmutableSet<E> set) {
    this.set = set;
  }

  public static <E> StabilizesPredicate<E> on(E e) {
    return new StabilizesPredicate<E>(ImmutableSet.of(e));
  }

  public static <E> StabilizesPredicate<E> on(E... elements) {
    return new StabilizesPredicate<E>(ImmutableSet.copyOf(elements));
  }

  public static <E> StabilizesPredicate<E> on(Set<E> elements) {
    return new StabilizesPredicate<E>(ImmutableSet.copyOf(elements));
  }

  @Override public boolean apply(Permutation<E> sigma) {
    return Permutations.stabilizes(sigma, set);
  }

  @Override public int hashCode() {
    return set.hashCode();
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof StabilizesPredicate) {
      return ((StabilizesPredicate) obj).set.equals(this.set);
    }
    return false;
  }

  @Override public String toString() {
    return "Stabilizes" + set;
  }
}