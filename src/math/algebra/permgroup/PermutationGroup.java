package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

public class PermutationGroup<E> extends AbstractSet<Permutation<E>> {
  public static <E> PermutationGroup<E> directProduct(
      Collection<PermutationGroup<E>> groups) {
    Set<E> domain = Sets.newLinkedHashSet();
    List<Permutation<E>> generators = Lists.newArrayList();
    for (PermutationGroup<E> g : groups) {
      checkArgument(Collections.disjoint(g.domain(), domain));
      domain.addAll(g.domain());
      generators.addAll(g.generators());
    }
    domain = ImmutableSet.copyOf(domain);
    generators = Lists.transform(generators, DomainExtension.forDomain(domain));
    CosetTables<E> tables2 = new CosetTables<E>();
    for (PermutationGroup<E> g : groups) {
      tables2.addAll(g.cosetTables.extend(domain));
    }
    return new PermutationGroup<E>(domain, generators,
        CosetTables.immutable(tables2));
  }

  public static <E> PermutationGroup<E> directProduct(
      PermutationGroup<E>... groups) {
    return directProduct(Arrays.asList(groups));
  }

  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Collection<Permutation<E>> generators) {
    return new PermutationGroup<E>(domain, generators);
  }

  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Permutation<E>... generators) {
    return generateGroup(domain, Arrays.asList(generators));
  }

  private static <E> List<Predicate<Permutation<E>>>
      basicFilters(Set<E> domain) {
    ImmutableList.Builder<Predicate<Permutation<E>>> builder =
        ImmutableList.builder();
    for (final E e : domain) {
      builder.add(new Predicate<Permutation<E>>() {
        @Override public boolean apply(Permutation<E> input) {
          return Permutations.stabilizes(input, e);
        }
      });
    }
    return builder.build();
  }

  private final Permutation<E> id;
  private final CosetTables<E> cosetTables;

  private final ImmutableSet<E> domain;

  private final Collection<Permutation<E>> generators;

  private PermutationGroup(ImmutableSet<E> domain, CosetTables<E> cosetTables) {
    this.domain = ImmutableSet.copyOf(domain);
    this.id = Permutations.identity(domain);
    this.cosetTables = cosetTables;
    this.generators =
        ImmutableSet.copyOf(Iterables.concat(cosetTables)).asList();
  }

  private PermutationGroup(Set<E> domain, Collection<Permutation<E>> generators) {
    this(domain, generators, CosetTables.build(domain, generators,
        basicFilters(domain)));
  }

  private PermutationGroup(Set<E> domain,
      Collection<Permutation<E>> generators, CosetTables<E> cosetTables) {
    this.domain = ImmutableSet.copyOf(domain);
    this.generators = ImmutableList.copyOf(generators);
    this.id = Permutations.identity(domain);
    this.cosetTables = cosetTables;
  }

  @Override public boolean contains(@Nullable Object o) {
    if (o instanceof Permutation) {
      @SuppressWarnings("unchecked")
      Permutation<E> p = (Permutation) o;
      return cosetTables.generates(p);
    }
    return false;
  }

  public int degree() {
    return domain.size();
  }

  public ImmutableSet<E> domain() {
    return domain;
  }

  @Override public boolean equals(Object o) {
    if (o instanceof PermutationGroup) {
      @SuppressWarnings("unchecked")
      PermutationGroup<E> g = (PermutationGroup) o;
      return size() == g.size() && g.containsAll(generators);
    }
    return super.equals(o);
  }

  public Collection<Permutation<E>> generators() {
    return generators;
  }

  public PermutationGroup<E> extend(Set<E> newDomain) {
    return extend(ImmutableSet.copyOf(newDomain));
  }

  private PermutationGroup<E> extend(ImmutableSet<E> newDomain) {
    checkArgument(newDomain.containsAll(domain));
    if (newDomain.size() == domain.size()) {
      return this;
    }
    Collection<Permutation<E>> generators2 =
        Collections2
          .transform(generators, DomainExtension.forDomain(newDomain));
    CosetTables<E> cosetTables2 = cosetTables.extend(newDomain);
    return new PermutationGroup<E>(newDomain, generators2, cosetTables2);
  }

  public Permutation<E> identity() {
    return id;
  }

  @Override public boolean isEmpty() {
    return false;
  }

  public boolean isSubgroupOf(PermutationGroup<E> g) {
    return size() <= g.size() && g.containsAll(generators);
  }

  @Override public Iterator<Permutation<E>> iterator() {
    return cosetTables.generatedPermutations().iterator();
  }

  @Override public int size() {
    return cosetTables.generatedPermutations().size();
  }

  public PermutationGroup<E> subgroup(Predicate<Permutation<E>> filter) {
    List<Predicate<Permutation<E>>> filters2 =
        Lists.newArrayListWithCapacity(cosetTables.size() + 1);
    filters2.add(filter);
    filters2.addAll(cosetTables.filters);
    CosetTables<E> cosetTables2 =
        CosetTables.build(domain, generators, filters2);
    return new PermutationGroup<E>(domain, cosetTables2.subList(1,
        cosetTables2.size()));
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder(generators.size() * 10);
    builder.append('<');
    Joiner.on(", ").appendTo(builder, generators);
    builder.append('>');
    return builder.toString();
  }
}
