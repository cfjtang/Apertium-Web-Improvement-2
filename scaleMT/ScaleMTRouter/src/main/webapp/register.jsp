<%-- 
    Document   : register
    Created on : 18-ago-2009, 12:37:25
    Author     : vmsanchez
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="org.scalemt.router.logic.Util" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Apertium Web Service register page</title>
    </head>
    <body onload="<%
            if(request.getAttribute("message")!=null)
                out.println("alert('"+(String)request.getAttribute("message")+"');");
            %>" style="font-family:helvetica;margin-left:2em;">
        <h1>Apertium Web Service register page</h1>
        <div id="termsmain" style="">
        <p>Here you can get an API key for using the <a href="http://wiki.apertium.org/wiki/Apertium_web_service">Apertium API</a>.
Without a key, API usage is limited to a small amount of requests per IP;
an API key allows for more generous traffic limits. You must accept the Terms of Service below before obtaining your key. Please, note that
the service is provided "as is" without warranty of any kind, and remember that you can also
download the <a href="http://wiki.apertium.org/wiki/ScaleMT#Downloading">source code</a> of the Apertium API services and install them in your own server.</p>

<div id="termsbox" style="border:2px solid #000066;margin-left:0px;margin-top: 1em;overflow:auto;height:200px;width:90%;padding-left: 20px;padding-right: 10px;">
    <h2>Terms of Service</h2>

<h3>Introduction:</h3>

<p>The Apertium API is operated by the <a href="http://www.apertium.org">Apertium</a> Project
and the <a href="http://transducens.dlsi.ua.es">Transducens</a> Research Group at <a href="http://www.ua.es">Universitat d'Alacant</a>.
To use it, register with us to obtain an API Key. Please make sure you read through, understand and agree
with the terms of this agreement before you start to use the Apertium API.</p>

<p>[These Terms of Service are derived from <a href="http://akismet.com/tos">Akismet.com
TOS</a>, available under a <a href="http://creativecommons.org/licenses/by-sa/2.5/">Creative
Commons Sharealike license</a>.]</p>


<h3>Terms of Service:</h3>

<p>The <a href="http://api.apertium.org">Apertium</a> web service ("Service") is a machine translation Application
Programming Interface ("API") operated by the Apertium Project and the Transducens Research Group at Universitat d'Alacant ("Providers").
Any use of the Service is subject to the following Terms and Conditions of Use ("Terms and Conditions"). When you use your Apertium API Key ("API Key")
and the Service, you agree that you have read, understood, and agree to be bound by the these Terms and Conditions.
</p>

<ol>
<li><p><b>General.</b> To obtain an API Key you must first register with the website.
After obtaining an API Key, you will be able to use the Service. Requests not including an API key are strictly limited to a small amount per IP;
the API key allows for more generous traffic limits.
The Providers may in its sole discretion change, modify, suspend, make improvements to or discontinue
any aspect of Service, temporarily or permanently, at any time and without notice to you. Under no circumstances will the
Providers be liable for any such change, modification, suspension,
improvement or discontinuance.  If you do not agree with any of these changes, you may terminate your account.</p></li>

<li><p><b>Registration.</b> By registering, you represent and warrant that the information you provide in connection with any registration
process is true and accurate, and that you will promptly notify the Providers if any of that information changes.
The Providers may use the information that you provide during the registration process, in particular your email address,
to communicate with you about the Service. The Providers reserve the right to terminate your access to
the Service if you provide false or inaccurate information.</p>

<p>If you obtain an API Key, you are responsible for maintaining the security of your API Key, and you are fully responsible for all activities
that occur under the account and any other actions taken in connection with your API Key. You must immediately notify the Providers
of any unauthorized uses of your API Key, your account or any other breaches of security. The Providers
will not be liable for any acts or omissions by you, including any damages of any kind incurred as a result of such acts or omissions.</p></li>

<li><p><b>Responsibility of API usage.</b> The Providers have not reviewed, and cannot review, all of the material you sent
through the API, and cannot therefore be responsible for that material's content, use or effects.</p></li>

<li><p><b>Changes.</b> The Service and these Terms and Conditions may be changed at the sole discretion of the Providers and without notice.
You are bound by any such updates or changes, including but not limited to those affecting these Terms and Conditions, and so should periodically
review these Terms and Conditions.</p></li>

<li><p><b>Limitation of warranties.</b> Except as otherwise expressly stated, the Service is provided "as is", and the
Providers make no representations or warranties, express or implied, including but not limited to warranties of merchantability,
fitness for a particular purpose, title or non-infringement of proprietary rights. The Providers
make no representations and warranties regarding uptime for the Service and the accuracy of the Service. You understand and agree that you
obtain the service at your own discretion and risk, and that the Providers will have no liability or responsibility for any damage to you
that results from the use of such services.</p></li>

<li><p><b>Limitation of liability.</b> Except as otherwise expressly stated, in no event will the Providers
be liable to you or any other party for any direct, indirect, special, consequential or exemplary damages, regardless of the basis or nature
of the claim, resulting from any use of the Service including without limitation any lost profits, business interruption, loss of data or otherwise, even
if the Providers were expressly advised of the possibility of such damages.</p></li>

<li><p><b>Termination.</b> The Providers may terminate this Agreement, your rights under this Agreement, and your access to and use of the
Service in its sole discretion, for any reason or no reason at all, with or without cause and without notice or liability to you or any third party.
Any termination of these Terms and Conditions automatically terminates the license to use the Service and to use your API Key.
You can also terminate this Agreement at any time by ceasing to use the API.</p></li>
</ol> 
</div>
</div>
        <div id="register">

        <h3>Register</h3>
        <form action="RegisterUserServlet" method="POST" style="left: auto;right:auto;">
            <table cellpadding="5px" style="margin:5px;">
                <tbody>
                    <tr><td>Your email:</td><td> <input type="text" name="email"  /></td></tr>
                    <tr><td>URL address of your web application*: </td><td><input type="text" name="url" value="http://" /></td>
                    <tr><td colspan="2"><input type="checkbox" name="accept"/> I accept the service terms and conditions</td></tr>
            
             <tr><td colspan="2"><script type="text/javascript"
               src="http://api.recaptcha.net/challenge?k=<%= Util.readConfigurationProperty("recaptcha_public") %>">
            </script>

            <noscript>
               <iframe src="http://api.recaptcha.net/noscript?k=<%= Util.readConfigurationProperty("recaptcha_public") %>"
                   height="300" width="500" frameborder="0"></iframe><br>
               <textarea name="recaptcha_challenge_field" rows="3" cols="40">
               </textarea>
               <input type="hidden" name="recaptcha_response_field"
                   value="manual_challenge">
            </noscript></td></tr>
              
             <tr><td colspan="2"><input type="submit" value="Register"></td></tr>
              </tbody>
            </table>
            <p>* If you are going to use the API from a desktop application, please provide a web page with information about that application.</p>
        </form>
        </div>
        <div id="back">
           <p><a href="http://api.apertium.org">Go back</a></p>
      </div>
        <div id="validaton">
            <p>
    <a href="http://validator.w3.org/check?uri=referer"><img
        src="http://www.w3.org/Icons/valid-html401"
        alt="Valid HTML 4.01 Transitional" height="31" width="88"></a>
  </p>
        </div>
    </body>
</html>
