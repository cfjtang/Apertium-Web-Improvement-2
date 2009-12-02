/*
 *  ScaleMT. Highly scalable framework for machine translation web services
 *  Copyright (C) 2009  Víctor Manuel Sánchez Cartagena
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.scalemt.router.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Directed graph with flows associated to each edge.
 *
 * @author vmsanchez
 */
public class FlowNetwork {


    /**
     * Compares edges costs.
     */
    private class MinCostEdgeComparator implements Comparator<Edge>
    {

        @Override
        public int compare(Edge o1, Edge o2) {
           return o1.getCost()-o2.getCost();
        }
        
    }

    /**
     * Graph vertex
     */
    public class Vertex {

        /**
         * Unique id of the vertex in the graph. There isn't any vertex whith
         * the same id in the same graph.
         */
        private String id;
        /**
         * Distance to the source vertex. Used in min cost path calculation.
         */
        private int distance;
        /**
         * Predecessor in the min cost path.
         */
        private Vertex minLengthPredecessor;

        private Vertex(String id) {
            this.id = id;
            this.distance = Integer.MAX_VALUE;
            this.minLengthPredecessor = null;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public Vertex getMinLengthPredecessor() {
            return minLengthPredecessor;
        }

        public void setMinLengthPredecessor(Vertex minLengthPredecessor) {
            this.minLengthPredecessor = minLengthPredecessor;
        }
    }

    /**
     * Graph edge
     */
    public class Edge {

        /**
         * Edge goes from this vertex to v2
         */
        private Vertex v1;
        /**
         * Edge goes from v1 to this vertex
         */
        private Vertex v2;
        /**
         * Edge maximum flow capacity
         */
        private int capacity;
        /**
         * Flow assigned to this edge
         */
        private int flow;
        /**
         * Residual flow: capacity-flow
         */
        private int residual;
        /**
         * Cost per flow unit
         */
        private int cost;

        private Edge(Vertex v1, Vertex v2, int capacity, int flow) {
            this.v1 = v1;
            this.v2 = v2;
            this.capacity = capacity;
            this.flow = flow;
            this.residual = capacity - flow;
            this.cost = 0;
        }

        private Edge(Vertex v1, Vertex v2, int capacity) {
            this(v1, v2, capacity, 0);
        }

        private Edge(Vertex v1, Vertex v2) {
            this(v1, v2, 0, 0);
        }

        /**
         * Creates an edge from <code>v2</code> to <code>v1</code> with no capacity.
         * @return Opposite edge
         */
        public Edge getEmptyOpposite() {
            return new Edge(v2, v1);
        }

        public int getCapacity() {
            return capacity;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
            this.residual = this.capacity - this.flow;
        }

        public int getFlow() {
            return flow;
        }

        public void setFlow(int flow) {
            this.flow = flow;
            this.residual = this.capacity - this.flow;
        }

        public int getResidual() {
            return residual;
        }

        public Vertex getV1() {
            return v1;
        }

        public void setV1(Vertex v1) {
            this.v1 = v1;
        }

        public Vertex getV2() {
            return v2;
        }

        public void setV2(Vertex v2) {
            this.v2 = v2;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Edge) {
                Edge compare = (Edge) obj;
                return v1 == compare.v1 && v2 == compare.v2;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + (this.v1 != null ? this.v1.hashCode() : 0);
            hash = 23 * hash + (this.v2 != null ? this.v2.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return v1.getId() + "-" + v2.getId() + " " + flow + "/" + capacity + "(" + residual + ")";
        }
    }

    /**
     * Adjacency list. Associates each vertex with the edges that start on it.
     */
    private Map<Vertex, Map<Vertex,Edge>> adj;

    public FlowNetwork() {
        adj = new HashMap<FlowNetwork.Vertex,Map<FlowNetwork.Vertex,FlowNetwork.Edge>>();
    }

    /**
     * Adds a new vertex with the given id
     * @param id Vertex id
     * @return Vertex object
     */
    public Vertex addVertex(String id) {
        Vertex v = new Vertex(id);
        adj.put(v, new HashMap<FlowNetwork.Vertex,FlowNetwork.Edge>());
        return v;
    }

    /**
     * Adds a new edge between v1 and v2, with the given capacity and no flow
     *
     * @param v1 Vertex where the edge starts
     * @param v2 Vertex where the edge ends
     * @param capacity Edge capacity
     */
    public void addEdge(Vertex v1, Vertex v2, int capacity) {
        Edge e = new Edge(v1, v2, capacity);
        Edge e2 = new Edge(v2, v1, 0);
        adj.get(v1).put(v2,e);
        adj.get(v2).put(v1,e2);
    }

    /**
     * Gets all the graph edges that start on the given vertex
     * @param v Vertex where the edges start
     * @return List of edges
     */
    public List<Edge> getEdges(Vertex v) {
        return new ArrayList<Edge>(adj.get(v).values());
    }

