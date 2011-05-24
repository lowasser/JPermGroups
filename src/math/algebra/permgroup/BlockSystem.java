package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import math.structures.Partition;
import math.structures.permutation.Permutation;

public class BlockSystem<E> extends ForwardingMap<E, Object> implements
    Equivalence<E> {
  private final ImmutableMap<E, Object> partition;
  private transient SetMultimap<Object, E> blocks;
  private transient int nBlocks = -1;

  public static <E> BlockSystem<E> minimalBlockSystem(PermutationGroup<E> g,
      Set<E> domain) {
    BlockSystem<E> current = new BlockSystem<E>(domain);
    for (E a : domain) {
      for (E b : domain) {
        BlockSystem<E> tmp = refine(g.generators(), current, a, b);
        if (!tmp.isTrivial())
          current = tmp;
      }
    }
    return current;
  }

  public Collection<Set<Object>> orbits(Collection<Permutation<E>> generators) {
    Set<Object> todo = Sets.newLinkedHashSet(blocks().keySet());
    ImmutableList.Builder<Set<Object>> orbits = ImmutableList.builder();
    while (true) {
      Set<Object> orbit = orbit(generators, todo.iterator().next());
      orbits.add(orbit);
      if (orbit.size() == todo.size())
        break;
      else
        todo.removeAll(orbit);
    }
    return orbits.build();
  }

  public Set<Object> orbit(Collection<Permutation<E>> generators, Object block) {
    Set<Object> orbit = Sets.newLinkedHashSet();
    orbiter(generators, orbit, block);
    return Collections.unmodifiableSet(orbit);
  }

  private void orbiter(Collection<Permutation<E>> generators,
      Set<Object> orbit, Object block) {
    if (!orbit.add(block))
      return;
    for (Permutation<E> sigma : generators) {
      orbiter(generators, orbit, image(sigma, block));
    }
  }

  public Object image(Permutation<E> sigma, Object block) {
    checkArgument(blocks().containsKey(block));
    E e = blocks().get(block).iterator().next();
    E eImage = sigma.apply(e);
    return partition.get(eImage);
  }

  private static <E> BlockSystem<E> refine(
      Collection<Permutation<E>> generators, BlockSystem<E> system, E a, E b) {
    if (system.equivalent(a, b) || system.nBlocks() <= 2) {
      return system;
    }
    Map<Object, Partition> fresh =
        Maps.newHashMapWithExpectedSize(system.nBlocks >= 0 ? system.nBlocks
            : 16);
    ImmutableMap.Builder<E, Partition> builder = ImmutableMap.builder();
    for (Entry<E, Object> entry : system.entrySet()) {
      Object part = entry.getValue();
      Partition p = fresh.get(part);
      if (p == null)
        fresh.put(part, p = new Partition());
      builder.put(entry.getKey(), p);
    }
    ImmutableMap<E, Partition> partition = builder.build();
    int nBlocks = refiner(generators, partition, a, b, fresh.size() - 1);
    return new BlockSystem<E>(partition, nBlocks);
  }

  private static <E> int refiner(Collection<Permutation<E>> generators,
      Map<E, Partition> partition, E x, E y, int nBlocks) {
    if (!partition.get(x).combine(partition.get(y))) {
      return nBlocks;
    }
    nBlocks--;
    for (Iterator<Permutation<E>> iterator = generators.iterator(); nBlocks > 1
        && iterator.hasNext();) {
      Permutation<E> sigma = iterator.next();
      E xImg = sigma.apply(x);
      E yImg = sigma.apply(y);
      nBlocks = refiner(generators, partition, xImg, yImg, nBlocks);
    }
    return nBlocks;
  }

  int nBlocks() {
    if (nBlocks >= 0) {
      return nBlocks;
    }
    return nBlocks = blocks().keySet().size();
  }

  BlockSystem(Set<E> domain) {
    ImmutableBiMap.Builder<E, Object> builder = ImmutableBiMap.builder();
    for (E e : domain) {
      builder.put(e, new Object());
    }
    ImmutableBiMap<E, Object> bimap = builder.build();
    this.partition = bimap;
    this.nBlocks = bimap.size();
    this.blocks = Multimaps.forMap(bimap.inverse());
  }

  private BlockSystem(Map<E, ?> partition, int nBlocks) {
    this.partition = ImmutableMap.copyOf(partition);
    this.nBlocks = nBlocks;
  }

  public SetMultimap<Object, E> blocks() {
    if (blocks == null) {
      return blocks =
          ImmutableSetMultimap.copyOf(Multimaps.invertFrom(
              Multimaps.forMap(partition), HashMultimap.<Object, E> create()));
    }
    return blocks;
  }

  public boolean isTrivial() {
    return nBlocks == 1 || nBlocks == partition.size();
  }

  @Override protected Map<E, Object> delegate() {
    return partition;
  }

  @Override public boolean equivalent(E a, E b) {
    checkArgument(containsKey(a));
    checkArgument(containsKey(b));
    return Objects.equal(get(a), get(b));
  }

  @Override public int hash(E e) {
    Object b = get(e);
    checkArgument(b != null);
    return b.hashCode();
  }
}
