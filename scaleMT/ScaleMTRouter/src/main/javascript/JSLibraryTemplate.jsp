

<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@page import="org.scalemt.router.logic.LoadBalancer, org.scalemt.rmi.transferobjects.LanguagePair, java.util.List" %>

var apertium = new function() {
    this.url = "http://api.apertium.org/json/translate";
    var __callbackId = 0;
	function gup( name , url){
	  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	  var regexS = "[\\?&]"+name+"=([^&#]*)";
	  var regex = new RegExp( regexS );
	  var results = regex.exec( url );
	  if( results == null )
		return "";
	  else
		return results[1];
	}

	this.key = "";

	librarySrc='http://api.apertium.org/JSLibrary.js'
	elements=document.getElementsByTagName("script");
	for (i=0; i< elements.length; i++)
	{
		src=elements[i].src;
		if(src.indexOf(librarySrc)==0)
			this.key=gup('key',src);
	}


    this.supported_pairs = [
     <%
        LoadBalancer lb=  LoadBalancer.getInstance();
        List<LanguagePair> supPairs=lb.getSupportedPairs();
        for(int i=0; i<supPairs.size(); i++)
        {
            out.print("{\"source\": \""+supPairs.get(i).getSource()+"\",\"target\": \""+supPairs.get(i).getTarget()+"\"}");
            if(i>=supPairs.size()-1)
                out.println();
            else
                out.println(",");
        }
     %>
    ];

     this.getJSON = function(url, callback) {

        var callBackName = "_json" + __callbackId++;
        url = url+"&callback=apertium."+callBackName;


        var scr = document.createElement("script");
        scr.type = "text/javascript";
        scr.src = url;

		//Setup the callback
		apertium[callBackName]=function(data) {
            delete apertium[callBackName];
             if (head)
                head.removeChild(scr);
			callback(data);
            };

		// send the request
        var head = document.getElementsByTagName("head")[0];
        head.insertBefore(scr, head.firstChild);


        // default to 10 second timeout
        var timeout = timeout || 20000;

        //set timeout
         window.setTimeout(function() {
            if (typeof apertium[callBackName] == "function") {

                // replace success with null callback in case the request is just very latent.
                apertium[callBackName] = function(data) {
                    delete jsonp[callBackName];
                };

                // call the error callback
                callback({"responseData":null,"responseDetails":"timeout","responseStatus":509});

                // set a longer timeout to safely clean up the unused callback.
                window.setTimeout(function() {
                    if (typeof apertium[callBackName] == "function") {
                        delete apertium[callBackName];
                    };
                }, 60000);
            };
        }, timeout);
    };

    this.translate = function(sourceText, sourceLang, targetLang, callback) {
        var source,
            format = "txt";
        if (sourceText.type) {
            format = sourceText.type;
            source = sourceText.text;
        } else {
            source = sourceText;
        }
        var qs = "?q=" + encodeURIComponent(source) + "&format=" + format +
                 "&langpair=" + sourceLang + encodeURIComponent("|") + targetLang +
                 "&key=" + apertium.key + "&markUnknown=no";
        var url = apertium.url + qs;

        apertium.getJSON(url, function(data) {
            var jsonData;
            if (data.responseStatus == 200) {
                jsonData = {"translation" : data.responseData.translatedText};
            } else {
                jsonData = {"translation" : null,
                            "error": {"code": data.responseStatus,
                                      "message": data.responseDetails}};
            }
            callback(jsonData);
        });
    };

    this.isTranslatablePair = function(source, target) {
        var result = false;
        for (var i=0; i<this.supported_pairs.length; i++) {
            pair = this.supported_pairs[i];

            if (pair.source == source && pair.target == target)
                return true;
        }
        return result;
    };

    this.isTranslatable = function(source) {
        var result = false;
        for (var i=0; i<this.supported_pairs.length; i++) {
            pair = this.supported_pairs[i];

            if (pair.source == source)
                return true;
        }
        return result;
    };

    this.getsources = function() {
        var arraySources = Array();
        for (var i=0; i<this.supported_pairs.length; i++) {
            pair = this.supported_pairs[i];
            var found = false;
            for (var j=0 ; j<arraySources.length; j++) {
                if (arraySources[j] == pair.source)
                    found = true;
            }
            if (!found)
                arraySources[arraySources.length] = pair.source;
        }
        return arraySources;
    };

    this.gettargets = function(sourceLang) {
        var arrayReturn = Array();
        for(var i=0; i<this.supported_pairs.length;i++) {
            pair = this.supported_pairs[i];
            if (sourceLang == pair.source)
                arrayReturn[arrayReturn.length] = pair.target;
        }
        return arrayReturn;
    };

    this.getSupportedLanguagePairs = function() {
        return this.supported_pairs;
    };
};

apertium.ContentType = {
  'TEXT' : 'txt',
  'HTML' : 'html'
};