<?php
require_once 'DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['name'])) {
	
	// receiving the post params
    $name = $_POST['name'];
	
	//search user by name
	$user = $db->searchUserByName($name);
	//echo json_encode($user);
	//$db->searchUserByName($name);
	
	if ($user != false) {
		//user is found
		$response["error"] = FALSE;
        $response["users"] = $user;
        echo json_encode($response);
	} else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "User not found";
        echo json_encode($response);
	}
}
else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Missing name";
    echo json_encode($response);
	
}