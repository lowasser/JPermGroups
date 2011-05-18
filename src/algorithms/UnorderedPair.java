package algorithms;

import com.google.common.base.Objects;

import javax.annotation.Nullable;

public final class UnorderedPair<E> {
  private final E a;
  private final E b;

  public static <E> UnorderedPair<E> of(@Nullable E a, @Nullable E b) {
    return new UnorderedPair<E>(a, b);
  }

  private UnorderedPair(E a, E b) {
    this.a = a;
    this.b = b;
  }

  public E getFirst() {
    return a;
  }

  public E getSecond() {
    return b;
  }

  @Override public int hashCode() {
    return Objects.hashCode(a) ^ Objects.hashCode(b);
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof UnorderedPair) {
      UnorderedPair<?> p = (UnorderedPair<?>) obj;
      return (Objects.equal(a, p.a) && Objects.equal(b, p.b))
          || (Objects.equal(a, p.b) && Objects.equal(b, p.a));
    }
    return false;
  }

  @Override public String toString() {
    return "{" + a + ", " + b + "}";
  }
}
