package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

final class MapPermutation<E> extends Permutation<E> {
  private final ImmutableBiMap<E, E> permMap;

  MapPermutation(Map<E, E> map) {
    checkNotNull(map);
    this.permMap = ImmutableBiMap.copyOf(map);
    checkArgument(permMap.keySet().equals(permMap.values()));
  }

  MapPermutation(Permutation<E> permutation) {
    this(checkNotNull(permutation).asMap());
  }

  @Override public Set<E> domain() {
    return permMap.keySet();
  }

  @Override public E image(E e) {
    checkArgument(permMap.containsKey(e));
    return permMap.get(e);
  }

  @Override public E preimage(E e) {
    return inverse().image(e);
  }

  @Override Function<E, E> createAsFunction() {
    return Functions.forMap(permMap);
  }

  @Override public Map<E, E> asMap() {
    return permMap;
  }

  @Override Permutation<E> createInverse() {
    return new MapPermutation<E>(permMap.inverse());
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj instanceof MapPermutation) {
      MapPermutation<?> tau = (MapPermutation<?>) obj;
      return Objects.equal(permMap, tau.permMap);
    }
    return super.equals(obj);
  }
}
