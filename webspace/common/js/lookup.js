var xmlHttp;
var timerId;
var direction = document.forms[0].direction.value;
var dirValue;
ajaxFunction(direction);


function ajaxFunction(dir) {
	//alert("ajaxFunction('" + dir + "')");
	cleanResult();
	//updateOptions(dir);

    try {
        // Firefox, Opera 8.0+, Safari
        xmlHttp = new XMLHttpRequest();
    } catch (e) {
    // Internet Explorer
    try {
        xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e) {
    try {
        xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
    } catch (e) {
    alert("Your browser does not support AJAX!");
    return false;
}
}
}
xmlHttp.onreadystatechange=loadXML;
var fileName = "common/xml/" + dir + "-trie.xml";
xmlHttp.open("GET",fileName,true);
xmlHttp.send(null);
showLoadingIndicator();

}

function loadXML() {
    if(xmlHttp.readyState==4) {
        if (xmlHttp.status == 200) {
            message("Dictionary loaded");
				var wordE = document.getElementById('word');
				var selectE = document.getElementById('direction');
				lookUp( wordE.value, true, selectE.value );
        } else {
        message("Dictionary not available yet!");
    }
}	
}  

function delayLookUp(value, showLexicalInfo, dir) {
   cmd = 'lookUp("' + value + '", ' + showLexicalInfo + ', "' + dir + '")';
   clearTimeout(timerId);
   timerId = setTimeout(cmd, 500);
}

function lookUp(value, showLexicalInfo, dir){
    //var keyID = event.keyCode;
    // key is not backspace	
    //if( keyID != 8 ) {
        cleanResult();
        if( value.length >= 1 ) {
            var xmldoc = xmlHttp.responseXML;
            var root = xmldoc.getElementsByTagName('root')[0];
            value = value.toLowerCase();
            lookUpRecursive(root, value, showLexicalInfo, dir);
        }
        message("");
    //}
}

function lookUpRecursive(parentNode, value, showLexicalInfo, dir) {
    for (var iNode = 0; iNode < parentNode.childNodes.length; iNode++ ) {
        var node = parentNode.childNodes[iNode];
        if( node.nodeName == "w") {
            var source = node.getAttribute("v");
            if(source.length >= value.length) {
                if(showLexicalInfo) {
                    showEntryDetailed(node, dir);
                } else {
                showEntrySimple(node, dir);
            }
        }
    } else {
    var attr = node.attributes;
    if(attr != undefined) {
        var nodevalue = node.getAttribute("v");
        nodevalue = nodevalue.toLowerCase();
        var nodevalueLength = nodevalue.length;
        var valueLength = value.length;
        if( valueLength < nodevalueLength ) {
            var subst = nodevalue.substring(0,valueLength);
            
            if( value == subst ) {
                lookUpRecursive(node, value, showLexicalInfo, dir);
            }
        }
        if( valueLength >= nodevalueLength ) {
            var subst = value.substring(0,nodevalueLength);
            if( nodevalue == subst ) {
                lookUpRecursive(node, value, showLexicalInfo, dir);
            } 
            
        }
    }
}

}
}

function message(text) {
    var divE = document.getElementById('message');
    divE.innerHTML = text;
}

function showLoadingIndicator() {
    var divE = document.getElementById('message');
    divE.innerHTML = '<img src="images/indicator_medium.gif"/>';
}

function showEntrySimple(parentNode, dir) {
    var divE = document.getElementById('result');
    if( parentNode.hasChildNodes() ) {
        for (var iNode = 0; iNode < parentNode.childNodes.length; iNode++) {
            var node = parentNode.childNodes[iNode];
            if(node.nodeName == "l") {
                var left = inner_text(node);
                var bE = document.createElement("b");
                var aE = document.createElement("a");
                aE.appendChild(document.createTextNode(left));
                var sl = getSourceLang(dir);
                var url = "http://" + sl + ".wiktionary.org/wiki/" + left;
                aE.setAttribute("href",url);
                aE.setAttribute("target","_blank");
                bE.appendChild(aE);
                divE.appendChild(bE);
                divE.appendChild(document.createTextNode(" → "));									
            } 
            if(node.nodeName == "r") {
                var right = inner_text(node);
                var aE = document.createElement("a");
                var url = "http://" + getTargetLang(dir) + ".wiktionary.org/wiki/" + right;
                aE.setAttribute("href", url);
                aE.setAttribute("target","_blank");					
                aE.appendChild(document.createTextNode(right));					
                divE.appendChild(aE);
                divE.appendChild(document.createElement("br"));				
            }
        }
    }
}

function showEntryDetailed(parentNode, dir) {
    var divE = document.getElementById('result');
    if( parentNode.hasChildNodes() ) {
        for (var iNode = 0; iNode < parentNode.childNodes.length; iNode++) {
            var node = parentNode.childNodes[iNode];
            if(node.nodeName == "l") {
                processNode(node, divE, "leftValue");
                divE.appendChild(document.createTextNode(" → "));									
            } 
            if(node.nodeName == "r") {
                processNode(node, divE, "rightValue");
                divE.appendChild(document.createElement("br"));				
            }
        }
    }
}

function getSourceLang(dir) {
    var data = new Array();
    data = dir.split("-");
    var sl = data[0];
    var tl = data[1];	
    var dir = data[2];
    if(dir == "lr") {
        return sl;
    } else {
    return tl;
}
}

function getTargetLang(dir) {
    var data = new Array();
    data = dir.split("-");
    var sl = data[0];
    var tl = data[1];	
    var dir = data[2];
    if(dir == "lr") {
        return tl;
    } else {
    return sl;
}
}

function processNode(node, divE, clase) {
    var leftV = inner_text(node);
    var spanLeft = document.createElement("span");
    spanLeft.setAttribute("class", clase);
    var leftElement = document.createTextNode(leftV + " ");
    spanLeft.appendChild(leftElement);
    divE.appendChild(spanLeft);
    if( node.hasChildNodes() ) {
        for (var jNode = 0; jNode < node.childNodes.length; jNode++) {
            var sNode = node.childNodes[jNode];
            if (sNode.nodeName == "s" ) {
                var nAttr = sNode.getAttribute("n");
                var spanElement = document.createElement("span");
                spanElement.setAttribute("class","attribute");
                if( jNode==(node.childNodes.length-1) ) {
                    var textE = document.createTextNode(nAttr);
                } else {
                var textE = document.createTextNode(nAttr + ".");
            }
            spanElement.appendChild(textE);
            divE.appendChild(spanElement);
        }											
    }
}
}

function cleanResult() {
    var divE = document.getElementById('result');
    if ( divE.hasChildNodes() ) {
        while ( divE.childNodes.length >= 1 ) {
            divE.removeChild( divE.firstChild );
        }
    }
}

function lexicalInfo(checkBox) {
    var inputE = document.getElementById('word');
    if(checkBox.checked==true) {
        inputE.setAttribute("onkeyup","lookUp(this.value, true, document.forms[0].direction.value);");
    } else {
    if(checkBox.checked == false) {
        inputE.setAttribute("onkeyup","lookUp(this.value, false, document.forms[0].direction.value);");
    }
}
}

function inner_text(node){
    if( node != undefined) {
        for (var jNode = 0; jNode < node.childNodes.length; jNode++) {
            var sNode = node.childNodes[jNode];
            if (sNode.nodeType == 3) {
                return sNode.data;
            }
        }
    }
}
