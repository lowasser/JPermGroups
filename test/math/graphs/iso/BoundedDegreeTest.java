package math.graphs.iso;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import math.algebra.permgroup.Groups;
import math.algebra.permgroup.PermGroup;
import math.structures.permutation.Permutations;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleGraph;

public class BoundedDegreeTest extends TestCase {
  public void testCycle() {
    SimpleGraph<Integer, Object> g =
        new SimpleGraph<Integer, Object>(Object.class);
    int n = 10;

    List<Integer> cyc = Lists.newArrayList();
    Map<Integer, Integer> swap = Maps.newHashMap();
    for (int i = 0; i < n; i++) {
      Graphs.addEdgeWithVertices(g, i, (i + 1) % n);
      swap.put(i, n - 1 - i);
      cyc.add(i);
    }
    PermGroup<Integer> aut = BoundedDegree.automorphismGroup(g);

    PermGroup<Integer> dihedral =
        Groups.generateGroup(ImmutableList.of(Permutations.permutation(swap),
            Permutations.cycle(cyc)));
    assertEquals(dihedral, aut);
  }
}
