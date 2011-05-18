package math.algebra.permgroup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import junit.framework.TestCase;
import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

public class PermutationGroupTests extends TestCase {
  private final ImmutableSet<Integer> domainP = ImmutableSet.of(1, 2, 3, 4);
  private final ImmutableSet<Integer> domainQ = ImmutableSet.of(1, 2, 3);
  private final Permutation<Integer> p1 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 1, 3, 3, 4, 4));
  private final Permutation<Integer> p2 = Permutations.permutation(ImmutableMap
    .of(1, 2, 2, 3, 3, 1, 4, 4));
  private final Permutation<Integer> p3 = Permutations.permutation(ImmutableMap
    .of(1, 3, 3, 1, 2, 2, 4, 4));

  public void testCyclicGroup1() {
    PermutationGroup<Integer> group1 = new PermutationGroup<Integer>(domainP,
        p1);
    assertEquals(2, group1.size());
    assertTrue(group1.contains(Permutations.identity(domainP)));
    assertTrue(group1.contains(p1));
    assertFalse(group1.contains(p2));
  }

  public void testCyclicGroup2() {
    PermutationGroup<Integer> group2 = new PermutationGroup<Integer>(domainP,
        p2);
    assertEquals(3, group2.size());
    assertTrue(group2.contains(Permutations.identity(domainP)));
    assertFalse(group2.contains(p1));
    assertTrue(group2.contains(p2));
  }

  public void testCyclicGroup12() {
    PermutationGroup<Integer> group12 = new PermutationGroup<Integer>(domainP,
        p1, p2);
    assertEquals(6, group12.size());
    assertTrue(group12.contains(Permutations.identity(domainP)));
    assertTrue(group12.contains(p1));
    assertTrue(group12.contains(p2));
    assertTrue(group12.contains(p3));
  }
}
