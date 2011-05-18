package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import algorithms.Partition;
import algorithms.UnorderedPair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

public class PermutationGroup<E> extends AbstractSet<Permutation<E>> {
  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Collection<Permutation<E>> generators) {
    return new PermutationGroup<E>(domain, generators);
  }

  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Permutation<E>... generators) {
    return generateGroup(domain, Arrays.asList(generators));
  }

  private static <E> UnorderedPair<E> image(Permutation<E> sigma,
      UnorderedPair<E> p) {
    return UnorderedPair.of(sigma.image(p.getFirst()),
        sigma.image(p.getSecond()));
  }

  private static <E> boolean unify(Map<E, Partition> part, UnorderedPair<E> p) {
    return part.get(p.getFirst()).combine(part.get(p.getSecond()));
  }

  private final ImmutableSet<E> domain;
  private final Permutation<E> id;
  private List<Set<Permutation<E>>> cosetTables;
  private List<Predicate<Permutation<E>>> constraints;

  private final Collection<Permutation<E>> groupMembers;

  private final ImmutableCollection<Permutation<E>> generators;

  private PermutationGroup(ImmutableSet<E> domain,
      List<Set<Permutation<E>>> cosetTables,
      List<Predicate<Permutation<E>>> constraints) {
    ImmutableList.Builder<Permutation<E>> builder = ImmutableList.builder();
    for (Set<Permutation<E>> table : cosetTables) {
      builder.addAll(table);
    }
    this.generators = builder.build();
    this.domain = domain;
    this.cosetTables = cosetTables;
    this.constraints = constraints;
    id = Permutations.identity(domain);
    groupMembers = constructGroupMembers();
  }

  private PermutationGroup(Set<E> domain, Collection<Permutation<E>> generators) {
    checkArgument(!domain.isEmpty());
    this.generators = ImmutableList.copyOf(generators);
    this.domain = ImmutableSet.copyOf(domain);
    for (Permutation<E> g : generators) {
      checkNotNull(g);
      checkArgument(g.domain().equals(domain),
          "Domain mismatch while creating permutation group with "
              + "domain %s, generator %s", domain, g);
    }

    id = Permutations.identity(domain);
    constraints = Lists.newArrayListWithCapacity(degree());
    for (final E e : domain) {
      constraints.add(new Predicate<Permutation<E>>() {
        @Override public boolean apply(Permutation<E> input) {
          return Permutations.stabilizes(input, e);
        }
      });
    }
    constructCosetTables(generators);
    groupMembers = constructGroupMembers();
  }

  @Override public boolean contains(Object o) {
    if (o instanceof Permutation) {
      return contains((Permutation<?>) o);
    }
    return false;
  }

  public int degree() {
    return domain.size();
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(Object o) {
    if (o instanceof PermutationGroup) {
      PermutationGroup h = (PermutationGroup) o;
      return size() == h.size() && isSubgroupOf(h);
    }
    return super.equals(o);
  }

  public Collection<Permutation<E>> generators() {
    return generators;
  }

  @Override public boolean isEmpty() {
    return false;
  }

  public boolean isSubgroupOf(PermutationGroup<E> g) {
    checkNotNull(g);
    return size() <= g.size() && g.containsAll(generators);
  }

  public boolean isTransitive() {
    E e = domain.iterator().next();
    return orbit(e).size() == degree();
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return groupMembers.iterator();
  }

  public Set<E> orbit(E e) {
    Set<E> orbit = Sets.newHashSet();
    Queue<E> queue = Lists.newLinkedList();
    queue.add(e);
    while (!queue.isEmpty()) {
      e = queue.poll();
      for (Permutation<E> g : generators) {
        E img = g.image(e);
        if (orbit.add(img)) {
          queue.add(img);
        }
      }
    }
    return Collections.unmodifiableSet(orbit);
  }

  public Collection<Set<E>> orbits() {
    Set<E> left = Sets.newLinkedHashSet(domain);
    List<Set<E>> orbits = Lists.newArrayList();
    while (!left.isEmpty()) {
      E e = left.iterator().next();
      Set<E> orbit = orbit(e);
      orbits.add(orbit);
      left.removeAll(orbits);
    }
    return orbits;
  }

  @Override public int size() {
    return groupMembers.size();
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<");
    Joiner.on(", ").appendTo(builder, generators);
    builder.append(">");
    return builder.toString();
  }

  private Map<E, Partition> blockifyOn(E a, E b) {
    Map<E, Partition> partition = Maps.newHashMapWithExpectedSize(degree());
    for (E e : domain) {
      partition.put(e, new Partition());
    }
    partition.get(a).combine(partition.get(b));
    Queue<UnorderedPair<E>> queue = Lists.newLinkedList();
    queue.offer(UnorderedPair.of(a, b));
    while (!queue.isEmpty()) {
      UnorderedPair<E> p = queue.poll();
      for (Permutation<E> g : generators) {
        UnorderedPair<E> p2 = image(g, p);
        if (unify(partition, p2)) {
          queue.offer(p2);
        }
      }
    }
    return partition;
  }

  private void constructCosetTables(Collection<Permutation<E>> generators) {
    cosetTables = Lists.newArrayList();
    for (int i = 0; i < constraints.size(); i++) {
      Set<Permutation<E>> table = Sets.newLinkedHashSet();
      table.add(id);
      cosetTables.add(table);
    }
    Set<Permutation<E>> todos = Sets.newLinkedHashSet(generators);
    while (!todos.isEmpty()) {
      Iterator<Permutation<E>> iterator = todos.iterator();
      Permutation<E> alpha = iterator.next();
      iterator.remove();
      todos.addAll(filter(alpha));
    }
    for (int i = 0; i < cosetTables.size(); i++) {
      cosetTables.set(i, ImmutableSet.copyOf(cosetTables.get(i)));
    }
    cosetTables = ImmutableList.copyOf(cosetTables);
  }

  private Collection<Permutation<E>> constructGroupMembers() {
    return Collections2.transform(Sets.cartesianProduct(cosetTables),
        new Function<List<Permutation<E>>, Permutation<E>>() {
          @Override public Permutation<E> apply(List<Permutation<E>> input) {
            return Permutations.compose(input);
          }
        });
  }

  private boolean contains(Permutation alpha) {
    for (int i = 0; i < constraints.size(); i++) {
      Predicate<Permutation<E>> constraint = constraints.get(i);
      Set<Permutation<E>> table = cosetTables.get(i);
      if (id.equals(alpha))
        return true;
      Permutation<E> found = null;
      for (Permutation<E> gamma : table) {
        @SuppressWarnings("unchecked")
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (constraint.apply(p)) {
          found = p;
          break;
        }
      }
      if (found == null) {
        return false;
      }
      alpha = found;
    }
    return id.equals(alpha);
  }

  private Set<Permutation<E>> filter(Permutation<E> alpha) {

    for (int i = 0; i < constraints.size(); i++) {
      if (id.equals(alpha)) {
        return ImmutableSet.of();
      }
      Predicate<Permutation<E>> constraint = constraints.get(i);
      Permutation<E> found = null;
      Set<Permutation<E>> table = cosetTables.get(i);
      for (Permutation<E> gamma : table) {
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (constraint.apply(p)) {
          found = p;
          break;
        }
      }
      if (found == null) {
        table.add(alpha);
        return newFilters(alpha, i);
      }
      alpha = found;
    }
    return ImmutableSet.of();
  }

  private Set<Permutation<E>> newFilters(Permutation<E> alpha, int index) {
    List<E> omega = domain.asList();
    Set<Permutation<E>> filters = Sets.newHashSet();
    for (int i = 0; i <= index; i++) {
      for (Permutation<E> p : cosetTables.get(i)) {
        filters.add(p.compose(alpha));
      }
    }
    for (int i = index + 1; i < omega.size(); i++) {
      for (Permutation<E> p : cosetTables.get(i)) {
        filters.add(alpha.compose(p));
      }
    }
    filters.remove(alpha);
    return filters;
  }
}
