<?
/**
 * @name         Report (a person)
 * @version      2.0
 * @package      report
 * @author       Greg Miernicki <g@miernicki.com> <gregory.miernicki@nih.gov>
 * @about        Developed in whole or part by the U.S. National Library of Medicine
 * @link         https://pl.nlm.nih.gov/about
 * @license	 http://www.gnu.org/copyleft/lesser.html GNU Lesser General Public License (LGPL)
 * @lastModified 2011.0921
 */



// default action
function shn_report_default() {

	global $global;
	require_once($global['approot'].'/mod/lpf/lib_lpf.inc');

	// check if event is closed...
	$q = "
		SELECT *
		FROM incident
		WHERE shortname = '".mysql_real_escape_string($_GET['shortname'])."';
	";
	$result   = $global['db']->Execute($q);
	if($result === false) { daoErrorLog(__FILE__, __LINE__, __METHOD__, __CLASS__, __FUNCTION__, $global['db']->ErrorMsg(), "report default 1"); }
	$row      = $result->FetchRow();
	$closed   = $row['closed'];
	$external = $row['external_report'];

	// status 1 means event is closed to all reporting
	if($closed == 1) {
		echo "
			<h2>Reporting Closed</h2>
			<br>
			This event is closed and is no longer accepting new person reports.
		";
		return;

	// status 2 means we can still report via an external iframe widget
	} elseif($closed == 2) {
		echo "
			<h2>External Reporting</h2>
			".htmlspecialchars_decode($external)."<br>
		";
		return;
	}

	// check if the person is not logged in or is anonymous (must be logged in and not anonymous to report)
	if(!isset($_SESSION['logged_in']) || $_SESSION['logged_in'] != 1 || !isset($_SESSION['group_id']) || $_SESSION['group_id'] == 3) {
		echo '
			<h2>Login Required</h2>
			<br>
			You must first login to report a new person to this registry.<br>
			<br>
			<br>
			<input id="login" class="styleTehButton" type="button" value="Login" onclick="login();">
			&nbsp; &nbsp; &nbsp; &nbsp;
			<input id="register" class="styleTehButton" type="button" value="Register as a new user" onclick="register();">
			<script>
				function register() {
					window.location = \'register\';
				}
				function login() {
					window.location = \'login\';
				}
			</script>
		';
		return;
	}

	// all good, so present them with a button to click that creates a stub person
	echo '
		<h2>Report a Person</h2>
		<br>
		You may wish to perform a <a href="index.php?mod=inw&act=default">quick search</a> before reporting. Otherwise,<br>
		<br>
		<br>
		<input id="login" class="styleTehButton" type="button" value="Create a New Person Record" onclick="report();">
		<script>
			function report() {
				window.location = \'index.php?mod=report&act=instantiate\';
			}
		</script>
	';
}



// build the stub record and redirec to it...
function shn_report_instantiate() {
	$p = new person();
	$p->createUUID();
	$p->rep_uuid = $_SESSION['user_p_uuid'];
	$p->setEvent($_GET['shortname']);
	$p->opt_status = "unk";
	$p->creation_time = date('Y-m-d H:i:s');
	$p->last_updated  = date('Y-m-d H:i:s');
	$p->expiry_date = "0000-01-01 00:00:00"; // special value for expiry date denotes a new record!
	$p->makePfifNote = false;
	$p->insert();
	$p->rapReportLog();

	$p_uuid_a = explode("/", $p->p_uuid);
	$p_uuid_portion = $p_uuid_a[1];
	header("Location: ".$p_uuid_portion);

	// fall back redirect if header() fails for some reason
	echo "
		<script>
			window.location = '".$p_uuid_portion."';
		</script>
		<a href=\"".$p_uuid_portion."\">click here if not automatically redirectd...</a>
	";
}

