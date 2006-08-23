<?php
/*
*
*
* PHP version 4 and 5
*
* LICENSE: This source file is subject to LGPL license
* that is available through the world-wide-web at the following URI:
* http://www.gnu.org/copyleft/lesser.html
*
* @package    Sahana - http://sahana.sourceforge.net
* @author     Ravindra <ravindra@opensource.lk>
* @copyright  Lanka Software Foundation - http://www.opensource.lk
*
*/

$act=$_GET{"act"};

if($act=='add_loc'){
    _shn_get_level_location();
}else if ($act=='sub_cat'){
		_shn_get_sub_catalogs();
}else if($act=='unit_cat'){
		_shn_get_units();
	}else if($act=='get_loc_val'){
			_shn_get_locations();
		}else{
	    _shn_get_children();
		}


function _shn_get_units()
{
	require_once('../3rd/adodb/adodb.inc.php');
	require_once('../conf/sysconf.inc');
	//Make the connection to $global['db']
	$db = NewADOConnection($conf['db_engine']);
	$db ->Connect($conf['db_host'].($conf['db_port']?':'.$conf['db_por
	t']:''),$conf['db_user'],$conf['db_pass'],$conf['db_name']);
	
	$cat=$_GET{"cat"};
	
	$q="select unit_uuid,name from ct_unit where unit_type_uuid ='$cat'";
	$res_child=$db->Execute($q);

   	if(!$res_child==NULL && !$res_child->EOF){
    		while(!$res_child->EOF){
        		$res=$res.",".$res_child->fields[0];
        		$res=$res.",".$res_child->fields[1];
        		$res_child->MoveNext();
    		}
		echo $res;
   	}else{
   		echo "null,";
	}

}



function _shn_get_sub_catalogs(){
require_once('../3rd/adodb/adodb.inc.php');
require_once('../conf/sysconf.inc');
//Make the connection to $global['db']
$db = NewADOConnection($conf['db_engine']);
$db ->Connect($conf['db_host'].($conf['db_port']?':'.$conf['db_por
t']:''),$conf['db_user'],$conf['db_pass'],$conf['db_name']);

$cat=$_GET{"cat"};
$flag=$_GET{"flag"};
		if($flag==false)
		{
		$q="select ct_uuid,name from ct_catalogue where parentid='{$cat}'";
    		$res_child=$db->Execute($q);
		}
		else if($flag==true)
		{
		$q="select ct_uuid,name from ct_catalogue where parentid='{$cat}' and final_flag='0' ";
    		$res_child=$db->Execute($q);
		}

        		$res=$res.","."";
        		$res=$res.","."";

   	if(!$res_child==NULL && !$res_child->EOF){
    		while(!$res_child->EOF){
        		$res=$res.",".$res_child->fields[0];
        		$res=$res.",".$res_child->fields[1];
        		$res_child->MoveNext();
    		}
		echo $res;
   	}else{
   		echo "null,";
	}
}

function _shn_get_children(){
require_once('../3rd/adodb/adodb.inc.php');
require_once('../conf/sysconf.inc');
//Make the connection to $global['db']
$db = NewADOConnection($conf['db_engine']);
$db ->Connect($conf['db_host'].($conf['db_port']?':'.$conf['db_por
t']:''),$conf['db_user'],$conf['db_pass'],$conf['db_name']);

	$level=$_GET{"lvl"}+1;
	$parent=$_GET{"sel"};

	$q = "select location.name,location.loc_uuid,parent_id from location where location.opt_location_type='{$level}' and parent_id='{$parent}'";
    //echo $q;
    $res_child=$db->Execute($q);
    if($res_child->EOF)
        return;
    while(!$res_child->EOF){
        $res=$res.",".$res_child->fields[1];
        $res=$res.",".$res_child->fields[0];
        $res_child->MoveNext();
    }
echo $res;
}

function _shn_get_locations(){
require_once('../3rd/adodb/adodb.inc.php');
require_once('../conf/sysconf.inc');
//Make the connection to $global['db']
$db = NewADOConnection($conf['db_engine']);
$db ->Connect($conf['db_host'].($conf['db_port']?':'.$conf['db_por
t']:''),$conf['db_user'],$conf['db_pass'],$conf['db_name']);

	$level=1;
	$sel_id=$_GET{"sel"};
	
	if($_GET{"type"}=="camp"){
		$q="select location_id from camp_general where c_uuid='{$sel_id}'";
		$res=$db->Execute($q);
    		if($res->EOF)
        		return;
    			$loc_id=$res->fields["location_id"];
	}else if($_GET{"type"}=="poc"){
		$q = "select location_id from location_details where poc_uuid='{$sel_id}'";
		$res=$db->Execute($q);
    		if($res->EOF)
        		return;
    			$loc_id=$res->fields["location_id"];
		}else{
			$loc_id=$_GET{"sel"};
		}
	
	
	
	$q = "select parent_id,opt_location_type from location where loc_uuid='{$loc_id}'";
	$res=$db->Execute($q);
    	if($res->EOF)
        	return;
        	
    	$parent=$res->fields["parent_id"];
    	$header.="loc_dir".",".$parent;
    $header.=",".$res->fields["opt_location_type"];
    	
    $level=$res->fields["opt_location_type"];
   	
	$q = "select location.name,location.loc_uuid from location where location.opt_location_type='{$level}' and parent_id='{$parent}'";
    //echo $q;
    $res_child=$db->Execute($q);
    if($res_child->EOF)
        return;
    $count=0;
    while(!$res_child->EOF){
    		if($count==0){
    			$res_data=$res_data.$res_child->fields["loc_uuid"];
        		$res_data=$res_data.":".$res_child->fields["name"];
    		}else{
        		$res_data=$res_data.",".$res_child->fields["loc_uuid"];
        		$res_data=$res_data.":".$res_child->fields["name"];
    		}
    		if($res_child->fields["loc_uuid"]==$loc_id){
    			$header.=",".$count;
    		}
    		$count++;
        $res_child->MoveNext();
    }
    //echo $header;
   // echo $res_data;
   
   echo $header.";".$res_data;
}


function _shn_get_level_location(){
require_once('../3rd/adodb/adodb.inc.php');
require_once('../conf/sysconf.inc');
//Make the connection to $global['db']
$db = NewADOConnection($conf['db_engine']);
$db ->Connect($conf['db_host'].($conf['db_port']?':'.$conf['db_por
t']:''),$conf['db_user'],$conf['db_pass'],$conf['db_name']);


$level=$_GET{"sel"};
if($level==1){
echo "none,";
}
$q = "select location.name,location.loc_uuid,parent_id from location where location.opt_location_type={$level}";
    $res_child=$db->Execute($q);
    if($res_child->EOF)
        return;
    while(!$res_child->EOF){
        $res=$res.",".$res_child->fields[1];
        $res=$res.",".$res_child->fields[0];
        $res_child->MoveNext();
    }
echo $res;
}
?>

