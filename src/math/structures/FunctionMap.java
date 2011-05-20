package math.structures;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Set;

public final class FunctionMap<K, V> extends AbstractMap<K, V> {
  private final class EntrySet extends ForwardingCollection<Entry<K, V>>
      implements Set<Entry<K, V>> {
    private final Collection<Entry<K, V>> entryCollection;

    public EntrySet() {
      this.entryCollection =
          Collections2.transform(keySet, new Function<K, Entry<K, V>>() {
            @Override public java.util.Map.Entry<K, V> apply(K input) {
              return Maps.immutableEntry(input, function.apply(input));
            }
          });
    }

    @Override protected Collection<java.util.Map.Entry<K, V>> delegate() {
      return entryCollection;
    }
  }

  private final Set<K> keySet;
  private final Function<K, V> function;
  private transient Integer hashCode = null;

  public FunctionMap(Set<K> keySet, Function<K, V> function) {
    this.keySet = ImmutableSet.copyOf(keySet);
    this.function = checkNotNull(function);
  }

  @Override public int size() {
    return keySet.size();
  }

  @Override public Collection<V> values() {
    return Collections2.transform(keySet, function);
  }

  private transient Set<Entry<K, V>> entrySet = null;

  @Override public Set<Entry<K, V>> entrySet() {
    return (entrySet == null) ? entrySet = new EntrySet() : entrySet;
  }

  @Override public int hashCode() {
    return (hashCode == null) ? hashCode = entrySet().hashCode() : hashCode;
  }
}
