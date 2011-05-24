package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

public final class Permutations {
  private static final Permutation<Object> IDENTITY =
      new AbstractPermutation<Object>() {
        @Override public Object apply(Object o) {
          return checkNotNull(o);
        }

        @Override public boolean equals(@Nullable Object obj) {
          if (obj instanceof Permutation) {
            return ((Permutation<?>) obj).support().isEmpty();
          }
          return false;
        }

        @Override public int hashCode() {
          return 0;
        }

        @Override public Permutation<Object> inverse() {
          return this;
        }

        @Override public Object preimage(Object o) {
          return checkNotNull(o);
        }

        @Override protected Set<Object> createSupport() {
          return ImmutableSet.of();
        }
      };

  public static <E> Permutation<E> compose(List<Permutation<E>> sigmas) {
    Map<E, E> tau = Maps.newHashMap();
    for (Permutation<E> sigma : sigmas) {
      if (!tau.isEmpty()) {
        List<Entry<E, E>> entryList = Lists.newArrayList();
        for (Entry<E, E> entry : sigma.asMap().entrySet()) {
          E e = entry.getKey();
          E sigmaE = entry.getValue();
          E sigmaTauE = tau.remove(sigmaE);
          if (!Objects.equal(sigmaTauE, e)) {
            entryList.add(Maps.immutableEntry(e, (sigmaTauE == null) ? sigmaE
                : sigmaTauE));
          }
        }
        for (Entry<E, E> entry : entryList) {
          tau.put(entry.getKey(), entry.getValue());
        }
      } else {
        tau.putAll(sigma.asMap());
      }
    }
    return new MapPermutation<E>(ImmutableBiMap.copyOf(tau));
  }

  // Composing sigma with tau
  public static <E> Permutation<E> compose(Permutation<E> sigma,
      Permutation<E> tau) {
    return compose(ImmutableList.of(sigma, tau));
  }

  public static <E> Permutation<E> compose(Permutation<E> sigma1,
      Permutation<E> sigma2, Permutation<E> sigma3) {
    return compose(ImmutableList.of(sigma1, sigma2, sigma3));
  }

  public static <E> Permutation<E> conjugate(Permutation<E> sigma,
      Permutation<E> tau) {
    return compose(tau.inverse(), sigma, tau);
  }

  public static <E> Permutation<E> cycle(List<E> cycle) {
    return (cycle.size() <= 1) ? Permutations.<E> identity() : new Cycle<E>(
        cycle);
  }

  @SuppressWarnings("unchecked") public static <E> Permutation<E> identity() {
    return (Permutation) IDENTITY;
  }

  public static <E> Permutation<E> permutation(Map<E, E> map) {
    return new MapPermutation<E>(map);
  }

  public static <E> Permutation<E>
      restrict(Permutation<E> sigma, Set<E> domain) {
    if (domain.containsAll(sigma.support())) {
      return sigma;
    }
    Set<E> support = Sets.newHashSet(sigma.support());
    support.retainAll(domain);
    return new RestrictedPermutation<E>(sigma, support);
  }

  public static <E> Permutation<E> transposition(E a, E b) {
    return new Transposition<E>(a, b);
  }

  public static <A, B> Permutation<B> induced(Permutation<A> sigma,
      Function<A, B> phi) {
    Map<B, B> map = Maps.newHashMap();
    for (A a : sigma.support()) {
      A aImg = sigma.apply(a);
      B b = phi.apply(a);
      B bImg = phi.apply(aImg);
      if (map.containsKey(b)) {
        checkArgument(Objects.equal(bImg, map.get(b)),
            "Function does not induce a bijection");
      } else {
        map.put(b, bImg);
      }
    }
    return permutation(map);
  }

  private Permutations() {
  }
}
