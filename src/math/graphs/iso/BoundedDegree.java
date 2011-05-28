package math.graphs.iso;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import math.algebra.permgroup.ColorPreserving;
import math.algebra.permgroup.Groups;
import math.algebra.permgroup.PermGroup;
import math.structures.Colorings;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;

public class BoundedDegree {
  private static class AutoFindingColoring<V> implements Function<V, Object> {
    private final Map<V, Object> colors;
    private final V v;
    private final Object vColor;

    AutoFindingColoring(Map<V, Object> colors, V v, Object vColor) {
      this.colors = colors;
      this.v = v;
      this.vColor = vColor;
    }

    @Override public Object apply(V x) {
      if (colors.containsKey(x)) {
        return colors.get(x);
      } else if (Objects.equal(v, x)) {
        return vColor;
      } else {
        return BLANK;
      }
    }
  }

  private static class Color {
    private final int tuplet;
    private final boolean edged;

    Color(int tuplet, boolean edged) {
      this.tuplet = tuplet;
      this.edged = edged;
    }

    @Override public boolean equals(@Nullable Object o) {
      if (o instanceof Color) {
        Color c = (Color) o;
        return tuplet == c.tuplet && edged == c.edged;
      }
      return false;
    }
  }

  private static final class IsoColoring<V1, V2> implements Equivalence<Object> {
    private final Map<Object, V1> v1Reps;
    private final Map<Object, V2> v2Reps;
    private final Function<? super V1, ?> color1;
    private final Function<? super V2, ?> color2;
    private final Set<Object> glue;

    IsoColoring(Map<Object, V1> v1Reps, Map<Object, V2> v2Reps,
        Function<? super V1, ?> color1, Function<? super V2, ?> color2,
        Set<Object> glue) {
      this.v1Reps = v1Reps;
      this.v2Reps = v2Reps;
      this.color1 = color1;
      this.color2 = color2;
      this.glue = glue;
    }

    @Override public boolean equivalent(Object a, Object b) {
      if (glue.contains(a)) {
        return glue.contains(b);
      }
      return !glue.contains(b) && Objects.equal(color(a), color(b));
    }

    @Override public int hash(Object o) {
      return glue.contains(o) ? glue.hashCode() : color(o).hashCode();
    }

    private Object color(Object o) {
      V1 v1 = v1Reps.get(o);
      return (v1 == null) ? color2.apply(v2Reps.get(o)) : color1.apply(v1);
    }
  }

  private static final Object BLANK = new Object();

  public static <V, E> PermGroup<V> automorphismGroup(SimpleGraph<V, E> g) {
    return automorphismGroup(g, Colorings.NON_COLORING);
  }

  public static <V, E> PermGroup<V> automorphismGroup(SimpleGraph<V, E> g,
      Equivalence<? super V> coloring) {
    List<Permutation<V>> generators = Lists.newArrayList();
    List<V> vertices = ImmutableList.copyOf(g.vertexSet());
    final Map<V, Object> colors =
        Maps.newHashMapWithExpectedSize(vertices.size());
    Collection<Set<V>> orbits = autOrbits(g, coloring);
    for (Set<V> orbit : orbits) {
      List<V> orbitList = ImmutableList.copyOf(orbit);
      for (int i = 0; i < orbitList.size(); i++) {
        final V v = orbitList.get(i);
        final Object vColor = new Object();
        for (int j = i + 1; j < orbitList.size(); j++) {
          final V w = orbitList.get(j);
          Map<V, V> iso =
              isomorphism(g, g, new AutoFindingColoring<V>(colors, w, vColor),
                  new AutoFindingColoring<V>(colors, v, vColor));
          if (iso != null) {
            generators.add(Permutations.permutation(iso));
          }
        }
        colors.put(v, vColor); // now we stabilize v
      }
    }
    return Groups.generateGroup(generators);
  }

