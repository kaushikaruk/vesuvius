<?php
/**
* The Situation Report (SITRep) Module
*
* PHP version 5
*
* LICENSE: This source file is subject to LGPL license
* that is available through the world-wide-web at the following URI:
* http://www.gnu.org/copyleft/lesser.html
*
* @author		Pradeeper <pradeeper@respere.com>
* @copyright    Respere - http://respere.com/
* @package		sahana
* @subpackage   sr
* @tutorial
* @license		http://www.gnu.org/copyleft/lesser.html GNU Lesser General
* Public License (LGPL)
*  
*/

include_once $global['approot'].'/inc/lib_menu.inc';
include_once $global['approot'].'/inc/lib_form.inc';
include_once $global['approot'].'/inc/lib_validate.inc';
include_once $global['approot'].'/inc/lib_errors.inc';
include_once "lib_form.inc";
include_once "handler_report_store.inc";
include_once "lib_reports.inc";
include_once($global['approot'].'inc/lib_log.inc');


function shn_sr_mainmenu() 
{
    global $global;
    $module = $global['module'];
    
    shn_mod_menuopen(_t('Situation Report'));
    	shn_mod_menuitem('default',_t('Home'));
    	shn_mod_menuitem('create',_t('Report'));
    	shn_mod_menuitem('Update',_t('View'));
    	shn_mod_menuitem('View',_t('Update'));
    shn_mod_menuclose();

    include $global['approot'].'/inc/handler_mainmenu.inc';

}

// Form submition capture
function shn_sr_form_submit()
{
	$key=$_POST['key'];
	
	switch ($key) {
		
		// Report registration
		case "reg":
			$rpt_name=$_POST['name'];
			$rpt_desc=$_POST['desc'];
			
			// checking whether the report name is already in the db
			$chk = shn_sr_chk_rpt_name($rpt_name);
			if ($chk=="no"){
				// if no then register
				shn_sr_store_reg_new_report($rpt_name,$rpt_desc);
			} else {
				// if name is there then ask for another name
				add_error('Report name <strong>'.$rpt_name.'</strong> is already in the database!');
				shn_sr_report_register();
			}
		break;
		
		// Report detail update
		case "reports":
			$rpt_id=$_POST['id'];
			$rpt_name=$_POST['name'];
			$rpt_summary=$_POST['summary'];
			$rpt_event=$_POST['event'];
			$rpt_action=$_POST['action'];
			$rpt_figure=$_POST['figure'];
			shn_sr_store_report_update($rpt_id, $rpt_name, $rpt_summary, $rpt_event, $rpt_action, $rpt_figure);
		break;	
	}
}

function shn_sr_default()
{
	print "<h1><center>Welcome to Situation Report</center></h1>";
	print "SITRep Dashboard! SitRep Module is currently under development.";
}

function shn_sr_create()
{
	print "<h1>Report an incident</h1>";
	// capture basic "report" information
	shn_sr_report_register();
}


function shn_sr_view()
{
	print "<h1>Situation Reports</h1>";
	// List all the reports
	shn_sr_view_reports();
}

// view a report
function shn_sr_report()
{
	// capture the id and report name from URL
	$rpt_id=$_GET['id'];
	$rpt_name=$_GET['rpt'];
	shn_sr_report_detail_form($rpt_id, $rpt_name);
}

function shn_sr_update()
{
	print "update";
}
?>