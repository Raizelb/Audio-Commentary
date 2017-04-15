<?php
require_once 'DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['user_id'])) {
 
    // receiving the post params
    $user_id = $_POST['user_id'];
 
    // get the user by email and password
    $commentator = $db->getCommentator($user_id);
 
    if ($commentator != false) {
        // user is found
        $response["error"] = FALSE;
        $response["commentator"] = $commentator;
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Wrong userID";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameter userID is missing!";
    echo json_encode($response);
}
?>