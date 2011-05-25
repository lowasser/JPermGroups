package math.algebra.permgroup;

import static math.structures.permutation.Permutations.transposition;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import junit.framework.TestCase;
import math.structures.Colorings;

public class ColorPreservingTests extends TestCase {
  private static final Set<Integer> SET = ImmutableSet.of(1, 2, 3, 4, 5, 6, 7);
  private static final Equivalence<Integer> COLORING_1 = Colorings
    .coloring(new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input % 3;
      }
    });
  private static final PermGroup<Integer> SYMMETRIC = Groups.symmetric(SET);
  private static final PermGroup<Integer> PRESERVING_1 = Groups.symmetric(
      ImmutableSet.of(1, 4, 7)).extend(
      ImmutableList.of(transposition(2, 5), transposition(3, 6)));
  private static final Equivalence<Integer> COLORING_2 = Colorings
    .coloring(new Function<Integer, Integer>() {
      @Override public Integer apply(Integer input) {
        return input % 4;
      }
    });
  private static final PermGroup<Integer> PRESERVING_2 = Groups
    .generateGroup(ImmutableList.of(transposition(1, 5), transposition(2, 6),
        transposition(3, 7)));

  public void testColorPreserving1() {
    assertEquals(PRESERVING_1,
        ColorPreserving.colorPreserving(SYMMETRIC, SET, COLORING_1));
  }

  public void testColorPreserving2() {
    assertEquals(PRESERVING_2,
        ColorPreserving.colorPreserving(SYMMETRIC, SET, COLORING_2));
  }
}
