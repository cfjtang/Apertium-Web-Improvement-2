<%--
    Document   : index
    Created on : 28-may-2009, 13:52:25
    Author     : vitaka
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web services test</title>
      <script src="prototype-1.6.0.2.js"></script>
      <script>


          function translate()
          {
              $$('#langPairs option').each(function(elem){
                if (elem.selected)
                {
                    var mu="no";
                    if($('cbUnknown').checked)
                        mu="yes"

                    $('translation').value='Loading...';
                     $('errorFooter').textContent='';
                    new Ajax.Request('json/translate', {
                    method: 'post',
                    parameters: {langpair: elem.value, q: $('source').value, key: '/hPZuyVJgLDX4t9RA6s508EK0ss', markUnknown: mu},
                    requestHeaders: {Accept: 'application/json'},
                    onSuccess: success,
                    onFailure: failure
                });

                }
                });

          }

           function success(transport)
          {
               var json = transport.responseJSON;
               if(json.responseStatus==200)
                   $('translation').value=json.responseData.translatedText;
               else
                   {
                     $('translation').value='';
                       $('errorFooter').textContent=json.responseStatus+": "+json.responseDetails;
                   }


          }

          function failure()
          {
              $('errorFooter').textContent='Unexpected error';
          }

          function getLangPairs()
          {
               new Ajax.Request('json/listPairs', {
                method: 'get',
                parameters: {},
                requestHeaders: {Accept: 'application/json'},
                onSuccess: successLangPairs,
                onFailure: failure
                });
          }




          function successLangPairs(transport)
          {
               var json = transport.responseJSON;
               if(json.responseStatus==200)
               {
                   var select = $('langPairs');
                   select.options.length = 0;
                   for(var i=0; i<json.responseData.length;i++)
                   {
                        select.options.add(new Option(json.responseData[i].sourceLanguage+"-"+json.responseData[i].targetLanguage,json.responseData[i].sourceLanguage+"|"+json.responseData[i].targetLanguage));
                   }
               }
               else
                   {

                       $('errorFooter').textContent=json.responseStatus+": "+json.responseDetails;
                   }
          }

            </script>
    </head>
    <body onload="getLangPairs();">
       <h1>Apertium Web Service test</h1>

       <h3>Welcome!</h3>
       <p>Welcome to the Apertium web service test page.</p>
       <p>You can find the complete API specification at <a href="http://wiki.apertium.org/wiki/ScaleMT#API">http://wiki.apertium.org/wiki/ScaleMT#API</a>  </p>
       <p>There is also an <a href="http://apertium.pikaspart.com" >example mashup</a> combining Apertium and Twitter APIs</p>

       <h3>Registration</h3>
       <p>Machine translation web service usage is limited to a certain amount of requests per IP.
           If you want to overcome that restriction, please <a href="register.jsp">register</a> and get an API key. This way we can track where our traffic come from. Registered users enjoy a more generous traffic limit.</p>

       <h3>Test</h3>
       <p> Source text: </p>
       <p><textarea id="source" cols="60" rows="10"></textarea></p>

       <p>Translation: </p>
       <p><textarea id="translation" readonly cols="60" rows="10" ></textarea></p>

       <p> <select id="langPairs"></select> <button onclick="translate()">Translate!</button>  Mark unknown: <input id="cbUnknown" type="checkbox"/> </p>

       <p id="errorFooter" class="error"></p>
    </body>
</html>
