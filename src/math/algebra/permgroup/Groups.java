package math.algebra.permgroup;

import static math.structures.permutation.Permutations.cycle;
import static math.structures.permutation.Permutations.identity;
import static math.structures.permutation.Permutations.transposition;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutation.Parity;
import math.structures.permutation.Permutations;

public final class Groups {
  private static final PermutationGroup<Object> TRIVIAL_GROUP =
      new AbstractPermutationGroup<Object>() {
        @Override public Collection<Permutation<Object>> generators() {
          return ImmutableList.of();
        }

        @Override public Set<Object> support() {
          return ImmutableSet.of();
        }

        @Override public Iterator<Permutation<Object>> iterator() {
          return Iterators.singletonIterator(identity());
        }

        @Override public int size() {
          return 1;
        }
      };

  private static final Predicate<Permutation> EVEN_PREDICATE =
      new Predicate<Permutation>() {
        @Override public boolean apply(Permutation sigma) {
          return sigma.parity() == Parity.EVEN;
        }
      };

  public static <E> PermutationGroup<E> alternating(Set<E> domain) {
    if (domain.size() <= 2)
      return trivial();
    return symmetric(domain).subgroup(EVEN_PREDICATE);
  }

  public static <E> PermutationGroup<E> generateGroup(
      Collection<Permutation<E>> generators) {
    return new RegularPermutationGroup<E>(generators);
  }

  public static <E> PermutationGroup<E> generateGroup(
      Permutation<E>... generators) {
    return generateGroup(Arrays.asList(generators));
  }

  public static <E> PermutationGroup<E> restrict(PermutationGroup<E> group,
      Set<E> b) {
    for (Permutation<E> g : group.generators()) {
      assert g.stabilizes(b);
    }
    List<Permutation<E>> generators =
        Lists.newArrayListWithCapacity(group.generators().size());
    for (Permutation<E> g : group.generators()) {
      generators.add(Permutations.restrict(g, b));
    }
    return generateGroup(generators);
  }

  @SuppressWarnings("unchecked") public static <E> PermutationGroup<E>
      trivial() {
    return (PermutationGroup<E>) TRIVIAL_GROUP;
  }

  public static <E> PermutationGroup<E> symmetric(Set<E> domain) {
    if (domain.size() <= 1)
      return trivial();
    return new SymmetricGroup<E>(domain);
  }

  private static <E> PermutationGroup<E> symmetric(ImmutableSet<E> domain) {
    if (domain.size() <= 1) {
      return trivial();
    }
    ImmutableList<E> domainList = domain.asList();
    Permutation<E> sigma = transposition(domainList.get(0), domainList.get(1));
    Permutation<E> tau = cycle(domainList);
    return generateGroup(ImmutableList.of(sigma, tau));
  }

  private Groups() {
  }
}
