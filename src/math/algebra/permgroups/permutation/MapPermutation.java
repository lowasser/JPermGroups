package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;
import java.util.Set;

final class MapPermutation<E> extends Permutation<E> {
  private final ImmutableBiMap<E, E> permMap;

  public MapPermutation(Map<E, E> map) {
    checkNotNull(map);
    this.permMap = ImmutableBiMap.copyOf(map);
    checkArgument(permMap.keySet().equals(permMap.values()));
  }

  public MapPermutation(Permutation<E> permutation) {
    this(checkNotNull(permutation).asMap());
  }

  @Override public Set<E> domain() {
    return permMap.keySet();
  }

  @Override public E image(E e) {
    checkArgument(permMap.containsKey(e));
    return permMap.get(e);
  }

  @Override Function<E, E> createAsFunction() {
    return Functions.forMap(permMap);
  }

  @Override Map<E, E> createAsMap() {
    return permMap;
  }

  @Override Permutation<E> createInverse() {
    return new MapPermutation<E>(permMap.inverse());
  }
}
