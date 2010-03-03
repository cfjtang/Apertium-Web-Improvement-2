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
    <body onload="<%
            if(request.getAttribute("message")!=null)
                out.println("alert('"+(String)request.getAttribute("message")+"');");
            %>" style="font-family:helvetica;margin-left:2em;">
        <h1>Apertium Web Service register page</h1>
        <div id="termsmain" style="">
        <h3>Terms and conditions</h3>

        These are the most important terms written in a language understandable by non-lawyers:
        <ul>
            <li>API usage is limited to a small amount of requests per IP if you do not have an API key</li>
            <li>An API key provides a more generous traffic limit</li>
            <li>Remember that the API usage has an economical cost for Apertium. Do not make unnecessary requests</li>
            <li>If you want unlimited access to the API, a financial contribution might be required </li>
            <li>Commercial applications have no additional restrictions</li>
        </ul>

<div id="termsbox" style="border:2px solid #000066;margin-left:0px;margin-top: 1em;overflow:auto;height:200px;width:90%;padding-left: 20px;padding-right: 10px;">
    <h2>Apertium API Terms and Conditions of use</h2>

    <p>You've chosen to use the Apertium API and we thank you for that.</p>
    <p>Apertium grants you a license to use its API under the terms and conditions set forth below.
        These terms and conditions may be updated at any time without notice. You can view the latest
        version at this address: http://api.apertium.org/register.jsp . Your use of the API results in unconditional acceptance of the most recent terms and conditions.</p>
    <p>License to use the API is granted non-exclusively and non-relicenseable, for unlimited time, until terminated by either party.<p>
    <p>Apertium may terminate your license at any time, for any reason. The goal of this document is to give you as much information as possible
        to avoid this from occurring. You can also terminate your license at any time by ceasing to use the API and removing your API key.

    <h3>1. Request and use of an API key</h3>

    <p>The proper use of the API requires obtaining an identifier (the "key") issued by Apertium as detailed below.</p>
    <p>Requests not including an API key are strictly limited to a small amount per IP.</p>
    <p>To obtain a key you will be asked to detail some necessary information.</p>
    <p>You agree to provide true, accurate and complete information.</p>
    <p>Providing this information is a critical condition on your right to use the API.</p>
    <p>It is your responsibility to keep your key confidential. You are responsible for all activities related to API use associated with your key and you agree to immediately notify Apertium of any fraudulent use of your key.</p>
    
    <h3>2. Terms and restrictions of use</h3>

    <p>Remember that the availability of the API represents a real cost to the Apertium community. Design your application to make wise use of API resources and offer a genuinely useful service to users.</p>
    <p>Don't attempt to exploit any security vulnerabilities. If you detect a possible flaw, be discreet and let us know as soon as possible.</p>
    <p>Don't make large numbers of unnecessary API requests which could cause slowdown or instability of the Apertium platform.</p>
    <p>If your application generates a large number of API requests, some of them could be rejected to avoid server overload. A financial contribution might be required if you want the service to process all your requests. If you have any doubt regarding the impact of your application, please contact us.</p>


    <h3>3. Commercial Use</h3>

    <p>If your application intends to profit, directly or indirectly, your application will be considered commercial.</p>
    <p>Commercial applications have no additional limitations.</p>

    <h3>4. Disclaimer of warranties</h3>

    <p>Nothing in these terms and conditions shall affect your statutory rights as a consumer.</p>
    <p>You expressly understand and agree that:</p>
    <ul>
        <li> Your use of the Apertium API is at your sole risk.</li>
        <li>The Apertium API is provided on an “as is” and “as available” basis.</li>
        <li>To the maximum extent permitted by applicable law, Apertium expressly disclaims all warranties and conditions of any kind, whether express or implied, including, but not limited to the implied warranties and conditions of merchantability, fitness for a particular purpose and non-infringement.</li>
    </ul>
    <p>Apertium makes no warranty that:</p>
    <ul>
        <li>the Apertium API will meet your requirements;</li>
        <li>the Apertium API will be uninterrupted, timely, secure, or error-free;</li>
        <li>the results that may be obtained from the use of the Apertium API will be accurate or reliable;</li>
        <li>the quality of any products, services, information, or other material purchased or obtained by you through the Apertium API will meet your expectations; and/or any errors in the Apertium API will be corrected.</li>
</ul>


    <h3>5. Limitation of liability</h3>

    <p>The stipulation of the present conditions only exonerates and limits Apertium’s liability in case of loss within the limits prescribed by the law.</p>
    <p>Apertium shall not be liable to you for any direct, indirect, incidental, special, consequential or exemplary damages resulting from:</p>
    <ul>
        <li> the use or the inability to use the Apertium API;</li>
<li> the cost of procurement of substitute goods and services resulting from any goods, data, information or services purchased or obtained or messages received or transactions entered into through or from the Apertium API;</li>
<li> unauthorized access to or alteration of your transmissions or data;</li>
<li> statements or conduct of any third party on the Apertium API; or</li>
<li> any other matter relating to the Apertium API ;</li>
</ul>
    <p>including but not limited to damages for loss of:</p>
<ul>
<li> goodwill;</li>
<li> data;</li>
<li> content;</li>
<li> profits ; or</li>
<li> any other matter related or intangible losses ;</li>
</ul>
<p>even if Apertium has been advised of the possibility of such damages.</p>
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
               src="http://api.recaptcha.net/challenge?k=6LeHXQsAAAAAAPIc3n1YkSeDb9f-NVAk3JrriCFn">
            </script>

            <noscript>
               <iframe src="http://api.recaptcha.net/noscript?k=6LeHXQsAAAAAAPIc3n1YkSeDb9f-NVAk3JrriCFn"
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
