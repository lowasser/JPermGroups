package math.algebra.permgroups.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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

  @Override Permutation<E> createInverse() {
    return new MapPermutation<E>(permMap.inverse());
  }

  @Override Map<E, E> createAsMap() {
    return permMap;
  }

  @Override public Set<E> domain() {
    return permMap.keySet();
  }
}