    /**
     * Resets flow assigned to each edge, preparing the flow network to execute max flow or
     * max flow min cost again.
     */
    public void resetFlows() {
        for (Entry<Vertex, Map<Vertex,Edge>> entry : adj.entrySet()) {
            entry.getKey().setDistance(Integer.MAX_VALUE);
            entry.getKey().setMinLengthPredecessor(null);
            Collection<Edge> edges = entry.getValue().values();
            for (Edge edge : edges) {
                edge.setFlow(0);
            }
        }
    }

    /**
     * Removes all edges and vertices
     */
    void clear() {
        adj.clear();
    }

    /**
     * Recursive method that finds a path between source and sink.
     *
     * @param source Vertex where the path starts
     * @param sink Vertex where the path ends
     * @param path Path to be appended at the beginning of the result of this method
     * @return <code>path</code> parameter appended to a path between <code>source</code> and <code>sink</code>.
     */
    private List<Edge> findPath(Vertex source, Vertex sink, List<Edge> path) {
        List<Edge> localPath = new LinkedList<Edge>(path);
        if (source == sink) {
            return localPath;
        }
        for (Edge edge : adj.get(source).values()) {
            if (edge.getResidual() > 0 && localPath.indexOf(edge) == -1) {
                List<Edge> parameter = new LinkedList<Edge>(localPath);
                parameter.add(edge);
                List<Edge> result = findPath(edge.getV2(), sink, parameter);
                localPath.remove(edge);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return new LinkedList<Edge>();
    }

    /**
     * Finds a path between <code>source</code> and <code>sink</code> whose
     * edges have residual capacity > 0 . The length (number of vertices) of this
     * path is minimum.
     *
     * @param source Vertex where the path starts
     * @param sink Vertex where the path ends
     * @param visited Vertex that cannot be included in the path. Accepts <code>null</code> value
     * if don't want to exclude any vertex
     * @return Path between <code>source</code> and <code>sink</code>.
     */
    private List<Edge> findPathBreadthFirst(Vertex source, Vertex sink, Vertex visited)
    {
         List<Edge> localPath = new LinkedList<Edge>();
         Map<Vertex,Edge> parents = new HashMap<Vertex, Edge>();

         if(visited!=null)
             parents.put(visited, null);
         Queue<Vertex> queue= new LinkedList<Vertex>();
         queue.add(source);

         while(!queue.isEmpty())
         {
            Vertex v = queue.poll();
            for(Vertex v2: adj.get(v).keySet())
            {
                Edge e = adj.get(v).get(v2);
                if(!parents.containsKey(v2) && e.getResidual()>0)
                {
                    parents.put(v2, e);
                    if(v2==sink)
                    {
                        Vertex end=v2;
                        while(parents.containsKey(end))
                        {
                            Edge edge=parents.get(end);
                            localPath.add(0,edge);
                            end=edge.getV1();
                            if(end==source)
                                break;
                        }
                        break;
                    }
                    else
                        queue.add(v2);
                }
            }
         }
         return localPath;
    }

    /**
     * Finds a minimum cost path between source and sink vertices.
     * All the edges in the path have residual capacity > 0.
     *<br/>
     * WARNING: This method is optimized for our special case, where only edges between
     * servers and sink have costs different from zero
     *
     * @param source Vertex where the path starts
     * @param sink Vertex where the path ends
     * @return Minimum cost path between source and sink
     */
    private List<Edge> findMinimumCostPathOptimized(Vertex source, Vertex sink) {
       
        List<Edge> path = new ArrayList<Edge>();

        //List<Edge> lastEdges = new ArrayList<Edge>();
        SortedSet<Edge> lastEdges = new TreeSet<Edge>(new MinCostEdgeComparator());

         //Last edges
        for (Entry<Vertex, Map<Vertex,Edge>> entry : adj.entrySet())
            for (Edge edge : entry.getValue().values()) {
                if(edge.getResidual()>0)
                    if(edge.getV2()==sink)
                        lastEdges.add(edge);
            }

         //Sort last edges by cost
         //Collections.sort(lastEdges, new MinCostEdgeComparator());

         //Get any path from source to the minimum cost vertex.
         //If it is not found, try with the following vertex
         for(Edge e: lastEdges)
         {
             //path=findPath(source, e.getV1(), new ArrayList<Edge>());
             path=findPathBreadthFirst(source, e.getV1(), sink);
             if(!path.isEmpty())
             {
                 path.add(e);
                break;
             }
         }

        return path;
    }


    /**
     * DEPRECATED
     * @param source
     * @param sink
     * @return
     */
    private List<Edge> findMinimumCostPath(Vertex source, Vertex sink) {
/*
        System.out.println("findMinimumCostPath");
        //  for (Entry<Vertex, List<Edge>> entry : adj.entrySet()) {
                System.out.println("Vertex:"+entry.getKey().getId());
            
            for (Edge edge : entry.getValue()) {
                //if(edge.getResidual()>0)
                //{
                    System.out.println(edge);
                //}
            }
            
          }

        // Step 1: Initialize graph
        for(Vertex v: adj.keySet())
        {
            v.setDistance(Integer.MAX_VALUE);
            v.setMinLengthPredecessor(null);
        }
        source.setDistance(0);

        // Step 2: relax edges repeatedly
        for (int i = 0; i < adj.size() - 1; i++) {
            for (Entry<Vertex, List<Edge>> entry : adj.entrySet()) {                        
                for (Edge edge : entry.getValue()) {
                    if(edge.getResidual()>0)
                    {
                    Vertex u = edge.getV1();
                    Vertex v = edge.getV2();
                    if (u.getDistance() + edge.getCost() < v.getDistance() ) {
                        v.setDistance(u.getDistance() + edge.getCost());
                        v.setMinLengthPredecessor(u);
                    }
                    }
                }
            }
        }

        // Step 3: check for negative-weight cycles
        for (Entry<Vertex, List<Edge>> entry : adj.entrySet()) {
            for (Edge edge : entry.getValue()) {
                if(edge.getResidual()>0)
                {
                Vertex u = edge.getV1();
                Vertex v = edge.getV2();
                if (u.getDistance() + edge.getCost() < v.getDistance() ) {
                    throw new RuntimeException("Graph contains a negative-weight cycle");
                }
                }
            }
        }


         for (Entry<Vertex, List<Edge>> entry : adj.entrySet()) {
                System.out.print("Vertex:"+entry.getKey().getId());
                if(entry.getKey().getMinLengthPredecessor()!=null)
                    System.out.println(" p:"+entry.getKey().getMinLengthPredecessor().getId());
                else
                    System.out.println(" p: null");
          }

        //Create path
        List<Edge> path = new LinkedList<Edge>();
        Vertex v = sink;
        while(v!=null && v!=source)
        {
            Vertex v1 = v.getMinLengthPredecessor();
            if(v1==null)
                break;
            List<Edge> v1Edges = adj.get(v1);
            for(Edge e: v1Edges)
            {
                if(e.getResidual()>0)
                {
                if(e.getV2()==v)
                {
                    path.add(0, e);
                    break;
                }
                }
            }
            v=v1;
        }
        if(!path.isEmpty() && path.get(0).getV1()==source)
            return path;
        else
            return new LinkedList<Edge>();

*/
        return new ArrayList<Edge>();
    }

    /**
     * Executes max flow algorithm. Computes the maximum flow that can run between
     * source and sink vertices.
     *
     * @param source Source vertex
     * @param sink Sink vertex
     * @return Maximum flow that can run between source and sink
     */
    public int maxFlow(Vertex source, Vertex sink) {
        return maxFlowCore(source, sink, false);
    }

    /**
     * Executes max flow min cost algorithm.  From all the flow distributions that
     * maximize the flow between source and skink, this algorithm chooses the one
     * that minimizes the cost. <br/>
     * The flow assigned to each edge can be obtained with {@link Edge#getFlow() }.
     *
     * @param source Source vertex
     * @param sink Sink vertex
     * @return Maximum flow between source and sink
     */
     public int maxFlowMinCost(Vertex source, Vertex sink) {
        return maxFlowCore(source, sink, true);
    }

    /**
     * Ford–Fulkerson algorithm. Common code to max flow and max flow min cost. The only difference between
     * these two algorithms is that, when the first one needs a path between two
     * vertices of the residual network, it takes the min length path , and the when the second one needs it,
     * it takes the minimum cost path .
     *
     * @param source Source vertex
     * @param sink Sink vertex
     * @param useBellmanFord <code>true</code> to take the minimum cost path when
     * the algorithm needs a path (min cost max flow algorithm) and <code>false</code>
     * to take any path (max flow algorithm)
     * @return Max flow between source and sink
     */
    private int maxFlowCore(Vertex source, Vertex sink, boolean useBellmanFord) {
        List<Edge> path;
        if (useBellmanFord) {
            path = findMinimumCostPathOptimized(source, sink);
        } else {
            path = findPathBreadthFirst(source, sink, null);//findPath(source, sink, new LinkedList<FlowNetwork.Edge>());
        }

        while (!path.isEmpty()) {
            int minResidual = Integer.MAX_VALUE;
            for (Edge edge : path) {
                if (edge.getResidual() < minResidual) {
                    minResidual = edge.getResidual();
                }
            }
            for (Edge edge : path) {
                edge.setFlow(edge.getFlow() + minResidual);
                //int indexv2 = adj.get(edge.v2).indexOf(edge.getEmptyOpposite());
                Edge opposite = adj.get(edge.v2).get(edge.v1);
                opposite.setFlow(opposite.getFlow() - minResidual);
            }
            if (useBellmanFord) {
                path = findMinimumCostPathOptimized(source, sink);
            } else {
                path = findPathBreadthFirst(source, sink, null);//findPath(source, sink, new LinkedList<FlowNetwork.Edge>());
            }
        }
        int maxflow = 0;
        for (Edge edge : adj.get(source).values()) {
            maxflow += edge.getFlow();
        }
        return maxflow;
    }
}
