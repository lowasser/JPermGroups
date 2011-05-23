package math.structures.permutation;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

public abstract class AbstractPermutation<E> implements Permutation<E> {
  private transient Set<E> support = null;

  transient Permutation<E> inverse = null;

  private transient Integer hashCode = null;

  @Override public boolean equals(@Nullable Object obj) {
    if (obj == this)
      return true;
    else if (obj instanceof Permutation) {
      @SuppressWarnings("unchecked")
      Permutation<E> sigma = (Permutation) obj;
      Set<E> support = support();
      boolean good = Objects.equal(support, sigma.support());
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
      for (E e : support()) {
        h += Maps.immutableEntry(e, apply(e)).hashCode();
      }
      return hashCode = h;
    }
    return hashCode;
  }

  @Override public Permutation<E> inverse() {
    return (inverse == null) ? inverse = createInverse() : inverse;
  }

  @Override public Set<E> support() {
    return (support == null) ? support = createSupport() : support;
  }

  protected Permutation<E> createInverse() {
    return new InversePermutation<E>(this);
  }

  protected abstract Set<E> createSupport();
}
