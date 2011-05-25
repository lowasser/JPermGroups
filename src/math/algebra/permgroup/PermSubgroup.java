package math.algebra.permgroup;

import java.util.Collection;

import math.structures.permutation.Permutation;

public interface PermSubgroup<E> extends PermGroup<E> {
  public abstract Collection<Permutation<E>> cosetRepresentatives();

  public abstract PermGroup<E> superGroup();

  public abstract int index();

  public abstract Collection<LeftCoset<E>> asCosets();
}
