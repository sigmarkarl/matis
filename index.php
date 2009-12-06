<?php
$d = $_GET['x'];
//$d = str_replace( "%", "/", $d );
//echo $d;
$ch = curl_init("http://localhost:5001/$d");
if( $d[ strlen($d)-1 ] == '/' ) $b = 1;
else $b = 0;

if( $b ) { 
	$fp = fopen("/tmp/link.txt", "w");
} else {
	$s = explode( "/", $d );
        $k = $s[ count($s)-1 ];
	header("Content-Type: application/octet-stream; ");
	header("Content-Transfer-Encoding: binary");
	header("Content-Disposition: attachment; filename=$k;");
	//header("Content-Length: 50000000;");
	//header("filename=\"$k\";");
	//flush();
	/*header('Content-type: application/binary');
	$s = explode( "/", $d );
	$k = $s[ count($s)-1 ];
	header("Content-Disposition: attachment; filename=$k");
	$f = fopen( "/mnt/sdrif/Private\ Files/FLX/strepto_simmi/$d" );
	header("Content-Length: " . filesize($f) ."; "); 
	fclose( $f );*/
	$fp = $STDOUT;
}

curl_setopt($ch, CURLOPT_FILE, $fp);
curl_setopt($ch, CURLOPT_HEADER, 0);

curl_exec($ch);
curl_close($ch);

if( $b ) {
	fclose( $fp );
	$fp = fopen( "/tmp/link.txt", "r" );
	$l = fread( $fp, 4096 );
	$p = strpos( $l, "Parent Directory" );
	$f = substr( $l, 0, $p );
	$n = strstr( $l, "Parent Directory" );
	
	$w = str_replace( "a href=\"/", "a href=\"/flx/?x=", $f );
	$v = str_replace( "a href=\"", "a href=\"/flx/?x=$d", $n );
	$s = "$w$v";
	$ok = fopen("/tmp/simmi.txt", "w");
	fwrite( $ok, $s );
	fclose( $ok );
	fclose( $fp );
	echo $s;
}
?>

