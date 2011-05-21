package math.algebra.permgroup;

import com.google.common.base.Functions;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import math.permutation.Permutation;
import math.structures.FunctionMap;

public final class Orbit<E> extends ForwardingSet<E> {
  private final ImmutableSet<E> orbit;

  public static <E> Map<E, Orbit<E>> orbits(PermutationGroup<E> group) {
    ImmutableMap.Builder<E, Orbit<E>> orbitMap = ImmutableMap.builder();
    Set<E> todo = Sets.newLinkedHashSet(group.domain());
    while (!todo.isEmpty()) {
      E e = todo.iterator().next();
      Orbit<E> orbit = orbit(e, group);
      orbitMap.putAll(new FunctionMap<E, Orbit<E>>(orbit, Functions
        .<Orbit<E>> constant(orbit)));
      todo.removeAll(orbit);
    }
    return orbitMap.build();
  }

  public static <E> Orbit<E> orbit(E e, PermutationGroup<E> group) {
    return orbit(e, group.generators());
  }

  public static <E> Orbit<E>
      orbit(E e, Collection<Permutation<E>> permutations) {
    Set<E> orbit = Sets.newLinkedHashSet();
    buildOrbit(e, orbit, permutations);
    return new Orbit<E>(orbit);
  }

  public static <E> Orbit<E> orbit(E e, Permutation<E> sigma) {
    return orbit(e, ImmutableList.of(sigma));
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
