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
        clientIpAddress.set(pRequest.getRemoteAddr());
        super.doPost(pRequest, pResponse);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        clientIpAddress.set(req.getRemoteAddr());
        super.doGet(req, resp);
    }


    

}
