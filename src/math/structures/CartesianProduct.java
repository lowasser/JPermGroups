package math.structures;

import com.google.common.base.Objects;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

public class CartesianProduct<A, B> extends AbstractSet<Pair<A, B>> {
  public static <A, B> Set<Pair<A, B>> of(Set<A> setA, Set<B> setB) {
    return new CartesianProduct<A, B>(setA, setB);
  }

  private final ImmutableSet<A> setA;
  private final ImmutableSet<B> setB;

  private CartesianProduct(Set<A> setA, Set<B> setB) {
    this.setA = ImmutableSet.copyOf(setA);
    this.setB = ImmutableSet.copyOf(setB);
  }

  @Override public boolean contains(Object o) {
    if (o instanceof Pair) {
      Pair<?, ?> p = (Pair<?, ?>) o;
      return setA.contains(p.getFirst()) && setB.contains(p.getSecond());
    }
    return false;
  }

  @Override public boolean isEmpty() {
    return setA.isEmpty() || setB.isEmpty();
  }

  @Override public Iterator<Pair<A, B>> iterator() {
    if (setA.isEmpty() || setB.isEmpty()) {
      return Iterators.emptyIterator();
    }
    return new AbstractIterator<Pair<A, B>>() {
      private A a = null;
      private Iterator<A> iterA = setA.iterator();
      private Iterator<B> iterB = Iterators.emptyIterator();

      @Override protected Pair<A, B> computeNext() {
        if (!iterB.hasNext()) {
          if (!iterA.hasNext()) {
            return endOfData();
          }
          a = iterA.next();
          iterB = setB.iterator();
        }
        return Pair.of(a, iterB.next());
      }
    };
  }

  @Override public int size() {
    return setA.size() * setB.size();
  }

  @Override public boolean equals(@Nullable Object o) {
    if (o instanceof CartesianProduct) {
      CartesianProduct<?, ?> product = (CartesianProduct<?, ?>) o;
      return Objects.equal(setA, product.setA)
          && Objects.equal(setB, product.setB);
    }
    return super.equals(o);
  }
}
