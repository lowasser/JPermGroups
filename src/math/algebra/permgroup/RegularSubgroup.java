package math.algebra.permgroup;

import com.google.common.base.Predicate;

import java.util.Collection;

import math.structures.permutation.Permutation;

final class RegularSubgroup<E> extends RegularPermutationGroup<E> implements
    Subgroup<E> {
  private final PermutationGroup<E> superGroup;
  private final CosetTables<E> cosetRepTables;

  static <E> RegularSubgroup<E> subgroup(PermutationGroup<E> group,
      Collection<? extends Predicate<? super Permutation<E>>> filters) {
    CosetTables<E> tables =
        CosetTables.subgroupTables(group.generators(), filters);
    return new RegularSubgroup<E>(group, tables, filters.size());
  }

  private RegularSubgroup(PermutationGroup<E> superGroup,
      CosetTables<E> tables, int nFilters) {
    super(tables.drop(nFilters));
    this.cosetRepTables = tables.take(nFilters);
    this.superGroup = superGroup;
    assert check(tables, superGroup);
  }

  private boolean check(CosetTables<E> tables, PermutationGroup<E> group) {
    boolean good = tables.generated().size() == group.size();
    for (Permutation<E> sigma : group.generators()) {
      good &= tables.generates(sigma);
    }
    return good;
  }

  @Override public Collection<Permutation<E>> cosetRepresentatives() {
    return cosetRepTables.generated();
  }

  @Override public PermutationGroup<E> superGroup() {
    return superGroup;
  }

  @Override public int index() {
    return cosetRepresentatives().size();
  }
}
