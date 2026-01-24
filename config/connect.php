<?php

$server = 'localhost';
$username = 'root';
$password = '';
$database = 'crumbify';

$conn = mysqli_connect($server, $username, $password, $database);

if (mysqli_connect_errno()) {
    http_response_code(500);
    echo json_encode([
        "status" => "error",
        "message" => "koneksi database gagal"
    ]);
    exit;
}


?>