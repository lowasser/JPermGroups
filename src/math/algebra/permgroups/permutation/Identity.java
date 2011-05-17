package math.algebra.permgroups.permutation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

final class Identity<E> extends Permutation<E> {
  private final ImmutableMap<E, E> map;

  Identity(Set<E> domain) {
    Map<E, E> tmp = Maps.newHashMapWithExpectedSize(domain.size());
    for (E e : domain) {
      tmp.put(e, e);
    }
    this.map = ImmutableMap.copyOf(tmp);
  }

  @Override Map<E, E> createAsMap() {
    return map;
  }

  @Override public E image(E e) {
    return e;
  }

  @Override public E preimage(E e) {
    return e;
  }

  @Override public Permutation<E> compose(Permutation<E> perm) {
    return perm;
  }

  @Override Permutation<E> createInverse() {
    return this;
  }
}
