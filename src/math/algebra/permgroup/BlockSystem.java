package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import algorithms.Pair;
import algorithms.Partition;

import com.google.common.base.Functions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;

final class BlockSystem<E> extends ForwardingMap<E, Object> {
  private final ImmutableMap<E, Partition> partition;
  private final PermutationGroup<E> group;
  private transient SetMultimap<Partition, E> blocks = null;
  private final int nBlocks;

  public static <E> BlockSystem<E>
      maximalBlockSystem(PermutationGroup<E> group) {
    return trivial(group).maximalSystem();
  }

  public static <E> BlockSystem<E> trivial(PermutationGroup<E> group) {
    ImmutableMap.Builder<E, Partition> builder = ImmutableMap.builder();
    ImmutableMap.Builder<Partition, E> reverse = ImmutableMap.builder();
    for (E e : group.domain()) {
      Partition p = new Partition();
      builder.put(e, p);
      reverse.put(p, e);
    }
    return new BlockSystem<E>(builder.build(),
        Multimaps.forMap(reverse.build()), group);
  }

  public boolean isTrivial() {
    int k = nBlocks();
    return k == 1 || k == size();
  }

  private BlockSystem(Map<E, Partition> partition, PermutationGroup<E> group,
      int nBlocks) {
    this.partition = ImmutableMap.copyOf(partition);
    this.group = group;
    this.nBlocks = nBlocks;
    assert isValid();
  }

  private BlockSystem(ImmutableMap<E, Partition> partition,
      SetMultimap<Partition, E> blocks, PermutationGroup<E> group) {
    this.partition = partition;
    this.group = group;
    this.blocks = blocks;
    this.nBlocks = blocks.size();
  }

  @SuppressWarnings("unchecked") @Override protected Map<E, Object> delegate() {
    return (Map) partition;
  }

  public SetMultimap<Partition, E> blocks() {
    if (blocks == null) {
      return blocks = Multimaps.unmodifiableSetMultimap(Multimaps.invertFrom(
          Multimaps.forMap(partition), HashMultimap.<Partition, E> create()));
    }
    return blocks;
  }

  public int nBlocks() {
    return nBlocks;
  }

  public BlockSystem<E> maximalSystem() {
    Iterator<E> iter = domain().iterator();
    E a = iter.next();
    BlockSystem<E> current = this;
    while (iter.hasNext()) {
      E b = iter.next();
      BlockSystem<E> candidate = current.extend(a, b);
      if (!candidate.isTrivial()) {
        current = candidate;
      }
    }
    return current;
  }

  private BlockSystem<E> extend(E a, E b) {
    return extend(group.generators(), a, b);
  }

  private BlockSystem<E> extend(Collection<Permutation<E>> perms, E a, E b) {
    if (get(a).equals(get(b))) {
      return this;
    }
    Map<E, Partition> part = freshPartitionCopy();
    union(part, a, b);
    int nB = nBlocks - 1;
    Queue<Pair<E, E>> queue = Lists.newLinkedList();
    queue.offer(Pair.of(a, b));
    while (!queue.isEmpty()) {
      Pair<E, E> p = queue.poll();
      for (Permutation<E> sigma : perms) {
        Pair<E, E> p2 = image(sigma, p);
        if (union(part, p2)) {
          nB--;
          queue.offer(p2);
        }
      }
    }
    return new BlockSystem<E>(part, group, nB);
  }

  private Map<E, Partition> freshPartitionCopy() {
    Map<Object, Partition> copy = Maps.newHashMap();
    for (Partition p : blocks().keySet()) {
      copy.put(p, new Partition());
    }
    return Maps.newHashMap(Maps.transformValues(this, Functions.forMap(copy)));
  }

  private static <E> boolean union(Map<E, Partition> part, Pair<E, E> pair) {
    return union(part, pair.getFirst(), pair.getSecond());
  }

  private static <E> boolean union(Map<E, Partition> part, E a, E b) {
    checkArgument(part.containsKey(a));
    checkArgument(part.containsKey(b));
    return part.get(a).combine(part.get(b));
  }

  private static <E> Pair<E, E> image(Permutation<E> sigma, Pair<E, E> p) {
    return Pair.of(sigma.image(p.getFirst()), sigma.image(p.getSecond()));
  }

  public Set<E> domain() {
    return group.domain();
  }

  private boolean isValid() {
    for (Permutation<E> sigma : group.generators()) {
      Map<Partition, Partition> induced = Maps
        .newHashMapWithExpectedSize(domain().size());
      for (E e : domain()) {
        Partition p = partition.get(e);
        Partition p2 = partition.get(sigma.image(e));

        Partition pPrime = induced.put(p, p2);
        if (pPrime != null && !pPrime.equals(p2)) {
          return false;
        }
      }
    }
    return true;
  }
}
