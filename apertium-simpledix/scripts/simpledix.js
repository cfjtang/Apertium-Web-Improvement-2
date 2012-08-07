var numFields = 0;
var gotoSelect = false;
var installed = true;
var name;
var insertionsLeft = 0;
var currentTimeout = false;

var ALL ;
var LIST_ERROR ;
var FLEX_ERROR ;
var WORD_ERROR ;
var WORD_UNDEF ;
var PARAD_UNDEF ;
var INSERT ;
var UNAVAILABLE ;
var TRANS_EXISTS ;
var TRANS_ERROR ;
var GENERATING  ;
var GENERATE  ;
var INSERT_OK ;
var INSERT_ERROR ;
var INSTALLED_ERROR ;
var INSTALLED ;
var EXPORT ;
var EXPORTING ;
var EXPORT_ERROR ;
var EXPORT_OK ;
var DELETE_CONFIRM ;
var ERROR ;
var DELETE_ALL_CONFIRM ;
var EXTENSION_ERROR ;
var MISSING ;
var CONFCHECK_ERROR;
var MISSING_CONF;
var UNDEF_LANG;
var WORD_INSERT;
var WORD_ALREADY_INSERTED;
var WORD_NOT_DEFINED;
var SHOW_INSERT_QUERIES;
var HIDE_INSERT_QUERIES;
var FIRST_GENERATE;
var DICTIONARY;
var SAME_LANGUAGES;

// append row to the HTML table
function appendRow(table) {
    var tbl = document.getElementById(table),      // table reference
        row = tbl.insertRow(tbl.rows.length),      // append table row
        i;
    // insert table cells to the new row
    for (i = 0; i < tbl.rows[0].cells.length; i++) {
        createCell(row.insertCell(i), i, 'row');
    }
}

// append column to the HTML table
function appendColumn(table) {
    var tbl = document.getElementById(table), // table reference
        i;
    // open loop for each row and append cell
    for (i = 0; i < tbl.rows.length; i++) {
        createCell(tbl.rows[i].insertCell(tbl.rows[i].cells.length), i, 'col');
    }
}
 
// create DIV element and append to the table cell
function createCell(cell, text, style) {
    var div = document.createElement('div'), // create DIV element
        txt = document.createTextNode(text); // create text node
    div.appendChild(txt);                    // append text node to the DIV
    div.setAttribute('class', style);        // set DIV class attribute
    div.setAttribute('className', style);    // set DIV class attribute for IE (?!)
    cell.appendChild(div);                   // append DIV to the table cell
}

function createContentCell(cell, content) {
    while( cell.hasChildNodes() ){
        cell.removeChild(cell.lastChild);
    }
    cell.appendChild(content);
    
    if (gotoSelect)
    {
		gotoSelect = false;
		content.focus();
	}
}

function clearCell(cell)
{
	while( cell.hasChildNodes() ){
        cell.removeChild(cell.lastChild);
    }
}

function addToContentCell(cell, content) 
{
    cell.appendChild(content);
}

function createTextBox(name)
{
    var tb = document.createElement('input');
    tb.setAttribute('type', 'text');
    tb.setAttribute('onkeyDown', 'return keyPress(event, this)');
    tb.setAttribute('onBlur', 'textLeft(this)');
    tb.setAttribute('onFocus', 'selectText(this)');
    tb.setAttribute('id', name);
    tb.setAttribute('name', name);
    return tb;
}

function createComboBox(name, objects, ul)
{
    var tb =  document.createElement('select');
    var opt = document.createElement("option");
    var li;
    var div;

    tb.setAttribute('id', name);
    tb.setAttribute('name', name);
    tb.setAttribute('onchange', "flexWord(this)");
    tb.setAttribute('onkeyup', "flexWord(this)");

    var o;
    for (o = 0; o < objects.length; o+=3)
    {
		li = document.createElement("li");
        div = document.createElement("div");
        div.innerHTML = objects[o+1];
        li.appendChild(div);
        div = document.createElement("div");
        div.innerHTML = objects[o];
        li.appendChild(div);
        div = document.createElement("div");
        div.innerHTML = objects[o+2];
        li.appendChild(div);
		ul.appendChild(li);
    }
	
    return tb;
}

function createList(objects)
{
    var tb =  document.createElement('ul');
    var opt;
    var text;

    var o;
    for (o = 0; o < objects.length; o++)
    {
        opt = document.createElement("li");
        opt.innerHTML = objects[o];
        tb.appendChild(opt);
    }
    return tb;
}

function addWord(table, deleteButton)
{
    var i;
    var tId = table.id;
    var tb = createTextBox('text_' + tId + '_' + numFields);
    var textCell = table.rows[0].insertCell(table.rows[0].cells.length);
    var cell;
    var ul = document.createElement("ul");
    ul.hidden = true;
    createContentCell(textCell, tb);
    cell = table.rows[1].insertCell(table.rows[1].cells.length);
    createContentCell(cell , createComboBox('parad_' + tId + '_' + Math.floor(numFields/2), [], ul));
    addToContentCell(cell, ul);
    createCell(table.rows[2].insertCell(table.rows[2].cells.length), '', 'col');
    if (deleteButton)
    {
		var button = document.createElement("input");
		button.setAttribute("type", "button");
		button.setAttribute("class", "deleteButton");
		button.setAttribute("onClick", "deleteColumn(this)");
		button.value = "X";
		addToContentCell(textCell, button);
	}

    numFields++;
    tb.focus();
}

function keyPress(e, elem)
{
	if(e.keyCode == 9) 
	{
		gotoSelect = true;
		textLeft(elem);
    }
    if (e.keyCode == 32 || e.charCode == 32)
    {
		//Enable for multiwords
        //~ addWord(e.currentTarget.parentNode.parentNode.parentNode.parentNode, true);
        return false;
    }
    if (currentTimeout != false)
    {
		clearTimeout(currentTimeout);
	}
    currentTimeout = setTimeout(function() {textLeft(elem);}, 1000);
    return true
}

function getColumn(elem)
{
    var children = elem.parentNode.parentNode.childNodes
    var col = 0;
    for (c in children)
    {
        if (children[c].nodeName != "TD" && children[c].nodeName != "td")
            continue;
            
        if (children[c] == elem.parentNode)
        {
            break;
        }
        col++;
    }
    return col;
}

