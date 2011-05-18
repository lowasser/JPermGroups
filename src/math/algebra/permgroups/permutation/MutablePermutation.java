package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

final class MutablePermutation<E> extends Permutation<E> {
  private final Map<E, E> permMap;

  MutablePermutation(Permutation<E> permutation) {
    this.permMap = Maps.newHashMap(permutation.asMap());
  }

  @Override public Function<E, E> asFunction() {
    return Functions.forMap(permMap);
  }

  @Override public Permutation<E> compose(Permutation<E> perm) {
    checkDomains(this, perm);
    for (Map.Entry<E, E> entry : permMap.entrySet()) {
      entry.setValue(perm.image(entry.getValue()));
    }
    return this;
  }

  @Override public Set<E> domain() {
    return permMap.keySet();
  }

  @Override public E image(E e) {
    checkArgument(permMap.containsKey(e));
    return permMap.get(e);
  }

  @Override Map<E, E> createAsMap() {
    return Collections.unmodifiableMap(permMap);
  }
}
