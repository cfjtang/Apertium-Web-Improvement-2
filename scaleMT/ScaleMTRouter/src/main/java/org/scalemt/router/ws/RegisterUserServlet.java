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
package org.scalemt.router.ws;

import org.scalemt.router.logic.UserManagement;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scalemt.router.logic.Util;
import org.scalemt.router.persistence.ExistingNameException;
import org.scalemt.router.persistence.UserEntity;

/**
 * Servlet that allows new user to register to use the web service
 * with higher priority.
 *
 * @author vmsanchez
 */
public class RegisterUserServlet extends HttpServlet {

    static Log logger = LogFactory.getLog(RegisterUserServlet.class);
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       String email = request.getParameter("email");
       String url = request.getParameter("url");
       String checked=request.getParameter("accept");
       String recaptcha_challenge_field= request.getParameter("recaptcha_challenge_field");
       String recaptcha_response_field=request.getParameter("recaptcha_response_field");
       String message=null;

       boolean registerOK=false;

      ReCaptcha reCaptcha =ReCaptchaFactory.newReCaptcha(Util.readConfigurationProperty("recaptcha_public"),Util.readConfigurationProperty("recaptcha_private") , true);

       if(email!=null && !"".equals(email) && email.length()<100 && url!=null && !"".equals(url) && url.length()<100)
       {
           if(checked!=null && (checked.equals("yes") || checked.equals("on")))
           {
               ReCaptchaResponse reCaptchaResponse= reCaptcha.checkAnswer(request.getRemoteAddr(), recaptcha_challenge_field, recaptcha_response_field);
               if(reCaptchaResponse.isValid())
               {

                try {
                    UserEntity user = UserManagement.getInstance().registerUser(email,url);
                    if(user==null)
                       message="Unexpected error. Please try again later or use another name";
                   else
                   {
                       message="Register OK. Your key is '"+user.getApi()+"'";
                       registerOK=true;
                   }
                } catch (ExistingNameException ex) {
                    UserEntity user=UserManagement.getInstance().checkEmailURL(email, url);
                    if(user==null)
                        message="Error. Your email is already registered.";
                    else
                    {
                        registerOK=true;
                        //Send email
                        try
                        {
                            String content="Your Apertium API key is '"+user.getApi()+"'\nThe Apertium API team";
                            Util.sendEmail(email, Util.readConfigurationProperty("mail_from"),Util.readConfigurationProperty("mail_from_name"), Util.readConfigurationProperty("mail_subject"), content);
                            message="We have sent you an email with your API key";
                        }
                        catch(Exception e)
                        {
                            logger.error("Error sending email", e);
                        }
                    }
                }
                catch(WrongFormatException e)
                {
                    if(e.getWrongfield().equals("email"))
                    {
                         message="Error. Your email is not valid.";
                    }
                    else if(e.getWrongfield().equals("url"))
                    {
                        message="Error. Your url is not valid.";
                    }
                }
               }
               else
               {
                   if("incorrect-captcha-sol".equals(reCaptchaResponse.getErrorMessage()))
                       message="Captcha failed. Please type it again";
                   else
                   {
                       message="Unexpected captcha error";
                       logger.error("Unsexpected captcha error: '"+reCaptchaResponse.getErrorMessage()+"'");
                   }
               }
                   
           }
           else
               message="You must accept the terms and conditions in order to register";

           
       }
       else
           message="You must type your email and the home page of your website or application";

       if(message!=null)
           request.setAttribute("message",message);
       RequestDispatcher rd;
       if(registerOK)
            rd= request.getRequestDispatcher("registerOK.jsp");
       else
            rd= request.getRequestDispatcher("register.jsp");
       rd.forward(request, response);

    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
