package math.algebra.permgroup;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import math.algebra.permgroups.permutation.Permutation;

final class StabilizingFilterList<E> extends
    ForwardingList<Predicate<Permutation<E>>> {
  private final ImmutableList<E> elements;

  public StabilizingFilterList(ImmutableCollection<E> elements) {
    this.elements = elements.asList();
  }

  @Override protected List<Predicate<Permutation<E>>> delegate() {
    return Lists.transform(elements, new Function<E,Predicate<Permutation<E>>>(){
      @Override public Predicate<Permutation<E>> apply(E e) {
        return StabilizesPredicate.on(e);
      }});
  }
}
