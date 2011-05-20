package math.algebra.permgroup;

import algorithms.Pair;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import math.algebra.permgroups.permutation.Permutation;
import math.algebra.permgroups.permutation.Permutations;

final class Project1st<A, B> extends Permutation<A> {
  private final Permutation<Pair<A, B>> sigma;
  private final ImmutableSet<A> domainA;
  private transient final B b;

  public static <A, B> Function<Permutation<A>, Permutation<Pair<A, B>>>
      projectUp(final Set<A> domainA, final Set<B> domainB) {
    final Permutation<B> idB = Permutations.identity(domainB);
    return new Function<Permutation<A>, Permutation<Pair<A, B>>>() {
      @Override public Permutation<Pair<A, B>> apply(Permutation<A> input) {
        return Permutations.directProduct(input, idB);
      }
    };
  }

  public static <A, B> Function<Permutation<Pair<A, B>>, Permutation<A>>
      projectDown(final Set<A> domainA, final Set<B> domainB) {
    return new Function<Permutation<Pair<A, B>>, Permutation<A>>() {
      final Set<A> theDomainA = ImmutableSet.copyOf(domainA);
      final B b = domainB.iterator().next();

      @Override public Permutation<A> apply(Permutation<Pair<A, B>> input) {
        return new Project1st<A, B>(input, theDomainA, b);
      }
    };
  }

  private Project1st(Permutation<Pair<A, B>> sigma,
      Set<A> domainA, B b) {
    this.sigma = sigma;
    this.domainA = ImmutableSet.copyOf(domainA);
    this.b = b;
  }

  @Override public A image(A a) {
    return sigma.image(Pair.of(a, b)).getFirst();
  }

  @Override public A preimage(A a) {
    return sigma.preimage(Pair.of(a, b)).getFirst();
  }

  @Override public Set<A> domain() {
    return domainA;
  }
}
