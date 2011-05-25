package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.Collection;

import math.structures.permutation.Permutation;

final class SubgroupView<E> extends ForwardingPermGroup<E> implements
    PermSubgroup<E> {
  private final Collection<Permutation<E>> cosetReps;
  private final PermGroup<E> subGroup;
  private final PermGroup<E> superGroup;

  SubgroupView(Collection<Permutation<E>> cosetReps, PermGroup<E> subGroup,
      PermGroup<E> superGroup) {
    this.cosetReps = cosetReps;
    this.subGroup = subGroup;
    this.superGroup = superGroup;
    assert subGroup.isSubgroupOf(superGroup);
    assert superGroup.containsAll(cosetReps);
  }

  @Override public Collection<Permutation<E>> cosetRepresentatives() {
    return cosetReps;
  }

  @Override public PermGroup<E> superGroup() {
    return superGroup;
  }

  @Override public int index() {
    return cosetReps.size();
  }

  @Override public Collection<LCoset<E>> asCosets() {
    return Collections2.transform(cosetRepresentatives(),
        new Function<Permutation<E>, LCoset<E>>() {
          @Override public LCoset<E> apply(Permutation<E> sigma) {
            return new LCoset<E>(sigma, SubgroupView.this);
          }
        });
  }

  @Override protected PermGroup<E> delegate() {
    return subGroup;
  }

}
