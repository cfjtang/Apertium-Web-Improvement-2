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
package org.scalemt.router.ondemandservers;

import org.scalemt.router.logic.Util;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * {@link IOnDemandServerInterface} implementation that allows starting and stopping Apertium
 * servers in a local network environment.<br/>
 * Instead of creating virtual machines, this implementation simply connects to an existing machine
 * by SSH and starts tha application <code>ApertiumServerSlave</code> in that machine.
 * <br/>
 * The list of available machines in the network is read from <code>LocalNetworkOnDemandServer.properties</code>.
 * The <code>hosts</code> property contains a comma-separated list of machines. Each machine information follows this format:
 * <code>username:password@host:path</code>. hostv is the only compulsory field; username, password and path are optional.
 * If they are not present, its values are taken from the properties <code>defaultUser</code>,<code>defaultPassword</code> and <code>defaultPath</code>.
 *
 * @see SSHHost
 * @author vmsanchez
 */
public class LocalNetworkOnDemandServer extends OnDemandServerInterfaceBase<SSHHost>{


    /**
     * User information for the SSH Connection
     */
    private class MyUserInfo implements UserInfo
    {

        private String password;

        public MyUserInfo(String password) {
            this.password = password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String getPassphrase() {
           return null;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptPassword(String arg0) {
            return true;
        }

        @Override
        public boolean promptPassphrase(String arg0) {
            return false;
        }

        @Override
        public boolean promptYesNo(String arg0) {
            logger.trace("Answered 'yes' to:"+arg0);
            return true;
        }

        @Override
        public void showMessage(String arg0) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

  

    /**
     * Default user name. Connections to server whose entry in the <code>hosts</code> property
     * doesn't have a user name are made with this user name.
     */
    private String defaultUser;

    /**
     * Default password. Connections to server whose entry in the <code>hosts</code> property
     * doesn't have a password are made with this password.
     */
    private String defaultPassword;

    /**
     * Default path. Connections to server whose entry in the <code>hosts</code> property
     * doesn't have a path are made with this path.
     */
    private String defaultPath;

    public LocalNetworkOnDemandServer() {
        super();
        try
        {
            String hostsprop = Util.readPropertyFromFile("hosts", "/LocalNetworkOnDemandServer.properties");
            String[] hosts=hostsprop.split(",");
            for(String strhost: hosts)
            {
                String host=null;
                String user=defaultUser;
                String password =defaultPassword;
                String path=defaultPath;
                String[] userAndHost=strhost.split("@");
                if(userAndHost.length==1 || userAndHost.length==2)
                {
                    int hostPathIndex=0;
                    if(userAndHost.length==2)
                    {
                        hostPathIndex=1;
                         String[] userAndPass = userAndHost[0].split(":");
                        if(userAndPass.length==1 || userAndPass.length==2)
                        {
                            user=userAndPass[0];
                            if(userAndPass.length==2)
                            {
                                password=userAndPass[1];
                            }
                        }
                    }

                    String[] hostAndPath = userAndHost[hostPathIndex].split(":");
                    if(hostAndPath.length==1 || hostAndPath.length==2)
                    {
                        host=hostAndPath[0];
                        if(hostAndPath.length==2)
                        {
                            path=hostAndPath[1];
                        }
                    }
                    availableServers.add(new SSHHost(host, user, password, path));
                }

               
            }
             
        }
        catch(Exception e)
        {
            logger.warn("Cannot read list of hosts for on-demand server management. This feature will be disabled", e);
        }
        defaultUser=Util.readPropertyFromFile("defaultUser", "/LocalNetworkOnDemandServer.properties");
        if(defaultUser==null)
            defaultUser="";
        defaultPassword=Util.readPropertyFromFile("defaultPassword", "/LocalNetworkOnDemandServer.properties");
        if(defaultPassword==null)
            defaultPassword="";
        defaultPath=Util.readPropertyFromFile("defaultPath", "/LocalNetworkOnDemandServer.properties");
        if(defaultPath==null)
            defaultPath="~";        
    }

    @Override
    protected String startServerImpl(SSHHost sshHost) throws OnDemandServerException {
         Session session=null;
         Channel channel=null;
            try
            {
                //logger.trace(sshHost.getUser() +" "+sshHost.getPassword()+" "+sshHost.getHost()+" "+sshHost.getPath());
                JSch jsch=new JSch();
                session=jsch.getSession(sshHost.getUser(),sshHost.getHost(), 22);
                session.setUserInfo(new MyUserInfo(sshHost.getPassword()));
                session.connect();
                String cmd="bash -c \" cd "+sshHost.getPath()+" ; ./run-apertium-server.sh "+sshHost.getHost()+"\" &";
                channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(cmd);
                channel.setInputStream(null);
                ByteArrayOutputStream errorStreamBytes = new ByteArrayOutputStream();
                PrintStream errorStream = new PrintStream(errorStreamBytes,true);
                ((ChannelExec)channel).setErrStream(errorStream);

                InputStream in=channel.getInputStream();
                channel.connect();
                 boolean lineOK=false;
                 StringBuilder builder=new StringBuilder();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                 String line;
                 if((line=reader.readLine())!=null)
                  {
                        if(!line.contains("INFO"))
                        {
                            logger.error("Unexpected remote command output: "+line);
                        }
                        else
                            lineOK=true;
                  }else
                  {
                      logger.error("No response from rmeote command");
                  }

                reader.close();


                if(!lineOK)
                {
                    throw new OnDemandServerException();
                }
                else
                    return sshHost.getHost();


            }
            catch(Exception e)
            {
                logger.error("cannot start remote server", e);
                throw new OnDemandServerException(e);
            }
             finally
             {
                try{channel.disconnect(); }catch(Exception e){}
                try{session.disconnect(); }catch(Exception e){}
             }
    }

    @Override
    protected void stopServerImpl(SSHHost sshHost) throws OnDemandServerException {
       Session session=null;
             Channel channel=null;
             try
            {
                JSch jsch=new JSch();
                session=jsch.getSession(sshHost.getUser(),sshHost.getHost(), 22);
                session.setUserInfo(new MyUserInfo(sshHost.getPassword()));
                session.connect();
                String cmd="bash -c \" cd "+sshHost.getPath()+" ; ./stop-apertium-server.sh \"";
                channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(cmd);
                channel.setInputStream(null);
                 ByteArrayOutputStream errorStreamBytes = new ByteArrayOutputStream();
                PrintStream errorStream = new PrintStream(errorStreamBytes,true);
                ((ChannelExec)channel).setErrStream(errorStream);
                InputStream in=channel.getInputStream();
                channel.connect();
                 boolean lineOK=false;
                 StringBuilder builder=new StringBuilder();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                 String line;
                 if((line=reader.readLine())!=null)
                  {
                       logger.error("Unexpected remote command output: "+line);

                  }else
                  {
                      lineOK=true;
                  }

                reader.close();


                if(lineOK)
                {

                     //SSHHost server=runningServers.remove(sshHost.getHost());
                     //availableServers.add(server);

                }
                else
                {
                    throw new OnDemandServerException();
                }


            }
            catch(Exception e)
            {
                logger.error("cannot stop remote server", e);
                throw new OnDemandServerException(e);
            }
              finally
             {
                try{channel.disconnect(); }catch(Exception e){}
                try{session.disconnect(); }catch(Exception e){}
             }
    }
}
