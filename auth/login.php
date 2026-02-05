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
$password = $_POST['password'];

$query = mysqli_query($conn, "
    SELECT id, username, name, email, password 
    FROM users 
    WHERE username = '$username'
");

if (mysqli_num_rows($query) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Username tidak ditemukan"
    ]);
    exit;
}

$user = mysqli_fetch_assoc($query);

/* Verifikasi password */
if (!password_verify($password, $user['password'])) {
    echo json_encode([
        "status" => false,
        "message" => "Password atau username salah"
    ]);
    exit;
}

/* Jangan kirim password ke frontend */
unset($user['password']);

echo json_encode([
    "status" => true,
    "message" => "Login berhasil",
    "user" => $user
]);