  public static <V, E> Collection<Set<V>> autOrbits(SimpleGraph<V, E> g,
      Equivalence<? super V> coloring) {
    Set<V> todo = Sets.newLinkedHashSet(g.vertexSet());
    ImmutableList.Builder<Set<V>> orbitsBuilder = ImmutableList.builder();
    while (!todo.isEmpty()) {
      Iterator<V> iter = todo.iterator();
      V v = iter.next();
      iter.remove();
      ImmutableSet.Builder<V> orbitBuilder = ImmutableSet.builder();
      orbitBuilder.add(v);
      while (iter.hasNext()) {
        V w = iter.next();
        if (!coloring.equivalent(v, w))
          continue;
        final Object vwColor = new Object();
        Function<V, Object> color1 =
            Functions.forMap(ImmutableMap.of(v, vwColor), BLANK);
        Function<V, Object> color2 =
            Functions.forMap(ImmutableMap.of(w, vwColor), BLANK);
        if (isomorphism(g, g, color1, color2) != null) {
          orbitBuilder.add(w);
          iter.remove();
        }
      }
      orbitsBuilder.add(orbitBuilder.build());
    }
    return orbitsBuilder.build();
  }

  public static <V1, E1, V2, E2> BiMap<V1, V2> isomorphism(
      SimpleGraph<V1, E1> g1, SimpleGraph<V2, E2> g2) {
    Function<Object, Object> coloring = Functions.constant(new Object());
    return isomorphism(g1, g2, coloring, coloring);
  }

  public static <V1, E1, V2, E2, C> BiMap<V1, V2>
      isomorphism(SimpleGraph<V1, E1> g1, SimpleGraph<V2, E2> g2,
          final Function<? super V1, C> color1,
          final Function<? super V2, C> color2) {
    int n1 = g1.vertexSet().size();
    int n2 = g2.vertexSet().size();
    int m1 = g1.edgeSet().size();
    int m2 = g2.edgeSet().size();
    if (n1 != n2 || m1 != m2) {
      return null;
    }
    if (m1 == 0 || m1 * 2 == n1 * (n1 - 1)) {
      ImmutableBiMap.Builder<V1, V2> builder = ImmutableBiMap.builder();
      Iterator<V1> iter1 = g1.vertexSet().iterator();
      Iterator<V2> iter2 = g2.vertexSet().iterator();
      while (iter1.hasNext()) {
        builder.put(iter1.next(), iter2.next());
      }
      return builder.build();
    }

    final BiMap<V1, Object> rep1 = representatives(g1.vertexSet());
    final BiMap<V2, Object> rep2 = representatives(g2.vertexSet());

    SimpleGraph<Object, Object> glued =
        new SimpleGraph<Object, Object>(Object.class);

    Graphs.addAllVertices(glued, rep1.values());
    Graphs.addAllVertices(glued, rep2.values());

    final Object glue1 = new Object();
    final Object glue2 = new Object();
    glued.addVertex(glue1);
    glued.addVertex(glue2);

    Equivalence<Object> coloring =
        new IsoColoring<V1, V2>(rep1.inverse(), rep2.inverse(), color1, color2,
            ImmutableSet.of(glue1, glue2));

    for (E1 e1 : g1.edgeSet()) {
      glued.addEdge(rep1.get(g1.getEdgeSource(e1)),
          rep1.get(g1.getEdgeTarget(e1)));
    }
    for (E2 e2 : g2.edgeSet()) {
      glued.addEdge(rep2.get(g2.getEdgeSource(e2)),
          rep2.get(g2.getEdgeTarget(e2)));
    }

    E1 e1 = g1.edgeSet().iterator().next();
    V1 v1S = g1.getEdgeSource(e1);
    Object e1S = rep1.get(v1S);
    V1 v1T = g1.getEdgeTarget(e1);
    Object e1T = rep1.get(v1T);
    glued.removeAllEdges(e1S, e1T);
    glued.addEdge(e1S, glue1);
    glued.addEdge(e1T, glue1);
    Object e0 = glued.addEdge(glue1, glue2);

    for (E2 e2 : g2.edgeSet()) {
      V2 v2S = g2.getEdgeSource(e2);
      Object e2S = rep2.get(v2S);
      V2 v2T = g2.getEdgeTarget(e2);
      Object e2T = rep2.get(v2T);

      if (!ImmutableSet.of(color1.apply(v1S), color1.apply(v1T)).equals(
          ImmutableSet.of(color2.apply(v2S), color2.apply(v2T))))
        continue;

      glued.addEdge(e2S, glue2);
      glued.addEdge(e2T, glue2);

      PermGroup<Object> aut =
          automorphismGroup(new UnmodifiableUndirectedGraph<Object, Object>(
              glued), e0, coloring);
      for (Permutation<Object> sigma : aut.generators()) {
        if (sigma.apply(glue1).equals(glue2)) {
          ImmutableBiMap.Builder<V1, V2> builder = ImmutableBiMap.builder();
          for (Map.Entry<V1, Object> v1Entry : rep1.entrySet()) {
            Object v1Rep = v1Entry.getValue();
            V1 v1 = v1Entry.getKey();
            Object v2Rep = sigma.apply(v1Rep);
            V2 v2 = rep2.inverse().get(v2Rep);
            builder.put(v1, v2);
          }
          return builder.build();
        }
      }
      glued.removeEdge(e2S, glue2);
      glued.removeEdge(e2T, glue2);
      glued.addEdge(e2S, e2T);
    }
    return null;
  }

