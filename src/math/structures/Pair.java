package math.structures;

import com.google.common.base.Function;
import com.google.common.base.Objects;

import javax.annotation.Nullable;

public final class Pair<A, B> {
  public static <A, B> Pair<A, B> of(A a, B b) {
    return new Pair<A, B>(a, b);
  }

  @Nullable private final A a;
  @Nullable private final B b;

  public static <A, B> Function<Pair<A, B>, A> firstFunction() {
    return new Function<Pair<A, B>, A>() {
      @Override public A apply(Pair<A, B> input) {
        return input.getFirst();
      }
    };
  }

  private Pair(@Nullable A a, @Nullable B b) {
    this.a = a;
    this.b = b;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof Pair) {
      Pair<?, ?> p = (Pair<?, ?>) obj;
      return Objects.equal(a, p.a) && Objects.equal(b, p.b);
    }
    return false;
  }

  public A getFirst() {
    return a;
  }

  public B getSecond() {
    return b;
  }

  @Override public int hashCode() {
    return Objects.hashCode(a, b);
  }

  @Override public String toString() {
    return new StringBuilder().append('(').append(a).append(", ").append(b)
      .append(')').toString();
  }

}
