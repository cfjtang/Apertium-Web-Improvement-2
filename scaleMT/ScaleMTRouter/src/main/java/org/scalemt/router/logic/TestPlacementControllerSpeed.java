/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Tests placement controller speed
 *
 * @author vmsanchez
 */
public class TestPlacementControllerSpeed {

    private PlacementController<Integer,Integer> placementController;
    private int[] serverCpuCapacities;
    private int[] serverMemoryCapacities;
    private int[] appMemoryDemands;

    public TestPlacementControllerSpeed() {
        placementController=new PlacementController<Integer, Integer>();
        serverCpuCapacities= new int[]{40000,27000,30000};
       // serverCpuCapacities= new int[]{1000,1600,2400,3000};
        serverMemoryCapacities= new int[]{1000,500,800,1400};
        //serverMemoryCapacities= new int[]{1000,2000,3000,4000};
        appMemoryDemands=new int[]{64,124,69,28,97};
        // appMemoryDemands=new int[]{400,800,1200,1600};
    }

    public static void main(String[] a) throws Exception {
      new TestPlacementControllerSpeed().test1();
    }

    public void test1() throws Exception
    {
        int numservers=50;
        List<Long> times = new ArrayList<Long>();

        while(numservers<=1500)
        {
            long time=doTest(numservers, 0.5, 40, 0.5, 10);
            times.add(time);
            numservers+=50;
        }

        FileWriter fw = new FileWriter(new File("placementSpeed0.5.txt"));
        Iterator<Long> it = times.iterator();
        numservers=50;
        while(numservers<=1500)
        {
            long time=it.next();
            fw.write(numservers+"\t"+time+"\n");
            numservers+=50;
        }
        fw.close();
    }

    public void test2() throws Exception
    {
        int numservers=50;
        List<Long> times = new ArrayList<Long>();

        while(numservers<=1500)
        {
            long time=doTest(numservers, 0.8, 40, 0.5, 10);
            times.add(time);
            numservers+=50;
        }

        FileWriter fw = new FileWriter(new File("placementSpeed0.8.txt"));
        Iterator<Long> it = times.iterator();
        numservers=50;
        while(numservers<=1500)
        {
            long time=it.next();
            fw.write(numservers+"\t"+time+"\n");
            numservers+=50;
        }
        fw.close();
    }

    public void test3() throws Exception
    {
        int numservers=50;
        List<Long> times = new ArrayList<Long>();

        while(numservers<=1500)
        {
            long time=doTest(numservers, 1, 40, 0.5, 10);
            times.add(time);
            numservers+=50;
        }

        FileWriter fw = new FileWriter(new File("placementSpeed1.txt"));
        Iterator<Long> it = times.iterator();
        numservers=50;
        while(numservers<=1500)
        {
            long time=it.next();
            fw.write(numservers+"\t"+time+"\n");
            numservers+=50;
        }
        fw.close();
    }

     private long doTest(int numServers,  double cpuLoadFactor, int numApps, double demandVariability, int numCycles)
    {

        //Generate random server capacities
        int totalServerCapacity=0;
        int[] serversCpu = new int[numServers];
        for(int i=0; i<numServers;i++)
        {
            int index=Math.min(serverCpuCapacities.length-1,(int) Math.floor(Math.random()*serverCpuCapacities.length));
            serversCpu[i]=serverCpuCapacities[index];
            totalServerCapacity+=serversCpu[i];
        }
        int[] serversMemory = new int[numServers];
        for(int i=0; i<numServers;i++)
        {
            int index=Math.min(serverMemoryCapacities.length-1,(int) Math.floor(Math.random()*serverMemoryCapacities.length));
            serversMemory[i]=serverMemoryCapacities[index];
        }
        
        return doTestCommon(numApps, totalServerCapacity, cpuLoadFactor, numServers, numCycles, serversCpu, serversMemory, demandVariability);
     }

    /**
     * Performs a series of speed tests and shows the results in standard output.
     * Starts with a set of empty servers (i.e. without any runnig daemon) and
     * simulates a series of placement cycles changing the application demands between them.
     *
     * @param numServers Number of servers. Their capacity is uniformly distributed over the set defined by {@link serverCpuCapacities}
     * @param cpuLoadFactor Initial rate between total cpu demand and total cpu capacity
     * @param memoryLoadFactor Rate between average application memory requirement and average server memory capacity.
     * @param demandVariability In each cycle, the cpu demand of each application is changed following this formula: <code>new demand = old demand * (1+/-rand(0,demandVariablity)</code>
     * @param numCycles Number of placement cycles
     */
    private long doTest(int numServers,  double cpuLoadFactor, double memoryLoadFactor, double demandVariability, int numCycles)
    {

        //Generate random server capacities
        int totalServerCapacity=0;
        int[] serversCpu = new int[numServers];
        for(int i=0; i<numServers;i++)
        {
            int index=Math.min(serverCpuCapacities.length-1,(int) Math.floor(Math.random()*serverCpuCapacities.length));
            serversCpu[i]=serverCpuCapacities[index];
            totalServerCapacity+=serversCpu[i];
        }
        int[] serversMemory = new int[numServers];
        for(int i=0; i<numServers;i++)
        {
            int index=Math.min(serverMemoryCapacities.length-1,(int) Math.floor(Math.random()*serverMemoryCapacities.length));
            serversMemory[i]=serverMemoryCapacities[index];
        }

        //Calculate number of applications
        double avgMemDemand=0;
        double avgMemCapacity=0;
        for(int i=0; i<appMemoryDemands.length;i++)
            avgMemDemand+=appMemoryDemands[i];
        for(int i=0; i<serversMemory.length;i++)
            avgMemCapacity+=serversMemory[i];
        avgMemDemand/=appMemoryDemands.length;
        avgMemCapacity/=serversMemory.length;
        int numApps = (int) ((avgMemCapacity / avgMemDemand) * numServers * memoryLoadFactor);

      return doTestCommon(numApps, totalServerCapacity, cpuLoadFactor, numServers, numCycles, serversCpu, serversMemory, demandVariability);
        }
    