  static <V, E> PermGroup<V> automorphismGroup(UndirectedGraph<V, E> g0, E e0,
      Equivalence<? super V> vColoring) {
    SimpleGraph<V, E> g = new SimpleGraph<V, E>(g0.getEdgeFactory());
    Graphs.addEdgeWithVertices(g, g0, e0);
    PermGroup<V> autR =
        Groups.symmetric(ImmutableSet.of(g0.getEdgeSource(e0),
            g0.getEdgeTarget(e0)));
    while (g.vertexSet().size() < g0.vertexSet().size()) {
      final SimpleGraph<V, E> gPrime =
          new SimpleGraph<V, E>(g0.getEdgeFactory());
      Graphs.addGraph(gPrime, g);
      extend(g0, gPrime);
      Set<V> newVertices = Sets.newHashSet(gPrime.vertexSet());
      newVertices.removeAll(g.vertexSet());
      Map<V, Set<V>> parent = Maps.newHashMap();
      for (V v : newVertices) {
        Set<V> parents = Sets.newHashSet(Graphs.neighborListOf(gPrime, v));
        parents.removeAll(newVertices);
        parent.put(v, ImmutableSet.copyOf(parents));
      }
      final SetMultimap<Set<V>, V> children =
          ImmutableSetMultimap.copyOf(Multimaps.invertFrom(
              Multimaps.forMap(parent), HashMultimap.<Set<V>, V> create()));

      Set<Set<V>> parents = children.keySet();

      List<Permutation<V>> generators = Lists.newArrayList();
      int max = 1;
      for (Collection<V> siblingsC : children.asMap().values()) {
        max = Math.max(max, siblingsC.size());
        for (Set<V> matesC : Colorings.colors((Set<V>) siblingsC, vColoring)) {
          generators.addAll(Groups.symmetric(matesC).generators());
        }
      }

      Function<Set<V>, Color> aColor = new Function<Set<V>, Color>() {
        @Override public Color apply(Set<V> a) {
          boolean isEdge = a.size() == 2;
          if (isEdge) {
            Iterator<V> iter = a.iterator();
            V v = iter.next();
            V w = iter.next();
            isEdge &= gPrime.containsEdge(v, w);
          }
          return new Color(children.get(a).size(), isEdge);
        }
      };
      PermGroup<V> preservingGroup =
          ColorPreserving.colorPreservingAction(autR, parents,
              Colorings.coloring(aColor));

      for (Permutation<V> sigma : preservingGroup.generators()) {
        Map<V, V> added = Maps.newHashMap();
        for (Map.Entry<Set<V>, Collection<V>> entry : children.asMap()
          .entrySet()) {
          Set<V> a = entry.getKey();
          Set<V> aImage = sigma.apply(a);
          assert children.get(aImage).size() == entry.getValue().size();
          Iterator<V> aImKidsIter = children.get(aImage).iterator();
          Iterator<V> aKidsIter = entry.getValue().iterator();
          while (aKidsIter.hasNext()) {
            added.put(aKidsIter.next(), aImKidsIter.next());
          }
        }
        for (V v : g.vertexSet()) {
          added.put(v, sigma.apply(ImmutableSet.of(v)).iterator().next());
        }
        generators.add(Permutations.permutation(added));
      }
      autR = Groups.generateGroup(generators);
      g = gPrime;
    }
    return autR;
  }

  private static <V, E> void extend(Graph<V, E> g0, Graph<V, E> g) {
    for (V v : ImmutableList.copyOf(g.vertexSet())) {
      for (E e : g0.edgesOf(v)) {
        Graphs.addEdgeWithVertices(g, g0, e);
      }
    }
  }

  private static <T> BiMap<T, Object> representatives(Set<T> set) {
    ImmutableBiMap.Builder<T, Object> builder = ImmutableBiMap.builder();
    for (T t : set) {
      builder.put(t, new Object());
    }
    return builder.build();
  }
}
