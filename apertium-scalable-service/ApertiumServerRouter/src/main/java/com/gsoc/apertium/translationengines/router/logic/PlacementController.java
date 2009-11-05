/*
 *  ApertiumServer. Highly scalable web service implementation for Apertium.
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
package com.gsoc.apertium.translationengines.router.logic;

import com.gsoc.apertium.translationengines.router.logic.FlowNetwork.Edge;
import com.gsoc.apertium.translationengines.router.logic.FlowNetwork.Vertex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * Solves the main problem of this application.<br/>
 * Given a set of applications, identified by instances of <code>T1</code>,  a set of servers
 * identified by instances of <code>T2</code> and a matrix that tells us how many instances
 * of each aplication are running on each server, the problem consists of satisfying the
 * maximum amount of CPU demand doing the minimum amount of application starts and stops in
 * the servers.<br/>
 * Each server has a a total memory capacity, a total CPU capacity and a maximum capacity that can
 * be assigned to an application instance. If an application demand is greater than this maximum
 * capacity, more than one instance of the same application must be started on the server. Each
 * application has a CPU demand and a memory demand. Each application instance consumes its whole memory demand,
 * no matter how many CPU has assigned.
 * <br/>
 * To use this class, follow these steps:
 * <ol>
 * <li>Set applications information: call {@link PlacementController#setApps(java.util.List) },{@link PlacementController#setCpuDemand(int[]) } and {@link PlacementController#setMemoryDemand(int[])  }</li>
 * <li>Set servers information: call {@link PlacementController#setServers(java.util.List) }, {@link PlacementController#setCpuCapacity(int[])  }, {@link PlacementController#setCpuCapacitiesPerDaemon(int[])  } and {@link PlacementController#setMemoryCapacity(int[])  } </li>
 * <li>Set matrix with which applications are running on each server (I) : call {@link PlacementController#setI(int[][]) } </li>
 * <li>Set matrix with which applications can run on each server (R): call {@link PlacementController#setR(int[][]) }</li>
 * <li>Execute placement algorithm: call {@link PlacementController#place() }</li>
 * <li>Get results: load distribution matrix: {@link PlacementController#getOutputL() } and new placement matrix: {@link PlacementController#getOutputI() }.
 * Load distribution matrix contains how many cpu demand of each application is assigned to each server, and placement matrix contains how many instances
 * of each application are running on each server.</li>
 * </ol>
 *
 * @author vmsanchez
 */
public class PlacementController <T1,T2> {

    /**
     * Commons-logging logger
     */
    static Log logger = LogFactory.getLog(PlacementController.class);

    /**
     * Compares the free memory of twi servers
     */
    private class ServerResidualMemoryComparator implements Comparator<ServerInformation>
    {
        public int compare(ServerInformation s1, ServerInformation s2) {
           return s1.getResidualMemory() - s2.getResidualMemory();
        }              
    }

    /**
     * Compares the non satisfied cpu demand of each application
     */
     private class ResidualAppTreeComparator implements Comparator<AppInformation>
    {

        public int compare(AppInformation o1, AppInformation o2) {
           return o2.getResidualCpu()-o1.getResidualCpu();
        }
    }

     /**
      * Compares the load memory ratio of two serves, i.e., cpu capacity / memory capacity.
      */
     private class ServerLoadMemoryRatioComparator implements Comparator<ServerInformation>
     {

        public int compare(ServerInformation o1, ServerInformation o2) {
            return o2.getCpuCapacity()/o2.getMemoryCapacity() - o1.getCpuCapacity()/o1.getMemoryCapacity();
        }

     }

     /**
      * Compares the load-memory ratio of two application instances, i.e., the load
      * assignes to an instance / the cpu demand of the application.
      */
     private class AppLoadMemoryRatioComparator implements Comparator<AppInstance>
     {

         //private int L[][];
         //private int serverPosition;

        public AppLoadMemoryRatioComparator(/*int serverPosition,int[][] L*/) {
            /*
            this.L = L;
            this.serverPosition=serverPosition;
             */
        }
        public int compare(AppInstance o1, AppInstance o2) {
           return  (int) (((double) o1.getLoad() / (double) o1.getAppInformation().getMemoryDemand()) - ((double) o2.getLoad() / (double) o2.getAppInformation().getMemoryDemand()));
        }

     }

     /**
      * Java Bean with information about an application instance.
      */
     private class AppInstance
     {
         /**
          * Application information: mmeory demand, cpu demand, etc.
          */
         private AppInformation appInformation;

          /**
         * For a given server, an application is pinned if it has received load enough
         * not to be stopped.
         */
        private boolean pinned;

         /**
          * Load assigned to this instance
          */
         private int load;

