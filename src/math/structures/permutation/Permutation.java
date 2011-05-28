package math.structures.permutation;

import com.google.common.base.Function;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A bijective function from {@code E} to {@code E}, such that at most finitely
 * many values are not mapped to themselves.
 * 
 * @author lowasser
 * 
 * @param <E>
 */
public interface Permutation<E> extends Function<E, E> {
  public static enum Parity {
    EVEN {
      @Override public Parity inverse() {
        return ODD;
      }
    },
    ODD {
      @Override public Parity inverse() {
        return EVEN;
      }
    };

    public abstract Parity inverse();
  }

  public Set<E> apply(Set<E> set);

  public Map<E, E> asMap();

  public Permutation<E> compose(List<Permutation<E>> taus);

  public Permutation<E> compose(Permutation<E> tau);

  /**
   * A {@code Set<E>} which is guaranteed to contain all elements that are moved
   * by this permutation.
   */
  public Set<E> domain();

  public Permutation<E> inverse();

  public boolean isIdentity();

  public int order();

  public Parity parity();

  public E preimage(E e);

  public boolean stabilizes(E e);

  public boolean stabilizes(Set<E> s);
}