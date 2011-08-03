//coding: utf-8
/*
	Apertium Web Post Editing tool
	Script file for index.php
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
	
	Contributed By Mougey Camille <commial@gmail.com> for Google Summer of Code 2011
	Mentors : Arnaud Vié, Luis Villarejo
*/

// script from : http://www.webbricks.org/bricks/fade/
function setOpacity(a,b){b=(b==1)?0.99999:b;a.style.opacity=b;a.style.filter='alpha(opacity='+b*100+')';a.style.MozOpacity=b;a.style.KhtmlOpacity=b}function fade(a,b,c,d){this.elem=a||document.body;this.to=b!==undefined?b:1;var e=this.elem.style;this.from=(c===undefined?(!e.opacity&&e.opacity!==0?(this.to>0?0:1):parseFloat(e.opacity)):c);d=d||{};this.duration=d.duration||500;this.frameRate=d.frameRate||30;this.onFinish=d.onFinish;this.totalFrames=Math.ceil(this.duration/1000*this.frameRate);this.perFrame=(this.to-this.from)/this.totalFrames;this.frameNb=0;var f=this;this.next=function(){this.prog=setTimeout(function(){f.frame()},1000/this.frameRate)};this.frame=function(){setOpacity(this.elem,this.from+this.perFrame*this.frameNb);if(this.frameNb===this.totalFrames){setOpacity(this.elem,this.to);if(typeof this.onFinish=='function'){setTimeout(this.onFinish,1)}}else{this.frameNb++;this.next()}};this.next()}

function loadTextSourceContent()
{
	/* Load the content in the div 'textsource_content' according to the value of 'input_type' */
	
	var element = document.getElementById('textsource_content');
	switch (document.getElementById('input_type').value) {
		case 'none':
			setElementContent(element, '');
			break;
		case 'file':
			setElementContent(element, document.getElementById('textsource_content_file').innerHTML);
			break;
		case 'wiki':
			setElementContent(element, document.getElementById('textsource_content_wiki').innerHTML);
			break;
		default:
			setElementContent(element, '');
			break;
		}
	}

function loadTMXContent()
{
	/* Load the content in the div 'TMX_content' according to the value of 'use_TMX' */
	
	var element = document.getElementById('TMX_content');
	switch (document.getElementById('use_TMX').value) {
		case 'none':
			setElementContent(element, '');
			break;
		case 'file':
			setElementContent(element, document.getElementById('TMX_content_file').innerHTML);
			break;
		case 'URL':
			setElementContent(element, document.getElementById('TMX_content_URL').innerHTML);
			break;
		case 'externTM':
			setElementContent(element, document.getElementById('TMX_content_externTM').innerHTML);
			break;
		default:
			setElementContent(element, '');
			break;
		}
	}

function setElementContent(element, content)
{
	/* Replace the content of the element with content, with a fade effect */
	
	setOpacity(element, 0);
	element.innerHTML = content;
	fade(element, 1);
	}
