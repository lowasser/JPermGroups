package math.algebra.permgroup;

import java.util.Collection;

import math.structures.permutation.Permutation;

public interface Subgroup<E> extends PermutationGroup<E> {
  public abstract Collection<Permutation<E>> cosetRepresentatives();

  public abstract PermutationGroup<E> superGroup();

  public abstract int index();
}
