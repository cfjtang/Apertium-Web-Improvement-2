<%-- 
    Document   : register
    Created on : 18-ago-2009, 12:37:25
    Author     : vmsanchez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Apertium Web Service register page</title>
    </head>
    <body>
        <h1>Apertium Web Service register page</h1>
        <p>
        <form action="RegisterUserServlet">
            User name: <input type="text" name="name" />
            <br/>
            <input type="submit" value="Register">
        </form>
        </p>
        <p>
            <%
            if(request.getAttribute("message")!=null)
                out.println((String)request.getAttribute("message"));
            %>
        </p>
    </body>
</html>