function getCell(elem, col, row)
{
    var children;
    var cell;

    children = elem.parentNode.parentNode.parentNode.childNodes[row].childNodes;
    for (c in children)
    {
        if (children[c].nodeName != "TD" && children[c].nodeName != "td")
            continue;
        col --;
        if (col < 0)
        {
            cell = children[c];
            break;
        }
    }
    return cell;
}

function textLeft(elem)
{
    var children = elem.parentNode.parentNode.childNodes
    var col = getColumn(elem);
    var cell = getCell(elem, col, 1);
    var config = "";
    var rb;
    var radioLength;

    if (currentTimeout != false)
    {
		clearTimeout(currentTimeout);
	}

	if (elem.oldword != elem.value)
	{
		elem.parentNode.parentNode.parentNode.parentNode.className = "table";
		
		if (elem.parentNode.parentNode.parentNode.parentNode.id == "table1")
		{
			rb = document.getElementsByName("cFiles1Group");
			clearCell(document.getElementById("translations1"));
			warnMsg("1");
		}
		else /*if (elem.parentNode.parentNode.parentNode.parentNode.id == "table2")*/
		{
			rb = document.getElementsByName("cFiles2Group");
			clearCell(document.getElementById("translations2"));
			warnMsg("2");
		}
		
		clearCell(elem.parentNode.parentNode.parentNode.childNodes[2].childNodes[0]);
			
		radioLength = rb.length;
	
		for(var i = 0; i < radioLength; i++) {
			if(rb[i].checked) {
				break;
			}
		}
		if (i == radioLength)
			return
		
		elem.oldword = elem.value;
		cell.childNodes[0].disabled = true;
		var request = $.ajax({
		  url: "listParadigms.php",
		  type: "GET",
		  data: {
				lang : getLang(elem),  
				word:elem.value,
				confFile:rb[i].value},
		  dataType: "html"
		});

		request.done(function(msg) {
			var myJSON = JSON.parse(msg);
			var result = [];
			for (i in myJSON.result)
			{
				result[i] = (myJSON.result[i].value);
			}
			var ul = document.createElement("ul");
			ul.hidden = true;
			createContentCell(cell,createComboBox(cell.childNodes[0].name, result, ul)); 
			addToContentCell(cell, ul);
			var category = document.getElementById("categorias");
			filterCell(category.options[category.selectedIndex].value, cell);
			flexWord(cell.childNodes[0]);
		});
		request.fail(function(jqXHR, textStatus) {
		  alert( LIST_ERROR );
		});
	}
}

function flexWord(elem)
{
    var children = elem.parentNode.parentNode.childNodes
    var col = getColumn(elem);
    var cell = getCell(elem, col, 2);
    var textCell = getCell(elem, col, 0);
    var rootElement;
    var rb;
    var radioLength;
    var leftLang = elem.parentNode.parentNode.parentNode.parentNode.id == "table1";
    
    elem.parentNode.parentNode.parentNode.parentNode.className = "table";
    
	if (leftLang)
	{
		rb = document.getElementsByName("cFiles1Group");
		warnMsg("1");
	}
	else
	{
		rb = document.getElementsByName("cFiles2Group");
		warnMsg("2");
	}
		
	radioLength = rb.length;

	for(var i = 0; i < radioLength; i++) {
		if(rb[i].checked) {
			break;
		}
	}
	
	if (elem.selectedIndex == -1)
		return;
	
	if (elem.options[elem.selectedIndex].value == '-')
	{
		clearCell(cell);
		return;
	}
	
    var request = $.ajax({
      url: "flexWord.php",
      type: "GET",
      data: {   lang : getLang(elem),
                word : textCell.childNodes[0].value,
                paradigm : elem.options[elem.selectedIndex].value,
                confFile : rb[i].value},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var result = [];
        var index = 0;
        for (i in myJSON.result)
        {
            if (myJSON.result[i].value != undefined)
            {
                result[index++] = (myJSON.result[i].value);
            }
        }
        if (myJSON.root != null)
        {
			createContentCell(cell, createList(result));

			rootElement = document.createElement("input");
			rootElement.setAttribute("type", "hidden");
			rootElement.setAttribute("value", myJSON.root);
			addToContentCell(cell, rootElement);
		}

    });

    request.fail(function(jqXHR, textStatus) {
      alert( FLEX_ERROR );
    });
    
    var lang1 = document.getElementById("lang1");
    var lang2 = document.getElementById("lang2");
    lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;
    var currLang;
    
    if (leftLang)
    {
		currLang = lang1;
	}
	else
	{
		currLang = lang2;
	}   
    
    var request2 = $.ajax({
      url: "getTranslations.php",
      type: "GET",
      data: {   lang1 : lang1,
                lang2 : lang2,
                currLang : currLang,
                word : textCell.childNodes[0].value,
                name : name},
      dataType: "html"
    });
    
    request2.done(function(msg) {
		var myJSON = JSON.parse(msg);
		var lang1 = document.getElementById("lang1");
		var lang2 = document.getElementById("lang2");
		lang1 = lang1.options[lang1.selectedIndex].value;
		lang2 = lang2.options[lang2.selectedIndex].value;
		var ul;
		var li;
		
		if (myJSON.currLang == lang1)
		{
			ul = document.getElementById("translations1");
		}
		else
		{
			ul = document.getElementById("translations2");
		}
		
		clearCell(ul);
		for (i in myJSON.result)
		{
			li = document.createElement("li");
			li.innerHTML = myJSON.result[i];
			ul.appendChild(li);
		}
	});
	
    request.fail(function(jqXHR, textStatus) {
      alert( FLEX_ERROR );
    });
}

function getLang(elem)
{
    var cur = elem;
    var langSelect;
    while (cur.nodeName != 'TABLE' && cur.nodeName != 'table')
    {
        cur = cur.parentNode;
    }
    if (cur.id == 'table1')
    {
        langSelect = document.getElementById("lang1"); 
    }
    else
    {
        langSelect = document.getElementById("lang2");
    }
    if (langSelect.selectedIndex >= 0)
		return langSelect.options[langSelect.selectedIndex].value;
	return "";
}