         /**
          * Constructor that receives the values of all the bean fields.
          * @param appInformation Application information: mmeory demand, cpu demand, etc.
          * @param load Load assigned to this instance
          */
        public AppInstance(AppInformation appInformation, int load) {
            this.appInformation = appInformation;
            this.load = load;
            this.pinned=false;
        }

        public AppInformation getAppInformation() {
            return appInformation;
        }

        public int getLoad() {
            return load;
        }

        public void setLoad(int load) {
            this.load = load;
        }

        public boolean isPinned() {
            return pinned;
        }

        public void setPinned(boolean pinned) {
            this.pinned = pinned;
        }



     }

     /**
      * Java Bean containing all the relevant information about an application: name,
      * cpu demand, memory demand, etc.
      */
    private class AppInformation
    {
        /**
         * Application identification
         */
        private T1 app;

        /**
         * CPU demand, in es-ca text characters per second
         */
        private int cpuDemand;

        /**
         * Memory demand, in megabytes
         */
        private int memoryDemand;

        /**
         * Amount of CPU demand not satisfied
         */
        private int residualCpu;


        /**
         * Treshold to be pinned.
         */
        private int pinTreshold;

        /**
         * Position in the application list
         */
        private int position;

        /**
         * Constructor that receives the values of some fields
         * @param app Application identification
         * @param cpuDemand CPU demand, in es-ca text characters per second
         * @param memoryDemand Memory demand, in megabytes
         * @param position Position in the application list
         */
        public AppInformation(T1 app, int cpuDemand, int memoryDemand, int position) {
            this.app = app;
            this.cpuDemand = cpuDemand;
            this.memoryDemand = memoryDemand;
            this.residualCpu=cpuDemand;
            this.position=position;
        }

        /**
         * Calculates the amount of cpu demand not satisfied by a given placement
         * @param serversInfo List of server
         * @param L Load distribution matrix
         */
        public void calculateResidualCPU(List<ServerInformation> serversInfo, int[][] L)
        {
           int residualCpu=this.getCpuDemand();
           for(int j=0;j<serversInfo.size();j++)
           {
               residualCpu-=L[getPosition()][j];
           }
           this.setResidualCpu(residualCpu);
        }
        

        public T1 getApp() {
            return app;
        }

        public void setApp(T1 app) {
            this.app = app;
        }

        public int getCpuDemand() {
            return cpuDemand;
        }

        public void setCpuDemand(int cpuDemand) {
            this.cpuDemand = cpuDemand;
        }

        public int getMemoryDemand() {
            return memoryDemand;
        }

        public void setMemoryDemand(int memoryDemand) {
            this.memoryDemand = memoryDemand;
        }

        public int getResidualCpu() {
            return residualCpu;
        }

        public void setResidualCpu(int residualCpu) {
            this.residualCpu = residualCpu;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getPinTreshold() {
            return pinTreshold;
        }

        public void setPinTreshold(int pinTreshold) {
            this.pinTreshold = pinTreshold;
        }        
    }

   /**
      * Java Bean containing all the relevant information about a server: name,
      * cpu capacity, memory capacity, etc.
      */
    private class ServerInformation
    {
        /**
         * Server identification
         */
        private T2 server;

        /**
         * Cpu capacity in es-ca text characters per second
         */
        private int cpuCapacity;

        /**
         * Memory capacity in megabytes
         */
        private int memoryCapacity;

        /**
         * Amount of memory not assigned to any application instance
         */
        private int residualMemory;

        /**
         * Amount of CPU capacity not assigned to any application instance
         */
        private int residualCPU;

        /**
         * Maximum capacity that can be assigned to an application instance
         */
        private int maxCapacityPerDaemon;

        /**
         * Position in the server list
         */
        private int position;

        /**
         * Constructor that receives the values some fields.
         * @param server Server identification
         * @param cpuCapacity Cpu capacity in es-ca text characters per second
         * @param maxCapacityPerDaemon Maximum capacity that can be assigned to an application instance
         * @param memoryCapacity Amount of memory not assigned to any application instance
         * @param position Position in the server list
         */
        public ServerInformation(T2 server, int cpuCapacity,int maxCapacityPerDaemon, int memoryCapacity, int position) {
            this.server = server;
            this.cpuCapacity = cpuCapacity;
            this.memoryCapacity = memoryCapacity;
            this.position = position;
            this.maxCapacityPerDaemon=maxCapacityPerDaemon;
            this.residualMemory=memoryCapacity;
            this.residualCPU=cpuCapacity;
        }

        private ServerInformation() {
            this.server = null;
            this.cpuCapacity = 0;
            this.memoryCapacity = 0;
            this.position = 0;
            this.residualMemory=memoryCapacity;
            this.residualCPU=cpuCapacity;
        }

