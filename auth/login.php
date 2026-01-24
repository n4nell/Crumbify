<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
include '../config/connect.php';

$username = trim($_POST['username']);
$password = trim($_POST['password']);

if ($username == '' || $password == '') {
    echo json_encode([
        "status" => "error",
        "message" => "username dan password wajib diisi"
    ]);
    exit;
}

$sql = "SELECT * FROM users WHERE username='$username' LIMIT 1";
$result = mysqli_query($conn, $sql);

if (mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => "error",
        "message" => "username tidak ditemukan"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($result);

if (password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => "sukses",
        "message" => "login berhasil",
        "data" => [
            "id"       => $user['id'],
            "email"    => $user['email'],
            "username" => $user['username'],
            "role"     => $user['role']
        ]
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "password atau username salah"
    ]);
}