function deleteColumn(elem)
{
    var col = getColumn(elem);
    var cur = elem;
    var curCol;
    var curRow;
    var realCol;
    while (cur.nodeName != 'tbody' && cur.nodeName != 'TBODY')
    {
        cur = cur.parentNode;
    }
    for (curRow in cur.childNodes)
    {
        realCol = 0;
        for (curCol in cur.childNodes[curRow].childNodes)
        {
            if (cur.childNodes[curRow].childNodes[curCol].nodeName != "TD" && cur.childNodes[curRow].childNodes[curCol].nodeName != "td")
                continue;
            if (realCol == col)
            {
                cur.childNodes[curRow].removeChild(cur.childNodes[curRow].childNodes[curCol]);
                break;
            }
            realCol ++;
        }
    }
    
}

function resetMsg(id)
{
	document.getElementById("toInsert" + id).hidden = true;
	document.getElementById("already" + id).hidden = true;
	document.getElementById("undef" + id).hidden = true;
	document.getElementById("warn" + id).hidden = true;
}

function warnMsg(id)
{
	if (!document.getElementById("toInsert" + id).hidden ||
		!document.getElementById("already" + id).hidden)
	{
		document.getElementById("warn" + id).hidden = false;
		document.getElementById("insert").disabled = "disabled";
		document.getElementById("insert").style.cssText = "";
	}
	document.getElementById("already" + id).hidden = true;
	document.getElementById("undef" + id).hidden = true;
	document.getElementById("toInsert" + id).hidden = true;
}

function init()
{
    var table;
    var i;
    var div;
    
    numFields = 0;
      
    table = document.getElementById("table2");
    while( table.childNodes[1].hasChildNodes() ){
        table.childNodes[1].removeChild(table.childNodes[1].lastChild);
    }
    for (i = 0; i < 4; i++)
    {
        table.childNodes[1].appendChild(document.createElement("tr"));
    }
    addWord(table, false);

    table = document.getElementById("table1");
    while( table.childNodes[1].hasChildNodes() ){
        table.childNodes[1].removeChild(table.childNodes[1].lastChild);
    }
    for (i = 0; i < 4; i++)
    {
        table.childNodes[1].appendChild(document.createElement("tr"));
    }
    addWord(table, false);
    
    clearCell(document.getElementById("result"));
    clearCell(document.getElementById("translations1"));
    clearCell(document.getElementById("translations2"));
    
	var button = document.getElementById("generate");
	button.disabled = "";
	button.value = GENERATE;
	var button = document.getElementById("insert");
	button.disabled = "disabled";
	button.value = FIRST_GENERATE;
	
	resetMsg(1);
	resetMsg(2);
	document.getElementById("insert").style.cssText = "";
}

function checkInDictionary(tableID, lang1, lang2, currLang)
{
    var rtable = document.getElementById(tableID);
    var table = rtable.childNodes[1];
    
    if (table.childNodes[2].childNodes[0].childNodes[1] == undefined)
    {
		var theMsgId;
		
		if (currLang == lang1)
		{
			theMsgId = "1";
		}
		else
		{
			theMsgId = "2";
		}
		resetMsg(theMsgId);
		rtable.className =  "redTable";
        document.getElementById("undef" + theMsgId).hidden = false;
		return;
	}
    
    var word = (table.childNodes[0].childNodes[0].childNodes[0].value);
    var option = (table.childNodes[1].childNodes[0].childNodes[0]);
    var paradigm = option.options[option.selectedIndex].value;
    var rootOfWord = (table.childNodes[2].childNodes[0].childNodes[1].value);
    
    var request = $.ajax({
      url: "inDictionary.php",
      type: "GET",
      data: {   lang1 : lang1,
                lang2 : lang2,
                currLang : currLang,
                word : word,
                rootOfWord : rootOfWord,
                paradigm : paradigm,
                name:name},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var theMsgId;
        if (currLang == lang1)
        {
			theMsgId = 1;
		}
		else
		{
			theMsgId = 2;
		}
        resetMsg(theMsgId);
        if (myJSON.result == "-1")
        {
            rtable.className =  "blueTable";
            document.getElementById("already" + theMsgId).hidden = false;
            rtable.query = "";
        }
        else
        {
            rtable.className =  "greenTable";
            rtable.query = myJSON.result;
            
            var tb = document.createElement("textArea");
            var li = document.createElement("li");
            var span = document.createElement("div");
            var input = document.createElement("input");
            input.type = "hidden";
            input.value = myJSON.node;
            span.innerHTML = currLang;
            tb.setAttribute('type', 'text');
            tb.value = rtable.query;
            tb.defaultValue = rtable.query;
            //~ document.getElementById("result").appendChild(tb);
            li.appendChild(span);
            li.appendChild(tb);
            li.appendChild(input);
            document.getElementById("result").appendChild(li);
            document.getElementById("toInsert" + theMsgId).hidden = false;
        }
    });

    request.fail(function(jqXHR, textStatus) {
      alert( WORD_ERROR );
    });
}

