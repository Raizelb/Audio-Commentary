<?php
require_once 'DB_Functions.php';
$db = new DB_Functions();

// json response array
$response = array("error" => FALSE);

if (isset($_POST['user_id'])) {
	
	// receiving the post params
    $user_id = $_POST['user_id'];
	
	//search user by name
	$comments = $db->loadComments($user_id);
	
	if ($comments != false) {
		//comments are found
		$response["error"] = FALSE;
        $response["comments"] = $comments;
        echo json_encode($response);
	} else {
        // comments not found
        $response["error"] = TRUE;
        $response["error_msg"] = "Comments not found";
        echo json_encode($response);
	}
}
else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Missing user ID";
    echo json_encode($response);
	
}