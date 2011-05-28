package math.structures;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import junit.framework.TestCase;

public class SubsetsTest extends TestCase {
  private static final Set<Integer> SET = ImmutableSet.of(0, 1, 2);

  public void testEmptySubsets() {
    assertEquals(ImmutableSet.of(ImmutableSet.of()),
        Subsets.subsetsOfSizeAtMost(SET, 0));
  }

  @SuppressWarnings("unchecked") public void testSubsets() {
    Set<Set<Integer>> subsets = Subsets.subsetsOfSizeAtMost(SET, 2);
    Set<Set<Integer>> expectedSets =
        ImmutableSet
          .<Set<Integer>> of(ImmutableSet.<Integer> of(), ImmutableSet.of(0),
              ImmutableSet.of(1), ImmutableSet.of(2), ImmutableSet.of(0, 1),
              ImmutableSet.of(0, 2), ImmutableSet.of(1, 2));
    assertEquals(expectedSets, subsets);
    assertEquals(ImmutableList.copyOf(expectedSets),
        ImmutableList.copyOf(subsets));
  }
}