function checkHasTranslation(lang1, lang2)
{
    var lword = (document.getElementById('table1').childNodes[1].childNodes[0].childNodes[0].childNodes[0].value);
    var rword = (document.getElementById('table2').childNodes[1].childNodes[0].childNodes[0].childNodes[0].value);

	var both = document.getElementById("B").checked;
	var lr = document.getElementById("LR").checked;
	var rl = document.getElementById("RL").checked;
	
	var lcat = "";
	var rcat = "";	
	
	var option;
	var index;
	
	option = document.getElementById("parad_table1_0")
	index = option.selectedIndex;
	lcat = option.parentNode.childNodes[2].childNodes[index].childNodes[0].innerHTML;
	
	option = document.getElementById("parad_table2_0")
	index = option.selectedIndex;
	rcat = option.parentNode.childNodes[2].childNodes[index].childNodes[0].innerHTML;
	
	if (lword == "" || rword == "")
	{
		alert( WORD_UNDEF);
		clearCell(document.getElementById("result"));
		var button = document.getElementById("generate");
		button.disabled = "";
		button.value = GENERATE;
		return;
	}
	if (document.getElementById("table1").className == "redTable" ||
		document.getElementById("table2").className == "redTable")
	{
		alert(PARAD_UNDEF);
		if (document.getElementById("table1").className == "redTable")
		{
			document.getElementById("undef1").hidden = false;
		}
		if (document.getElementById("table2").className == "redTable")
		{
			document.getElementById("undef2").hidden = false;
		}
		var button = document.getElementById("generate");
		button.disabled = "";
		button.value = GENERATE;
		return;
	}

    var request = $.ajax({
      url: "hasTranslation.php",
      type: "GET",
      data: {   lang1 : lang1,
                lang2 : lang2,
                lword : lword,
                rword : rword,
                name : name,
                both : both,
                lr : lr,
                rl : rl,
                lcat : lcat,
                rcat : rcat},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
		var tb;
		var li;
		var span;
		var input;
		
		var button = document.getElementById("generate");
		button.disabled = "";
		button.value = GENERATE;
		if (myJSON.result.length == 0)
		{
			clearCell(document.getElementById("result"));
			alert(TRANS_EXISTS);
		}
		else
		{
			for (i in myJSON.result)
			{
				tb = document.createElement("textarea");
				li = document.createElement("li");
				span = document.createElement("div");
				span.innerHTML = myJSON.result[i].lang;
				input = document.createElement("input");
				input.type = "hidden";
				input.value = myJSON.result[i].node;
				tb.setAttribute('type', 'text');
				tb.setAttribute('value', myJSON.result[i].query);
				tb.setAttribute('defaultValue', myJSON.result[i].query);
				tb.defaultValue = myJSON.result[i].query;
				tb.value = myJSON.result[i].query;
				tb.className = "redBorder";
				//~ document.getElementById("result").appendChild(tb);
				li.appendChild(span);
				li.appendChild(tb);
				li.appendChild(input);
				document.getElementById("result").appendChild(li);
			}
			button = document.getElementById("insert");
			if (installed)
			{
				button.disabled="";
				button.value = INSERT;	
				button.style.cssText = "color:red";
			}
			else
			{
				button.disabled="Disabled";
				button.value = UNAVAILABLE;
				button.style.cssText = "";
			}
			setClass("Mostrar", HIDE_INSERT_QUERIES, "extendedDivResult", "theResult");
		}
    });

    request.fail(function(jqXHR, textStatus) {
      alert( TRANS_ERROR  );
    });

}

function checkHasTranslationWait(lang1, lang2)
{
	var tableL = document.getElementById("table1");
	var tableR = document.getElementById("table2");

	if (tableL.className == "table" || tableR.className == "table")
	{
		setTimeout(function() {checkHasTranslationWait(lang1, lang2);}, 500);
		return;
	}
	checkHasTranslation(lang1, lang2);
}

function genInsert()
{
    var table;
    var i;
    var word;
    var rootOfWord;
    var paradigm;
    var option;
    var lang1 = document.getElementById("lang1");
    var lang2 = document.getElementById("lang2");
    lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;
    var button = document.getElementById("insert");
    var div = document.getElementById("result");
    
	while (div.childNodes.length > 0)
	{
		div.removeChild(div.childNodes[0]);
	}    
	
	button.disabled = "disabled";
	button.value = GENERATING;
	button.style.cssText = "";
	
	button = document.getElementById("generate");
	button.disabled = "disabled";
	button.value = GENERATING;
	
    checkInDictionary("table1", lang1, lang2, lang1);
    checkInDictionary("table2", lang1, lang2, lang2);

    checkHasTranslationWait(lang1, lang2);
}

function insert()
{
	var list = document.getElementById("result");
	var first = true;
	var lang1 = document.getElementById("lang1");
	var lang2 = document.getElementById("lang2");
	lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;
    
    document.getElementById("insert").style.cssText = "";
    
	insertionsLeft = list.childNodes.length;
	for (i = 0; i < list.childNodes.length; i++)
	{
		if (list.childNodes[i].childNodes[0].className == "green")
		{
			continue;
		}
		var request = $.ajax({
		  url: "insert.php",
		  type: "GET",
		  data: { 	"name" : name, 
					"query" : list.childNodes[i].childNodes[1].value,
					"elem" : i, 
					"node" : list.childNodes[i].childNodes[2].value, 
					"lang1" : lang1,
					"lang2" : lang2},
		  dataType: "html"
		});
		
		request.done(function(msg) {
			var myJSON = JSON.parse(msg);
			document.getElementById("result").childNodes[myJSON.elem].childNodes[0].className = "green";
			insertionsLeft--;
			if (insertionsLeft == 0)
			{
				resetMsg(1);
				resetMsg(2);
				
				document.getElementById("already1").hidden=false;
				document.getElementById("already2").hidden=false;
				
				alert(INSERT_OK);
			}
		});

		request.fail(function(jqXHR, textStatus) {
		  alert( INSERT_ERROR );
		});
	}
}

function insertWait()
{
	if (document.getElementById("insert").value != INSERT)
	{
		setTimeout(function() {checkHasTranslationWait(lang1, lang2);}, 500);
		return;
	}
	insert();
}

function getInstalledPairs()
{
	var request = $.ajax({
      url: "getInstalledLangs.php",
      type: "GET",
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var cb = document.getElementById("lang1");
        var cb2 = document.getElementById("lang2");
        var opt;
        while (cb.options.length > 0)
        {
			cb.remove(0);
		}
        while (cb2.options.length > 0)
        {
			cb2.remove(0);
		}
        for (i in myJSON.result)
        {
            if (myJSON.result[i].value != undefined)
            {
				opt = document.createElement("option");
				opt.text = myJSON.result[i].value;
				opt.value = myJSON.result[i].value;
				cb.options.add(opt);
            }
        }
        searchPairs();
    });

    request.fail(function(jqXHR, textStatus) {
      alert( INSTALLED_ERROR );
    });
}

