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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.scalemt.router.logic.UserManagement;
import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.scalemt.router.persistence.ExistingNameException;
import org.scalemt.router.persistence.UserEntity;

/**
 * Servlet that allows new user to register to use the web service
 * with higher priority.
 *
 * @author vmsanchez
 */
public class RegisterUserServlet extends HttpServlet {
   
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
       String message=null;

       if(email!=null && !"".equals(email) && email.length()<100 && url!=null && !"".equals(url) && url.length()<100)
       {
            try {
                UserEntity user = UserManagement.getInstance().registerUser(email,url);
                if(user==null)
                   message="Unexpected error. Please try again later or use another name";
               else
                   message="Register OK. Your key is '"+user.getApi()+"'";
            } catch (ExistingNameException ex) {
                message="Error. Your email is already registered.";
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

       if(message!=null)
           request.setAttribute("message",message);
       RequestDispatcher rd = request.getRequestDispatcher("register.jsp");
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
