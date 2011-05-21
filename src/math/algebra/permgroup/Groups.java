package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.permutation.Permutation;
import math.permutation.Permutations;
import math.structures.CartesianProduct;
import math.structures.Pair;

public final class Groups {
  private static final Predicate<Permutation> EVEN_PREDICATE =
      new Predicate<Permutation>() {
        @Override public boolean apply(Permutation sigma) {
          return sigma.sign() == 1;
        }
      };

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

  public static <E> PermutationGroup<E> restrict(PermutationGroup<E> group,
      Set<E> b) {
    assert group.domain().containsAll(b);
    for (Permutation<E> g : group.generators()) {
      assert Permutations.stabilizes(g, b);
    }
    List<Permutation<E>> generators =
        Lists.newArrayListWithCapacity(group.generators().size());
    for (Permutation<E> g : group.generators()) {
      generators.add(Permutations.restrict(g, b));
    }
    return generateGroup(b, generators);
  }

  public static <E> PermutationGroup<E> symmetric(Set<E> domain) {
    return symmetric(ImmutableSet.copyOf(domain));
  }

  private static <E> PermutationGroup<E> symmetric(ImmutableSet<E> domain) {
    if (domain.size() <= 1) {
      return trivial(domain);
    }
    ImmutableList<E> domainList = domain.asList();
    Permutation<E> sigma =
        Permutations
          .transposition(domain, domainList.get(0), domainList.get(1));
    Permutation<E> tau =
        Permutations.cyclePermutation(domain, ImmutableList.of(domainList));
    return generateGroup(domain, ImmutableList.of(sigma, tau));
  }

  public static <A, B> PermutationGroup<A> kernel(PermutationGroup<A> g,
      PermutationGroup<B> h, Function<Permutation<A>, Permutation<B>> phi) {
    return g
      .subgroup(Predicates.compose(Predicates.equalTo(h.identity()), phi));
  }

  @SuppressWarnings("unchecked") public static <E> PermutationGroup<E>
      alternating(Set<E> domain) {
    return symmetric(domain).subgroup((Predicate) EVEN_PREDICATE);
  }

  public static PermutationGroup<Integer> dihedral(int n) {
    List<Integer> cycle = Lists.newArrayListWithCapacity(n);
    for (int i = 0; i < n; i++)
      cycle.add(i);
    Set<Integer> domain = ImmutableSet.copyOf(cycle);
    Map<Integer, Integer> flip = Maps.newHashMapWithExpectedSize(n);
    for (int i = 0; i < n; i++) {
      flip.put(i, n - 1 - i);
    }
    return generateGroup(domain, ImmutableList.of(
        Permutations.cyclePermutation(domain, ImmutableList.of(cycle)),
        Permutations.permutation(flip)));
  }
}
