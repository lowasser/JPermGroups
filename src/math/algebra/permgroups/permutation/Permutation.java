package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;

import java.util.Map;
import java.util.Set;

import math.structures.FunctionMap;

public abstract class Permutation<E> {
  public abstract E image(E e);

  public abstract E preimage(E e);

  public abstract Set<E> domain();

  public final int degree() {
    return domain().size();
  }

  private transient Map<E, E> map = null;

  public Map<E, E> asMap() {
    return (map == null) ? map = createAsMap() : map;
  }

  private transient Function<E, E> function = null;

  public Function<E, E> asFunction() {
    return (function == null) ? function = createAsFunction() : function;
  }

  Function<E, E> createAsFunction() {
    return new Function<E, E>() {
      @Override public E apply(E input) {
        return image(input);
      }
    };
  }

  Map<E, E> createAsMap() {
    return new FunctionMap<E, E>(domain(), asFunction());
  }

  public Permutation<E> compose(Permutation<E> perm) {
    checkNotNull(perm);
    return new MapPermutation<E>(new ComposedPermutation<E>(this, perm));
  }

  private transient Permutation<E> inverse = null;

  public Permutation<E> inverse() {
    return (inverse == null) ? inverse = createInverse() : inverse;
  }

  Permutation<E> createInverse() {
    return new InversePermutation<E>(this);
  }

  static <E> void checkDomains(Permutation<E> p, Permutation<E> q) {
    if (!p.domain().equals(q.domain())) {
      throw new DomainMismatchException(p, q);
    }
  }

  @Override public int hashCode() {
    return asMap().hashCode();
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Permutation) {
      Permutation p = (Permutation) obj;
      if (!domain().equals(p.domain()))
        return false;
      for (E e : domain())
        if (!Objects.equal(image(e), p.image(e)))
          return false;
      return true;
    }
    return false;
  }

  @Override public String toString() {
    return asMap().toString();
  }

  public Permutation<E> extend(Set<E> newDomain) {
    checkArgument(newDomain.containsAll(domain()));
    return (newDomain.size() == degree()) ? this : new ExtendedPermutation<E>(
        newDomain, this);
  }
}
