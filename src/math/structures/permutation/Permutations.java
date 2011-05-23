package math.structures.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.FunctionMap;

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
    Map<E, E> map = Maps.newHashMap();
    sigmas = Lists.reverse(sigmas);
    for (Permutation<E> sigma : sigmas)
      map.putAll(new FunctionMap<E, E>(sigma.support(), Functions
        .<E> identity()));
    Iterator<Entry<E, E>> entryIter = map.entrySet().iterator();
    while (entryIter.hasNext()) {
      Entry<E, E> entry = entryIter.next();
      E e = entry.getKey();
      E img = e;
      for (Permutation<E> sigma : sigmas)
        img = sigma.apply(img);
      if (Objects.equal(e, img))
        entryIter.remove();
      else
        entry.setValue(img);
    }
    return new MapPermutation<E>(ImmutableBiMap.copyOf(map));
  }

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

  public static <E> Permutation<E> transposition(E a, E b) {
    return new Transposition<E>(a, b);
  }
  
  private Permutations() {
  }
}
