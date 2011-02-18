<?//coding: utf-8
/*
	Apertium Web Post Editing tool
	
	Contributed by Arnaud Vié <unas.zole@gmail.com> for Google Summer of Code 2010
	Mentors : Luis Villarejo, Mireia Farrús
*/

include_once('includes/template.php');


//init_environment();


page_header("Apertium translation", array(
));

?>

<p>What would you like to translate ?</p>

<ul>
	<li>A short text ? <a href="translate.php">Click here</a></li>
	<li>A document ? Use the form below !</li>
</ul>

<form action="translate.php" method="post" enctype="multipart/form-data">
<input type="file" name="in_doc" />
<input type="text" name="in_doc_type" />
<input type="submit" value="Translate !" />
</form>


<?	
page_footer();

?>