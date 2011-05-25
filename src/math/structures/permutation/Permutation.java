package math.structures.permutation;

import com.google.common.base.Function;

import java.util.Map;
import java.util.Set;

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

  public Permutation<E> inverse();

  public Parity parity();

  public E preimage(E e);

  public boolean stabilizes(E e);

  public boolean stabilizes(Set<E> s);

  public Set<E> support();

  public boolean isIdentity();

  public Map<E, E> asMap();

  public int order();
}