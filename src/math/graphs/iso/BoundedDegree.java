package math.graphs.iso;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
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

import math.algebra.permgroup.Groups;
import math.algebra.permgroup.PermGroup;
import math.structures.permutation.Permutation;
import math.structures.permutation.Permutations;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;

public class BoundedDegree {
  private static class Color {
    private final int tuplet;
    private final boolean edged;

    Color(int tuplet, boolean edged) {
      this.tuplet = tuplet;
      this.edged = edged;
    }

    public boolean equals(@Nullable Object o) {
      if (o instanceof Color) {
        Color c = (Color) o;
        return tuplet == c.tuplet && edged == c.edged;
      }
      return false;
    }
  }

  public static <V1, E1, V2, E2, C> BiMap<V1, V2> isomorphism(
      SimpleGraph<V1, E1> g1, SimpleGraph<V2, E2> g2) {
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

    BiMap<V1, Object> rep1 = representatives(g1.vertexSet());
    BiMap<V2, Object> rep2 = representatives(g2.vertexSet());

    SimpleGraph<Object, Object> glued =
        new SimpleGraph<Object, Object>(Object.class);

    glued.vertexSet().addAll(rep1.values());
    glued.vertexSet().addAll(rep2.values());

    Object glue1 = new Object();
    Object glue2 = new Object();
    glued.addVertex(glue1);
    glued.addVertex(glue2);

    for (E1 e1 : g1.edgeSet()) {
      glued.addEdge(rep1.get(g1.getEdgeSource(e1)),
          rep1.get(g1.getEdgeTarget(e1)));
    }
    for (E2 e2 : g2.edgeSet()) {
      glued.addEdge(rep2.get(g2.getEdgeSource(e2)),
          rep2.get(g2.getEdgeTarget(e2)));
    }

    E1 e1 = g1.edgeSet().iterator().next();
    Object e1S = rep1.get(g1.getEdgeSource(e1));
    Object e1T = rep1.get(g1.getEdgeTarget(e1));
    glued.removeAllEdges(e1S, e1T);
    glued.addEdge(e1S, glue1);
    glued.addEdge(e1T, glue1);
    Object e0 = glued.addEdge(glue1, glue2);

    for (E2 e2 : g2.edgeSet()) {
      Object e2S = rep2.get(g2.getEdgeSource(e2));
      Object e2T = rep2.get(g2.getEdgeTarget(e2));
      glued.addEdge(e2S, glue2);
      glued.addEdge(e2T, glue2);
      PermGroup<Object> aut =
          automorphismGroup(new UnmodifiableUndirectedGraph<Object, Object>(
              glued), e0, new MapMaker().makeComputingMap(Functions
            .constant(new Object())));
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

  static <C, V, E> PermGroup<V> automorphismGroup(
      UndirectedGraph<V, E> g0, E e0, Map<? super V, C> vColor) {
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
      List<Permutation<V>> generators = Lists.newArrayList();
      for (Collection<V> siblingsC : children.asMap().values()) {
        generators.addAll(Groups.symmetric((Set<V>) siblingsC).generators());
      }

      Function<Set<V>, Color> coloring = new Function<Set<V>, Color>() {
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
          colorPreservingSubgroup(autR, children.keySet(), coloring);
      for (Permutation<V> sigma : preservingGroup.generators()) {
        Map<V, V> added = Maps.newHashMap();
        for (Map.Entry<Set<V>, Collection<V>> entry : children.asMap()
          .entrySet()) {
          Set<V> a = entry.getKey();
          Set<V> aImage = Sets.newHashSet();
          for (V v : a)
            aImage.add(sigma.apply(v));
          assert children.get(aImage).size() == entry.getValue().size();
          Iterator<V> aImKidsIter = children.get(aImage).iterator();
          Iterator<V> aKidsIter = entry.getValue().iterator();
          while (aKidsIter.hasNext()) {
            added.put(aKidsIter.next(), aImKidsIter.next());
          }
        }
        generators.add(Permutations.compose(sigma,
            Permutations.permutation(added)));
      }
      autR = preserving(Groups.generateGroup(generators), vColor);
      System.err.println(autR);
      g = gPrime;
    }
    return autR;
  }

  private static <E> PermGroup<E> colorPreservingSubgroup(
      PermGroup<E> g, final Collection<Set<E>> aSet,
      final Function<Set<E>, ?> coloring) {
    return g.subgroup(new Predicate<Permutation<E>>() {
      @Override public boolean apply(Permutation<E> sigma) {
        for (Set<E> a : aSet) {
          Set<E> image = Sets.newHashSetWithExpectedSize(a.size());
          for (E e : a)
            image.add(sigma.apply(e));
          if (!Objects.equal(coloring.apply(a), coloring.apply(image)))
            return false;
        }
        return true;
      }
    });
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

  static <T> PermGroup<T> preserving(PermGroup<T> group,
      final Map<? super T, ?> coloring) {
    return group.subgroup(new Predicate<Permutation<T>>() {
      @Override public boolean apply(Permutation<T> input) {
        for (T t : input.support()) {
          Object c = coloring.get(t);
          if (c != null && !Objects.equal(c, coloring.get(input.apply(t))))
            return false;
        }
        return true;
      }
    });
  }
}
