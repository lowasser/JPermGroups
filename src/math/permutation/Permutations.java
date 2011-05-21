package math.permutation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.structures.Pair;

public final class Permutations {
  public static <E> Permutation<E> compose(Iterable<Permutation<E>> perms) {
    Iterator<Permutation<E>> iter = perms.iterator();
    checkArgument(iter.hasNext(),
        "Cannot determine the domain of the composed permutation");
    Permutation<E> p = iter.next();
    while (iter.hasNext()) {
      p = new ComposedPermutation<E>(p, iter.next());
    }
    return new MapPermutation<E>(p);
  }

  public static <E> Permutation<E> compose(Permutation<E> p,
      Permutation<E>... perms) {
    for (Permutation<E> q : perms) {
      p = new ComposedPermutation<E>(p, q);
    }
    return new MapPermutation<E>(p);
  }

  public static <E> Permutation<E> cyclePermutation(Set<E> domain,
      List<? extends List<E>> cycles) {
    Map<E, E> map = Maps.newHashMapWithExpectedSize(domain.size());
    for (E e : domain) {
      map.put(e, e);
    }
    for (List<E> cycle : cycles) {
      if (cycle.isEmpty()) {
        continue;
      }
      int k = cycle.size();
      E write = cycle.get(k - 1);
      for (int i = k - 2; i >= 0; i--) {
        write = map.put(cycle.get(i), write);
      }
      map.put(cycle.get(k - 1), write);
    }
    return extend(new MapPermutation<E>(map), domain);
  }

  public static <A, B> Permutation<Pair<A, B>> directProduct(
      Permutation<A> sigmaA, Permutation<B> sigmaB) {
    return new DirectProductPermutation<A, B>(sigmaA, sigmaB);
  }

  public static <E> Permutation<E> identity(Set<E> domain) {
    return new Identity<E>(domain);
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

  public static <E> Permutation<Pair<E, E>> pairAction(Permutation<E> sigma) {
    return directProduct(sigma, sigma);
  }

  public static <E> Permutation<E> permutation(Map<E, E> map) {
    return new MapPermutation<E>(map);
  }

  public static <E> Permutation<E> transposition(Set<E> domain, E a, E b) {
    Permutation<E> swap = new MapPermutation<E>(ImmutableMap.of(a, b, b, a));
    return extend(swap, domain);
  }

  public static <E> Permutation<E> extend(Permutation<E> sigma, Set<E> domain) {
    return (domain.size() == sigma.degree()) ? sigma
        : new ExtendedPermutation<E>(domain, sigma);
  }

  public static <E> boolean preserves(Permutation<E> p, Map<E, ?> coloring) {
    boolean good = true;
    checkArgument(coloring.keySet().containsAll(p.domain()));
    Iterator<E> iter = p.domain().iterator();
    while (good && iter.hasNext()) {
      E e = iter.next();
      E eImg = p.image(e);
      good &= coloring.get(e).equals(coloring.get(eImg));
    }
    return good;
  }

  public static <E> Permutation<E> restrict(Permutation<E> sigma, Set<E> orbit) {
    return new RestrictedPermutation<E>(sigma, orbit);
  }

  public static <E> boolean stabilizes(Permutation<E> p, E e) {
    return p.image(e).equals(e);
  }

  public static <E> boolean stabilizes(Permutation<E> p, Set<E> s) {
    if (s.size() > p.domain().size()) {
      return false;
    }
    Set<E> image = Sets.newHashSetWithExpectedSize(s.size());
    for (E e : s) {
      image.add(p.image(e));
    }
    return image.equals(s);
  }

  private Permutations() {
  }
}
