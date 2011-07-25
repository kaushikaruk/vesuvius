<?php

/**
 * Loads PFIF Persons or Notes depending on first argument. Uses harvest log information to keep
 * track of where it left off.
 *
 * @package     pfif
 * @version      1.1
 * @author       Carl H. Cornwell <ccornwell@mail.nih.gov>
 * @author       Leif Neve <lneve@mail.nih.gov>
 * LastModified: 2011:0719
 * License:      LGPL
 * @link         TBD
 */
// print "Configuring error reporting  ...\n";
error_reporting(E_ALL ^ E_NOTICE);
// print "Configuring error display  ...\n";
ini_set("display_errors", "stdout");

// cron job task for for pfif import
// set approot since we don't know it yet
$global['approot'] = getcwd() . "/../../";
require_once("../../conf/sahana.conf");
require_once("../../3rd/adodb/adodb.inc.php");
require_once("../../inc/handler_db.inc");
require_once("pfif.inc");
require_once("pfif_repository.inc");
require_once("pfif_croninit.inc");

/**
 * Switch database in order to support multiple DB instances.
 * NOT USED CURRENTLY
 */
function shn_db_use($db_name, $db_host=null) {
   global $global, $conf;

   if (empty($db_host))
      $db_host = $conf['db_host'];
   $global['db']->Close();
   $global['db'] = NewADOConnection($conf['db_engine']);
   $_host_port = $db_host . (isset($conf['db_port']) ? ':' . $conf['db_port'] : '');
   $global['db']->Connect($_host_port, $conf['db_user'], $conf['db_pass'], $db_name);
}

/**
 *  Log harvest end
 */
function update_harvest_log($r, $req_params, $status) {
   $pfif_info = $_SESSION['pfif_info'];
   $pfif_info['end_time'] = time();
   //var_dump("ending harvest with pfif_info:", $pfif_info);
   $r->end_harvest($status, $req_params, $pfif_info);
}

/*
 -----------------------------------------------------------------------------------------------------------------------
*/

// Update persons or notes, depending on arg 1.
if ($argc < 2) {
   die("Wrong number of arguments: Expecting at least 2.");
} else if ($argv[1] != "person" && $argv[1] != "note") {
   die("Expect 'person' or 'note' as first argument.");
} 
$is_person = ($argv[1] == "person") ? true : false;
$is_scheduled = ($argc > 2 && $argv[2] == "test") ? false : true;
$mode = $is_scheduled ? "scheduled" : "test";

print "\nStarting PFIF ".$argv[1]." import at ".strftime("%c")."\n";

//print_r($_SERVER);
print "Using db " . $global['db']->database . " in " . $mode . " mode\n";
// Store Japanese correctly.
$global['db']->Execute("SET NAMES 'utf8'");
//print_r($conf);

// Get all PFIF repository sources.
$repositories = Pfif_Repository::find_source(($is_person)? 'person' : 'note');
if (!$repositories) {
   die("No repositories ready for harvest.\n");
}
//var_dump("Found repositories for import", $repositories);

$sched_time = time();
$import_repos = array();
foreach ($repositories as $r) {
   if ($r->is_ready_for_harvest($sched_time)) {
      add_pfif_service($r);             // initializes pfif_conf global
      $import_repos[$r->id] = $r;
      //var_dump("importing from repository",$r);
   }
}
unset($r);
unset($repositories);

$import_queue = $pfif_conf['services'];
//print "Queued imports:\n".print_r($import_queue,true)."\n";

foreach ($import_queue as $service_name => $service) {
   $repos = $import_repos[$pfif_conf['map'][$service_name]];
   //var_dump("repository", $repos);

   $req_params = $repos->get_request_params();
   $pfif_uri = $service['feed_url'] .
           '?min_entry_date=' . $req_params['min_entry_date'] .
           '&max_results=' . $service['max_results'] .
           '&key=' . $service['auth_key'] .
           '&skip=' . $req_params['skip'] .
           '&version=' . $service['version'];
   $p = new Pfif();
   $p->setPfifConf($pfif_conf, $service_name);
   //print_r($pfif_conf);
   try {
      $repos->start_harvest($mode, 'in');
      print "\nImport started from $pfif_uri at " . $repos->get_log()->start_time . "\n";
      if ($is_person) {
         $loaded = $p->loadPersonsFromXML($pfif_uri);
      } else {
         $loaded = $p->loadNotesFromXML($pfif_uri);
      }
      if ($loaded > 0) {
         print "Loaded $loaded XML records. ";
         if ($is_scheduled) { // Output to database for production
            if ($is_person) {
               $stored = $p->storePersonsInDatabase();
            } else {
               $stored = $p->storeNotesInDatabase();
            }
            print "Stored $stored records.\n";
         } else { 
            // Output to file for test/debug. (This leverages export functionality.)
            $xml = $p->storeInXML(false); // false=debug
            //print $xml;
            $logfile_name = 'crontest_' . $service_name . '.xml';
            $fh = fopen($logfile_name, 'a+');
            $charstowrite = strlen($xml);
            $written = fwrite($fh, $xml, $charstowrite);
            fclose($fh);
            print "wrote $written of $charstowrite characters to $logfile_name\n";
         }
         // Note: For tests, only last_entry_date is saved. (No skip, etc.)
         update_harvest_log($repos, $req_params, 'completed');
      } else {
         if ($loaded == -1) {
            print "Import failed from repository $service_name\n";
         } else {
            print "0 records for import from repository $service_name\n";
         }
         update_harvest_log($repos, $req_params, 'error');
      }
      unset($p);
   } catch (Exception $e) {
      error_log("Error in import: " . $e->getMessage() . "\n");
   }
   unset($_SESSION['pfif_info']);
}
unset($service);
print "PFIF import completed " . strftime("%c") . "\n";
