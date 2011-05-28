package math.algebra.permgroup;

import static math.structures.permutation.Permutations.identity;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutation.Parity;

public final class Groups {
  private static final PermGroup<Object> TRIVIAL_GROUP =
      new AbstractPermGroup<Object>() {
        @Override public Collection<Permutation<Object>> generators() {
          return ImmutableList.of();
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

  public static <E> PermGroup<E> alternating(Set<E> domain) {
    if (domain.size() <= 2) {
      return trivial();
    }
    return symmetric(domain).subgroup(EVEN_PREDICATE);
  }

  public static <E> PermGroup<E> generateGroup(
      Collection<Permutation<E>> generators) {
    return new RegularPermGroup<E>(generators);
  }

  public static <E> PermGroup<E> generateGroup(Permutation<E>... generators) {
    return generateGroup(Arrays.asList(generators));
  }

  public static <E> PermGroup<E> symmetric(Set<E> domain) {
    if (domain.size() <= 1) {
      return trivial();
    }
    return new SymmetricGroup<E>(domain);
  }

  @SuppressWarnings("unchecked") public static <E> PermGroup<E> trivial() {
    return (PermGroup<E>) TRIVIAL_GROUP;
  }

  private Groups() {
  }
}
