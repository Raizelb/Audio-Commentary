<?php
 
/**
 * @author Ravi Tamada
 * @link http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */
 
class DB_Functions {
 
    private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
	
	/**
	 * Check uniqid is already existed or not
	 */
	public function isUniqidExisted($uuid) {
		$stmt = $this->conn->prepare("SELECT user_id from users WHERE user_id = ?");
		
		$stmt->bind_param("s",$uuid);
		
		$stmt->execute();
		
		$stmt->store_result();
		
		if($stmt->num_rows > 0) {
			//uniqid existed
			$stmt->close();
			return true;
		} else {
			$stmt->close();
			return false;
		}
	}
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($name, $email, $password) {
		do {
			$uuid = uniqid('', true);
		}
        while($this->isUniqidExisted($uuid) == true);
        $hash = $this->hashSSHA($password);
        $encrypted_password = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
 
        $stmt = $this->conn->prepare("INSERT INTO users(user_id, name, email, encrypted_password, salt, created_at) VALUES(?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("sssss", $uuid, $name, $email, $encrypted_password, $salt);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            return $user;
        } else {
            return false;
        }
    }
	
	/**
	 * Get commentator's profile
	 */
	public function getCommentator($user_id) {
		$stmt = $this->conn->prepare("SELECT * FROM commentators WHERE user_id = ?");
		$stmt->bind_param("s", $user_id);
		
		if($stmt->execute()) {
			$commentator = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			return $commentator;
		} else {
			return false;
		}
	}
	
