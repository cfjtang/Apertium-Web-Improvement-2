<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	Functions for string manipulation
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

function nl2br_r($txt)
{
	//convert line breaks to html
	return str_replace(array("\r", "\n"), array('', ''), nl2br($txt));
}

function str_endswith($string, $test, $case_insensitive = true) 
{
	return substr_compare($string, $test, -strlen($test), strlen($test), $case_insensitive) === 0;
}

function escape($elt)
{
	//escape info sent by the user to prevent exploits, and remove magic quotes if needed
	if(is_array($elt))
	{
		$out = array();
		foreach($elt as $key => $value)
		{
			$out[$key] = escape($value);
		}
		return $out;
	}
	elseif(!is_int($elt))
	{
		if (get_magic_quotes_gpc()) 
		{
			$str = stripslashes($elt);
		}
		else
		{
			$str = $elt;
		}
		//return htmlspecialchars($str, ENT_QUOTES, 'UTF-8', false);
		return $str;
	}
	else
	{
		return $elt;
	}
}

function escape_attribute($str, $double=true)
{
	$entities_escaped_str = str_replace(array("\r\n", "\n", "\r"), '&#10;', htmlentities($str, ENT_COMPAT, 'UTF-8'));
	if($double)
	{
		//since browers tend to translate the &#10; entity no matter where it is, let's do another escaping so that entities aren't even visible by the brower
		$entities_escaped_str = str_replace('&', '&amp;', $entities_escaped_str);
	}
	return $entities_escaped_str;
}

function unescape_attribute($str, $double=true)
{
	if($double)
	{
		$entities_unescaped_str = str_replace('&amp;', '&', $str); //restore entities
	}
	$entities_unescaped_str = html_entity_decode(str_replace('&#10;', "\n", $entities_unescaped_str), ENT_COMPAT, 'UTF-8'); //and decode them
	return $entities_unescaped_str;
}

function utf8_strpos_unescaped($haystack, $needle, $offset = 0)
{
	//finds first occurrence of a string, ignoring its escaped occurrences (when it's preceded with an unescaped backslash)
	
	$unescaped = false;
	$occurrence = false;
	$start = $offset;
	
	while(!$unescaped)
	{
		$occurrence = utf8_strpos($haystack, $needle, $start);
		
		if($occurrence === false)
		{
			return false;
		}
		
		$number_of_backslashes = 0;
		$char_index = $occurrence - 1;
		
		while(utf8_substr($haystack, $char_index, 1) == "\\")
		{
			$number_of_backslashes++;
			$char_index--;
		}
		
		$unescaped = (($number_of_backslashes % 2) == 0);
		
		$start = $occurrence + utf8_strlen($needle);
	}
	
	return $occurrence;
}



//Fix UTF-8 support for some critical functions...

function utf8_strpos($haystack, $needle, $offset=0)
{
	return mb_strpos($haystack, $needle, $offset, 'UTF-8');
}

function utf8_strrpos($haystack, $needle, $offset=0)
{
	return mb_strrpos($haystack, $needle, $offset, 'UTF-8');
}

function utf8_stripos($haystack, $needle, $offset=0)
{
	return mb_stripos($haystack, $needle, $offset, 'UTF-8');
}

function utf8_strlen($str)
{
	return mb_strlen($str, 'UTF-8');
}

function utf8_substr($string, $start, $length=array())
{
	if(is_array($length))
	{
		return mb_substr($string, $start, utf8_strlen($string), 'UTF-8');
	}
	else
	{
		return mb_substr($string, $start, $length, 'UTF-8');
	}
}

function utf8_substr_replace($string, $replacement, $start, $length=array())
{
	if(is_array($length))
	{
		return utf8_substr($string, 0, $start) . $replacement;
	}
	else
	{
		return utf8_substr($string, 0, $start) . $replacement . utf8_substr($string, $start+$length);
	}
}

function utf8_strtoupper($string)
{
	return mb_strtoupper($string, 'UTF-8');
}

function utf8_strtolower($string)
{
	return mb_strtolower($string, 'UTF-8');
}

function utf8_ucwords($string)
{
	return mb_convert_case($string, MB_CASE_TITLE, "UTF-8");
}



function applySourceCase($source, $target)
{
	//recognise case models on the source and apply them to the target
	//Valid case models are :
	//	Title : first letters of all words are uppercase
	//	Acronym : All uppercase
	//	Text body : all lowercase
	//	Beginning of sentence : first letter of the text is uppercase
	//If none of these models is matched, don't apply any rule
	
	if(utf8_ucwords($source) == $source) //Title model
	{
		return utf8_ucwords($target);
	}
	else
	{
		$is_uppercase = (utf8_strtoupper($source) == $source);
		$is_lowercase = (utf8_strtolower($source) == $source);
		
		if($is_uppercase AND $is_lowercase) //case is meaningless on this string : no model to match
		{
			return $target;
		}
		elseif($is_uppercase) //Acronym model
		{
			return utf8_strtoupper($target);
		}
		elseif($is_lowercase) //Text body model
		{
			return utf8_strtolower($target);
		}
		else
		{
			$firstchar = utf8_substr($source, 0, 1);
			if(utf8_strtoupper($firstchar) == $firstchar) //Beginning of sentence model
			{
				return utf8_strtoupper(utf8_substr($target, 0, 1)) . utf8_substr($target, 1) ;
			}
			else //No model matched
			{
				return $target;
			}
		}
	}
}

function str_replace_words($search, $replace, $casesensitive, $text)
{
	//replace a list of words, with different possible case policies
	
	for($i = 0; $i < count($search); $i++)
	{
		if($search[$i] != '')
		{
			$search[$i] = '#\b'.preg_quote($search[$i], '#').'\b#u' . (($casesensitive[$i] == 'apply' OR $casesensitive[$i] == 'no') ? 'i' : '') . (($casesensitive[$i] == 'apply') ? 'e' : '');
			if($casesensitive[$i] == 'apply')
			{
				$replace[$i] = 'applySourceCase("$0", "' . addslashes($replace[$i]) . '")';
			}
		}
		else
		{
			unset($search[$i]);
			unset($replace[$i]);
		}
	}
	
	if(count($search) > 0)
	{
		$result = preg_replace($search, $replace, $text);
	}
	else
	{
		$result = $text;
	}
	
	return $result;
}

?>