      /**
       * Calculates the amount of memory not assigned to any application instance.
       * @param apps List of applications
       * @param I Placement matrix
       */
        private void calculateResidualMemory(List<AppInformation> apps,int[][] I) {
            int residualMemoryTmp =memoryCapacity;
            for(int i=0; i< I.length;i++)
                residualMemoryTmp-=apps.get(i).getMemoryDemand()*I[i][getPosition()];
            
            //TODO: What happens if residual memory < 0?
            residualMemory=residualMemoryTmp;
        }

        /**
         * Calculates the amount of CPU capacity not assigned to any application instance.
         * @param L Load distribution matrix
         */
        private void calculateResidualCPU(int L[][]) {
            int residualCPUTmp =cpuCapacity;
            for(int i=0; i< L.length;i++)
                residualCPUTmp-=L[i][getPosition()];

            //TODO: What happens if residual CPU < 0?
            residualCPU=residualCPUTmp;
        }

        /**
         * Copies server information to anocher <code>ServerInformation</code> object
         * @param backupMachine Objects where the fields are copied to.
         */
         private void copyTo(ServerInformation backupMachine) {
            backupMachine.setServer(getServer());
            backupMachine.setPosition(getPosition());
            backupMachine.setCpuCapacity(getCpuCapacity());
            backupMachine.setMemoryCapacity(getMemoryCapacity());
            backupMachine.setResidualCPU(getResidualCPU());
            backupMachine.setResidualMemory(getResidualMemory());
        }

        public int getCpuCapacity() {
            return cpuCapacity;
        }

        public void setCpuCapacity(int cpuCapacity) {
            this.cpuCapacity = cpuCapacity;
        }

        public int getMaxCapacityPerDaemon() {
            return maxCapacityPerDaemon;
        }

        public void setMaxCapacityPerDaemon(int maxCapacityPerDaemon) {
            this.maxCapacityPerDaemon = maxCapacityPerDaemon;
        }

        

        public int getMemoryCapacity() {
            return memoryCapacity;
        }

        public void setMemoryCapacity(int memoryCapacity) {
            this.memoryCapacity = memoryCapacity;
        }

        public T2 getServer() {
            return server;
        }

        public void setServer(T2 server) {
            this.server = server;
        }

        public int getResidualMemory() {
            return residualMemory;
        }

        public void setResidualMemory(int residualMemory) {
            this.residualMemory = residualMemory;
        }

        public int getResidualCPU() {
            return residualCPU;
        }

        public void setResidualCPU(int residualCPU) {
            this.residualCPU = residualCPU;
        }

         public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    /**
     * Result of invoking {@link PlacementController#placementChanging(boolean, java.util.List, java.util.List, int[][], int[][], int[][]) }.
     * Contains a load distribution matrix, a placement matrix and the number of changes performed.
     */
    private class PlacementChangingResult
    {
        /**
         * New load distribution matrix
         */
        private int[][] L;
        /**
         * New placement matrix
         */
        private int[][] I;

        /**
         * Number of placement changes made
         */
        private int numPlacementChanges;

        public PlacementChangingResult() {
        }

        

        public PlacementChangingResult(int numApps, int numServers) {
            L=new int[numApps][numServers];
            I=new int[numApps][numServers];
        }


        public int[][] getI() {
            return I;
        }

        public void setI(int[][] I) {
            this.I = I;
        }

        public int[][] getL() {
            return L;
        }

        public void setL(int[][] L) {
            this.L = L;
        }

        public int getNumPlacementChanges() {
            return numPlacementChanges;
        }

        public void setNumPlacementChanges(int numPlacementChanges) {
            this.numPlacementChanges = numPlacementChanges;
        }
        
    }


    //Input
    /**
     * List of applications (input parameter)
     */
    private List<T1> apps;

    /**
     * Applications cpu demand (input parameter)
     */
    private int[] cpuDemand;

    /**
     * Applications memory demand (input parameter)
     */
    private int[] memoryDemand;

    /**
     * List of servers (input parameter)
     */
    private List<T2> servers;

    /**
     * Servers cpu capacity (input parameter)
     */
    private int[] cpuCapacity;

    /**
     * Servers maximum load assigned to an instance (input parameter)
     */
    private int[] cpuCapacitiesPerDaemon;

    /**
     * Servers memory capacity (input parameter)
     */
    private int[] memoryCapacity;

    /**
     * Placement matrix (input parameter)
     */
    private int[][] I;

    /**
     * Restriction matrix (input parameter)
     */
    private int[][] R;

    /**
     * Repetitions of the main loop
     */
    private int k=10;

    /**
     * Load distribution matrix
     */
     private int[][] L;

    //Output
    /**
     * Final placement matrix
     */
    private int[][] outputI;

    /**
     * Final load distribution matrix
     */
    private int[][] outputL;


