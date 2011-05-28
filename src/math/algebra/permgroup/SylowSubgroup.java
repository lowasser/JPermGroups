package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.Collection;

import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

public class SylowSubgroup<E> extends ForwardingPermGroup<E> implements
    PermSubgroup<E> {
  private static final class SylowSubgroupBuilder<E> {
    private final int p;
    private final PermGroup<E> g;
    private PermGroup<E> pi;
    private final Collection<Permutation<E>> c;

    SylowSubgroupBuilder(int p, PermGroup<E> g) {
      this.p = p;
      this.g = g;
      pi = Groups.trivial();
      c = Lists.newArrayList();
      c.add(Permutations.<E> identity());
      for (Permutation<E> sigma : g.generators()) {
        pBuild(sigma);
      }
    }

    public SylowSubgroup<E> build() {
      return new SylowSubgroup<E>(ImmutableList.copyOf(c), p, pi, g);
    }

    private void pBuild(Permutation<E> alpha) {
      for (Permutation<E> gamma : c) {
        PermGroup<E> tmp =
            pi.extend(ImmutableList.of(Permutations.compose(gamma.inverse(),
                alpha)));
        if (factorOut(tmp.size(), p) == 1) {
          pi = tmp;
          return;
        }
      }
      c.add(alpha);
      for (Permutation<E> sigma : g.generators()) {
        pBuild(Permutations.compose(sigma, alpha));
      }
    }
  }

  public static <E> SylowSubgroup<E> sylowSubgroup(PermGroup<E> g, int p) {
    return new SylowSubgroupBuilder<E>(p, g).build();
  }

  private static int factorOut(int n, int k) {
    if (n % k != 0) {
      return n;
    }
    n = factorOut(n, k * k);
    return (n % k == 0) ? n / k : n;
  }

  private final Collection<Permutation<E>> representatives;
  private final int p;
  private final PermGroup<E> pi;

  private final PermGroup<E> g;

  private SylowSubgroup(Collection<Permutation<E>> representatives, int p,
      PermGroup<E> pi, PermGroup<E> g) {
    this.representatives = representatives;
    this.p = p;
    this.pi = pi;
    this.g = g;
    checkArgument(BigInteger.valueOf(p).isProbablePrime(10));
    assert g.containsAll(representatives);
    assert pi.isSubgroupOf(g);
    assert representatives.size() % p != 0;
    assert g.size() == pi.size() * representatives.size();
  }

  @Override public Collection<LCoset<E>> asCosets() {
    return Collections2.transform(cosetRepresentatives(),
        new Function<Permutation<E>, LCoset<E>>() {
          @Override public LCoset<E> apply(Permutation<E> sigma) {
            return new LCoset<E>(sigma, SylowSubgroup.this);
          }
        });
  }

  @Override public Collection<Permutation<E>> cosetRepresentatives() {
    return representatives;
  }

  public int getP() {
    return p;
  }

  @Override public int index() {
    return cosetRepresentatives().size();
  }

  @Override public PermGroup<E> superGroup() {
    return g;
  }

  @Override protected PermGroup<E> delegate() {
    return pi;
  }

}
