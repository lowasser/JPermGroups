package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;
import algorithms.CartesianProduct;
import algorithms.Pair;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;

public final class Groups {
  public static <E> PermutationGroup<E> trivial(Set<E> domain) {
    return new PermutationGroup<E>(domain);
  }

  private Groups() {
  }

  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Collection<Permutation<E>> generators) {
    return new PermutationGroup<E>(domain, generators);
  }

  public static <E> PermutationGroup<E> generateGroup(Set<E> domain,
      Permutation<E>... generators) {
    return generateGroup(domain, Arrays.asList(generators));
  }

  public static <E> PermutationGroup<E> intersection(
      PermutationGroup<E>... groups) {
    return intersection(Arrays.asList(groups));
  }

  public static <E> PermutationGroup<E> intersection(
      Collection<PermutationGroup<E>> groups) {
    checkArgument(!groups.isEmpty());
    Ordering<PermutationGroup<E>> sizeOrdering =
        new Ordering<PermutationGroup<E>>() {
          @Override public int compare(PermutationGroup<E> left,
              PermutationGroup<E> right) {
            return left.size() - right.size();
          }
        };
    List<PermutationGroup<E>> theGroups =
        sizeOrdering.reverse().sortedCopy(groups);
    PermutationGroup<E> g = theGroups.get(theGroups.size() - 1);
    List<Predicate<Permutation<E>>> filters =
        Lists.transform(theGroups.subList(0, theGroups.size() - 1),
            new Function<PermutationGroup<E>, Predicate<Permutation<E>>>() {
              @Override public Predicate<Permutation<E>> apply(
                  PermutationGroup<E> input) {
                return Predicates.in(input);
              }
            });
    return g.subgroup(filters);
  }

  public static <A, B> PermutationGroup<Pair<A, B>> directProduct(
      PermutationGroup<A> g, PermutationGroup<B> h) {
    ImmutableSet<A> gDomain = g.domain();
    ImmutableSet<B> hDomain = h.domain();
    CosetTables<Pair<A, B>> cosetTables =
        CosetTables.directProduct(gDomain, g.getCosetTables(), hDomain,
            h.getCosetTables());
    List<Permutation<Pair<A, B>>> generators = Lists.newArrayList();
    generators.addAll(Collections2.transform(g.generators(),
        Project1st.projectUp(gDomain, hDomain)));
    generators.addAll(Collections2.transform(h.generators(),
        Project2nd.projectUp(gDomain, hDomain)));
    return new PermutationGroup<Pair<A, B>>(CartesianProduct.of(gDomain,
        hDomain), generators, cosetTables);
  }
}
