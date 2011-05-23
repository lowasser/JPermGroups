package math.algebra.permgroup.permutation;

import static math.structures.permutation.Permutations.compose;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import junit.framework.TestCase;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

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
  }

  public void testComposition(Permutation<Integer> p, Permutation<Integer> q) {
    Permutation<Integer> pq = compose(p, q);
    for (Integer i : Iterables.concat(p.support(), q.support())) {
      assertEquals(p.apply(q.apply(i)), pq.apply(i));
    }
  }

  public void testEquals() {
    assertEquals(p3, compose(p2, p1));
    assertFalse(p3.equals(compose(p1, p2)));
  }

  public void testEqualsIdentity() {
    Permutation<Integer> idP = Permutations.identity();
    assertFalse(idP.equals(Permutations.identity()));
    assertTrue(idP.equals(Permutations.identity()));
  }

  public void testInverse() {
    Permutation<Integer> idP = Permutations.identity();
    assertEquals(idP, compose(p1,p1.inverse()));
    assertEquals(idP, compose(p1.inverse(),p1));
    assertFalse(idP.equals(p1));
  }
}
