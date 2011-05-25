package math.structures;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public final class Colorings {
  private Colorings() {
  }

  public static final Equivalence<Object> NON_COLORING =
      new Equivalence<Object>() {
        @Override public boolean equivalent(@Nullable Object a,
            @Nullable Object b) {
          return true;
        }

        @Override public int hash(Object o) {
          return 0;
        }
      };

  public static <A, B> Function<A, B> caching(Function<A, B> f) {
    final Map<A, B> cache =
        new MapMaker().maximumSize(100).weakKeys().makeComputingMap(f);
    return new Function<A, B>() {
      @Override public B apply(A input) {
        return cache.get(input);
      }
    };
  }

  public static <E> Collection<Set<E>> colors(Set<E> domain,
      Equivalence<? super E> coloring) {
    List<E> todo = Lists.newLinkedList(domain);
    ImmutableList.Builder<Set<E>> colors = ImmutableList.builder();
    while (!todo.isEmpty()) {
      ImmutableSet.Builder<E> color = ImmutableSet.builder();
      Iterator<E> iter = todo.iterator();
      E a = iter.next();
      color.add(a);
      while (iter.hasNext()) {
        E b = iter.next();
        if (coloring.equivalent(a, b)) {
          iter.remove();
          color.add(b);
        }
      }
      colors.add(color.build());
    }
    return colors.build();
  }

  public static <E> Equivalence<E> coloring(final Function<E, ?> colorFunc) {
    return new Equivalence<E>() {
      @Override public boolean equivalent(E a, E b) {
        return Objects.equal(colorFunc.apply(a), colorFunc.apply(b));
      }

      @Override public int hash(E e) {
        return Objects.hashCode(colorFunc.apply(e));
      }
    };
  }
}
