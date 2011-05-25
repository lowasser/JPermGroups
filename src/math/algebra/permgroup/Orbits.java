package math.algebra.permgroup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import math.structures.permutation.Permutation;

public class Orbits {
  private static class OrbitBuilder<E> {
    private final Set<E> orbit;
    private final Collection<Permutation<E>> generators;

    OrbitBuilder(Collection<Permutation<E>> generators) {
      this.generators = generators;
      this.orbit = Sets.newHashSetWithExpectedSize(4);
    }

    public void add(E e) {
      if (orbit.add(e)) {
        for (Permutation<E> sigma : generators) {
          add(sigma.apply(e));
        }
      }
    }

    public Set<E> build() {
      return Collections.unmodifiableSet(orbit);
    }
  }

  public static <E> Collection<Set<E>> orbits(
      Collection<Permutation<E>> generators, Set<E> domain) {
    Set<E> todo = Sets.newLinkedHashSet(domain);
    List<Set<E>> orbits = Lists.newArrayList();
    while (!todo.isEmpty()) {
      OrbitBuilder<E> orbit = new OrbitBuilder<E>(generators);
      orbit.add(todo.iterator().next());
      Set<E> theOrbit = orbit.build();
      orbits.add(theOrbit);
      todo.removeAll(theOrbit);
    }
    return Collections.unmodifiableCollection(orbits);
  }
}
