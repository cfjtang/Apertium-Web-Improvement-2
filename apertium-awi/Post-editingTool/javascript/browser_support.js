//coding: utf-8
/*
	Apertium Web Post Editing tool
	Small module to fix browser compatibility
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

function initBrowsers()
{
	//Fix selection for cross browser support
	if(!window.getSelection)
	{
		if(!document.getSelection)
		{
			//load ierange library if needed
			var load_script = document.createElement('script');
			load_script.type = 'text/javascript';
			load_script.src = 'javascript/ierange.js';
			document.getElementsByTagName("head")[0].appendChild(load_script);
		}
		else
		{
			window.getSelection = document.getSelection;
		}
	}
}

function getEvent(e)
{
	//fix IE generated events to be standards compliants
	
	var event = e || window.event;
	if( ! event.target )
	{
		event.target = event.srcElement;
	}
	
	return event;
}