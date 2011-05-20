package math.permutation;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import javax.annotation.Nullable;

final class Identity<E> extends Permutation<E> {
  private final Set<E> domain;

  Identity(Set<E> domain) {
    this.domain = ImmutableSet.copyOf(domain);
  }

  @Override public Function<E, E> asFunction() {
    return Functions.identity();
  }

  @Override public Permutation<E> compose(Permutation<E> perm) {
    return perm;
  }

  @Override public Set<E> domain() {
    return domain;
  }

  @Override public boolean equals(@Nullable Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof Identity) {
      return domain().equals(((Permutation) obj).domain());
    }
    return super.equals(obj);
  }

  @Override public E image(E e) {
    checkArgument(domain.contains(e));
    return e;
  }

  @Override public E preimage(E e) {
    checkArgument(domain.contains(e));
    return e;
  }

  @Override public Permutation<E> inverse() {
    return this;
  }

  @Override public Permutation<E> extend(Set<E> newDomain) {
    checkArgument(newDomain.containsAll(domain));
    return new Identity<E>(newDomain);
  }
}