package math.algebra.permgroup;

import java.util.Collection;

import math.structures.permutation.Permutation;

public interface PermSubgroup<E> extends PermGroup<E> {
  public abstract Collection<LCoset<E>> asCosets();

  public abstract Collection<Permutation<E>> cosetRepresentatives();

  public abstract int index();

  public abstract PermGroup<E> superGroup();
}
