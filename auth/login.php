<?php
header("Content-Type: application/json");
include "../config/connect.php";

if (!isset($_POST['username']) || !isset($_POST['password'])) {
    echo json_encode([
        "status" => false,
        "message" => "Username dan password wajib diisi"
    ]);
    exit;
}

$username = mysqli_real_escape_string($conn, $_POST['username']);
$password = mysqli_real_escape_string($conn, $_POST['password']);

$query = mysqli_query($conn, "
    SELECT id, username, name, email 
    FROM users 
    WHERE username = '$username' 
      AND password = '$password'
");

if (mysqli_num_rows($query) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Username atau password salah"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($query);

echo json_encode([
    "status" => true,
    "message" => "Login berhasil",
    "user" => $user
]);
