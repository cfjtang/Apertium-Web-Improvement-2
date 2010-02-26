/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author vmsanchez
 */
public class LoggerStatiticsWriter {

    static Log logger = LogFactory.getLog(LoggerStatiticsWriter.class);

    private static LoggerStatiticsWriter instance=null;

    public static LoggerStatiticsWriter getInstance()
    {
        if(instance==null)
            instance=new LoggerStatiticsWriter();
        return instance;
    }

    private Map<Long,RequestAccumulatedData> datamap;
    private DateFormat dateFormat;

    public LoggerStatiticsWriter() {
        datamap=new HashMap<Long, RequestAccumulatedData>();
        dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    }

    public void logRequestReceived(String ip,String referer, String key, String langPair,String format)
    {
        RequestAccumulatedData accumulatedData=new RequestAccumulatedData();
        accumulatedData.setStartingHour(new Date());
        accumulatedData.setPair(langPair);
        accumulatedData.setFormat(format);
        accumulatedData.setIp(ip);
        accumulatedData.setReferer(referer);
        datamap.put(Thread.currentThread().getId(), accumulatedData);
    }

    public void logRequestProcessing(String user, int numChars, int cpuCost)
    {
        RequestAccumulatedData data = datamap.get(Thread.currentThread().getId());
        if(data!=null)
        {
            data.setUser(user);
            data.setNumCharacters(numChars);
            data.setCpuCost(cpuCost);
        }
    }
    
    public void logRequestProcessed(String code)
    {
        RequestAccumulatedData data = datamap.get(Thread.currentThread().getId());
        if(data!=null)
        {
            data.setDuration(new Date().getTime()-data.getStartingHour().getTime());
            data.setResultCode(code);
            String separation=" ";
            logger.debug(dateFormat.format(data.getStartingHour()) +separation+data.getDuration()+separation+data.getPair()+separation+data.getFormat()+separation+data.getUser()+separation+data.getIp()+separation+data.getReferer()+separation+data.getResultCode()+separation+data.getNumCharacters()+separation+data.getCpuCost());
            datamap.remove(Thread.currentThread().getId());
        }
    }



    

}
