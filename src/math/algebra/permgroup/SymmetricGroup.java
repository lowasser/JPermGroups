package math.algebra.permgroup;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

final class SymmetricGroup<E> extends AbstractPermGroup<E> {
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
      if (domain.size() > 2) {
        Permutation<E> tau = Permutations.cycle(domainList);
        return generators = ImmutableList.of(sigma, tau);
      } else {
        return generators = ImmutableList.of(sigma);
      }
    }
    return generators;
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
      return domain.containsAll(sigma.domain());
    }
    return false;
  }

  private Permutation<E> unrank(BigInteger d) {
    List<E> output = Lists.newArrayList(domain.asList());
    for (int n = output.size(); n > 0; n--) {
      BigInteger[] quotRem = d.divideAndRemainder(BigInteger.valueOf(n));
      Collections.swap(output, n - 1, quotRem[1].intValue());
      d = quotRem[0];
    }
    Map<E, E> perm = Maps.newHashMapWithExpectedSize(output.size());
    for (int i = 0; i < output.size(); i++) {
      perm.put(domain.asList().get(i), output.get(i));
    }
    return Permutations.permutation(perm);
  }

  @Override public int size() {
    if (size.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) >= 0) {
      return Integer.MAX_VALUE;
    }
    return size.intValue();
  }

}
