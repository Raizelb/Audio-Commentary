<?php
 //Change the values according to your database
 
 define('HOST','databases.000webhost.com');
 define('USER','id446256_audiocommentaryaccounts');
 define('PASS','Gacon514');
 define('DB','id446256_audiocommentaryaccounts');
 
 $con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');