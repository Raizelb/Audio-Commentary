<?php
require_once 'DB_Functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['user_id']) && isset($_POST['comment_content']) && isset($_POST['comment_rating'])) {
 
    // receiving the post params
    $user_id = $_POST['user_id'];
    $comment_content = $_POST['comment_content'];
	$comment_rating = $_POST['comment_rating'];
 
    // get the user by email and password
    $comment = $db->storeComments($user_id, $comment_content, $comment_rating);
	//echo json_encode($comment);
 
    if ($comment != false) {
        // user is found
        $response["error"] = FALSE;
        $response["comment"] = $comment;
        echo json_encode($response);
    } else {
        // user is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Couldn't store comment";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Required parameters email or password is missing!";
    echo json_encode($response);
}
?>