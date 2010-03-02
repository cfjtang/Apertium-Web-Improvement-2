<%-- 
    Document   : registerOK
    Created on : 22-feb-2010, 12:24:01
    Author     : vmsanchez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>You got your API Key!</title>
    </head>
    <body>
        <div id="result">
        <p>
         <%
            if(request.getAttribute("message")!=null)
                out.println((String)request.getAttribute("message"));
            %>
            </p>
            </div>
            <div id="back">
           <p><a href="http://api.apertium.org">Go back</a></p>
      </div>
            
    </body>
</html>
