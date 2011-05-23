package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;

import java.util.Map;
import java.util.Set;

final class MapPermutation<E> extends AbstractPermutation<E> {
  /**
   * Must not map any element to itself.
   */
  private final ImmutableBiMap<E, E> map;

  MapPermutation(Map<E, E> map) {
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    for (Map.Entry<E, E> entry : checkNotNull(map).entrySet()) {
      E e = entry.getKey();
      E img = entry.getValue();
      if (!Objects.equal(e, img))
        builder.put(e, img);
    }
    this.map = builder.build();
    checkArgument(this.map.keySet().equals(this.map.values()),
        "Map %s is not a bijection", map);
  }

  MapPermutation(Permutation<E> sigma) {
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    for (E e : checkNotNull(sigma).support())
      builder.put(e, sigma.apply(e));
    this.map = builder.build();
  }

  MapPermutation(ImmutableBiMap<E, E> map) {
    this.map = checkNotNull(map);
    checkArgument(map.values().equals(map.keySet()));
  }

  @Override public E apply(E e) {
    E result = map.get(checkNotNull(e));
    return (result == null) ? e : result;
  }

  @Override public int hashCode() {
    return map.hashCode();
  }

  @Override public E preimage(E e) {
    E result = map.inverse().get(checkNotNull(e));
    return (result == null) ? e : result;
  }

  @Override protected Set<E> createSupport() {
    return map.keySet();
  }

  @Override protected Permutation<E> createInverse() {
    MapPermutation<E> inverse = new MapPermutation<E>(map.inverse());
    inverse.inverse = this;
    return inverse;
  }
}
