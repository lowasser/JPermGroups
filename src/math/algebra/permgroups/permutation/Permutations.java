package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class Permutations {
  private Permutations() {
  }

  public static <E> Permutation<E> compose(Permutation<E> p,
      Permutation<E>... perms) {
    p = new MutablePermutation<E>(p);
    for (Permutation<E> q : perms) {
      p = p.compose(q);
    }
    return new MapPermutation<E>(p);
  }

  public static <E> Permutation<E> extend(Permutation<E> sigma, Set<E> domain) {
    return new ExtendedPermutation<E>(domain, sigma);
  }

  public static <E> Permutation<E> compose(Iterable<Permutation<E>> perms) {
    Iterator<Permutation<E>> iter = perms.iterator();
    checkArgument(iter.hasNext());
    Permutation<E> p = new MutablePermutation<E>(iter.next());
    while (iter.hasNext()) {
      p = p.compose(iter.next());
    }
    return new MapPermutation<E>(p);
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

  public static <E1, E2> Permutation<E2> induced(Permutation<E1> sigma,
      Function<E1, E2> f) {
    Map<E2, E2> map = Maps.newHashMap();
    for (E1 e1 : sigma.domain()) {
      E2 e2 = f.apply(e1);
      E1 e1I = sigma.image(e1);
      E2 e2I = f.apply(e1I);
      if (map.containsKey(e2)) {
        checkArgument(e2I.equals(map.get(e2)),
            "Function does not induce a bijective mapping");
      } else {
        map.put(e2, e2I);
      }
    }
    return permutation(map);
  }

  public static <E> boolean isIdentity(Permutation<E> sigma) {
    for (E e : sigma.domain()) {
      if (!Objects.equal(e, sigma.image(e))) {
        return false;
      }
    }
    return true;
  }
}
