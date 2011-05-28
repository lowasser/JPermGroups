package math.structures.permutation;

import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.FunctionMap;

public abstract class AbstractPermutation<E> implements Permutation<E> {
  static int gcd(int a, int b) {
    a = Math.abs(a);
    b = Math.abs(b);
    if (a < b) {
      int tmp = a;
      a = b;
      b = tmp;
    }
    while (b > 0) {
      int tmp = a % b;
      a = b;
      b = tmp;
    }
    return a;
  }

  static int lcm(int a, int b) {
    b /= gcd(a, b);
    return a * b;
  }

  private transient Set<E> domain = null;
  transient Permutation<E> inverse = null;
  private transient Integer hashCode = null;
  transient Parity parity = null;
  private transient int order = -1;
  private transient Map<E, E> asMap;

  @Override public Set<E> apply(Set<E> set) {
    return ImmutableSet.copyOf(Collections2.transform(set, this));
  }

  @Override public Map<E, E> asMap() {
    return (asMap == null) ? asMap = createAsMap() : asMap;
  }

  @Override public Permutation<E> compose(Permutation<E> tau) {
    return compose(ImmutableList.of(tau));
  }

  @Override public Set<E> domain() {
    return (domain == null) ? domain = createDomain() : domain;
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Permutation) {
      @SuppressWarnings("unchecked")
      Permutation<E> sigma = (Permutation) obj;
      Set<E> support = domain();
      boolean good = Objects.equal(support, sigma.domain());
      for (Iterator<E> iter = support.iterator(); good && iter.hasNext();) {
        E e = iter.next();
        good &= Objects.equal(apply(e), sigma.apply(e));
      }
      return good;
    }
    return false;
  }

  @Override public int hashCode() {
    if (hashCode == null) {
      int h = 0;
      for (E e : domain()) {
        h += Maps.immutableEntry(e, apply(e)).hashCode();
      }
      return hashCode = h;
    }
    return hashCode;
  }

  @Override public Permutation<E> inverse() {
    return (inverse == null) ? inverse = createInverse() : inverse;
  }

  @Override public boolean isIdentity() {
    return domain().isEmpty();
  }

  @Override public int order() {
    if (order >= 0) {
      return order;
    }
    return order = computeOrder();
  }

  @Override public Parity parity() {
    return (parity == null) ? parity = computeParity() : parity;
  }

  @Override public boolean stabilizes(E e) {
    return Objects.equal(e, apply(e));
  }

  @Override public boolean stabilizes(Set<E> s) {
    for (E e : s) {
      if (!s.contains(apply(e))) {
        return false;
      }
    }
    return true;
  }

  @Override public String toString() {
    return createAsMap().toString();
  }

  protected int computeOrder() {
    int order = 1;
    Set<E> todo = Sets.newLinkedHashSet(domain());
    while (!todo.isEmpty()) {
      int k = 0;
      for (E e = todo.iterator().next(); todo.remove(e); e = apply(e)) {
        k++;
      }
      order = lcm(order, k);
    }
    return order;
  }

  protected Parity computeParity() {
    Parity p = Parity.EVEN;
    Set<E> todo = Sets.newLinkedHashSet(domain());
    while (!todo.isEmpty()) {
      Iterator<E> iter = todo.iterator();
      E start = iter.next();
      iter.remove();
      int count = 1;
      for (E e = apply(start); !Objects.equal(e, start); e = apply(e)) {
        count++;
        todo.remove(e);
      }
      if ((count & 1) == 0) {
        p = p.inverse();
      }
    }
    return p;
  }

  protected Map<E, E> createAsMap() {
    return new FunctionMap<E, E>(domain(), this);
  }

  protected abstract Set<E> createDomain();

  protected Permutation<E> createInverse() {
    return new InversePermutation<E>(this);
  }

  protected abstract Permutation<E> inverseCompose(List<Permutation<E>> taus);
}
