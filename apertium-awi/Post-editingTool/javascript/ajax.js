//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for dynamic processing of data with Ajax
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

var createXMLHttpRequest = function(){ return false; };

function initAjax()
{
	//initialise the Ajax communication system
	
	//set the right way for creating ajax managers
	if(window.XMLHttpRequest) 
	{
		createXMLHttpRequest = function()
		{
			return new XMLHttpRequest();
		};
	} 
	else if(window.ActiveXObject) 
	{
		createXMLHttpRequest = function()
		{
			return new ActiveXObject("Microsoft.XMLHTTP");
		};
	}
}

function ajaxRequest(str, resultManager)
{
	//perform an ajax request.
	//str is the query URI string
	//resultManager is a function that will be called with the results as parameter
	
	var xhr;
	ajax_loader = document.getElementById('ajax_loader');
	ajax_loader.style.display = '';

	if(xhr = createXMLHttpRequest())
	{
		xhr.onreadystatechange = function(){
			if(xhr.readyState == 4)
			{
				resultManager(xhr.responseText);
				ajax_loader.style.display = 'none';
				delete xhr;
			}
		};

		xhr.open('POST', 'ajax.php', true);
		
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.setRequestHeader("Content-length", str.length);
		xhr.setRequestHeader("Connection", "close");
		
		xhr.send(str);
	}

}

function buildRequestString(form, fields)
{
	//builds the URI string containing the data from the needed fields of the form
	//form must be a form object
	//fields must be an array of string, that are the names of the fields we need to include
	
	var str = '';
	
	for(var i = 0;i < form.elements.length;i++) 
	{
		var elt = form.elements[i];
		
		if(in_array(elt.name, fields))
		{
			switch(elt.type) 
			{
				case "text":
				case "hidden":
				case "password":
				case "textarea":
				case "select-one":
					str += elt.name + "=" + fixedEncodeURIComponent(elt.value) + "&";
					break; 

				case "checkbox":
				case "radio":
					if(elt.checked)
					{
						str += elt.name + "=" + fixedEncodeURIComponent(elt.value) + "&";
					}
					break;
					
			}
		}
	} 
	str = str.substr(0,(str.length - 1));
	return str;
}


function in_array (needle, haystack, argStrict) {
    // http://kevin.vanzonneveld.net

    var key = '', strict = !!argStrict;

    if (strict) {
        for (key in haystack) {
            if (haystack[key] === needle) {
                return true;
            }
        }
    } else {
        for (key in haystack) {
            if (haystack[key] == needle) {
                return true;
            }
        }
    }

    return false;
}

function fixedEncodeURIComponent (str) 
{  
	return encodeURIComponent(str).replace(/!/g, '%21').replace(/'/g, '%27').replace(/\(/g, '%28').replace(/\)/g, '%29').replace(/\*/g, '%2A').replace(/%20/g, '+');  
}  

