
//coding: utf-8
/*
	Apertium Web Post Editing tool
	Main page script
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús

	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

window.onload = initJSMain;

function initJSMain()
{
	//initialise scripts for the page
	
	initBrowsers();
	initJSEditors();
	
	initPasteEvent();
	
	initAjax();
	
	initDictionaries();
	
	document.onclick = handleClick;
	
	document.onkeydown = handleKeyDown;
	document.onkeypress = handleKeyPress;
	document.addEventListener("textInput", handleKeyPress, false);
	
}

function handleClick(e)
{
	var event = getEvent(e);
	
	//manage clicks on main interface buttons
	if(event.target.tagName.toLowerCase() == 'input' && event.target.type.toLowerCase() == 'submit')
	{
		switch(event.target.name)
		{
			case 'check_input' :
				
				prepareSubmit();
				
				var query_string = 'action_request=proof_input&' + buildRequestString(document.mainform, new Array('language_pair', 'text_input') );
				var resultManager = function(str)
				{
					text_in_js_on.innerHTML = str+'<br />';
						
					//store previous logs
					extractLogs(text_in_js_on);
					//build words lists for logging
					text_in_js_on.text_object = buildWordList(text_in_js_on);

				};
				
				ajaxRequest(query_string, resultManager);
				
				return false; //disable the form submitting
				
				break;
			
			case 'check_output' :
				
				prepareSubmit();
				
				var query_string = 'action_request=proof_output&' + buildRequestString(document.mainform, new Array('language_pair', 'text_output') );
				var resultManager = function(str)
				{
					text_out_js_on.innerHTML = str+'<br />';
	
					//store previous logs
					extractLogs(text_out_js_on);
					//build words lists for logging
					text_out_js_on.text_object = buildWordList(text_out_js_on);

				};
				
				ajaxRequest(query_string, resultManager);
				
				return false; //disable the form submitting
				
				break;
			
			case 'submit_input' :
				
				prepareSubmit();
				
				var query_string = 'action_request=translate&' + buildRequestString(document.mainform, new Array('inputTMX', 'language_pair', 'text_input', 'pretrans_src[]', 'pretrans_dst[]', 'inputTMX_content') );
				var resultManager = function(str)
				{
					text_out_js_on.innerHTML = str+'<br />';

					//store previous logs from input
					extractLogs(text_in_js_on);
					//delete logs from output
					text_out_js_on.final_log = '';
					//build words lists for logging
					text_out_js_on.text_object = buildWordList(text_out_js_on);

				};
				
				ajaxRequest(query_string, resultManager);
				
				return false; //disable the form submitting
				
				break;
			
			case 'replace_input' :
				
				prepareSubmit();
				
				var query_string = 'action_request=replace_input&' + buildRequestString(document.mainform, new Array('language_pair', 'text_input', 'pretrans_src[]', 'pretrans_dst[]', 'pretrans_case[]') );
				var resultManager = function(str)
				{
					text_in_js_on.innerHTML = str+'<br />';

					//store previous logs from input
					extractLogs(text_in_js_on);
					//add log for the replacements
					text_in_js_on.final_log += 'Manual replacements : <ul>';
					if(document.mainform["pretrans_src[]"].nodeType && document.mainform["pretrans_src[]"].nodeType == 1)
					{
						var src_list = [ document.mainform["pretrans_src[]"] ];
						var dst_list = [ document.mainform["pretrans_dst[]"] ];
					}
					else
					{
						var src_list = document.mainform["pretrans_src[]"];
						var dst_list = document.mainform["pretrans_dst[]"];
					}
				
					for(var i = 0; i < src_list.length; i++)
					{
						text_in_js_on.final_log += '<li>'+src_list[i].value+' by <strong>'+dst_list[i].value+'</strong></li>';
					}
					text_in_js_on.final_log += '</ul><br />\n';
					//build words lists for logging
					text_in_js_on.text_object = buildWordList(text_in_js_on);
					
				};
				
				ajaxRequest(query_string, resultManager);

				return false; //disable the form submitting
				
				break;
			
			case 'replace_output' :
				
				prepareSubmit();
				
				var query_string = 'action_request=replace_output&' + buildRequestString(document.mainform, new Array('language_pair', 'text_output', 'posttrans_src[]', 'posttrans_dst[]', 'posttrans_case[]') );
				var resultManager = function(str)
				{
					text_out_js_on.innerHTML = str+'<br />';
						
					//store previous logs
					extractLogs(text_out_js_on);
					//add log for the replacements
					text_out_js_on.final_log += 'Manual replacements : <ul>';
					if(document.mainform["posttrans_src[]"].nodeType && document.mainform["pretrans_src[]"].nodeType == 1)
					{
						var src_list = [ document.mainform["posttrans_src[]"] ];
						var dst_list = [ document.mainform["posttrans_dst[]"] ];
					}
					else
					{
						var src_list = document.mainform["posttrans_src[]"];
						var dst_list = document.mainform["posttrans_dst[]"];
					}
				
					for(var i = 0; i < src_list.length; i++)
					{
						text_out_js_on.final_log += '<li>'+src_list[i].value+' by <strong>'+dst_list[i].value+'</strong></li>';
					}
					text_out_js_on.final_log += '</ul><br />\n';
					//build words lists for logging
					text_out_js_on.text_object = buildWordList(text_out_js_on);

				};
				
				ajaxRequest(query_string, resultManager);

				return false; //disable the form submitting
				
				break;
			
			case 'get_logs' :
				
				var out = text_in_js_on.final_log + "<strong>Translated the text</strong><br />\n" + extractLogs(text_out_js_on);
				
				//generate popup with output
				newwindow = window.open('','logs');
				newwindow.document.write(out);
				newwindow.document.close();	
				
				return false; //disable the form submitting
				
				break;
		}
	}
	
	//if the event hasn't been intercepted yet, let the text editor lib handle it
	return TE_handleClick(event);
}