function searchPairs()
{
	var lang1 = document.getElementById("lang1");
    lang1 = lang1.options[lang1.selectedIndex].value;
	var request = $.ajax({
      url: "getRightPairs.php",
      type: "GET",
      data: {lang : lang1},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var cb = document.getElementById("lang2");
        var opt;
        while (cb.options.length > 0)
        {
			cb.remove(0);
		}
        for (i in myJSON.result)
        {
            if (myJSON.result[i].value != undefined)
            {
				opt = document.createElement("option");
				opt.text = myJSON.result[i].value;
				opt.value = myJSON.result[i].value;
				cb.options.add(opt);
            }
        }
        //pairExists();
		
    });

    request.fail(function(jqXHR, textStatus) {
      alert( INSTALLED_ERROR );
    });
    
    if (document.getElementById("cFiles1") != null)
    {
		var request2 = $.ajax({
		  url: "getUserConfigs.php",
		  type: "GET",
		  data: {
			  name : name,
			  lang : lang1,
			  lang1: lang1,
			  lang2: lang2},
		  dataType: "html"
		});

		request2.done(function(msg) {
			var myJSON = JSON.parse(msg);
			var lang1 = document.getElementById("lang1");
			var table = document.getElementById("cFiles1");
			var tr;
			var td;
			var rbutton;
			var aLink;
			lang1 = lang1.options[lang1.selectedIndex].value;
			clearCell(table);

			tr = document.createElement("tr");
			
			td = document.createElement("td");
			rbutton = document.createElement("input");
			rbutton.type = "radio";
			rbutton.name = "cFiles1Group";
			rbutton.checked = "checked";
			rbutton.value = "simpledix/config." + lang1 + "-" + lang2 + "." + lang1 + ".xml";
			td.appendChild(rbutton);
			tr.appendChild(td);
			
			td = document.createElement("td");
			aLink = document.createElement("a");
			aLink.href = "simpledix/config." + lang1 + "-" + lang2 + "." + lang1 + ".xml";
			aLink.innerHTML = "default";
			aLink.target = "_blank";	
			td.appendChild(aLink);
			tr.appendChild(td);
			
			table.appendChild(tr);
			
			for (i in myJSON.result)
			{
				tr = document.createElement("tr");
				
				td = document.createElement("td");
				rbutton = document.createElement("input");
				rbutton.type = "radio";
				rbutton.name = "cFiles1Group";
				rbutton.checked = "checked";
				rbutton.value = myJSON.result[i];
				td.appendChild(rbutton);
				tr.appendChild(td);
				
				td = document.createElement("td");
				aLink = document.createElement("a");
				aLink.href = myJSON.result[i];
				aLink.innerHTML = "user defined";
				aLink.target = "_blank";	
				td.appendChild(aLink);
				tr.appendChild(td);
				
				table.appendChild(tr);
			}
		});

		request2.fail(function(jqXHR, textStatus) {
		  alert( INSTALLED_ERROR );
		});
	}
}

function pairExists()
{
	var lang1 = document.getElementById("lang1");
    var lang2 = document.getElementById("lang2");
    lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;
    		
    init();

	var request = $.ajax({
      url: "pairExists.php",
      type: "GET",
      data: {lang1 : lang1,
			 lang2 : lang2,
			 name : name},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var opt;
        var tb = document.getElementById("installed");
        var tb = document.getElementById("insert");
		if (myJSON.result)
		{
			tb.innerHTML = INSTALLED;
			if (tb.value != GENERATING)
			{
				tb.disabled="";
				tb.value = INSERT;
			}
			installed = true;
		}
		else
		{
			tb.innerHTML = UNAVAILABLE;
			if (tb.value != GENERATING)
			{
				tb.disabled="disabled";
				tb.value = UNAVAILABLE;
			}
			installed = false;
		}
    });

    request.fail(function(jqXHR, textStatus) {
      window.location = "upload.php";
    });
    
	var request2 = $.ajax({
      url: "getUserConfigs.php",
      type: "GET",
      data: {
		  name : name,
		  lang : lang2,
		  lang1: lang1,
		  lang2: lang2},
      dataType: "html"
    });

    request2.done(function(msg) {
        var myJSON = JSON.parse(msg);
		var lang2 = document.getElementById("lang2");
		var table = document.getElementById("cFiles2");
		var tr;
		var td;
		var rbutton;
		var aLink;
		lang2 = lang2.options[lang2.selectedIndex].value;
		clearCell(table);

		tr = document.createElement("tr");
		
		td = document.createElement("td");
		rbutton = document.createElement("input");
		rbutton.type = "radio";
		rbutton.name = "cFiles2Group";
		rbutton.checked = "checked";
		rbutton.value = "simpledix/config." + lang2 + ".xml";
		td.appendChild(rbutton);
		tr.appendChild(td);
		
		td = document.createElement("td");
		aLink = document.createElement("a");
		aLink.href = "simpledix/config." + lang2 + ".xml";
		aLink.innerHTML = "default";
		aLink.target = "_blank";	
		td.appendChild(aLink);
		tr.appendChild(td);
		
		table.appendChild(tr);
		
        for (i in myJSON.result)
        {
			tr = document.createElement("tr");
			
			td = document.createElement("td");
			rbutton = document.createElement("input");
			rbutton.type = "radio";
			rbutton.name = "cFiles2Group";
			rbutton.checked = "checked";
			rbutton.value = myJSON.result[i];
			td.appendChild(rbutton);
			tr.appendChild(td);
			
			td = document.createElement("td");
			aLink = document.createElement("a");
			aLink.href = myJSON.result[i];
			aLink.innerHTML = "user defined";
			aLink.target = "_blank";	
			td.appendChild(aLink);
			tr.appendChild(td);
			
			table.appendChild(tr);
        }
    });

    request2.fail(function(jqXHR, textStatus) {
      alert(INSTALLED_ERROR );
    });

}

function exportDix()
{
	if(!confirm(EXPORT))
		return;
	var lang1 = document.getElementById("lang1");
    var lang2 = document.getElementById("lang2");
    lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;

	var div = document.getElementById("links");
	while (div.childNodes.length > 0)
	{
		div.removeChild(div.childNodes[0]);
	}    
	
	document.getElementsByName("Exportar")[0].value = EXPORTING;
	document.getElementsByName("Exportar")[0].disabled = "Disabled";
    
    var request = $.ajax({
      url: "exportDix.php",
      type: "GET",
      data: {lang1 : lang1,
			 lang2 : lang2,
			 name : name},
      dataType: "html"
    });
    
    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var opt;
        var a;
        var li;
        var ul = document.getElementById("links");
        while (div.childNodes.length > 0)
        {
			div.removeChild(div.childNodes[0]);
		}
		for (i in myJSON.result)
		{
			a = document.createElement("a");
			a.href = "fileDownloader.php?file="+myJSON.result[i];
			a.innerHTML = myJSON.result[i].split("/")[2] + "<br/>";
			a.target = "_blank";
			li = document.createElement("li");
			li.appendChild(a);
			ul.appendChild(li);
		}
		document.getElementsByName("Exportar")[0].value = EXPORT;
		document.getElementsByName("Exportar")[0].disabled = "";
		document.getElementById("dictionaries").hidden = false;
		alert(EXPORT_OK);
    });

    request.fail(function(jqXHR, textStatus) {
      alert( EXPORT_ERROR );
    });
}

