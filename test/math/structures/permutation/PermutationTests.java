package math.structures.permutation;

import static math.structures.permutation.Permutations.compose;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

public class PermutationTests extends TestCase {
  private final ImmutableSet<Integer> domainP = ImmutableSet.of(1, 2, 3, 4);
  private final ImmutableSet<Integer> domainQ = ImmutableSet.of(5, 6, 7);
  private final Permutation<Integer> p1 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 1, 3, 3, 4, 4));
  private final Permutation<Integer> p2 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 3, 3, 1, 4, 4));
  private final Permutation<Integer> p3 = Permutations.permutation(ImmutableMap
    .of(1, 3, 3, 1, 2, 2, 4, 4));
  private final Permutation<Integer> q1 = Permutations.permutation(ImmutableMap
    .of(5, 7, 7, 5, 6, 6));

  public void testComposition() {
    testComposition(p1, p2);
    testComposition(p2, p1);

    int n = 10;
    Random gen = new Random(0);
    for (int z = 0; z < 100; z++) {
      testComposition(randomPermutation(n, gen), randomPermutation(n, gen));
    }
  }

  public void testComposition(Permutation<Integer> p, Permutation<Integer> q) {
    Permutation<Integer> pq = compose(p, q);
    for (Integer i : Iterables.concat(p.domain(), q.domain())) {
      assertEquals(p.apply(q.apply(i)), pq.apply(i));
    }
  }

  private Permutation<Integer> randomPermutation(int n, Random gen) {
    int[] arr = new int[n];
    for (int i = 0; i < n; i++) {
      arr[i] = i;
    }
    for (int i = 0; i < n; i++) {
      int j = gen.nextInt(n);
      int tmp = arr[i];
      arr[i] = arr[j];
      arr[j] = tmp;
    }
    Map<Integer, Integer> map = Maps.newHashMapWithExpectedSize(n);
    for (int i = 0; i < n; i++)
      map.put(i, arr[i]);
    return Permutations.permutation(map);
  }

  public void testEquals() {
    assertEquals(p3, compose(p2, p1));
    assertFalse(p3.equals(compose(p1, p2)));
  }

  public void testInverse() {
    Permutation<Integer> idP = Permutations.identity();
    assertEquals(idP, compose(p1, p1.inverse()));
    assertEquals(idP, compose(p1.inverse(), p1));
    assertFalse(idP.equals(p1));
  }
}
