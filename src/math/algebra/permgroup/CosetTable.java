package math.algebra.permgroup;

import static math.structures.permutation.Permutations.compose;

import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

import math.structures.permutation.Permutation;

final class CosetTable<E> extends ForwardingSet<Permutation<E>> {
  final int index;
  private final Set<Permutation<E>> representatives;
  private final Predicate<Permutation<E>> filter;

  public static <E> CosetTable<E> stabilizingTable(int index, E stabilized) {
    return new CosetTable<E>(index, Sets.<Permutation<E>> newHashSet(),
        StabilizesPredicate.on(stabilized));
  }

  public static <E> CosetTable<E> immutable(CosetTable<E> table) {
    return new CosetTable<E>(table.index,
        ImmutableSet.copyOf(table.representatives), table.filter);
  }

  private CosetTable(int index, Set<Permutation<E>> representatives,
      Predicate<Permutation<E>> filter) {
    this.index = index;
    this.representatives = representatives;
    this.filter = filter;
  }

  @Override protected Set<Permutation<E>> delegate() {
    return representatives;
  }

  public Permutation<E> filter(Permutation<E> alpha) {
    for (Permutation<E> gamma : this) {
      Permutation<E> tmp = compose(gamma.inverse(), alpha);
      if (filter.apply(tmp)) {
        return tmp;
      }
    }
    return null;
  }
}
