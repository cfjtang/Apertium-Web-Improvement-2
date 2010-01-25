package com.gsoc.apertium.translationengines.router.logic;



import org.scalemt.router.logic.PlacementController;
import java.util.ArrayList;
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
public class TestPlacement {

    @Test
    public void test2servers()
    {
         PlacementController<String,String> placementController = new PlacementController<String, String>();
        List<String> apps = new ArrayList<String>();
        apps.add("app0");
        apps.add("app1");
        apps.add("app2");
        apps.add("app3");
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        cpuDemand[0]=0;
        cpuDemand[1]=0;
        cpuDemand[2]=2000;
        cpuDemand[3]=0;
        memoryDemand[0]=100;
        memoryDemand[1]=100;
        memoryDemand[2]=100;
        memoryDemand[3]=100;

        List<String> servers = new ArrayList<String>();
        servers.add("s0");
        servers.add("s1");
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=7000;
        cpuCapacity[1]=6000;
        memoryCapacity[0]=500;
        memoryCapacity[1]=500;

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }
        assertTrue(1== newI[2][0]);
        assertTrue(0== newI[2][1]);
    }
    @Test
   public void testI()
    {
         PlacementController<String,String> placementController = new PlacementController<String, String>();
          List<String> apps = new ArrayList<String>();
          apps.add("app0");
          apps.add("app1");
          apps.add("app2");
          apps.add("app3");
          apps.add("app4");
          int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        cpuDemand[0]=1;
        cpuDemand[1]=0;
        cpuDemand[2]=0;
        cpuDemand[3]=0;
        cpuDemand[4]=0;

        memoryDemand[0]=100;
        memoryDemand[1]=100;
        memoryDemand[2]=100;
        memoryDemand[3]=100;
        memoryDemand[4]=100;

          List<String> servers = new ArrayList<String>();
        servers.add("s0");
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=30000;
        memoryCapacity[0]=600;

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }
         assertTrue(1==newI[0][0]);
         for(int i=1;i<apps.size();i++)
           assertTrue(0==newI[i][0]);
    }
    
    @Test
    public void test1()
    {
         PlacementController<String,String> placementController = new PlacementController<String, String>();
        List<String> apps = new ArrayList<String>();
        apps.add("app0");
        apps.add("app1");
        apps.add("app2");
        apps.add("app3");
        apps.add("app4");
        apps.add("app5");
        apps.add("app6");
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        cpuDemand[0]=10000;
        cpuDemand[1]=5000;
        cpuDemand[2]=5000;
        cpuDemand[3]=3500;
        cpuDemand[4]=2000;
        cpuDemand[5]=500;
        cpuDemand[6]=1300;
        memoryDemand[0]=100;
        memoryDemand[1]=100;
        memoryDemand[2]=100;
        memoryDemand[3]=100;
        memoryDemand[4]=100;
        memoryDemand[5]=100;
        memoryDemand[6]=100;

        List<String> servers = new ArrayList<String>();
        servers.add("s0");
        servers.add("s1");
        servers.add("s2");
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=7000;
        cpuCapacity[1]=6000;
        cpuCapacity[2]=5900;
        memoryCapacity[0]=500;
        memoryCapacity[1]=500;
        memoryCapacity[2]=500;

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }
        
    }

    @Test
    public void test2()
    {
        PlacementController<String,String> placementController = new PlacementController<String, String>();
        List<String> apps = new ArrayList<String>();
        apps.add("app0");
        apps.add("app1");
        apps.add("app2");
        apps.add("app3");
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        cpuDemand[0]=1000;
        cpuDemand[1]=5000;
        cpuDemand[2]=5000;
        cpuDemand[3]=3500;
        memoryDemand[0]=100;
        memoryDemand[1]=100;
        memoryDemand[2]=100;
        memoryDemand[3]=100;

        List<String> servers = new ArrayList<String>();
        servers.add("s0");
        servers.add("s1");
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=7000;
        cpuCapacity[1]=6000;
        memoryCapacity[0]=500;
        memoryCapacity[1]=500;

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }

    }

    /*
    @Test
   public void test3()
    {
        PlacementController<String,String> placementController = new PlacementController<String, String>();
        List<String> apps = new ArrayList<String>();
        apps.add("app0");
        apps.add("app1");
        apps.add("app2");
        apps.add("app3");
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        cpuDemand[0]=0;
        cpuDemand[1]=100;
        cpuDemand[2]=1;
        cpuDemand[3]=1;
        memoryDemand[0]=100;
        memoryDemand[1]=100;
        memoryDemand[2]=100;
        memoryDemand[3]=100;

        List<String> servers = new ArrayList<String>();
        servers.add("s0");
        servers.add("s1");
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=4000;
        cpuCapacity[1]=6000;
        memoryCapacity[0]=250;
        memoryCapacity[1]=500;

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

         I[2][0]=1;
         I[3][0]=1;

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }

        assertTrue(1==newI[1][1]);
    }
*/

   @Test
   public void testApertium()
    {
       System.out.println("Test Apertium");
        PlacementController<String,String> placementController = new PlacementController<String, String>();
        List<String> apps = new ArrayList<String>();
        for(int i =1; i<= 41;i++)
            apps.add("app"+i);
        
        int[] cpuDemand = new int[apps.size()];
        int[] memoryDemand = new int[apps.size()];
        for(int i =0; i< cpuDemand.length;i++)
            cpuDemand[i]=0;
        cpuDemand[12]=73;

        for(int i =0; i< memoryDemand.length;i++)
            memoryDemand[i]=100;

        List<String> servers = new ArrayList<String>();
        servers.add("s0");
        
        int[] cpuCapacity = new int[servers.size()];
        int[] memoryCapacity = new int[servers.size()];
        cpuCapacity[0]=27016;
        memoryCapacity[0]=347;
        

        int[][] R = new int[apps.size()][servers.size()];
        int[][] I = new int[apps.size()][servers.size()];
        for(int i=0; i<apps.size();i++)
            for(int j=0; j<servers.size();j++)
            {
                R[i][j]=1;
                I[i][j]=0;
            }

         I[7][0]=1;
         I[14][0]=1;
         I[30][0]=1;

        placementController.setApps(apps);
        placementController.setCpuDemand(cpuDemand);
        placementController.setMemoryDemand(memoryDemand);
        placementController.setServers(servers);
        placementController.setCpuCapacity(cpuCapacity);
        placementController.setCpuCapacitiesPerDaemon(cpuCapacity);
        placementController.setMemoryCapacity(memoryCapacity);
        placementController.setK(10);
        placementController.setR(R);
        placementController.setI(I);

        placementController.place();

        int[][] newI = placementController.getOutputI();
        int[][] newL = placementController.getOutputL();
         for(int i=0; i<apps.size();i++)
         {
            for(int j=0; j<servers.size();j++)
            {
                System.out.print(newL[i][j]+"("+newI[i][j]+") ");
            }
            System.out.println();
         }

        assertTrue(1==newI[12][0]);
    }
}
