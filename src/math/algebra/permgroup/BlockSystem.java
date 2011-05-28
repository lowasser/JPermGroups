package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Equivalence;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.structures.FunctionMap;
import math.structures.Partition;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

public class BlockSystem<E> extends ForwardingMap<E, Object> implements
    Equivalence<E> {

  public static <E> BlockSystem<E> minimalBlockSystem(PermGroup<E> g,
      Set<E> domain) {
    BlockSystem<E> current = new BlockSystem<E>(domain);
    E a = domain.iterator().next();
    for (E b : domain) {
      BlockSystem<E> tmp = refine(g.generators(), current, a, b);
      if (!tmp.isTrivial()) {
        current = tmp;
      }
    }
    return current;
  }

  public static <E> BlockSystem<E> minimalBlockSystem(PermGroup<E> g,
      Set<E> domain, int p) {
    BlockSystem<E> current = new BlockSystem<E>(domain);
    for (E a : domain) {
      for (E b : domain) {
        BlockSystem<E> tmp = refine(g.generators(), current, a, b);
        if (!tmp.isTrivial()) {
          current = tmp;
        }
        if (current.size() == p) {
          break;
        }
      }
    }
    return current;
  }

  public static <E> BlockSystem<Set<E>> minimalBlockSystemAction(
      PermGroup<E> g, Set<Set<E>> domain) {
    BlockSystem<Set<E>> current = new BlockSystem<Set<E>>(domain);
    Set<E> a = domain.iterator().next();
    for (Set<E> b : domain) {
      BlockSystem<Set<E>> tmp = refine(g.generators(), current, a, b);
      if (!tmp.isTrivial()) {
        current = tmp;
      }
    }
    return current;
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
      if (p == null) {
        fresh.put(part, p = new Partition());
      }
      builder.put(entry.getKey(), p);
    }
    ImmutableMap<E, Partition> partition = builder.build();
    int nBlocks = refiner(generators, partition, a, b, fresh.size() - 1);
    return new BlockSystem<E>(partition, nBlocks);
  }

  private static <E> BlockSystem<Set<E>> refine(
      Collection<Permutation<E>> generators, BlockSystem<Set<E>> system,
      Set<E> a, Set<E> b) {
    if (system.equivalent(a, b) || system.nBlocks() <= 2) {
      return system;
    }
    Map<Object, Partition> fresh =
        Maps.newHashMapWithExpectedSize(system.nBlocks >= 0 ? system.nBlocks
            : 16);
    ImmutableMap.Builder<Set<E>, Partition> builder = ImmutableMap.builder();
    for (Entry<Set<E>, Object> entry : system.entrySet()) {
      Object part = entry.getValue();
      Partition p = fresh.get(part);
      if (p == null) {
        fresh.put(part, p = new Partition());
      }
      builder.put(entry.getKey(), p);
    }
    ImmutableMap<Set<E>, Partition> partition = builder.build();
    int nBlocks = refiner(generators, partition, a, b, fresh.size() - 1);
    return new BlockSystem<Set<E>>(partition, nBlocks);
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

  private static <E> int refiner(Collection<Permutation<E>> generators,
      Map<Set<E>, Partition> partition, Set<E> x, Set<E> y, int nBlocks) {
    if (!partition.get(x).combine(partition.get(y))) {
      return nBlocks;
    }
    nBlocks--;
    for (Iterator<Permutation<E>> iterator = generators.iterator(); nBlocks > 1
        && iterator.hasNext();) {
      Permutation<E> sigma = iterator.next();
      Set<E> xImg = sigma.apply(x);
      Set<E> yImg = sigma.apply(y);
      nBlocks = refiner(generators, partition, xImg, yImg, nBlocks);
    }
    return nBlocks;
  }

  private final ImmutableMap<E, Object> partition;

  private transient SetMultimap<Object, E> blocks;

  private transient int nBlocks = -1;

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

  private BlockSystem(ImmutableMap<E, Object> partition,
      SetMultimap<Object, E> blocks) {
    this.partition = checkNotNull(partition);
    this.blocks = checkNotNull(blocks);
    this.nBlocks = blocks.size();
  }

  private BlockSystem(Map<E, ?> partition, int nBlocks) {
    this.partition = ImmutableMap.copyOf(partition);
    this.nBlocks = nBlocks;
  }

  public PermGroup<Object> blockAction(PermGroup<E> g) {
    List<Permutation<Object>> generators =
        Lists.newArrayListWithCapacity(g.generators().size());
    for (Permutation<E> sigma : g.generators()) {
      Map<Object, Object> map = Maps.newHashMapWithExpectedSize(nBlocks());
      for (Object block : blocks().keySet()) {
        map.put(block, image(sigma, block));
      }
      generators.add(Permutations.permutation(map));
    }
    return Groups.generateGroup(generators);
  }

  public SetMultimap<Object, E> blocks() {
    if (blocks == null) {
      return blocks =
          ImmutableSetMultimap.copyOf(Multimaps.invertFrom(
              Multimaps.forMap(partition), HashMultimap.<Object, E> create()));
    }
    return blocks;
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

  public Object image(Permutation<E> sigma, Object block) {
    Iterator<E> iterator = blocks().get(block).iterator();
    if (!iterator.hasNext()) {
      return block;
    }
    E e = iterator.next();
    E eImage = sigma.apply(e);
    return partition.get(eImage);
  }

  public boolean isTrivial() {
    return nBlocks == 1 || nBlocks == partition.size();
  }

  public BlockSystem<E> orbit(Collection<Permutation<E>> generators,
      Object block) {
    Set<Object> orbit = Sets.newLinkedHashSet();
    orbiter(generators, orbit, block);
    return subSystem(orbit);
  }

  public Collection<BlockSystem<E>>
      orbits(Collection<Permutation<E>> generators) {
    Set<Object> todo = Sets.newLinkedHashSet(blocks().keySet());
    ImmutableList.Builder<BlockSystem<E>> orbits = ImmutableList.builder();
    while (true) {
      BlockSystem<E> orbit = orbit(generators, todo.iterator().next());
      orbits.add(orbit);
      if (orbit.size() == todo.size()) {
        break;
      } else {
        todo.removeAll(orbit.blocks().keySet());
      }
    }
    return orbits.build();
  }

  public Collection<BlockSystem<E>> orbits(PermGroup<E> group) {
    return orbits(group.generators());
  }

  public PermSubgroup<E> stabilizingSubgroup(PermGroup<E> g) {
    Collection<Predicate<Permutation<E>>> filters = Lists.newArrayList();
    for (final Object block : blocks().keySet()) {
      filters.add(new Predicate<Permutation<E>>() {
        @Override public boolean apply(Permutation<E> sigma) {
          return Objects.equal(block, image(sigma, block));
        }
      });
    }
    return g.subgroup(filters);
  }

  @Override protected Map<E, Object> delegate() {
    return partition;
  }

  int nBlocks() {
    if (nBlocks >= 0) {
      return nBlocks;
    }
    return nBlocks = blocks().keySet().size();
  }

  private void orbiter(Collection<Permutation<E>> generators,
      Set<Object> orbit, Object block) {
    if (!orbit.add(block)) {
      return;
    }
    for (Permutation<E> sigma : generators) {
      orbiter(generators, orbit, image(sigma, block));
    }
  }

  private BlockSystem<E> subSystem(Set<Object> blocks) {
    if (this.size() == blocks.size()) {
      return this;
    }
    ImmutableSetMultimap.Builder<Object, E> subBlocksBuilder =
        ImmutableSetMultimap.<Object, E> builder();
    ImmutableMap.Builder<E, Object> subPartitionBuilder =
        ImmutableMap.builder();
    for (Object b : blocks) {
      Set<E> block = blocks().get(b);
      subBlocksBuilder.putAll(b, block);
      subPartitionBuilder.putAll(new FunctionMap<E, Object>(block, Functions
        .constant(b)));
    }
    return new BlockSystem<E>(subPartitionBuilder.build(),
        subBlocksBuilder.build());
  }
}
