package math.algebra.permgroups.permutation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Permutations {
  private Permutations() {
  }

  public static <E> Permutation<E> permutation(Map<E, E> map) {
    return new MapPermutation<E>(map);
  }

  public static <E> Permutation<E> identity(Set<E> domain) {
    return new Identity<E>(domain);
  }

  public static <E> boolean stabilizes(Permutation<E> p, E e) {
    return p.image(e).equals(e);
  }

  public static <E> boolean stabilizes(Permutation<E> p, Set<E> s) {
    Set<E> image = Sets.newHashSetWithExpectedSize(s.size());
    for (E e : s) {
      image.add(p.image(e));
    }
    return image.equals(s);
  }

  public static <E> Permutation<E> cyclePermutation(Set<E> domain,
      List<List<E>> cycles) {
    Map<E, E> map = Maps.newHashMapWithExpectedSize(domain.size());
    for (E e : domain) {
      map.put(e, e);
    }
    for (List<E> cycle : cycles) {
      if (cycle.isEmpty()) {
        continue;
      }
      int k = cycle.size();
      E write = map.get(cycle.get(k - 1));
      for (int i = k - 2; i >= 0; i--) {
        write = map.put(cycle.get(i), write);
      }
      map.put(cycle.get(k - 1), write);
    }
    return new MapPermutation<E>(map);
  }
}
