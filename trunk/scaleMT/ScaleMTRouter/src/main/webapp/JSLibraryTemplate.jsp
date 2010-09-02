<%@page contentType="text/javascript" pageEncoding="UTF-8"%>
<%@page import="org.scalemt.router.logic.LoadBalancer, org.scalemt.rmi.transferobjects.LanguagePair, java.util.List" %>
var apertium=new function(){this.url="http://api.apertium.org/json/translate";var a=0;function b(e,d){e=e.replace(/[\[]/,"\\[").replace(/[\]]/,"\\]");var c="[\\?&]"+e+"=([^&#]*)";var g=new RegExp(c);var f=g.exec(d);if(f==null){return""}else{return f[1]}}this.key="";librarySrc="http://api.apertium.org/JSLibrary.js";elements=document.getElementsByTagName("script");for(i=0;i<elements.length;i++){src=elements[i].src;if(src.indexOf(librarySrc)==0){this.key=b("key",src)}}this.supported_pairs=[<%
LoadBalancer lb=  LoadBalancer.getInstance();
List<LanguagePair> supPairs=lb.getSupportedPairs();
        for(int i=0; i<supPairs.size(); i++){
            out.print("{\"source\": \""+supPairs.get(i).getSource()+"\",\"target\": \""+supPairs.get(i).getTarget()+"\"}");
            if(i<supPairs.size()-1)
                out.print(",");
        }%>];this.getJSON=function(d,h){var c="_json"+a++;d=d+"&callback=apertium."+c;var g=document.createElement("script");g.type="text/javascript";g.src=d;apertium[c]=function(j){delete apertium[c];if(e){e.removeChild(g)}h(j)};var e=document.getElementsByTagName("head")[0];e.insertBefore(g,e.firstChild);var f=f||20000;window.setTimeout(function(){if(typeof apertium[c]=="function"){apertium[c]=function(j){delete jsonp[c]};h({responseData:null,responseDetails:"timeout",responseStatus:509});window.setTimeout(function(){if(typeof apertium[c]=="function"){delete apertium[c]}},60000)}},f)};this.translate=function(j,h,d,k){var f,g="txt";if(j.type){g=j.type;f=j.text}else{f=j}var c="?q="+encodeURIComponent(f)+"&format="+g+"&langpair="+h+encodeURIComponent("|")+d+"&key="+apertium.key+"&markUnknown=no";var e=apertium.url+c;apertium.getJSON(e,function(m){var l;if(m.responseStatus==200){l={translation:m.responseData.translatedText}}else{l={translation:null,error:{code:m.responseStatus,message:m.responseDetails}}}k(l)})};this.isTranslatablePair=function(e,f){var c=false;for(var d=0;d<this.supported_pairs.length;d++){pair=this.supported_pairs[d];if(pair.source==e&&pair.target==f){return true}}return c};this.isTranslatable=function(e){var c=false;for(var d=0;d<this.supported_pairs.length;d++){pair=this.supported_pairs[d];if(pair.source==e){return true}}return c};this.getsources=function(){var c=Array();for(var e=0;e<this.supported_pairs.length;e++){pair=this.supported_pairs[e];var f=false;for(var d=0;d<c.length;d++){if(c[d]==pair.source){f=true}}if(!f){c[c.length]=pair.source}}return c};this.gettargets=function(e){var d=Array();for(var c=0;c<this.supported_pairs.length;c++){pair=this.supported_pairs[c];if(e==pair.source){d[d.length]=pair.target}}return d};this.getSupportedLanguagePairs=function(){return this.supported_pairs}};apertium.ContentType={TEXT:"txt",HTML:"html"};