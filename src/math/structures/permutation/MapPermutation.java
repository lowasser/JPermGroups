package math.structures.permutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MapPermutation<E> extends AbstractPermutation<E> {
  /**
   * Must not map any element to itself.
   */
  private final ImmutableBiMap<E, E> map;

  MapPermutation(ImmutableBiMap<E, E> map) {
    this.map = checkNotNull(map);
    assert map.values().equals(map.keySet());
    assert validMap();
  }

  MapPermutation(Map<E, E> map) {
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    for (Map.Entry<E, E> entry : checkNotNull(map).entrySet()) {
      E e = checkNotNull(entry.getKey());
      E img = checkNotNull(entry.getValue());
      if (!Objects.equal(e, img)) {
        builder.put(e, img);
      }
    }
    this.map = builder.build();
    checkArgument(this.map.keySet().equals(this.map.values()),
        "Map %s is not a bijection", map);
  }

  MapPermutation(Permutation<E> sigma) {
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    for (E e : checkNotNull(sigma).domain()) {
      builder.put(e, sigma.apply(e));
    }
    this.map = builder.build();
  }

  @Override public E apply(E e) {
    E result = map.get(checkNotNull(e));
    return (result == null) ? e : result;
  }

  @Override public Permutation<E> compose(List<Permutation<E>> taus) {
    if (taus.isEmpty()) {
      return this;
    }
    Map<E, E> tau = Maps.newHashMap(asMap());
    for (Permutation<E> sigma : taus) {
      if (!tau.isEmpty()) {
        List<Entry<E, E>> entryList = Lists.newArrayList();
        for (Entry<E, E> entry : sigma.asMap().entrySet()) {
          E e = entry.getKey();
          E sigmaE = entry.getValue();
          E sigmaTauE = tau.remove(sigmaE);
          if (!Objects.equal(sigmaTauE, e)) {
            entryList.add(Maps.immutableEntry(e, (sigmaTauE == null) ? sigmaE
                : sigmaTauE));
          }
        }
        for (Entry<E, E> entry : entryList) {
          tau.put(entry.getKey(), entry.getValue());
        }
      } else {
        tau.putAll(sigma.asMap());
      }
    }
    return new MapPermutation<E>(tau);
  }

  @Override public Permutation<E> compose(Permutation<E> tau) {
    if (tau.isIdentity()) {
      return this;
    }
    Map<E, E> sigmaMap = Maps.newHashMap(asMap());
    ImmutableBiMap.Builder<E, E> builder = ImmutableBiMap.builder();
    for (Map.Entry<E, E> entry : tau.asMap().entrySet()) {
      E e = entry.getKey();
      E tauE = entry.getValue();
      E sigmaTauE = sigmaMap.remove(tauE);
      if (sigmaTauE == null) {
        sigmaTauE = tauE;
      }
      if (!Objects.equal(e, sigmaTauE)) {
        builder.put(e, sigmaTauE);
      }
    }
    builder.putAll(sigmaMap);
    return new MapPermutation<E>(builder.build());
  }

  @Override public int hashCode() {
    return map.hashCode();
  }

  @Override public E preimage(E e) {
    E result = map.inverse().get(checkNotNull(e));
    return (result == null) ? e : result;
  }

  @Override protected Map<E, E> createAsMap() {
    return map;
  }

  @Override protected Set<E> createDomain() {
    return map.keySet();
  }

  @Override protected Permutation<E> inverseCompose(List<Permutation<E>> taus) {
    if (taus.isEmpty()) {
      return this;
    }
    Map<E, E> tau = Maps.newHashMap(map.inverse());
    for (Permutation<E> sigma : taus) {
      if (!tau.isEmpty()) {
        List<Entry<E, E>> entryList = Lists.newArrayList();
        for (Entry<E, E> entry : sigma.asMap().entrySet()) {
          E e = entry.getKey();
          E sigmaE = entry.getValue();
          E sigmaTauE = tau.remove(sigmaE);
          if (!Objects.equal(sigmaTauE, e)) {
            entryList.add(Maps.immutableEntry(e, (sigmaTauE == null) ? sigmaE
                : sigmaTauE));
          }
        }
        for (Entry<E, E> entry : entryList) {
          tau.put(entry.getKey(), entry.getValue());
        }
      } else {
        tau.putAll(sigma.asMap());
      }
    }
    return new MapPermutation<E>(tau);
  }

  private boolean validMap() {
    boolean good = true;
    for (Map.Entry<E, E> entry : map.entrySet()) {
      good &= !Objects.equal(entry.getKey(), entry.getValue());
    }
    return good;
  }
}