    /**
     * List of applications information, made with all the input parameters related to applications
     */
    private List<AppInformation> appsInfo;

    /**
     * List of servers information, made with all the input parameters related to servers
     */
    private List<ServerInformation> serversInfo;
   


    //FLow network stuff
    /**
     * Graph representing servers and applications
     */
    private  FlowNetwork flowNetwork;

    /**
     * Vertices representing applications
     */
    private List<Vertex> appVertices;
    /**
     * Vertices representing servers
     */
    private List<Vertex> serverVertices;
    /**
     * Source vertex
     */
    private Vertex vSource;
    /**
     * Sink vertex
     */
    private Vertex vsink;
     

    /**
     * Default constructor.
     */
    public PlacementController() {
        flowNetwork = new FlowNetwork();
    appVertices = new LinkedList<Vertex>();
     serverVertices = new LinkedList<Vertex>();
    
    }

    
    //Input getters and setters
   public int[][] getI() {
        return I;
    }

    public void setI(int[][] I) {
        this.I = I;
    }

    public int[][] getR() {
        return R;
    }

    public void setR(int[][] R) {
        this.R = R;
    }

    public List<T1> getApps() {
        return apps;
    }

    public void setApps(List<T1> apps) {
        this.apps = apps;
    }

    public int[] getCpuCapacity() {
        return cpuCapacity;
    }

    public void setCpuCapacity(int[] cpuCapacity) {
        this.cpuCapacity = cpuCapacity;
    }

    public int[] getCpuCapacitiesPerDaemon() {
        return cpuCapacitiesPerDaemon;
    }

    public void setCpuCapacitiesPerDaemon(int[] cpuCapacitiesPerDaemon) {
        this.cpuCapacitiesPerDaemon = cpuCapacitiesPerDaemon;
    }

    public int[] getCpuDemand() {
        return cpuDemand;
    }

