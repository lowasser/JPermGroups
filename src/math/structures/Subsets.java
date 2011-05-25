package math.structures;

import static com.google.common.base.Preconditions.checkPositionIndex;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

public class Subsets<E> extends AbstractSet<Set<E>> {
  private final int k;
  private final ImmutableMap<E, Integer> domain;

  public static <E> Set<Set<E>> subsetsOfSizeAtMost(Set<E> set, int k) {
    checkPositionIndex(k, set.size());
    ImmutableMap.Builder<E, Integer> builder = ImmutableMap.builder();
    int i = 0;
    for (E e : set) {
      builder.put(e, i++);
    }
    return new Subsets<E>(builder.build(), k);
  }

  private Subsets(ImmutableMap<E, Integer> domain, int k) {
    this.k = k;
    this.domain = domain;
  }

  private class Subset extends AbstractSet<E> {
    private int[] indices;

    private Subset(int[] indices, int k) {
      checkPositionIndex(k, indices.length);
      this.indices = new int[k];
      System.arraycopy(indices, 0, this.indices, 0, k);
    }

    @Override public boolean contains(@Nullable Object o) {
      int i = getIndex(o);
      return i >= 0 && Arrays.binarySearch(indices, i) >= 0;
    }

    @Override public Iterator<E> iterator() {
      return new AbstractIterator<E>() {
        private int i = 0;

        @Override protected E computeNext() {
          if (i >= indices.length)
            return endOfData();
          return get(indices[i++]);
        }
      };
    }

    @Override public int size() {
      return indices.length;
    }
  }

  private class SubsetsIterator extends AbstractIterator<Set<E>> {
    private int kk = -1;
    private final int[] indices = new int[k + 1];

    private Set<E> current() {
      return new Subset(indices, kk);
    }

    @Override protected Set<E> computeNext() {
      System.err.println(kk);
      System.err.println(Arrays.toString(indices));
      if (kk < 0) {
        kk = 0;
        indices[0] = domain.size();
        return ImmutableSet.of();
      } 
      for (int i = kk - 1; i >= 0; i--) {
        if (indices[i] + 1 < indices[i + 1]) {
          indices[i]++;
          for (int j = i + 1; j < kk; j++) {
            indices[j] = indices[j - 1] + 1;
          }
          return current();
        }
      }
      kk++;
      if (kk > k) {
        return endOfData();
      }
      for (int i = 0; i < kk; i++) {
        indices[i] = i;
      }
      indices[kk] = domain.size();
      return current();
    }
  }

  private E get(int i) {
    return domain.entrySet().asList().get(i).getKey();
  }

  private int getIndex(@Nullable Object o) {
    Integer i = domain.get(o);
    return (i == null) ? -1 : i;
  }

  @Override public Iterator<Set<E>> iterator() {
    return new SubsetsIterator();
  }

  private transient int size = -1;

  @Override public int size() {
    if (size >= 0) {
      return size;
    }
    int n = domain.size();
    int ans = 1;
    int comb = 1;
    for (int i = 0; i < k; i++) {
      comb *= n - i;
      comb /= i + 1;
      ans += comb;
    }
    return this.size = ans;
  }

  @Override public boolean isEmpty() {
    return false;
  }

  @Override public boolean contains(@Nullable Object o) {
    if (o instanceof Set) {
      Set<?> s = (Set<?>) o;
      return s.size() <= k && domain.keySet().containsAll(s);
    }
    return false;
  }
}
