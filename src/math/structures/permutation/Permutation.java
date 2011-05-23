package math.structures.permutation;

import com.google.common.base.Function;

import java.util.Set;

public interface Permutation<E> extends Function<E, E> {
  public Set<E> support();

  public Permutation<E> inverse();

  public E preimage(E e);
}
