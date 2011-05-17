package math.algebra.permgroups;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

final class MapPermutation<E> extends Permutation<E> {
  private final ImmutableMap<E, E> permMap;
  private final ImmutableMap<E, E> inverseMap;

  private MapPermutation(ImmutableMap<E, E> permutation,
      ImmutableMap<E, E> inverse) {
    this.permMap = permutation;
    this.inverseMap = inverse;
    assert permMap.keySet().equals(inverseMap.keySet());
  }

  public MapPermutation(Map<E, E> map) {
    checkNotNull(map);
    int degree = map.size();
    Map<E, E> pInv = Maps.newHashMapWithExpectedSize(degree);
    for (Map.Entry<E, E> entry : map.entrySet()) {
      checkArgument(!pInv.containsKey(entry.getValue()),
          "%s is not a bijective map", map);
      pInv.put(entry.getValue(), entry.getKey());
    }

    checkArgument(pInv.keySet().equals(map.keySet()),
        "%s is not a bijective map", map);

    this.permMap = ImmutableMap.copyOf(map);
    this.inverseMap = ImmutableMap.copyOf(pInv);
  }

  public MapPermutation(Permutation<E> permutation) {
    this(checkNotNull(permutation).asMap());
  }

  @Override Permutation<E> createInverse() {
    return new MapPermutation<E>(inverseMap, permMap);
  }

  @Override Map<E, E> createAsMap() {
    return permMap;
  }
}
