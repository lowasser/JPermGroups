package math.algebra.permgroups;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

public abstract class Permutation<E> {
  public E image(E e) {
    return asMap().get(e);
  }

  public E preimage(E e) {
    return inverse().image(e);
  }

  public Set<E> domain() {
    return asMap().keySet();
  }

  public final int degree() {
    return domain().size();
  }

  private transient Map<E, E> map = null;

  public Map<E, E> asMap() {
    return (map == null) ? map = createAsMap() : map;
  }

  abstract Map<E, E> createAsMap();

  public Permutation<E> compose(Permutation<E> perm) {
    checkNotNull(perm);
    return new MapPermutation<E>(new ComposedPermutation<E>(this, perm));
  }

  private transient Permutation<E> inverse = null;

  public Permutation<E> inverse() {
    return (inverse == null) ? inverse = createInverse() : inverse;
  }

  Permutation<E> createInverse() {
    return new MapPermutation<E>(this).inverse();
  }

  static <E> void checkDomains(Permutation<E> p, Permutation<E> q) {
    if (!p.domain().equals(q.domain())) {
      throw new DomainMismatchException(p, q);
    }
  }
}
