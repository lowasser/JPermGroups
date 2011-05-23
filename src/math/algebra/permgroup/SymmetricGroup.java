package math.algebra.permgroup;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

final class SymmetricGroup<E> extends PermutationGroup<E> {
  private final ImmutableSet<E> domain;
  private transient Collection<Permutation<E>> generators;
  private final BigInteger size;

  SymmetricGroup(Set<E> domain) {
    this.domain = ImmutableSet.copyOf(domain);
    this.size = factorial(this.domain.size());
  }

  @Override public Collection<Permutation<E>> generators() {
    if (generators == null) {
      ImmutableList<E> domainList = domain.asList();
      Permutation<E> sigma =
          Permutations.transposition(domainList.get(0), domainList.get(1));
      Permutation<E> tau = Permutations.cycle(domainList);
      return generators = ImmutableList.of(sigma, tau);
    }
    return generators;
  }

  @Override public Set<E> support() {
    return domain;
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return new AbstractIterator<Permutation<E>>() {
      BigInteger i = BigInteger.ZERO;

      @Override protected Permutation<E> computeNext() {
        if (i.compareTo(size) >= 0)
          return endOfData();
        Permutation<E> result = unrank(i);
        i = i.add(BigInteger.ONE);
        return result;
      }
    };
  }

  private static BigInteger factorial(int size) {
    BigInteger f = BigInteger.valueOf(size);
    for (int i = size - 1; i > 0; i--) {
      f = f.multiply(BigInteger.valueOf(i));
    }
    return f;
  }

  @Override public boolean contains(@Nullable Object o) {
    if (o instanceof Permutation) {
      Permutation<?> sigma = (Permutation<?>) o;
      return domain.containsAll(sigma.support());
    }
    return false;
  }

  private Permutation<E> unrank(BigInteger d) {
    int k = d.bitLength();
    int[] t = new int[2 << k];
    for (int i = 0; i <= k; i++) {
      for (int j = 0; (j >> i) == 0; j++) {
        t[(1 << i) + j] = 1 << (k - i);
      }
    }
    int n = domain.size();
    Map<E, E> map = Maps.newHashMap();
    for (int i = 0; i < n; i++) {
      BigInteger[] divideAndRemainder =
          d.divideAndRemainder(BigInteger.valueOf(n - i));
      int digit = divideAndRemainder[1].intValue();
      d = divideAndRemainder[0];
      int node = 1;
      for (int j = 0; j < k; j++) {
        t[node]--;
        node <<= 1;
        if (digit >= t[node]) {
          digit -= t[node];
          node++;
        }
      }
      t[node] = 0;
      List<E> dom = domain.asList();
      map.put(dom.get(i), dom.get(node - (1 << k)));
    }
    return Permutations.permutation(map);
  }

  @Override public int size() {
    if (size.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
      return Integer.MAX_VALUE;
    }
    return size.intValue();
  }

}
