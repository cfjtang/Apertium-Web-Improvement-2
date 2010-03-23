/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.scalemt.router.ws;

import java.io.IOException;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

/**
 *
 * @author vmsanchez
 */
public class MyXmlRpcServlet extends XmlRpcServlet{

    static ThreadLocal clientIpAddress = new ThreadLocal();

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        URL url = XmlRpcServlet.class.getResource("/XmlRpcServlet.properties");
		if (url == null) {
			throw new XmlRpcException("Failed to locate resource XmlRpcServlet.properties");
		}
		try {
			return newPropertyHandlerMapping(url);
		} catch (IOException e) {
			throw new XmlRpcException("Failed to load resource " + url + ": " + e.getMessage(), e);
		}
    }

    @Override
    public void doPost(HttpServletRequest pRequest, HttpServletResponse pResponse) throws IOException, ServletException {
        /*
         *  Log client IP address contained in X-Forwarded-For header,
         * if request passed through a proxy
         */
        String ip=pRequest.getRemoteAddr();
        String apacheProxyHeader=pRequest.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }

        clientIpAddress.set(ip);
        super.doPost(pRequest, pResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         /*
         *  Log client IP address contained in X-Forwarded-For header,
         * if request passed through a proxy
         */
        String ip=req.getRemoteAddr();
        String apacheProxyHeader=req.getHeader("X-Forwarded-For");
        if(apacheProxyHeader!=null)
        {
            String[] ips=apacheProxyHeader.split(",");
            if(ips.length>0)
                if(ips[0].length()>0)
                    ip=ips[0];
        }

        clientIpAddress.set(ip);
        super.doGet(req, resp);
    }

}
