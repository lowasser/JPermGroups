package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import math.algebra.permgroups.permutation.Permutation;

public class SylowDecomposition<E> {
  private PermutationGroup<E> sylowSubgroup;
  private List<Permutation<E>> cosetRepresentatives;
  private final int p;
  private final PermutationGroup<E> group;

  public static <E> SylowDecomposition<E>
      sylow(PermutationGroup<E> group, int p) {
    return new SylowDecomposition<E>(group, p);
  }

  private SylowDecomposition(PermutationGroup<E> group, int p) {
    this.sylowSubgroup = PermutationGroup.trivial(group.domain());
    this.cosetRepresentatives =
        Lists.newArrayListWithCapacity(factorOut(group.size(), p));
    this.p = p;
    this.group = group;
    for (Permutation<E> g : group.generators()) {
      pBuild(g);
    }
    checkState(sylowSubgroup.isSubgroupOf(group),
        "Sylow subgroup is not a subgroup");
    checkState(isPGroup(sylowSubgroup, p), "Sylow subgroup is not a p-group");
    checkState(
        group.size() / sylowSubgroup.size() == factorOut(group.size(), p),
        "Sylow subgroup is not a maximal p-subgroup");
    checkState(
        group.size() == cosetRepresentatives.size() * sylowSubgroup.size(),
        "Expected that # of coset representatives %s times Sylow subgroup size "
            + "%s would equal group size %s", cosetRepresentatives.size(),
        sylowSubgroup.size(), group.size());
  }

  private void pBuild(Permutation<E> alpha) {
    for (Permutation<E> gamma : cosetRepresentatives) {
      PermutationGroup<E> tmp =
          sylowSubgroup
            .extend(ImmutableList.of(gamma.inverse().compose(alpha)));
      if (isPGroup(tmp, p)) {
        sylowSubgroup = tmp;
        return;
      }
    }
    cosetRepresentatives.add(alpha);
    for (Permutation<E> g : group.generators()) {
      pBuild(g.compose(alpha));
    }
  }

  public PermutationGroup<E> getSylowSubgroup() {
    return sylowSubgroup;
  }

  public List<Permutation<E>> getCosetRepresentatives() {
    return cosetRepresentatives;
  }

  private static boolean isPGroup(PermutationGroup<?> g, int p) {
    return factorOut(g.size(), p) == 1;
  }

  private static int factorOut(int n, int p) {
    if (n % p == 0) {
      int n2 = factorOut(n, p * p);
      return (n2 % p == 0) ? n2 / p : n2;
    }
    return n;
  }
}
