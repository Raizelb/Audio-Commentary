<?php
 
require_once 'DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['team']) && isset($_POST['description']) && isset($_POST['user_id'])) {
 
    // receiving the post params
    $team = $_POST['team'];
    $description = $_POST['description'];
    $user_id = $_POST['user_id'];
 
    //search user by name
	$result = $db->registerCommentator($team, $description, $user_id);
	//echo json_encode($result);
	//$db->searchUserByName($name);
	
	if ($result != false) {
		//user is found
		$response["error"] = FALSE;
        $response["result"] = $result;
        echo json_encode($response);
	} else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Can't update user";
        echo json_encode($response);
	}
}
else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Missing credentials";
    echo json_encode($response);
	
}
?>