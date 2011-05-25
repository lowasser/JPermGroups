package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

public class GroupAction<E> extends AbstractPermGroup<Set<E>> {
  private final PermGroup<E> g;
  private final int k;
  private transient Collection<Permutation<Set<E>>> generators;

  private transient final Function<Permutation<E>, Permutation<Set<E>>> inducer =
      new Function<Permutation<E>, Permutation<Set<E>>>() {
        @Override public Permutation<Set<E>> apply(Permutation<E> sigma) {
          return Permutations.actionOnSetsOfSize(sigma, k);
        }
      };

  @Override public Collection<Permutation<Set<E>>> generators() {
    return (generators == null) ? generators =
        Collections2.transform(g.generators(), inducer) : generators;
  }

  @Override public Iterator<Permutation<Set<E>>> iterator() {
    return Iterators.transform(g.iterator(), inducer);
  }

  @Override public int size() {
    return g.size();
  }

  @Override public PermSubgroup<Set<E>> subgroup(
      Collection<? extends Predicate<? super Permutation<Set<E>>>> filters) {
    Collection<Predicate<Permutation<E>>> inducedFilters =
        Lists.newArrayListWithCapacity(filters.size());
    for (final Predicate<? super Permutation<Set<E>>> filter : filters) {
      inducedFilters.add(new Predicate<Permutation<E>>() {
        @Override public boolean apply(Permutation<E> sigma) {
          return filter.apply(Permutations.actionOnSetsOfSize(sigma, k));
        }
      });
    }
    PermSubgroup<E> h = g.subgroup(inducedFilters);
    final GroupAction<E> hAction = new GroupAction<E>(h, k);
    Collection<Permutation<E>> hReps = h.cosetRepresentatives();
    return new SubgroupView<Set<E>>(Collections2.transform(hReps, inducer),
        hAction, this);
  }

  GroupAction(PermGroup<E> g, int k) {
    this.g = g;
    this.k = k;
  }
}
