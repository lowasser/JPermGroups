package math.algebra.permgroup;

import static math.structures.permutation.Permutations.compose;

import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

final class CosetTable<E> extends ForwardingSet<Permutation<E>> {
  public static <E> CosetTable<E> immutable(CosetTable<E> table) {
    return new CosetTable<E>(table.index,
        ImmutableSet.copyOf(table.representatives), table.filter);
  }

  public static <E> CosetTable<E> mutableCopy(CosetTable<E> table) {
    return new CosetTable<E>(table.index, Sets.newHashSet(table), table.filter);
  }

  public static <E> CosetTable<E> stabilizingTable(int index, E stabilized) {
    return table(index, StabilizesPredicate.on(stabilized));
  }

  public static <E> CosetTable<E> table(int index,
      Predicate<? super Permutation<E>> filter) {
    Set<Permutation<E>> table = Sets.newHashSet();
    table.add(Permutations.<E> identity());
    return new CosetTable<E>(index, table, filter);
  }

  final int index;

  private final Set<Permutation<E>> representatives;

  private final Predicate<? super Permutation<E>> filter;

  private CosetTable(int index, Set<Permutation<E>> representatives,
      Predicate<? super Permutation<E>> filter) {
    this.index = index;
    this.representatives = representatives;
    this.filter = filter;
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

  public Predicate<? super Permutation<E>> getFilter() {
    return filter;
  }

  @Override protected Set<Permutation<E>> delegate() {
    return representatives;
  }
}
