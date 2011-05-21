package math.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import math.structures.FunctionMap;

public abstract class Permutation<E> {
  static <E> void checkDomains(Permutation<E> p, Permutation<E> q) {
    if (!p.domain().equals(q.domain())) {
      throw new DomainMismatchException(p, q);
    }
  }

  private transient Map<E, E> map = null;
  private transient Function<E, E> function = null;
  private transient Permutation<E> inverse = null;
  private transient int sign = 0;
  private transient Collection<List<E>> cycleDecomposition = null;

  public Function<E, E> asFunction() {
    return (function == null) ? function = createAsFunction() : function;
  }

  public Map<E, E> asMap() {
    return (map == null) ? map = createAsMap() : map;
  }

  public Permutation<E> compose(Permutation<E> perm) {
    checkNotNull(perm);
    return new MapPermutation<E>(new ComposedPermutation<E>(this, perm));
  }

  public Collection<List<E>> cycleDecomposition() {
    return (cycleDecomposition == null) ? cycleDecomposition =
        createCycleDecomposition() : cycleDecomposition;
  }

  Collection<List<E>> createCycleDecomposition() {
    Set<E> todo = Sets.newLinkedHashSet(domain());
    ImmutableList.Builder<List<E>> cycles = ImmutableList.builder();
    while (!todo.isEmpty()) {
      List<E> cycle = Lists.newArrayList();
      E start = todo.iterator().next();
      E e = start;
      do {
        cycle.add(e);
        e = image(e);
      } while (!Objects.equal(e, start));
      todo.removeAll(cycle);
      if (cycle.size() > 1) {
        cycles.add(ImmutableList.copyOf(cycle));
      }
    }
    return cycles.build();
  }

  public final int degree() {
    return domain().size();
  }

  public abstract Set<E> domain();

  @SuppressWarnings("unchecked") @Override public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Permutation) {
      Permutation p = (Permutation) obj;
      if (!domain().equals(p.domain()))
        return false;
      for (E e : domain())
        if (!Objects.equal(image(e), p.image(e)))
          return false;
      return true;
    }
    return false;
  }

  public Permutation<E> extend(Set<E> newDomain) {
    checkArgument(newDomain.containsAll(domain()));
    return (newDomain.size() == degree()) ? this : new ExtendedPermutation<E>(
        newDomain, this);
  }

  @Override public int hashCode() {
    return asMap().hashCode();
  }

  public abstract E image(E e);

  public Permutation<E> inverse() {
    return (inverse == null) ? inverse = createInverse() : inverse;
  }

  public abstract E preimage(E e);

  public int sign() {
    if (sign == 0) {
      int tmp = 1;
      for (List<E> cycle : cycleDecomposition()) {
        if (cycle.size() % 2 == 0) {
          tmp = -tmp;
        }
      }
      return sign = tmp;
    }
    return sign;
  }

  @Override public String toString() {
    return asMap().toString();
  }

  Function<E, E> createAsFunction() {
    return new Function<E, E>() {
      @Override public E apply(E input) {
        checkArgument(domain().contains(input));
        return image(input);
      }
    };
  }

  Map<E, E> createAsMap() {
    return new FunctionMap<E, E>(domain(), asFunction());
  }

  Permutation<E> createInverse() {
    return new InversePermutation<E>(this);
  }
}
