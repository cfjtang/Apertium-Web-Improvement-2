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

import org.scalemt.router.ws.Constants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Some utility methods that don't fit in another classes.
 *
 * @author vitaka
 */
public class Util {

    /**
     * Commons-logging logger
     */
   static Log logger = LogFactory.getLog(Util.class);

   /**
    * Resource bundle containing <code>configuration.properties</code> properties
    */
    private static ResourceBundle bundle=null;

    /**
     * Formats a JSON output depending on <code>callback</code> and <code>context</code> parameters.<br/>
     * If <code>callback</code> is present, the returned code is a call to a function whose name is
     * the value of <code>callback</code> with one parameter: the JSON output. If <code>context</code> is present,
     * the function receives the follwing parameters:
     * <ol>
     * <li><code>context</code> parameter</li>
     * <li>responseData</li>
     * <li>response status</li>
     * <li>error details</li>
     * </ol>
     *
     * @param jsonOutput JSON output to be formatted
     * @param responseData 'responseData' field of the JSON output
     * @param callback Callback function
     * @param context Additional parameter to callback function
     * @param responseStatus Status code representing the result of procesing the request
     * @param errorDetails Error details if the status code represents an error
     * @return Formatted JSON output
     */
    public static String formatJSONOutput(String jsonOutput,String responseData, String callback, String context, int responseStatus, String errorDetails)
    {
         String responseStr=jsonOutput.toString();
         if (callback != null && !callback.equals("")) {
            if (context != null && !context.equals("")) {
                responseStr = callback + "('" + context + "'," + responseData + "," + responseStatus + "," + Util.writeStr(errorDetails) + ")";

            } else {
                responseStr = callback + "(" + responseStr + ")";
            }
        }

        return responseStr;
    }

    /**
     * Generates a JSON objects with 3 fields:<br/>
     * <ul>
     * <li><code>responseData</code>: Contains the fields of the input map <code>responseData</code></li>
     * <li><code>responseDetails</code>: Contains the value of the input parameter <code>responseDetails</code></li>
     * <li><code>responseStatus</code>: Contains the value of the input parameter <code>responseStatus</code></li>
     * </ul>
     *
     * @param responseData 
     * @param responseStatus
     * @param responseDetails
     * @return A JSONOBjects with the 3 fiels above mentioned
     * @throws org.json.JSONException If there is an error building JSON object
     */
    public static JSONObject generateJSON(Map<String,String> responseData,int responseStatus, String responseDetails ) throws JSONException
    {

       JSONObject jsonObject = new JSONObject();
       jsonObject.put(Constants.JSON_RESPONSEDATA,responseData);
       jsonObject.put(Constants.JSON_RESPONSEDETAILS, responseDetails==null? JSONObject.NULL :responseDetails);
       jsonObject.put(Constants.JSON_RESPONSESTATUS, responseStatus);
       return jsonObject;
      
      
    }

     /**
     * Generates a JSON objects with 3 fields:<br/>
     * <ul>
     * <li><code>responseData</code>: Contains an array with the values of the input parameter <code>responseData</code></li>
     * <li><code>responseDetails</code>: Contains the value of the input parameter <code>responseDetails</code></li>
     * <li><code>responseStatus</code>: Contains the value of the input parameter <code>responseStatus</code></li>
     * </ul>
     *
     * @param responseData
     * @param responseStatus
     * @param responseDetails
     * @return A JSONObjects with the 3 fields above mentioned
     * @throws org.json.JSONException If there is an error building JSON object
     */
    public static JSONObject generateJSONArray(Collection responseData,int responseStatus, String responseDetails ) throws JSONException
    {

       JSONObject jsonObject = new JSONObject();
       jsonObject.put(Constants.JSON_RESPONSEDATA,responseData);
       jsonObject.put(Constants.JSON_RESPONSEDETAILS, responseDetails==null? JSONObject.NULL :responseDetails);
       jsonObject.put(Constants.JSON_RESPONSESTATUS, responseStatus);
       return jsonObject;


    }

