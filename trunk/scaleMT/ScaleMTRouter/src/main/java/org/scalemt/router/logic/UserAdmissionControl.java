/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vmsanchez
 */
public class UserAdmissionControl {

    private static UserAdmissionControl instance =null;

    public static UserAdmissionControl getInstance()
    {
        if(instance==null)
            instance=new UserAdmissionControl();
        return instance;
    }

    private long limitRegistered=10000000;
    private long limitAnonymous=1000000;
    private List<String> whiteListUsers;
    private List<String> whiteListIPs;
    private boolean limitsEnabled=false;

    public UserAdmissionControl() {

        String strLimits=Util.readConfigurationProperty("user_limit_enabled");
        if("true".equals(strLimits) || "yes".equals(strLimits))
            limitsEnabled=true;

        try
        {
            limitRegistered=Long.parseLong(Util.readConfigurationProperty("user_limit_registered"));
        }
        catch(Exception e){}

        try
        {
            limitAnonymous=Long.parseLong(Util.readConfigurationProperty("user_limit_anonymous"));
        }
        catch(Exception e){}

        whiteListUsers=new ArrayList<String>();
        String users=Util.readConfigurationProperty("user_whitelist");
        if(users!=null)
        {
            String[] pieces = users.split(",");
            for(String piece: pieces)
                whiteListUsers.add(piece);
        }

        whiteListIPs=new ArrayList<String>();
        String ips=Util.readConfigurationProperty("ip_whitelist");
        if(ips!=null)
        {
            String[] pieces = ips.split(",");
            for(String piece: pieces)
                whiteListIPs.add(piece);
        }
    }

    public boolean canTranslate(Requester rq)
    {
        if(!limitsEnabled)
            return true;

        long cost =LoadBalancer.getInstance().getLoadPredictor().getRequestHistory().getCostUser(rq);
        boolean result=true;

        if(rq instanceof RegisteredRequester)
        {
            if ( whiteListUsers.contains(((RegisteredRequester) rq).getEmail()))
                result=true;
            else
                result = (cost<limitRegistered);
        }
        else
        {
            if(whiteListIPs.contains(( (AnonymousRequester) rq ).getIp()))
                result=true;
            else
                result= (cost<limitAnonymous);
        }
        return result;
    }
}
