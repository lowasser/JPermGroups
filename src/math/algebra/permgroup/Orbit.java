package math.algebra.permgroup;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import math.permutation.Permutation;

public final class Orbit<E> extends ForwardingSet<E> {
  private final ImmutableSet<E> orbit;

  public static <E> Orbit<E> orbit(E e, PermutationGroup<E> group) {
    return orbit(e, group.generators());
  }

  public static <E> Orbit<E>
      orbit(E e, Collection<Permutation<E>> permutations) {
    Set<E> orbit = Sets.newLinkedHashSet();
    buildOrbit(e, orbit, permutations);
    return new Orbit<E>(orbit);
  }

  public static <E> Orbit<E> orbit(E e, Permutation<E>... sigmas) {
    return orbit(e, Arrays.asList(sigmas));
  }

  private Orbit(Set<E> orbit) {
    this.orbit = ImmutableSet.copyOf(orbit);
  }

  private static <E> void buildOrbit(E e, Set<E> visited,
      Collection<Permutation<E>> permutations) {
    if (!visited.add(e))
      return;
    for (Permutation<E> sigma : permutations) {
      buildOrbit(sigma.image(e), visited, permutations);
    }
  }

  @Override protected Set<E> delegate() {
    return orbit;
  }
}
