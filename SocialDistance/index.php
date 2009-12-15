<?php
//passthru( "java" );

require_once 'facebook.php';

$appapikey = 'e18e41fe84964fe36d39332f9fd9450b';
$appsecret = 'b27705900c8e4373dc3683397b8cbaff';
$facebook = new Facebook($appapikey, $appsecret);
$user_id = $facebook->require_login();

$name = $_GET['name'];
$exc = $_GET['exc'];

//header('Content-Type: text/html; charset=utf-8');

if( $name == null ) {
	$skey = $_POST['fb_sig_session_key'];
	$fkey = $_POST['fb_sig_friends'];
	$ukey = $_POST['fb_sig_user'];

	echo "<p>Welcome, <fb:name uid=\"$user_id\" useyou=\"false\" />!</p>";
	echo "Please write the name of the person you would like to find your Facebook social distance from:<p>";
	echo "<table><form name=\"input\" action=\"http://apps.facebook.com/socialdistance/\" method=\"get\"><tr><td>name:</td><td><input type=\"text\" name=\"name\"/></td></tr><tr><td>birthday:</td><td><input type=\"text\" name=\"birthday\"/>(optional)</td></tr><tr><td><input type=\"submit\" name=\"submit\" value=\"submit\"/></td></tr></form></table>";

	if( $exc != "sim" ) {
		//passthru( "java" );
		exec( "java -Dfile.encoding=\"utf-8\" -jar socialdistance.jar fb_sig_user=$ukey fb_sig_session_key=$skey fb_sig_friends=$fkey > /dev/null &" );
		//echo "erm";
	} else {
		echo "no exec";
	}
	//else echo "<br>simmi";
} else {
	$user = $_GET['fb_sig_user'];
	$uname = urlencode( $name );
	passthru( "java -Dfile.encoding=\"utf-8\" -Duser=\"$user\" -Dsearch=\"$uname\" -jar socialdistance.jar" );
	echo "<br>";
	echo "<a href=\"index.php?exc=sim\">Try again</a>";
}
?>
