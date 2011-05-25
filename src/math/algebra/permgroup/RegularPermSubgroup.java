package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;

import math.structures.permutation.Permutation;

final class RegularPermSubgroup<E> extends RegularPermGroup<E> implements
    PermSubgroup<E> {
  private final PermGroup<E> superGroup;
  private final CosetTables<E> cosetRepTables;

  static <E> RegularPermSubgroup<E> subgroup(PermGroup<E> group,
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    CosetTables<E> tables =
        CosetTables.subgroupTables(group.generators(), filters);
    return new RegularPermSubgroup<E>(group, tables, filters.size());
  }

  private RegularPermSubgroup(PermGroup<E> superGroup,
      CosetTables<E> tables, int nFilters) {
    super(tables.drop(nFilters));
    this.cosetRepTables = tables.take(nFilters);
    this.superGroup = superGroup;
    assert check(tables, superGroup);
  }

  private boolean check(CosetTables<E> tables, PermGroup<E> group) {
    boolean good = tables.generated().size() == group.size();
    for (Permutation<E> sigma : group.generators()) {
      good &= tables.generates(sigma);
    }
    return good;
  }

  @Override public Collection<Permutation<E>> cosetRepresentatives() {
    return cosetRepTables.generated();
  }

  @Override public PermGroup<E> superGroup() {
    return superGroup;
  }

  @Override public int index() {
    return cosetRepresentatives().size();
  }

  @Override public Collection<LeftCoset<E>> asCosets() {
    return Collections2.transform(cosetRepresentatives(),
        new Function<Permutation<E>, LeftCoset<E>>() {
          @Override public LeftCoset<E> apply(Permutation<E> sigma) {
            return LeftCoset.coset(sigma, RegularPermSubgroup.this);
          }
        });
  }
}
