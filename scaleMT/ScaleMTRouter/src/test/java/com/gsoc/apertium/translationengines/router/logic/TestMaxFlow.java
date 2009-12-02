package com.gsoc.apertium.translationengines.router.logic;


import org.scalemt.router.logic.FlowNetwork;
import org.scalemt.router.logic.FlowNetwork.Edge;
import org.scalemt.router.logic.FlowNetwork.Vertex;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vmsanchez
 */
public class TestMaxFlow {

@Test
 public void test0()
 {
     FlowNetwork flowNetwork=new FlowNetwork();

      Vertex v1 = flowNetwork.addVertex("1");
      Vertex v2 = flowNetwork.addVertex("2");
      Vertex v3 = flowNetwork.addVertex("3");
      Vertex v4 = flowNetwork.addVertex("4");
      //Vertex v5 = flowNetwork.addVertex("5");

      flowNetwork.addEdge(v1, v2, 7);
      flowNetwork.addEdge(v2, v3, 4);
      flowNetwork.addEdge(v2, v4, 2);
      flowNetwork.addEdge(v4, v3, 8);


      int maxflow = flowNetwork.maxFlow(v1, v3);
      assertEquals(6,maxflow);
      System.out.println(maxflow);

      List<Edge> edges;

      edges = flowNetwork.getEdges(v1);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v2);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v3);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v4);
      for(Edge edge: edges)
          System.out.println(edge);
 }

@Test
 public void test1()
 {
     FlowNetwork flowNetwork=new FlowNetwork();

      Vertex v1 = flowNetwork.addVertex("1");
      Vertex v2 = flowNetwork.addVertex("2");
      Vertex v3 = flowNetwork.addVertex("3");
      Vertex v4 = flowNetwork.addVertex("4");
      Vertex v5 = flowNetwork.addVertex("5");

      flowNetwork.addEdge(v1, v2, 5);
      flowNetwork.addEdge(v1, v4, 5);
      flowNetwork.addEdge(v1, v5, 6);
      flowNetwork.addEdge(v2, v3, 4);
      flowNetwork.addEdge(v3, v4, 8);
      flowNetwork.addEdge(v3, v5, 9);
      flowNetwork.addEdge(v4, v5, 7);

      int maxflow = flowNetwork.maxFlow(v1, v5);

      assertEquals(15,maxflow);
      System.out.println(maxflow);

      List<Edge> edges;

      edges = flowNetwork.getEdges(v1);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v2);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v3);
      for(Edge edge: edges)
          System.out.println(edge);
      edges = flowNetwork.getEdges(v4);
      for(Edge edge: edges)
          System.out.println(edge);
 
 }

@Test
 public void test2()
 {
      FlowNetwork flowNetwork=new FlowNetwork();

     Vertex v1 = flowNetwork.addVertex("1");
      Vertex v2 = flowNetwork.addVertex("2");
      Vertex v3 = flowNetwork.addVertex("3");
      Vertex v4 = flowNetwork.addVertex("4");
      Vertex v5 = flowNetwork.addVertex("5");

      flowNetwork.addEdge(v1, v2, 5);
      flowNetwork.addEdge(v1, v3, 7);
      flowNetwork.addEdge(v1, v4, 3);
      flowNetwork.addEdge(v2, v5, 6);
      flowNetwork.addEdge(v3, v2, 2);
      flowNetwork.addEdge(v3, v5, 5);
      flowNetwork.addEdge(v4, v3, 4);
      flowNetwork.addEdge(v4, v5, 8);

      int maxflow = flowNetwork.maxFlow(v1, v5);

       assertEquals(14,maxflow);
      System.out.println(maxflow);

      List<Edge> edges = flowNetwork.getEdges(v1);

      for(Edge edge: edges)
      {
          System.out.println(edge.getV1().getId()+"-"+edge.getV2().getId()+" "+edge.getFlow()+"/"+edge.getCapacity()+"("+edge.getResidual()+")");
      }
 }


}
