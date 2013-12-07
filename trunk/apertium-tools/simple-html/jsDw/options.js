$(document).ready(function(){
	document.getElementById('pagestyle').setAttribute('href', 'jsDw/style.css');
		if( navigator.userAgent.match(/Android/i)
 			|| navigator.userAgent.match(/webOS/i)
 			|| navigator.userAgent.match(/iPhone/i)
 			|| navigator.userAgent.match(/iPad/i)
 			|| navigator.userAgent.match(/iPod/i)
 			|| navigator.userAgent.match(/BlackBerry/i)
 			|| navigator.userAgent.match(/Windows Phone/i)
 		){
 			document.getElementById('pagestyle').setAttribute('href', 'jsDw/style_mobile.css');
 			$('#swapLanguages').html("Swap");
		}
});
