package math.algebra.permgroup.permutation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import junit.framework.TestCase;
import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

public class PermutationTests extends TestCase {
  private final ImmutableSet<Integer> domainP = ImmutableSet.of(1, 2, 3, 4);
  private final ImmutableSet<Integer> domainQ = ImmutableSet.of(1, 2, 3);
  private final Permutation<Integer> p1 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 1, 3, 3, 4, 4));
  private final Permutation<Integer> p2 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 3, 3, 1, 4, 4));
  private final Permutation<Integer> p3 = Permutations.permutation(ImmutableMap
    .of(1, 3, 3, 1, 2, 2, 4, 4));

  public void testComposition() {
    testComposition(p1, p2);
    testComposition(p2, p1);
  }

  public void testEquals() {
    assertEquals(p3, p1.compose(p2));
    assertFalse(p3.equals(p2.compose(p1)));
  }

  public void testEqualsIdentity() {
    Permutation<Integer> idP = Permutations.identity(domainP);
    assertFalse(idP.equals(Permutations.identity(domainQ)));
    assertTrue(idP.equals(Permutations.identity(domainP)));
  }

  public void testInverse() {
    Permutation<Integer> idP = Permutations.identity(domainP);
    assertEquals(idP, p1.compose(p1.inverse()));
    assertEquals(idP, p1.inverse().compose(p1));
    assertEquals(idP, p1.compose(p1.inverse()));
    assertFalse(idP.equals(p1));
  }

  public void testComposition(Permutation<Integer> p, Permutation<Integer> q) {
    Permutation<Integer> pq = p.compose(q);
    for (Integer i : p.domain()) {
      assertEquals(q.image(p.image(i)), pq.image(i));
    }
    assertEquals(pq.domain(), p.domain());
  }
}
