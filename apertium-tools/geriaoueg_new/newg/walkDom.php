<?php
//Code for traversing the DOM tree
function walkDom($node, $level = 0)
{
$indent = "";
for ($i = 0; $i < $level; $i++)
$indent .= "&nbsp;&nbsp;"; //prettifying the output
if($node->plaintext != "")
{
echo $indent."<b> ".$node->tag."</b>".$node->find('text');
echo "<br><br>";
}
$cNodes = $node->childNodes();
//$cNodes = $node->childNodes;
if (count($cNodes) > 0)
{
$level++; // go one level deeper
foreach($cNodes as $cNode)
walkDom($cNode, $level); //so this is recursion my professor kept talkin’ about
$level = $level-1; // come a level up, and had to do it this way or else wordpress would take away one dash. :(
}
}
?>