function deleteUser()
{
	if(confirm(DELETE_CONFIRM))
		window.location = "deleteUser.php?name=" + name;
}

function fastDelete(name, elem)
{
	var request = $.ajax({
      url: "deleteUser.php?name=" + name,
      type: "GET",
      data: {},
      dataType: "html"
    });
    request.done(function(msg) {
		var p = document.createElement("p");
		var node = elem.parentNode;
		p.innerHTML = msg;
		clearCell(node);
		node.appendChild(p);
	});
	
	request.fail(function(jqXHR, textStatus) {
		alert(ERROR);
	});
}

function deleteAll()
{
	if(!confirm(DELETE_ALL_CONFIRM))
		return;
	
	var list = document.getElementById("deleteList");
	
	for (i in list.childNodes)
	{
		if (list.childNodes[i].childNodes[1] != undefined)
			list.childNodes[i].childNodes[1].click();;
	}
}

function checkFastMode(where)
{
	rb = document.getElementsByName("speed");
	
	var radioLength = rb.length;

	for(var i = 0; i < radioLength; i++) {
		if(rb[i].checked) {
			break;
		}
	}
	
	if (i == 0)
	{
		return;
	}
	if (i == 1)
	{
		if (where == '2')
		{
			document.getElementById("table1").childNodes[1]
				.childNodes[0].childNodes[0].childNodes[0].focus();
		}
	}
	if (i == 2)
	{
		if (where == '1')
		{
			document.getElementById("table1").childNodes[1]
				.childNodes[0].childNodes[0].childNodes[0].focus();
			if (document.getElementById("insert").disabled == "")
			{
				insertWait();
			}
		}
	}
}

function checkFastMode1(where)
{
	rb = document.getElementsByName("speed");
	
	var radioLength = rb.length;

	for(var i = 0; i < radioLength; i++) {
		if(rb[i].checked) {
			break;
		}
	}
	
	if (i == 0)
	{
		return;
	}
	if (i == 1)
	{
		return;
	}
	if (i == 2)
	{
		if (where == '1')
		{
			genInsert();
		}
	}
}

function selectText(elem)
{
	elem.select();
}

function toggleClass(button, visText, invisText, class1, class2, what)
{
	var elem = document.getElementById(what);
	if (elem.className == class2)
	{
		elem.className = class1;
		if (button.value)
			button.value = visText;
		else
			document.getElementById(button).value = visText;
	}
	else
	{
		
		elem.className = class2;
		if (button.value)
			button.value = invisText;
		else
			document.getElementById(button).value = invisText;
	}
}

function setClass(button, text, aClass, what)
{
	var elem = document.getElementById(what);

	elem.className = aClass;
	if (button.value)
		button.value = text;
	else
		document.getElementById(button).value = text;


}

function check_file(elem, suffixs)
{
    str=elem.value.toLowerCase();
	for (i in suffixs)
	{
		if (str.indexOf(suffixs[i], str.length - suffixs[i].length) != -1)
		{
			return;
		}
	}
	elem.className = "roundRed";
	elem.value = "";
	alert(EXTENSION_ERROR + suffixs);
}

function checkUpload()
{
	var lang1 = document.getElementById("lang1");
	var lang2 = document.getElementById("lang2");
	
	if (lang1.type != "text")
	{
		lang1 = lang1.options[lang1.selectedIndex];
		lang2 = lang2.options[lang2.selectedIndex];
	}
	
	if (lang1.value == lang2.value)
	{
		alert(SAME_LANGUAGES);
		return;
	}
	
	if (document.getElementById("req1").className == ""
		&& document.getElementById("leftConf").value == "")
	{
		alert(MISSING_CONF);
		return;
	}
	if (document.getElementById("req2").className == ""
		&& document.getElementById("rightConf").value == "")
	{
		alert(MISSING_CONF);
		return;
	}
	if (document.getElementById("lang1").value == "" ||
		document.getElementById("lang2").value == "")
	{
		alert(UNDEF_LANG);
		return;
	}
	
	
	if (document.getElementById("leftDix").value != "" &&
		document.getElementById("rightDix").value != "" &&
		document.getElementById("biDix").value != "")
	{
		document.getElementById("form").submit();
		return;
	}
	
	if (document.getElementById("leftDix").value == "")
	{
		document.getElementById("leftDix").className = "roundRed";
	}
	if (document.getElementById("rightDix").value == "")
	{
		document.getElementById("rightDix").className = "roundRed";
	}
	if (document.getElementById("biDix").value == "")
	{
		document.getElementById("biDix").className = "roundRed";
	}
	
	alert (MISSING);
}

function showInstalled()
{
	var cb = document.getElementById("showInstalledCB");
	var hold1 = document.getElementById("lang1holder");
	var hold2 = document.getElementById("lang2holder");
	
	clearCell(hold1);
	clearCell(hold2);
	
	var opt1 = document.getElementById("opt1");
	var opt2 = document.getElementById("opt2");
	var req1 = document.getElementById("req1");
	var req2 = document.getElementById("req2");	
	
	opt1.className = "";
	opt2.className = "";
	req1.className = "hidden";
	req2.className = "hidden";
	
	hold1.innerHTML = DICTIONARY;
	hold2.innerHTML = DICTIONARY;
	
	if (cb.checked != "")
	{
		var select = document.createElement("select");
		select.id = "lang1";  
		select.name = "lang1" 
		select.onchange="searchPairs()";
		select.onkeyup="searchPairs()";
		
		hold1.appendChild(select);
		
		select = document.createElement("select");
		select.id = "lang2";  
		select.name = "lang2" 
		
		hold2.appendChild(select);
		
		getInstalledPairs();
		
		document.getElementById("req1").className = "hidden";
		document.getElementById("req2").className = "hidden";
		document.getElementById("opt1").className = "";
		document.getElementById("opt2").className = "";
		document.getElementById("opt1").parentNode.className = "confUpload";
		document.getElementById("opt2").parentNode.className = "confUpload";
	}
	else
	{
		var tb = document.createElement("input");
		tb.type = "text";
		tb.id = "lang1";
		tb.name = "lang1";
		//tb.setAttribute('onBlur', 'confExists(this)');
		
		hold1.appendChild(tb);
		
		tb = document.createElement("input");
		tb.type = "text";
		tb.id = "lang2";
		tb.name = "lang2";
		//tb.setAttribute('onBlur', 'confExists(this)');
		
		hold2.appendChild(tb);
		
		document.getElementById("req1").className = "";
		document.getElementById("req2").className = "";
		document.getElementById("opt1").className = "hidden";
		document.getElementById("opt2").className = "hidden";
		document.getElementById("opt1").parentNode.className = "roundYellow";
		document.getElementById("opt2").parentNode.className = "roundYellow";
	}
}