    private long doTestCommon(int numApps, int totalServerCapacity, double cpuLoadFactor, int numServers, int numCycles, int[] serversCpu, int[] serversMemory,  double demandVariability) {

        long start,end;
        //generate application demands
        int[] appsCpu = new int[numApps];
        int[] appsMemory = new int[numApps];
        for (int i = 0; i < numApps; i++) {
            int index = Math.min(appMemoryDemands.length - 1, (int) Math.floor(Math.random()*appMemoryDemands.length));
            appsMemory[i] = appMemoryDemands[index];
        }
        double[] appsCpu01 = new double[numApps];
        double total01 = 0;
        for (int i = 0; i < numApps; i++) {
            double rand = Math.random();
            appsCpu01[i] = rand;
            total01 += rand;
        }
        int totalCpuDemand = (int) (totalServerCapacity * cpuLoadFactor);
        for (int i = 0; i < numApps; i++) {
            appsCpu[i] = (int) ((appsCpu01[i] / total01) * totalCpuDemand);
        }
        int[][] R = new int[numApps][numServers];
        for (int i = 0; i < numApps; i++) {
            for (int j = 0; j < numServers; j++) {
                R[i][j] = 1;
            }
        }
        int[][] I = new int[numApps][numServers];
        for (int i = 0; i < numApps; i++) {
            for (int j = 0; j < numServers; j++) {
                I[i][j] = 0;
            }
        }
        List<Integer> appsId = new ArrayList<Integer>();
        List<Integer> serversId = new ArrayList<Integer>();
        for (int i = 0; i < numApps; i++) {
            appsId.add(i);
        }
        for (int i = 0; i < numServers; i++) {
            serversId.add(i);
        }
        int loadDemand;
        int loadSatisfied;
        int placementChanges;
        int[][] L;
        int[][] newI;
        //System.out.println("Running test with " + numServers + " servers and " + numApps + " applications. Server capacity: " + totalServerCapacity);
        List<Long> executionTimes= new LinkedList<Long>();
        for (int i = 0; i < numCycles; i++) {
            loadDemand = 0;
            for (int j = 0; j < appsCpu.length; j++) {
                loadDemand += appsCpu[j];
            }
            placementController.setApps(appsId);
            placementController.setCpuDemand(appsCpu);
            placementController.setMemoryDemand(appsMemory);
            placementController.setServers(serversId);
            placementController.setCpuCapacity(serversCpu);
            placementController.setCpuCapacitiesPerDaemon(serversCpu);
            placementController.setMemoryCapacity(serversMemory);
            placementController.setR(R);
            placementController.setI(I);
            //System.out.println("Starting. Load demand:" + loadDemand);
            start = System.currentTimeMillis();
            placementController.place();
            end = System.currentTimeMillis();
            newI = placementController.getI();
            L = placementController.getOutputL();
            //Calculate satisfied load
            loadSatisfied = 0;
            for (int i2 = 0; i2 < numApps; i2++) {
                for (int j2 = 0; j2 < numServers; j2++) {
                    loadSatisfied += L[i2][j2];
                }
            }
            //Calculate placement changes
            placementChanges = 0;
            for (int i2 = 0; i2 < numApps; i2++) {
                for (int j2 = 0; j2 < numServers; j2++) {
                    placementChanges += Math.abs(I[i2][j2] - newI[i2][j2]);
                }
            }
            executionTimes.add(end-start);
            //System.out.println("Finished. Load satisfied:" + loadSatisfied + " load satisfaction:" + (loadDemand > 0 ? (loadSatisfied / (double) loadDemand) : 0.0) + " placement changes:" + placementChanges + " execution time:" + (end - start));
            //Change apps demand
            double demandChangeFactor;
            for (int j = 0; j < numApps; j++) {
                demandChangeFactor = 1 - demandVariability + Math.random() * demandVariability * 2;
                appsCpu[j] *= Math.max(0, demandChangeFactor);
            }
            I = newI;
        }
        long avgTime=0;
        for(Long time: executionTimes)
           avgTime+=time;
        avgTime/=executionTimes.size();
        System.out.println("Average execution time:" + avgTime);
        return avgTime;
    }
}
