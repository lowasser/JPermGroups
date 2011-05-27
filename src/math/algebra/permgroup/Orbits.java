package math.algebra.permgroup;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import math.structures.permutation.Permutation;

public class Orbits {

  private static class OrbitBuilderAction<E> {
    private final Set<Set<E>> orbit;
    private final Collection<Permutation<E>> generators;

    OrbitBuilderAction(Collection<Permutation<E>> generators) {
      this.generators = generators;
      this.orbit = Sets.newHashSet();
    }

    public void add(Set<E> set) {
      if (orbit.add(set)) {
        for (Permutation<E> sigma : generators) {
          add(sigma.apply(set));
        }
      }
    }

    public Set<Set<E>> build() {
      return Collections.unmodifiableSet(orbit);
    }
  }
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

  public static <E> Collection<Set<Set<E>>> actionOrbits(PermGroup<E> group,
      Set<Set<E>> domain) {
    return actionOrbits(group.generators(), domain);
  }

  public static <E> Collection<Set<Set<E>>> actionOrbits(
      Collection<Permutation<E>> generators, Set<Set<E>> domain) {
    Set<Set<E>> todo = Sets.newLinkedHashSet(domain);
    List<Set<Set<E>>> orbits = Lists.newArrayList();
    while (!todo.isEmpty()) {
      OrbitBuilderAction<E> orbit = new OrbitBuilderAction<E>(generators);
      orbit.add(todo.iterator().next());
      Set<Set<E>> theOrbit = orbit.build();
      orbits.add(theOrbit);
      assert todo.containsAll(theOrbit);
      todo.removeAll(theOrbit);
    }
    return Collections.unmodifiableCollection(orbits);
  }
}
