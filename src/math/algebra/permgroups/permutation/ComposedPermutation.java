package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

final class ComposedPermutation<E> extends Permutation<E> {
  private final Permutation<E> p;
  private final Permutation<E> q;
  private Map<E, E> map = null;

  ComposedPermutation(Permutation<E> p, Permutation<E> q) {
    this.p = checkNotNull(p);
    this.q = checkNotNull(q);
    checkDomains(p, q);
  }

  @Override public Set<E> domain() {
    return p.domain();
  }

  @Override Map<E, E> createAsMap() {
    Map<E, E> tmp = Maps.newHashMap(p.asMap());
    for (Map.Entry<E, E> entry : tmp.entrySet()) {
      entry.setValue(q.image(entry.getValue()));
    }
    return ImmutableMap.copyOf(tmp);
  }

  @Override public E image(E e) {
    return q.image(p.image(e));
  }

  @Override public E preimage(E e) {
    return p.preimage(q.preimage(e));
  }

  @Override Permutation<E> createInverse() {
    return new ComposedPermutation<E>(q.inverse(), p.inverse());
  }
}
