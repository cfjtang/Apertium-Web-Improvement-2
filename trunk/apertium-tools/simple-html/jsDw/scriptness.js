$(document).ready(function(){

	$('#dropDownSub').hide();
	
	var FromOrTo;
	
	$('#swapLanguages').click(function(){
		fromText = $('#selectFrom em').text();
		toText = $('#selectTo em').text();
		$('#selectTo em').html(fromText);
		$('#selectFrom em').html(toText);
		
	});

	$('.itemSelect').toggle(function(){
		if($(this).attr("id")=="selectFrom"){
			FromOrTo="from";
			$('#dropDownSub').hide();
			$('#dropDownSub').css('margin-left',00);
			
		} else {
			FromOrTo = "to";
			$('#dropDownSub').hide();
			$('#dropDownSub').css('margin-left',287);
		}
			$('#dropDownSub').show();
		
	}, function(){
		$('#dropDownSub').hide()	
	});
	
	$('#dropDownSub a').click(function(){
		
			$('#dropDownSub a').removeClass('language-selected');
			$(this).addClass('language-selected');
		if(FromOrTo=="from"){	
			$('#selectFrom em').html($(this).text());
		} else $('#selectTo em').html($(this).text());
	});
	
	return false;
});


$(document).click(function(){
	$('#dropDownSub').hide();
});