    /**
     * Reads a property value from <code>configuration.properties</code> file
     * @param property Name of the property to read
     * @return Property value, or null if the property cannot be found
     */
    public static String readConfigurationProperty(String property)
    {
        if(bundle==null)
        {
            try
            {
            bundle = new PropertyResourceBundle(Util.class.getResourceAsStream("/configuration.properties"));
            }
            catch(Exception e)
            {
                logger.warn("Cannot read configuration file", e);
                return null;
            }
        }

        try
        {
        return bundle.getString(property);
        }
        catch(Exception e)
        {
            logger.warn("Cannot find property "+property, e);
            return null;
        }
        
    }

    /**
     * Reads a property value from a file present in classpath.
     * @param property Name of the property to read
     * @param file File classpath-relative path. The file is looked up using {@link Class#getResourceAsStream(java.lang.String) } method-
     * @return Property value, or <code>null</code> if the file or the property cannot be found
     */
    public static String readPropertyFromFile(String property,String file)
    {
        try
        {
        Properties properties = new Properties();
        properties.load(Util.class.getResourceAsStream(file));
        return properties.getProperty(property);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    /**
     * Reads a comma-separated list of values from a property from a file present in classpath.
     * @param property Name of the property to read
     * @param file File classpath-relative path. The file is looked up using {@link Class#getResourceAsStream(java.lang.String) } method-
     * @return Property values, or <code>null</code> if the file or the property cannot be found
     */
    public static List<String> readCommaSeparatedProperty(String property,String file)
    {
        String propvalue=readPropertyFromFile(property, file);
        String[] values=propvalue.split(",");
        List<String> returnList = new ArrayList<String>();
        for(String value:values)
            returnList.add(value);
        return returnList;
    }

    /**
     * Return a JSON-compatible representation of a <code>String</code>.<br/>
     * It surrounds it with <code>"</code> unless the string is null. In that case,
     * it simply returns the "null" word.
     *
     * @param str
     * @return
     */
    public static String writeStr(String str)
    {
        if(str==null)
            return "null";
        else
            return "\""+str+"\"";
    }

    /**
	 * Sends an email through the default SMTP server.
	 *
	 * @param to To field
	 * @param from From field
	 * @param subject Subject
	 * @param body Message contents
	 * @throws ServerErrorException If there is an unexpected error
	 */
 
	public static void sendEmail(String to,String from,String fromName,String subject,String body) throws Exception
	{

		sendEmail(readConfigurationProperty("mail_smtp_server"), to, from,fromName, subject, body);
	}

	/**
	 * Sends an email through the given SMTP server.
	 *
	 * @param smtpServer SMTP server.
	 * @param to To field
	 * @param from From field
	 * @param subject Subject
	 * @param body Message contents
	 * @throws ServerErrorException If there is an unexpected error
	 */
	private static void sendEmail(String smtpServer, String to, String from,String fromName,
			String subject, String body) throws Exception{
		try {
			Properties props = System.getProperties();
			// -- Attaching to default Session, or we could start a new one --
			props.put("mail.smtp.host", smtpServer);
			Session session = Session.getDefaultInstance(props, null);
			// -- Create a new message --
			Message msg = new MimeMessage(session);
			// -- Set the FROM and TO fields --
			msg.setFrom(new InternetAddress(from,fromName));
                        
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(
					to, false));
			// -- We could include CC recipients too --
			// if (cc != null)
			// msg.setRecipients(Message.RecipientType.CC
			// ,InternetAddress.parse(cc, false));
			// -- Set the subject and body text --
			msg.setSubject(subject);
			msg.setText(body);
			// -- Set some other header information --
			msg.setHeader("X-Mailer", "LOTONtechEmail");
			msg.setSentDate(new Date());
			// -- Send the message --
			Transport.send(msg);
			logger.debug("Message sent OK.");
		} catch (Exception ex) {
			logger.error("Can't send email", ex);
			throw new Exception("Can't send email", ex);
		}
	}

}