function confExists(elem)
{
    var request = $.ajax({
      url: "confExists.php",
      type: "GET",
      data: {lang : elem.value},
      dataType: "html"
    });	
    
    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        
        var opt;
        var req;
        
        if (elem.name == "lang1")
        {
			opt = document.getElementById("opt1");
			req = document.getElementById("req1");
		}
		else
		{
			opt = document.getElementById("opt2");
			req = document.getElementById("req2");
		}
	
		
		if (myJSON.result)
		{
			opt.className = "";
			req.className = "hidden";
			opt.parentNode.className = "confUpload";
		}
		else
		{
			opt.className = "hidden";
			req.className = "";		
			opt.parentNode.className = "roundYellow";
		}
    });

    request.fail(function(jqXHR, textStatus) {
      alert( CONFCHECK_ERROR );
    });
}

function getUserPair()
{
	var request = $.ajax({
      url: "installedPair.php",
      type: "GET",
      data: {name : name},
      dataType: "html"
    });

    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var cb1 = document.getElementById("lang1");
        var cb2 = document.getElementById("lang2");
        var opt;
		clearCell(cb1);
		clearCell(cb2);
		if (myJSON.lang1)
		{
			opt = document.createElement("option");
			opt.text = myJSON.lang1;
			opt.value = myJSON.lang1;
			cb1.options.add(opt);
		}
		if (myJSON.lang2)
		{
			opt = document.createElement("option");
			opt.text = myJSON.lang2;
			opt.value = myJSON.lang2;
			cb2.options.add(opt);
		}
    
		document.getElementById("lang1Label").innerHTML = myJSON.lang1;
		document.getElementById("lang2Label").innerHTML = myJSON.lang2;	
		getConfFiles();
    });

    request.fail(function(jqXHR, textStatus) {
      alert( INSTALLED_ERROR );
    });	
}

function getConfFiles()
{
	var lang1 = document.getElementById("lang1");
    var lang2 = document.getElementById("lang2");
    lang1 = lang1.options[lang1.selectedIndex].value;
    lang2 = lang2.options[lang2.selectedIndex].value;
   
	var request = $.ajax({
	  url: "getUserConfigs.php",
	  type: "GET",
	  data: {
		  name : name,
		  lang : lang1,
		  lang1: lang1,
		  lang2: lang2},
	  dataType: "html"
	});

	request.done(function(msg) {
		var myJSON = JSON.parse(msg);
		var lang1 = document.getElementById("lang1");
		var table = document.getElementById("cFiles1");
		var tr;
		var td;
		var rbutton;
		var aLink;
		lang1 = lang1.options[lang1.selectedIndex].value;
		clearCell(table);

		tr = document.createElement("tr");
		
		td = document.createElement("td");
		rbutton = document.createElement("input");
		rbutton.type = "radio";
		rbutton.name = "cFiles1Group";
		rbutton.checked = "checked";
		rbutton.value = "simpledix/config." + lang1 + "-" + lang2 + "." + lang1 +".xml";
		td.appendChild(rbutton);
		tr.appendChild(td);
		
		td = document.createElement("td");
		aLink = document.createElement("a");
		aLink.href = "simpledix/config." + lang1 + "-" + lang2 + "." + lang1 + ".xml";
		aLink.innerHTML = "default";
		aLink.target = "_blank";	
		td.appendChild(aLink);
		tr.appendChild(td);
		
		table.appendChild(tr);
		
		for (i in myJSON.result)
		{
			tr = document.createElement("tr");
			
			td = document.createElement("td");
			rbutton = document.createElement("input");
			rbutton.type = "radio";
			rbutton.name = "cFiles1Group";
			rbutton.checked = "checked";
			rbutton.value = myJSON.result[i];
			td.appendChild(rbutton);
			tr.appendChild(td);
			
			td = document.createElement("td");
			aLink = document.createElement("a");
			aLink.href = myJSON.result[i];
			aLink.innerHTML = "user defined";
			aLink.target = "_blank";	
			td.appendChild(aLink);
			tr.appendChild(td);
			
			table.appendChild(tr);
		}		
	});

	request.fail(function(jqXHR, textStatus) {
	  alert( INSTALLED_ERROR );
	});
    
    
	var request2 = $.ajax({
      url: "getUserConfigs.php",
      type: "GET",
      data: {
		  name : name,
		  lang : lang2,
		  lang1 : lang1,
		  lang2 : lang2
		  },
      dataType: "html"
    });
    
    request2.done(function(msg) {
        var myJSON = JSON.parse(msg);
		var lang2 = document.getElementById("lang2");
		var table = document.getElementById("cFiles2");
		var tr;
		var td;
		var rbutton;
		var aLink;
		lang2 = lang2.options[lang2.selectedIndex].value;
		clearCell(table);

		tr = document.createElement("tr");
		
		td = document.createElement("td");
		rbutton = document.createElement("input");
		rbutton.type = "radio";
		rbutton.name = "cFiles2Group";
		rbutton.checked = "checked";
		rbutton.value = "simpledix/config." + lang1 + "-" + lang2 + "." + lang2 + ".xml";
		td.appendChild(rbutton);
		tr.appendChild(td);
		
		td = document.createElement("td");
		aLink = document.createElement("a");
		aLink.href = "simpledix/config." + lang1 + "-" + lang2 + "." + lang2 + ".xml";
		aLink.innerHTML = "default";
		aLink.target = "_blank";	
		td.appendChild(aLink);
		tr.appendChild(td);
		
		table.appendChild(tr);
		
        for (i in myJSON.result)
        {
			tr = document.createElement("tr");
			
			td = document.createElement("td");
			rbutton = document.createElement("input");
			rbutton.type = "radio";
			rbutton.name = "cFiles2Group";
			rbutton.checked = "checked";
			rbutton.value = myJSON.result[i];
			td.appendChild(rbutton);
			tr.appendChild(td);
			
			td = document.createElement("td");
			aLink = document.createElement("a");
			aLink.href = myJSON.result[i];
			aLink.innerHTML = "user defined";
			aLink.target = "_blank";	
			td.appendChild(aLink);
			tr.appendChild(td);
			
			table.appendChild(tr);
        }
        getKinds();   
    });

    request2.fail(function(jqXHR, textStatus) {
      alert(INSTALLED_ERROR );
    });

	 
}

