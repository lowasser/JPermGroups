package math.algebra.permgroup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import math.structures.permutation.Permutation;

public class Orbits {
  private static class OrbitBuilder<E> {
    private final Set<E> orbit;
    private final Collection<Permutation<E>> generators;

    OrbitBuilder(Collection<Permutation<E>> generators) {
      this.generators = generators;
      this.orbit = Sets.newHashSet();
    }

    public void add(E e) {
      if (orbit.add(e)) {
        for (Permutation<E> sigma : generators) {
          add(sigma.apply(e));
        }
      }
    }

    public Set<E> build() {
      return ImmutableSet.copyOf(orbit);
    }
  }

  public static <E> Collection<Set<E>> orbits(
      Collection<Permutation<E>> generators, Set<E> domain) {
    Set<E> todo = Sets.newLinkedHashSet(domain);
    ImmutableList.Builder<Set<E>> orbits = ImmutableList.builder();
    while (!todo.isEmpty()) {
      OrbitBuilder<E> orbit = new OrbitBuilder<E>(generators);
      orbit.add(todo.iterator().next());
      Set<E> theOrbit = orbit.build();
      orbits.add(theOrbit);
      todo.removeAll(theOrbit);
    }
    return orbits.build();
  }
}
