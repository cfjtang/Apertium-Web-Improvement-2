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
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.RegionInfo;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;
import java.util.List;


/**
 * Java Bean with a host name and an Amazon EC2 instance ID.
 * @author vitaka
 */
class AmazonHostWrapper implements HasHost
{

    /**
     * Instance public host name
     */
    private String host;

    /**
     * Instance id
     */
    private String instanceId;

    public AmazonHostWrapper(String host) {
        this.host = host;
    }

    public AmazonHostWrapper(String host, String instanceId) {
        this.host = host;
        this.instanceId = instanceId;
    }
    

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }



    @Override
    public String getHost() {
       return host;
    }

    @Override
    public void setHost(String host) {
        this.host=host;
    }  
}

/**
 * {@link IOnDemandServerInterface} implementation that allows starting and stopping Amazon EC2
 * machine instances running Apertium.<br/>
 *
 * EC2 information is read from <code>AmazonOnDemandServer.properties</code>.<br/>
 * <ul>
 * <li><code>amazon_id</code> is the Amazon Web Service Access Key ID</li>
 * <li><code>amazon_key</code> is the Amazon Web Service Secret Access Key</li>
 * <li><code>amazon_image_id</code> is the ID of an AMI that must run ApertiumServerSlave when started</li>
 * <li><code>amazon_security_groups</code> is a comma-separated list of security groups</li>
 * <li><code>amazon_key_name</code> is the new instance associated key pair.</li>
 * <li><code>amazon_region_url</code> is the URL of the region where the new instances will be launched and the AMI will be looked for.</li>
 * <li><code>amazon_avzone</code>is availability zone where the new instances will be launched. It is a good idea to launch the request router and the apertium instance in the same availability zone.</li>
 * </ul>
 * 
 *
 * @author vmsanchez
 */
class AmazonOnDemandServer extends OnDemandServerInterfaceBase<AmazonHostWrapper>{

    private String accessId;
    private String accessKey;
    private String imageId;
    private List<String> securityGroups;
    private String keyName;
    private String regionUrl;
    private String avZone;
    

    public AmazonOnDemandServer() {
        super();
        accessId=Util.readPropertyFromFile("amazon_id", "/AmazonOnDemandServer.properties");
        accessKey=Util.readPropertyFromFile("amazon_key", "/AmazonOnDemandServer.properties");
        imageId=Util.readPropertyFromFile("amazon_image_id", "/AmazonOnDemandServer.properties");
        securityGroups = Util.readCommaSeparatedProperty("amazon_security_groups", "/AmazonOnDemandServer.properties");
        keyName=Util.readPropertyFromFile("amazon_key_name", "/AmazonOnDemandServer.properties");
        for(int i=0; i<super.maxAvailableServers;i++)
            super.availableServers.add(new AmazonHostWrapper(""));
        avZone=Util.readPropertyFromFile("amazon_avzone", "/AmazonOnDemandServer.properties");
        regionUrl=Util.readPropertyFromFile("amazon_region_url", "/AmazonOnDemandServer.properties");
        if(regionUrl==null || "".equals(regionUrl))
            regionUrl=RegionInfo.REGIONURL_EU_WEST;
    }

    @Override
    protected String startServerImpl(AmazonHostWrapper t) throws OnDemandServerException {
         Jec2 ec2 = new Jec2(accessId, accessKey);
         ec2.setRegionUrl(regionUrl);

         try
         {
         LaunchConfiguration lc = new LaunchConfiguration(imageId, 1, 1);
         if(avZone!=null && !"".equals(avZone))
            lc.setAvailabilityZone(avZone);
         lc.setKeyName(keyName);
         lc.setSecurityGroup(securityGroups);
         lc.setUserData(Util.readConfigurationProperty("requestrouter_rmi_host").getBytes());
         ReservationDescription rd =ec2.runInstances(lc);
         
         List<Instance> instances= rd.getInstances();
         if(instances.size()<1)
             throw new NotAvailableServersException();
         Instance instance=instances.get(0);
         t.setHost(instance.getDnsName());
         t.setInstanceId(instance.getInstanceId());
         return t.getHost();
        
         }
         catch(EC2Exception ece2)
         {
                 throw new OnDemandServerException(ece2);
         }
    }

    @Override
    protected void stopServerImpl(AmazonHostWrapper t) throws OnDemandServerException {

         Jec2 ec2 = new Jec2(accessId, accessKey);
         ec2.setRegionUrl(regionUrl);

        try {
             List<TerminatingInstanceDescription> descriptions=ec2.terminateInstances(new String[]{t.getInstanceId()});
             //TODO: Check shutdown state
        } catch (EC2Exception ex) {
             throw new OnDemandServerException(ex);
        }
    }

}
