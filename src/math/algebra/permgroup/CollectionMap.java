package math.algebra.permgroup;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.Collection;

final class CollectionMap<E1, E2> implements
    Function<Collection<E1>, Collection<E2>> {
  public static <E1, E2> CollectionMap<E1, E2> forFunction(
      Function<E1, E2> function) {
    return new CollectionMap<E1, E2>(function);
  }

  private final Function<E1, E2> function;

  private CollectionMap(Function<E1, E2> function) {
    this.function = checkNotNull(function);
  }

  @Override public Collection<E2> apply(Collection<E1> input) {
    return Collections2.transform(input, function);
  }
}
