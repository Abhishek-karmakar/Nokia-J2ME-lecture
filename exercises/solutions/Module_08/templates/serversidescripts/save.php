<?php
/**
* This is a dummy server script that just saves the given parameters to 
* a xml-file. Requires PHP5.
*/


$name      = getParameter("name");
$score     = getParameter("score");
$longitude = getParameter("longitude");
$latitude  = getParameter("latitude");

if( $name != null and $score != null )
{
    if($longitude == null or $latitude == null) {
        $longitude = "No loc.";
        $latitude  = "No loc.";
    }
    
    $dom = new domDocument();
    $dom->load("highscore.xml");
    $player = $dom->createElement("player");

    $name  = $dom->createElement("name",      $name);
    $score = $dom->createElement("score",     $score);
    $long  = $dom->createElement("longitude", $longitude); 
    $lat   = $dom->createElement("latitude",  $latitude); 
        
    $player->appendChild($name);
    $player->appendChild($score);
    $player->appendChild($long);
    $player->appendChild($lat);

    $dom->documentElement->appendChild($player);
    
    $dom->save("highscore.xml");
    
    print 1;
}

function getParameter( $key ) 
{
    $ret = null;
    if( array_key_exists($key, $_GET) ) 
    {
        $ret = $_GET[$key];
        
        if($ret === "")
            $ret = null;
    }
    return $ret;
}
?>
