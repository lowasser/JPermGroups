package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public final class Permutations {
  private static final Permutation<Object> IDENTITY =
      new AbstractPermutation<Object>() {
        @Override public Object apply(Object o) {
          return checkNotNull(o);
        }

        @Override public Permutation<Object> compose(
            List<Permutation<Object>> taus) {
          return Permutations.compose(taus);
        }

        @Override public Permutation<Object> compose(Permutation<Object> tau) {
          return tau;
        }

        @Override public Set<Object> domain() {
          return ImmutableSet.of();
        }

        @Override public boolean equals(@Nullable Object obj) {
          if (obj instanceof Permutation) {
            return ((Permutation<?>) obj).domain().isEmpty();
          }
          return false;
        }

        @Override public int hashCode() {
          return 0;
        }

        @Override public Permutation<Object> inverse() {
          return this;
        }

        @Override public boolean isIdentity() {
          return true;
        }

        @Override public int order() {
          return 1;
        }

        @Override public Parity parity() {
          return Parity.EVEN;
        }

        @Override public Object preimage(Object o) {
          return checkNotNull(o);
        }

        @Override public boolean stabilizes(Object o) {
          return true;
        }

        @Override public boolean stabilizes(Set<Object> s) {
          return true;
        }

        @Override protected Map<Object, Object> createAsMap() {
          return ImmutableMap.of();
        }

        @Override protected Set<Object> createDomain() {
          return ImmutableSet.of();
        }

        @Override protected Permutation<Object> inverseCompose(
            List<Permutation<Object>> taus) {
          return Permutations.compose(taus);
        }
      };

  public static <E> Permutation<E> compose(List<Permutation<E>> sigmas) {
    if (sigmas.isEmpty()) {
      return identity();
    }
    Permutation<E> sigma = sigmas.get(0);
    if (sigmas.size() == 1) {
      return sigma;
    }
    return sigma.compose(sigmas.subList(1, sigmas.size()));
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

  public static <A, B> Permutation<B> induced(Permutation<A> sigma,
      Function<A, B> phi) {
    Map<B, B> map = Maps.newHashMap();
    for (A a : sigma.domain()) {
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

  public static <E> Permutation<E> permutation(Map<E, E> map) {
    return new MapPermutation<E>(map);
  }

  public static <E> Permutation<E> transposition(E a, E b) {
    return new Transposition<E>(a, b);
  }

  private Permutations() {
  }
}
