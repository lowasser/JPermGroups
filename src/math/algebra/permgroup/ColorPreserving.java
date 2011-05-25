package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

public final class ColorPreserving {
  private ColorPreserving() {
  }

  public static <E, C> LCoset<E> colorPreserving(
      @Nullable LCoset<E> sigmaG, Set<E> bSet, Function<E, C> coloring) {
    if (sigmaG == null) {
      return null;
    }
    Permutation<E> sigma = sigmaG.getRepresentative();
    PermGroup<E> g = sigmaG.getGroup();
    assert g.stabilizes(bSet);
    if (bSet.size() == 1) {
      E b = bSet.iterator().next();
      C bColor = coloring.apply(b);
      E bImage = sigma.apply(b);
      C bImageColor = coloring.apply(bImage);
      return Objects.equal(bColor, bImageColor) ? sigmaG : null;
    }

    Collection<Orbit<E>> orbits = Orbit.orbits(g, bSet);
    if (orbits.size() > 1) {
      LCoset<E> answer = sigmaG;
      for (Orbit<E> orbit : orbits) {
        answer = colorPreserving(answer, orbit, coloring);
        if (answer == null)
          break;
      }
      return answer;
    }

    BlockSystem<E> system = BlockSystem.minimalBlockSystem(g, bSet);
    PermSubgroup<E> stabilizingSubgroup = system.stabilizingSubgroup(g);
    Collection<LCoset<E>> colorPreservers = Lists.newArrayList();
    for (LCoset<E> coset : stabilizingSubgroup.asCosets()) {
      colorPreservers.add(colorPreserving(coset, bSet, coloring));
    }
    return glue(colorPreservers);
  }

  static <E> LCoset<E> glue(Collection<LCoset<E>> cosets) {
    Collection<Permutation<E>> generators = Lists.newArrayList();
    Iterator<LCoset<E>> cosetIterator =
        Iterables.filter(cosets, Predicates.notNull()).iterator();
    if (!cosetIterator.hasNext())
      return null;
    LCoset<E> c1 = cosetIterator.next();
    Permutation<E> rho1 = c1.getRepresentative();
    PermGroup<E> h = c1.getGroup();
    generators.addAll(h.generators());
    while (cosetIterator.hasNext()) {
      generators.add(Permutations.compose(rho1.inverse(), cosetIterator.next()
        .getRepresentative()));
    }
    return new LCoset<E>(rho1, generators);
  }
}
