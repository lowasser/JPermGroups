package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
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

  private final ImmutableSet<E> domain;
  private final Permutation<E> id;
  private final ImmutableSetMultimap<E, Permutation<E>> cosetTables;
  private final Collection<Permutation<E>> groupMembers;
  private final ImmutableCollection<Permutation<E>> generators;

  private PermutationGroup(ImmutableSet<E> domain,
      ImmutableSetMultimap<E, Permutation<E>> cosetTables) {
    this.generators = cosetTables.values();
    this.domain = domain;
    this.cosetTables = cosetTables;
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
    cosetTables = constructCosetTables(generators);
    groupMembers = constructGroupMembers();
  }

  public Collection<Permutation<E>> generators() {
    return generators;
  }

  @Override public boolean isEmpty() {
    return false;
  }

  @Override public boolean contains(Object o) {
    if (o instanceof Permutation) {
      return contains((Permutation<?>) o);
    }
    return false;
  }

  private boolean contains(Permutation alpha) {
    for (Map.Entry<E, Collection<Permutation<E>>> entry : cosetTables.asMap()
      .entrySet()) {
      if (id.equals(alpha))
        return true;
      E e = entry.getKey();
      Permutation<E> found = null;
      for (Permutation<E> gamma : entry.getValue()) {
        @SuppressWarnings("unchecked")
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (Permutations.stabilizes(p, e)) {
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

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<");
    Joiner.on(", ").appendTo(builder, generators);
    builder.append(">");
    return builder.toString();
  }

  private Collection<Permutation<E>> constructGroupMembers() {
    @SuppressWarnings("unchecked")
    List<Set<Permutation<E>>> tables = (List) cosetTables.asMap().values()
      .asList();

    return Collections2.transform(Sets.cartesianProduct(tables),
        new Function<List<Permutation<E>>, Permutation<E>>() {
          @Override public Permutation<E> apply(List<Permutation<E>> input) {
            return Permutations.compose(input);
          }
        });
  }

  private ImmutableSetMultimap<E, Permutation<E>> constructCosetTables(
      Collection<Permutation<E>> generators) {
    SetMultimap<E, Permutation<E>> cTables = HashMultimap.create(degree(),
        10);
    for (E e : domain) {
      cTables.put(e, id);
    }
    Set<Permutation<E>> todos = Sets.newLinkedHashSet(generators);
    while (!todos.isEmpty()) {
      Iterator<Permutation<E>> iterator = todos.iterator();
      Permutation<E> alpha = iterator.next();
      iterator.remove();
      todos.addAll(filter(cTables, alpha));
    }
    return ImmutableSetMultimap.copyOf(cTables);
  }

  private Set<Permutation<E>> filter(SetMultimap<E, Permutation<E>> cTables,
      Permutation<E> alpha) {
    List<E> omega = domain.asList();

    for (int i = 0; i < omega.size(); i++) {
      if (id.equals(alpha)) {
        return ImmutableSet.of();
      }
      E e = omega.get(i);
      Permutation<E> found = null;
      Set<Permutation<E>> table = cTables.get(e);
      for (Permutation<E> gamma : table) {
        Permutation<E> p = gamma.inverse().compose(alpha);
        if (Permutations.stabilizes(p, e)) {
          found = p;
          break;
        }
      }
      if (found == null) {
        cTables.put(e, alpha);
        return newFilters(cTables, alpha, i);
      }
      alpha = found;
    }
    return ImmutableSet.of();
  }

  private Set<Permutation<E>> newFilters(
      SetMultimap<E, Permutation<E>> cTables, Permutation<E> alpha, int index) {
    List<E> omega = domain.asList();
    Set<Permutation<E>> filters = Sets.newHashSet();
    for (E e : omega.subList(0, index + 1)) {
      for (Permutation<E> p : cTables.get(e)) {
        filters.add(p.compose(alpha));
      }
    }
    for (E e : omega.subList(index + 1, omega.size())) {
      for (Permutation<E> p : cTables.get(e)) {
        filters.add(alpha.compose(p));
      }
    }
    filters.remove(alpha);
    return filters;
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return groupMembers.iterator();
  }

  @Override public int size() {
    return groupMembers.size();
  }

  public boolean isSubgroupOf(PermutationGroup<E> g) {
    checkNotNull(g);
    return size() <= g.size() && g.containsAll(generators);
  }

  @SuppressWarnings("unchecked") @Override public boolean equals(Object o) {
    if (o instanceof PermutationGroup) {
      PermutationGroup h = (PermutationGroup) o;
      return size() == h.size() && isSubgroupOf(h);
    }
    return super.equals(o);
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

  public boolean isTransitive() {
    E e = domain.iterator().next();
    return orbit(e).size() == degree();
  }

  public int degree() {
    return domain.size();
  }
}
