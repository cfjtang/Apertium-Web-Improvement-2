/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.logic;

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

    private int limitRegistered=10000000;
    private int limitAnonymous=1000000;

    public UserAdmissionControl() {
        try
        {
            limitRegistered=Integer.parseInt(Util.readConfigurationProperty("user_limit_registered"));
        }
        catch(Exception e){}

        try
        {
            limitAnonymous=Integer.parseInt(Util.readConfigurationProperty("user_limit_anonymous"));
        }
        catch(Exception e){}
        
    }

    public boolean canTranslate(Requester rq)
    {
        int cost =LoadBalancer.getInstance().getLoadPredictor().getRequestHistory().getCostUser(rq);
        boolean result=true;

        if(rq instanceof RegisteredRequester)
            result = (cost<limitRegistered);
        else
            result= (cost<limitAnonymous);
        return result;
    }

}