function getKinds()
{
	var rb1 = document.getElementsByName("cFiles1Group");
	var rb2 = document.getElementsByName("cFiles2Group");
	var radioLength = rb1.length;
	for(var i = 0; i < radioLength; i++) {
		if(rb1[i].checked) {
			break;
		}
	}
	if (i == radioLength)
	{
		setTimeout(function() {getKinds();}, 500);
		return;
	}
		
	radioLength = rb2.length;
	for(var i2 = 0; i2 < radioLength; i2++) {
		if(rb2[i2].checked) {
			break;
		}
	}
	if (i2 == radioLength)
	{
		setTimeout(function() {getKinds();}, 500);
		return;
	}
 	
 	 	
    var request = $.ajax({
      url: "getKinds.php",
      type: "GET",
      data: {   configFile: rb1[i].value,
                configFile2: rb2[i2].value},
      dataType: "html"
    });  
    
    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var opt;
        var cb = document.getElementById("categorias");
        opt = document.createElement("option");
        opt.value = ALL;
        opt.text = ALL;
        cb.options.add(opt);
        for (i in myJSON.result)
        {
			opt = document.createElement("option");
			opt.value = myJSON.result[i].value;
			opt.text = myJSON.result[i].value;
			cb.options.add(opt);
		}
	});
	
    request.fail(function(jqXHR, textStatus) {
      alert( LIST_ERROR );
    });
}

function clearInput(item)
{
	document.getElementById(item).value = "";
}

function getAllConfigs()
{
	var ul = document.getElementById("allConfs");
	
	var request = $.ajax({
      url: "getConfigFiles.php",
      type: "GET",
      dataType: "html"
    });
      
    request.done(function(msg) {
        var myJSON = JSON.parse(msg);
        var li;
        var a;
        for (i in myJSON.result)
        {
			li = document.createElement("li");
			a = document.createElement("a");
			a.href = myJSON.result[i];
			a.target = "_blank";
			a.innerHTML = myJSON.result[i].split("/")[1] + "<br/>";
			li.appendChild(a);
			ul.appendChild(li);
		}	
	});

    request.fail(function(jqXHR, textStatus) {
      alert(CONFCHECK_ERROR );
    });
}

function filterCategories(cat)
{
	var category = cat.options[cat.selectedIndex].value;

	filterTable(category, "table1");
	filterTable(category, "table2");
	warnMsg(1);
	warnMsg(2);
}

function filterTable(category, tableId)
{
	var table = document.getElementById(tableId);
	for (i = 0; i < table.rows[1].cells.length; i++)
	{
		filterCell(category, table.rows[1].cells[i]);
	}
}

function filterCell(category, cell)
{
	var tb = cell.childNodes[0];
	var items = cell.childNodes[1].childNodes;
	var opt = document.createElement("option");
	var ul = document.createElement("ul");
	var li;
	var div;
	
	ul.hidden = true;
	
	if (cell.childNodes[2])
	{
		cell.removeChild(cell.childNodes[2]);
	}
	
	cell.appendChild(ul);
	
	clearCell(tb);
    
    var o;
    var cat;
    for (o = 0; o < items.length; o++)
    {
		cat = items[o].childNodes[2].innerHTML;
		if (category == ALL || category == cat)
		{
			opt = document.createElement("option");
			opt.text = items[o].childNodes[0].innerHTML;
			opt.value = items[o].childNodes[1].innerHTML;
			tb.options.add(opt);
			
			li = document.createElement("li");
			div = document.createElement("div");
			div.innerHTML = cat;
			li.appendChild(div);
			ul.appendChild(li);
		}
    }
    
    tb.onchange();
}

function toggleClassCb(cb, class1, class2, what)
{
	var elem = document.getElementById(what);
	
	if (cb.checked)
	{
		elem.className = class1;
	}
	else
	{
		elem.className = class2;
	}
}

function hide(elem)
{
	elem.parentNode.hidden = 'hidden';
}

function changeSource()
{
	var leftUpload = document.getElementById("leftDix").parentNode;
	var rightUpload = document.getElementById("rightDix").parentNode;
	var biUpload = document.getElementById("biDix").parentNode;
	var left, right, bi;
	var rb = document.getElementsByName("fileSource");	
	var radioLength = rb.length;

	for(var i = 0; i < radioLength; i++) {
		if(rb[i].checked) {
			break;
		}
	}	
	
	clearCell(leftUpload);
	clearCell(rightUpload);
	clearCell(biUpload);
	
	left = document.createElement("input");
	left.id = "leftDix";
	right = document.createElement("input");
	right.id = "rightDix";
	bi = document.createElement("input");
	bi.id = "biDix";
	
	
	if (rb[i].value == "file")
	{
		left.type = "file";
		left.name = "leftDix";
		left.setAttribute("onclick", "this.className = ''; this.value = ''")
		left.setAttribute("onchange", "check_file(this, ['.dix', '.metadix'])");
		right.type = "file";
		right.name = "rightDix";
		right.setAttribute("onclick", "this.className = ''; this.value = ''")
		right.setAttribute("onchange", "check_file(this, ['.dix', '.metadix'])");
		bi.type = "file";
		bi.name = "biDix";
		bi.setAttribute("onclick", "this.className = ''; this.value = ''")
		bi.setAttribute("onchange", "check_file(this, ['.dix', '.metadix'])");
	}
	if (rb[i].value == "text")
	{
		left.type = "text";
		left.name = "leftDixUrl";
		right.type = "text";
		right.name = "rightDixUrl";
		bi.type = "text";
		bi.name = "biDixUrl";
	}
	
	leftUpload.appendChild(left);
	rightUpload.appendChild(right);
	biUpload.appendChild(bi);
}
