package math.algebra.permgroup;


import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import math.permutation.Permutation;
import math.permutation.Permutations;
import math.structures.Pair;

final class Project2nd<A, B> extends Permutation<B> {
  private final Permutation<Pair<A, B>> sigma;
  private final ImmutableSet<B> domainB;
  private transient final A a;

  public static <A, B> Function<Permutation<B>, Permutation<Pair<A, B>>>
      projectUp(final Set<A> domainA, final Set<B> domainB) {
    final Permutation<A> idA = Permutations.identity(domainA);
    return new Function<Permutation<B>, Permutation<Pair<A, B>>>() {
      @Override public Permutation<Pair<A, B>> apply(Permutation<B> input) {
        return Permutations.directProduct(idA, input);
      }
    };
  }

  public static <A, B> Function<Permutation<Pair<A, B>>, Permutation<B>>
      projectDown(final Set<A> domainA, final Set<B> domainB) {
    return new Function<Permutation<Pair<A, B>>, Permutation<B>>() {
      final Set<B> theDomainB = ImmutableSet.copyOf(domainB);
      final A a = domainA.iterator().next();

      @Override public Permutation<B> apply(Permutation<Pair<A, B>> input) {
        return new Project2nd<A, B>(input, theDomainB, a);
      }
    };
  }

  private Project2nd(Permutation<Pair<A, B>> sigma,
      Set<B> domainB, A a2) {
    this.sigma = sigma;
    this.domainB = ImmutableSet.copyOf(domainB);
    this.a = a2;
  }

  @Override public B image(B b) {
    return sigma.image(Pair.of(a, b)).getSecond();
  }

  @Override public B preimage(B b) {
    return sigma.preimage(Pair.of(a, b)).getSecond();
  }

  @Override public Set<B> domain() {
    return domainB;
  }
}
