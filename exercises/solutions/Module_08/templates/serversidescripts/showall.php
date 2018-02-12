<?php
/**
* Makes XSLT-transformation from the highscore.xml - file. Outputs
* a plain text - file.
*/

header("Content-Type: text/txt; charset=utf-8"); 
$dom = new domDocument();
$dom->load("totext.xslt");
$proc = new xsltprocessor;
$xsl = $proc->importStylesheet($dom);
$document = new DomDocument();
$document->load("highscore.xml");
print $proc->transformToXml($document);

?>