    public void setCpuDemand(int[] cpuDemand) {
        this.cpuDemand = cpuDemand;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int[] getMemoryCapacity() {
        return memoryCapacity;
    }

    public void setMemoryCapacity(int[] memoryCapacity) {
        this.memoryCapacity = memoryCapacity;
    }

    public int[] getMemoryDemand() {
        return memoryDemand;
    }

    public void setMemoryDemand(int[] memoryDemand) {
        this.memoryDemand = memoryDemand;
    }

    public List<T2> getServers() {
        return servers;
    }

    public void setServers(List<T2> servers) {
        this.servers = servers;
    }


    /**
     * Builds lists with complete information about servers and applications, by processing the input parameters
     * @param apps List of applications names
     * @param cpuDemand Applications CPU demand
     * @param memoryDemand Applications memory demand
     * @param servers List of server names
     * @param cpuCapacity Servers CPU capacity
     * @param maxCapacityPerDaemom Servers maximum capacity per application instance
     * @param memoryCapacity Servers memory capacity
     * @param I Input placement matrix
     */
     private void processParameters(List<T1> apps,int[] cpuDemand,int[] memoryDemand,List<T2> servers,int[] cpuCapacity,int[] maxCapacityPerDaemom,int[] memoryCapacity,int[][] I) {

         appsInfo = new ArrayList<AppInformation>();
         serversInfo= new ArrayList<ServerInformation>();

         ListIterator<T1> iterator = apps.listIterator();
         while(iterator.hasNext())
         {
            int i =iterator.nextIndex();
            T1 app = iterator.next();
            AppInformation appInformation = new AppInformation(app, cpuDemand[i], memoryDemand[i],appsInfo.size());
            appsInfo.add(appInformation);
         }

         ListIterator<T2> siterator = servers.listIterator();
         while(siterator.hasNext())
         {
            int i =siterator.nextIndex();
            T2 server = siterator.next();
            ServerInformation serverInformation = new ServerInformation(server, cpuCapacity[i],maxCapacityPerDaemom[i], memoryCapacity[i],serversInfo.size());
            
            serversInfo.add(serverInformation);
         }
    }

     /**
      * Calculates the satisfied demand by a given load distribution matrix
      * @param L Load distribution matrix
      * @return Satisfied demand
      */
     private int calculateSatisfiedDemand(int[][] L)
     {
         int satisfiedDemand=0;
         for(int i=0;i<L.length;i++)
             for(int j=0; j<L[i].length;j++)
                 satisfiedDemand+=L[i][j];
         return satisfiedDemand;
     }

     /**
      * Placement controller main method. Tries to  satisfy the maximum amount of CPU demand doing the minimum amount of application starts and stops in
      * the servers. After executing this method, new load distribution matrix and placement matrix can be obtained with
      * {@link PlacementController#getOutputL() } and {@link PlacementController#getOutputI() }.
      */
    public void place()
    {

        processParameters(apps, cpuDemand, memoryDemand, servers, cpuCapacity,cpuCapacitiesPerDaemon, memoryCapacity, I);
        L = new int[appsInfo.size()][serversInfo.size()];
        int bestSolution=0;
        for(int i=0; i<k;i++)
        {
             logger.trace("Start max flow "+i);
            int maxDemand= calcMaxDemandSatisfiedByCurrentPlacement(appsInfo,serversInfo,I,L);
            logger.trace("End max flow "+i);
            if(allDemandsSatisfied(appsInfo, serversInfo, L))
                break;
           logger.trace("Start load shifting "+i);
            loadShifting(appsInfo, serversInfo, I, L);
            logger.trace("End load shifting "+i);
            PlacementChangingResult resultNotPin = placementChanging(false,serversInfo, appsInfo, L, I,R);
            PlacementChangingResult resultPin = placementChanging(true,serversInfo, appsInfo, L, I,R);
            int notPinResult = calculateSatisfiedDemand(resultNotPin.getL());
            int pinResult = calculateSatisfiedDemand(resultPin.getL());
            int localResult=Math.max(notPinResult, pinResult);
            if(localResult<=bestSolution)
                break;
            else
            {
                bestSolution=localResult;
                if(pinResult>notPinResult || (pinResult==notPinResult && resultPin.getNumPlacementChanges()<resultNotPin.getNumPlacementChanges()))
                {
                    L=resultPin.getL();
                    I=resultPin.getI();
                }
                else
                {
                     L=resultNotPin.getL();
                    I=resultNotPin.getI();
                }
            }
        }

        outputL=new int[appsInfo.size()][serversInfo.size()];
        outputI=new int[appsInfo.size()][serversInfo.size()];
        //Copy results to output
        for(int i=0; i<appsInfo.size();i++)
            for(int j=0; j<serversInfo.size();j++)
            {
                outputI[i][j]=I[i][j];
                outputL[i][j]=L[i][j];
            }
        
    }

    /**
     * Stops and starts application instances on each server to satisfy the maximum amount
     * of demand.
     *
     * @param pinApp Whether to ignore pinned applications.
     * @param serversInfo Servers information
     * @param appsInfo Applications information
     * @param paramL Load distribution matrix
     * @param paramI Placement matrix
     * @param paramR Restriction matrix
     * @return New placement matrix, new load distribution matrix and number of placement changes made
     */
     private PlacementChangingResult placementChanging(boolean pinApp, List<ServerInformation> serversInfo ,List<AppInformation> appsInfo,int[][] paramL, int[][] paramI,int[][] paramR) {

        PlacementChangingResult result = new PlacementChangingResult();
        int[][] L = new int[appsInfo.size()][serversInfo.size()];
        int[][] I = new int[appsInfo.size()][serversInfo.size()];
        for(int i=0;i<appsInfo.size();i++)
            for(int j=0;j<serversInfo.size();j++)
            {
                L[i][j]=paramL[i][j];
                I[i][j]=paramI[i][j];
            }
        
        int placementChanges=0;

       //Compute residual cpu demand of all applications and put them in a tree
       //TreeSet<AppInformation> residualAppTree = new TreeSet<AppInformation>(new ResidualAppTreeComparator());
       //AVLTree residualAppTree = new AVLTree(new ResidualAppTreeComparatorCPU());

       List<AppInformation>  residualAppTree = new LinkedList<AppInformation>();

       int minMemory = Integer.MAX_VALUE;
       for(int i=0; i<appsInfo.size();i++)
       {
           AppInformation app=appsInfo.get(i);
           app.calculateResidualCPU(serversInfo,L);
           if(app.getResidualCpu()>0)
           {
               if(app.getMemoryDemand()<minMemory)
                   minMemory=app.getMemoryDemand();
               residualAppTree.add(app);
           }
       }
       Collections.sort(residualAppTree,new ResidualAppTreeComparator());
       

       //Sort underutilized machines by load/memory ratio
       List<ServerInformation> underutilizedMachines = new ArrayList<ServerInformation>();
       for(ServerInformation server: serversInfo)
       {
           server.calculateResidualCPU(L);
           if(server.getResidualCPU()>0)
               underutilizedMachines.add(server);
       }
       Collections.sort(underutilizedMachines, new ServerLoadMemoryRatioComparator());

       //Outer loop
       ListIterator<ServerInformation> underutilizedMachinesIterator = underutilizedMachines.listIterator();
       while(underutilizedMachinesIterator.hasNext())
       {
           ServerInformation machine = underutilizedMachinesIterator.next();

            //Sort applications running in this machine by load/memory ratio
            //List<AppInformation> appsRunning = new ArrayList<AppInformation>();
            List<AppInstance> appsRunning = new ArrayList<AppInstance>();
            for(int i=0; i<appsInfo.size();i++)
            {
                AppInformation app = appsInfo.get(i);
               
                int remainingLoad=L[i][machine.getPosition()];
                    for(int j=0;j<I[i][machine.getPosition()];j++)
                    {
                        AppInstance instance=null;
                        if(remainingLoad>=machine.getMaxCapacityPerDaemon())
                        {
                            instance=new AppInstance(app, machine.getMaxCapacityPerDaemon());
                            remainingLoad-=machine.getMaxCapacityPerDaemon();
                        }
                        else if(remainingLoad>0)
                        {
                            instance=new AppInstance(app, remainingLoad);
                            remainingLoad=0;
                        }

                        if(instance!=null)
                        {
                            if(pinApp)
                            {
                                 if(instance.getLoad() >app.getPinTreshold())
                                instance.setPinned(true);
                            }

                            if(!instance.isPinned())
                                appsRunning.add(instance);
                        }
                            
                    }
               
            }
            Collections.sort(appsRunning, new AppLoadMemoryRatioComparator());

            //Backup server column
            int[] LserverColumn = new int[appsInfo.size()];
            int[] IserverColumn = new int[appsInfo.size()];

            //Best Solution
            int[] LbestSolutionColumn = new int[appsInfo.size()];
            int[] IbestSolutionColumn = new int[appsInfo.size()];

            int bestSolutionPlacementChanges=0;
            for(int i=0;i<LbestSolutionColumn.length;i++)
            {
                LbestSolutionColumn[i]=0;
                IbestSolutionColumn[i]=0;
            }
            int maxCPUBestSolution=0;
            //List<AppInformation> bestSolutionApps=new ArrayList<AppInformation>();

            //Intermediate loop
           for(int i=0; i<=appsRunning.size();i++)
           {
               int localPlacementChanges=0;
                //Backup server column
               for(int j=0; j<appsInfo.size();j++)
               {
                    LserverColumn[j] = L[j][machine.getPosition()];
                    IserverColumn[j] = I[j][machine.getPosition()];
               }

               //Backup machine and apps
               ServerInformation backupMachine = new ServerInformation();
               machine.copyTo(backupMachine);
               List<AppInformation> modifiedApps = new LinkedList<AppInformation>();

               //Stop M1,M2...Mi apps
               for(int j=1;j<=i;j++)
               {
                   AppInstance app = appsRunning.get(j-1);
                   //machine.getApps().remove(app);
                   L[app.getAppInformation().getPosition()][machine.getPosition()]-=app.getLoad();
                   I[app.getAppInformation().getPosition()][machine.getPosition()]-=1;
                   localPlacementChanges++;

                   //¿Update residual app tree?
                   //residualAppTree.removeElement(app);
                   app.getAppInformation().calculateResidualCPU(serversInfo,L);
                   //residualAppTree.addElement(app);
                   modifiedApps.add(app.getAppInformation());
               }
               machine.calculateResidualCPU(L);
               machine.calculateResidualMemory(appsInfo, I);
              Collections.sort(residualAppTree,new ResidualAppTreeComparator());

               //Inner loop
               Iterator<AppInformation> residualAppTreeEnum =residualAppTree.iterator();
               while(residualAppTreeEnum.hasNext() && machine.getResidualCPU()>0 && machine.getResidualMemory()>=minMemory )
               {
                   AppInformation app = (AppInformation) residualAppTreeEnum.next();
                   if(app.getMemoryDemand()<=machine.getResidualMemory() && paramR[app.getPosition()][machine.getPosition()]==1)
                   {
                       
                       //El tema
                       do
                       {
                           //arrancar nueva instancia
                           
                           //¿hace falta el if?
                           //TODO: ¿Que pasa cuando hay demonios sin carga?
                           //if(I[app.getPosition()][machine.getPosition()]!=1)
                           //{
                               localPlacementChanges++;
                           //}
                           machine.setResidualMemory(machine.getResidualMemory()-app.getMemoryDemand());
                           int availableCPU=Math.min(machine.getResidualCPU(), machine.getMaxCapacityPerDaemon());
                           if(availableCPU<app.getResidualCpu())
                           {
                               L[app.getPosition()][machine.getPosition()]+=availableCPU;
                               I[app.getPosition()][machine.getPosition()]+=1;
                               app.setResidualCpu(app.getResidualCpu()-availableCPU);
                               machine.setResidualCPU(machine.getResidualCPU()-availableCPU);
                           }
                           else
                           {
                               L[app.getPosition()][machine.getPosition()]+=app.getResidualCpu();
                               I[app.getPosition()][machine.getPosition()]+=1;
                               machine.setResidualCPU(machine.getResidualCPU()-app.getResidualCpu());
                               app.setResidualCpu(0);
                           }
                       }
                       while(machine.getResidualCPU()>0 && machine.getResidualMemory()>=app.getMemoryDemand() && app.getResidualCpu()>0);
  
                       modifiedApps.add(app);
                       //fin el tema

                   }
               }

               //Is this solution the best?
               int cpuSatisfied = 0;
               for(int k=0;k<appsInfo.size();k++)
                   cpuSatisfied+=L[k][machine.getPosition()];
               if(cpuSatisfied>maxCPUBestSolution)
               {
                   maxCPUBestSolution=cpuSatisfied;
                   for(int k=0;k<appsInfo.size();k++)
                   {
                       LbestSolutionColumn[k]=L[k][machine.getPosition()];
                       IbestSolutionColumn[k]=I[k][machine.getPosition()];
                   }
                   bestSolutionPlacementChanges=localPlacementChanges;
               }

               //Restore L and I
               for(int j=0; j<appsInfo.size();j++)
               {
                    L[j][machine.getPosition()]=LserverColumn[j];
                    I[j][machine.getPosition()]=IserverColumn[j];
               }

                //Restore objects
               backupMachine.copyTo(machine);
               for(AppInformation app:modifiedApps)
               {                  
                   app.calculateResidualCPU(serversInfo, L);
               }
               
           }

           //Assign the best solution to L and I
           for(int i=0; i<appsInfo.size();i++)
           {
               L[i][machine.getPosition()]=LbestSolutionColumn[i];
               I[i][machine.getPosition()]=IbestSolutionColumn[i];
           }

            //Update machine
            machine.calculateResidualCPU(L);
            machine.calculateResidualMemory(appsInfo, I);


            //Update residual tree
            for(AppInformation app: appsInfo)
                app.calculateResidualCPU(serversInfo, L);
            Collections.sort(residualAppTree,new ResidualAppTreeComparator());
            Iterator<AppInformation> it=((LinkedList)residualAppTree).descendingIterator();
            
            boolean computeMinMemoryAgain=false;
            while(it.hasNext())
            {
                AppInformation app = it.next();
                if(app.getResidualCpu()>0)
                    break;
                else
                {
                    it.remove();
                    if(app.getMemoryDemand()==minMemory)
                    computeMinMemoryAgain=true;
                }
            }
             //Compute new value of minMemory
            if(computeMinMemoryAgain)
            {
                    minMemory=Integer.MAX_VALUE;
                    Iterator<AppInformation> it2=residualAppTree.iterator();
                    while(it2.hasNext())
                    {
                        AppInformation app=it2.next();
                        if(app.getMemoryDemand()<minMemory)
                            minMemory=app.getMemoryDemand();
                    }
            }

            placementChanges+=bestSolutionPlacementChanges;
            
       }

       //Set I values
       /*
          for(int i=0;i<appsInfo.size();i++)
            for(int j=0;j<serversInfo.size();j++)
            {
                I[i][j]=L[i][j]>0 ? 1 : 0;
            }
        */

       if(!pinApp)
       {
            //Dry run: Compute pinning tresholds
           int maxResidualCPU=0;
           for(AppInformation app: residualAppTree)
           {
               if(app.getResidualCpu()>maxResidualCPU)
                   maxResidualCPU=app.getResidualCpu();
           }
           Iterator<AppInformation> appIterator = appsInfo.iterator();
           while(appIterator.hasNext())
           {
                AppInformation app = appIterator.next();
                int minLoad=Integer.MAX_VALUE;
                int i=app.getPosition();
                for(int j=0;j<serversInfo.size();j++)
                {
                    if(I[i][j]>0 && paramI[i][j]==0 && L[i][j]<minLoad)
                        minLoad=L[i][j];
                }
                app.setPinTreshold(Math.max(1, Math.min(maxResidualCPU, minLoad)));
           }

       }

       result.setNumPlacementChanges(placementChanges);
       result.setL(L);
       result.setI(I);
       return result;


    }


     /**
      * Computes a load distribution matrix compatible with the existing placement that:
      * <ul>
      * <li>Each application has at most one underutilzed instance in the entire system</li>
      * <li>If application m has one underutilized instance running on machine n, then:
      * <ul>
      * <li>application m's iddle instances must run on machines whose residual memory is
      * larger than or equal to that of machine n</li>
      * <li>application m's fully utilized instances must run on machines whose residual memory
      * is smaller than or equal to that of machine n</li>
      * </ul>
      * </li>
      * </ul>
      * @param appsInfo Applications information
      * @param serversInfo Servers information
      * @param I PLacement matrix
      * @param L Load distribution matrix (modified by this method)
      */
    private void loadShifting(List<AppInformation> appsInfo,List<ServerInformation> serversInfo, int[][] I,int[][] L) {
        flowNetwork.resetFlows();

        //Sort servers by residual memory
        for(ServerInformation serverInformation: serversInfo)
            serverInformation.calculateResidualMemory(appsInfo, I);
        List<ServerInformation> orderedServerInformation = new ArrayList<ServerInformation>(serversInfo);
        Collections.sort(orderedServerInformation, new ServerResidualMemoryComparator());

        //Set costs
        for(int i=0; i<orderedServerInformation.size();i++)
        {
            Vertex serverVertex =serverVertices.get(orderedServerInformation.get(i).getPosition());
            List<Edge> edges = flowNetwork.getEdges(serverVertex);
            for(Edge edge: edges)
            {
                if(edge.getV2().getId().equals("sink"))
                    edge.setCost(i);
            }
        }

        //Execute algorithm
        flowNetwork.maxFlowMinCost(vSource, vsink);

        //get results
         for(int i=0; i<appVertices.size();i++)
         {
             for(int j=0; j<serverVertices.size();j++)
                 L[i][j]=0;
             List<Edge> edges = flowNetwork.getEdges(appVertices.get(i));
             for(Edge edge: edges)
             {
                try
                {
                int numserver = Integer.parseInt(edge.getV2().getId());
                L[i][numserver]=edge.getFlow();
                }
                catch(NumberFormatException e){}
             }
         }
    }

    /**
     * Checks if all the application demands are satisfied by the given load distribution matrix
     * @param appsInfo Applications information
     * @param serversInfo Servers information
     * @param L Load distribution matrix
     * @return <code>true</code> if all the application demands are satisfied, <code>false</code> otherwise
     */
    private boolean allDemandsSatisfied(List<AppInformation> appsInfo, List<ServerInformation> serversInfo , int[][] L)
    {
        boolean satisfied=true;
        for(int i=0; i<appsInfo.size() && satisfied;i++)
        {
            int demand = appsInfo.get(i).getCpuDemand();
            int capacity=0;
            for(int j=0; j<serversInfo.size();j++)
                capacity+=L[i][j];
            if(capacity<demand)
                satisfied=false;
        }
        return satisfied;
    }

    /**
     * Calculates the maximum demand satisfied by the given placement matrix, using max flow algorithm.
     * 
     * @param appsInfo Applications information
     * @param serversInfo Servers information
     * @param I Placement matrix
     * @param L Load distribution matrix ( modified by this method)
     * @return Maximum CPU demand satisfied by the given placement matrix
     */
     private int calcMaxDemandSatisfiedByCurrentPlacement(List<AppInformation> appsInfo,List<ServerInformation> serversInfo , int[][] I,int[][] L) {

         flowNetwork.clear();
         vSource = flowNetwork.addVertex("source");
        vsink = flowNetwork.addVertex("sink");

         //Build graph
         ListIterator<AppInformation> iterator = appsInfo.listIterator();
         appVertices.clear();
         while(iterator.hasNext())
         {
            int i =iterator.nextIndex();
            AppInformation app = iterator.next();
            Vertex pairVertex = flowNetwork.addVertex("app"+app.getApp().toString());
            flowNetwork.addEdge(vSource,pairVertex, app.getCpuDemand());
            appVertices.add(pairVertex);
         }

         serverVertices.clear();
         ListIterator<ServerInformation> siterator = serversInfo.listIterator();
         while(siterator.hasNext())
         {
             int i = siterator.nextIndex();
             ServerInformation server = siterator.next();
             
             Vertex serverVertex = flowNetwork.addVertex(Integer.toString(i));
             serverVertices.add(serverVertex);
             for(int j=0; j< appsInfo.size();j++)
             {
                AppInformation appInformation = appsInfo.get(j);
                if(I[j][i]>=1)
                    flowNetwork.addEdge(appVertices.get(j), serverVertex, I[j][i]*server.getMaxCapacityPerDaemon());
             }
             flowNetwork.addEdge(serverVertex, vsink, server.getCpuCapacity());
         }


         //Execute max flow
         int maxCapacity = flowNetwork.maxFlow(vSource, vsink);


         //get results
         for(int i=0; i<appVertices.size();i++)
         {
             for(int j=0; j<serverVertices.size();j++)
                 L[i][j]=0;
             List<Edge> edges = flowNetwork.getEdges(appVertices.get(i));
             for(Edge edge: edges)
             {
                try
                {
                int numserver = Integer.parseInt(edge.getV2().getId());
                L[i][numserver]=edge.getFlow();
                }
                catch(NumberFormatException e){}
             }
         }

         return maxCapacity;
    }



    //Output getters
   public int[][] getOutputI() {
        return outputI;
    }

    public void setOutputI(int[][] outputI) {
        this.outputI = outputI;
    }

    public int[][] getOutputL() {
        return outputL;
    }

    public void setOutputL(int[][] outputL) {
        this.outputL = outputL;
    }





}