	/**
	 * Store comments
	 */
	public function storeComments($user_id, $comment_content, $comment_rating) {
		date_default_timezone_set("Europe/London");
		$comment_date = date('Y/m/d h:i:s a',time());
		$stmt = $this->conn->prepare("INSERT INTO comments(user_id, comment_content, comment_rating, comment_date) VALUES(?, ?, ?, ?)");
		$stmt->bind_param("ssss", $user_id, $comment_content, $comment_rating, $comment_date);
		$result = $stmt->execute();
		$stmt->close();
		
		// check for successful store
		if ($result) {
			$stmt = $this->conn->prepare("SELECT * FROM comments WHERE user_id = ? AND comment_date = ?");
			$stmt->bind_param("ss", $user_id, $comment_date);
			$stmt->execute();
			$comment = $stmt->get_result()->fetch_assoc();
			$stmt->close();
			
			//echo json_encode($comment);
			if($this->updateRating($user_id)) {
				return $comment;
			}
			else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Update commentator's rating
	 */
	public function updateRating($user_id) {
		$stmt = $this->conn->prepare("UPDATE commentators 
									INNER JOIN comments ON commentators.user_id = ? 
									SET commentators.rating = (SELECT AVG(comment_rating) 
									FROM comments WHERE comments.user_id = ?)");
		$stmt->bind_param("ss", $user_id, $user_id);
		$result = $stmt->execute();
		$stmt->close();
		if($result) {
			return true;
		} else {
			return false;
		}
		
	}
 
    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $password) {
 
        $stmt = $this->conn->prepare("SELECT * FROM users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        if ($stmt->execute()) {
            $user = $stmt->get_result()->fetch_assoc();
            $stmt->close();
 
            // verifying user password
            $salt = $user['salt'];
            $encrypted_password = $user['encrypted_password'];
            $hash = $this->checkhashSSHA($salt, $password);
            // check for password equality
            if ($encrypted_password == $hash) {
                // user authentication details are correct
                return $user;
            }
        } else {
            return NULL;
        }
    }
	/**
	 * Search for users by name
	 */
	public function searchUserByName($name) {
		$param = "%".$name."%";
		$stmt = $this->conn->prepare("SELECT * FROM users WHERE name LIKE ? AND commentator_status = 1");
		$stmt->bind_param("s",$param);
		
		if ($stmt->execute()) {
			$user = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
			//echo json_encode($user);

            $stmt->close();
			
			return $user;
		}
		else {
			return NULL;
		}
		
	}
	
	/**
	 * Register commentator
	 */
	public function registerCommentator($team, $description, $user_id) {
		$stmt = $this->conn->prepare("INSERT INTO commentators(team_support, description, user_id, rating) VALUES (?,?,?,0)");
 
        $stmt->bind_param("sss", $team,$description,$user_id);
		$result = $stmt->execute();
		$stmt->close();
		
		
		if($result && $this->updateUser($user_id)) {
			$stmt = $this->conn->prepare("SELECT * FROM commentators WHERE user_id = ?");
            $stmt->bind_param("s", $user_id);
            $stmt->execute();
            $commentator = $stmt->get_result()->fetch_assoc();
            $stmt->close();
			
			return $commentator;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Update Users
	 */
	public function updateUser($user_id) {
		$stmt = $this->conn->prepare("UPDATE users SET commentator_status = 1 WHERE user_id = ?");
		$stmt->bind_param("s", $user_id);
		$result = $stmt->execute();
		$stmt->close();
		
		//echo json_encode($result);
		if($result) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Load comments
	 */
	public function loadComments($id) {
		$stmt = $this->conn->prepare("SELECT * FROM comments where user_id = ?");
		
		$stmt->bind_param("s", $id);
		
		if ($stmt->execute()) {
			$comments = $stmt->get_result()->fetch_all(MYSQLI_ASSOC);
			//echo json_encode($user);

            $stmt->close();
			
			return $comments;
		}
		else {
			return NULL;
		}
	}
 
    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email from users WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
	
	// updating user GCM registration ID
    public function updateGcmID($user_id, $gcm_registration_id) {
        $response = array();
        $stmt = $this->conn->prepare("UPDATE users SET gcm_registration_id = ? WHERE user_id = ?");
        $stmt->bind_param("ss", $gcm_registration_id, $user_id);
 
        if ($stmt->execute()) {
            // User successfully updated
            $response["error"] = false;
            $response["message"] = 'GCM registration ID updated successfully';
        } else {
            // Failed to update user
            $response["error"] = true;
            $response["message"] = "Failed to update GCM registration ID";
            $stmt->error;
        }
        $stmt->close();
 
        return $response;
    }
	
	// fetching single user by id
    public function getUser($user_id) {
        $stmt = $this->conn->prepare("SELECT user_id, name, email, gcm_registration_id, created_at FROM users WHERE user_id = ?");
        $stmt->bind_param("s", $user_id);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($user_id, $name, $email, $gcm_registration_id, $created_at);
            $stmt->fetch();
            $user = array();
            $user["user_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["gcm_registration_id"] = $gcm_registration_id;
            $user["created_at"] = $created_at;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }
	
	// fetching multiple users by ids
    public function getUsers($user_ids) {
 
        $users = array();
        if (sizeof($user_ids) > 0) {
            $query = "SELECT user_id, name, email, gcm_registration_id, created_at FROM users WHERE user_id IN (";
 
            foreach ($user_ids as $user_id) {
                $query .= $user_id . ',';
            }
 
            $query = substr($query, 0, strlen($query) - 1);
            $query .= ')';
 
            $stmt = $this->conn->prepare($query);
            $stmt->execute();
            $result = $stmt->get_result();
 
            while ($user = $result->fetch_assoc()) {
                $tmp = array();
                $tmp["user_id"] = $user['user_id'];
                $tmp["name"] = $user['name'];
                $tmp["email"] = $user['email'];
                $tmp["gcm_registration_id"] = $user['gcm_registration_id'];
                $tmp["created_at"] = $user['created_at'];
                array_push($users, $tmp);
            }
        }
 
        return $users;
    }
	
	// messaging in a chat room / to persional message
    public function addMessage($user_id, $chat_room_id, $message) {
        $response = array();
 
        $stmt = $this->conn->prepare("INSERT INTO messages (chat_room_id, user_id, message) values(?, ?, ?)");
        $stmt->bind_param("iss", $chat_room_id, $user_id, $message);
 
        $result = $stmt->execute();
 
        if ($result) {
            $response['error'] = false;
 
            // get the message
            $message_id = $this->conn->insert_id;
            $stmt = $this->conn->prepare("SELECT message_id, user_id, chat_room_id, message, created_at FROM messages WHERE message_id = ?");
            $stmt->bind_param("i", $message_id);
            if ($stmt->execute()) {
                $stmt->bind_result($message_id, $user_id, $chat_room_id, $message, $created_at);
                $stmt->fetch();
                $tmp = array();
                $tmp['message_id'] = $message_id;
                $tmp['chat_room_id'] = $chat_room_id;
                $tmp['message'] = $message;
                $tmp['created_at'] = $created_at;
                $response['message'] = $tmp;
            }
        } else {
            $response['error'] = true;
            $response['message'] = 'Failed send message';
        }
 
        return $response;
    }
	// fetching all chat rooms
    public function getAllChatrooms() {
        $stmt = $this->conn->prepare("SELECT * FROM chat_rooms");
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }
 
    // fetching single chat room by id
    function getChatRoom($chat_room_id) {
        $stmt = $this->conn->prepare("SELECT cr.chat_room_id, cr.name, cr.created_at as chat_room_created_at, u.name as username, c.* FROM chat_rooms cr LEFT JOIN messages c ON c.chat_room_id = cr.chat_room_id LEFT JOIN users u ON u.user_id = c.user_id WHERE cr.chat_room_id = ?");
        $stmt->bind_param("i", $chat_room_id);
        $stmt->execute();
        $tasks = $stmt->get_result();
        $stmt->close();
        return $tasks;
    }
	
	/**
     * Fetching user by email
     * @param String $email User email id
     */
    public function getUserByEmail($email) {
        $stmt = $this->conn->prepare("SELECT user_id, name, email, created_at FROM users WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($user_id, $name, $email, $created_at);
            $stmt->fetch();
            $user = array();
            $user["unique_id"] = $user_id;
            $user["name"] = $name;
            $user["email"] = $email;
            $user["created_at"] = $created_at;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }
 
}
 
?>