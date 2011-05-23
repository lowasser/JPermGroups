package math.structures.permutation;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;

import java.util.List;

final class Cycle<E> extends MapPermutation<E> {
  private final List<E> cycle;

  Cycle(List<E> cycle) {
    super(cycleMap(cycle = ImmutableList.copyOf(cycle)));
    this.cycle = cycle;
  }

  private static <E> ImmutableBiMap<E, E> cycleMap(List<E> cycle) {
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    builder.put(cycle.get(cycle.size() - 1), cycle.get(0));
    for (int i = 1; i < cycle.size(); i++)
      builder.put(cycle.get(i - 1), cycle.get(i));
    return builder.build();
  }

  @Override protected math.structures.permutation.Permutation.Parity
      computeParity() {
    return ((cycle.size() & 1) == 0) ? Parity.ODD : Parity.EVEN; // not a typo
  }
}
