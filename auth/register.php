<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
include '../config/connect.php';

$email    = trim($_POST['email']);
$username = trim($_POST['username']);
$password = trim($_POST['password']);

if ($email == '' || $username == '' || $password == '') {
    echo json_encode([
        "status" => "error",
        "message" => "email, username, dan password wajib diisi"
    ]);
    exit;
}

/* cek email */
$cekEmail = mysqli_query($conn, "SELECT id FROM users WHERE email='$email'");
if (mysqli_num_rows($cekEmail) > 0) {
    echo json_encode([
        "status" => "error",
        "message" => "email sudah terdaftar"
    ]);
    exit;
}

/* cek username */
$cekUsername = mysqli_query($conn, "SELECT id FROM users WHERE username='$username'");
if (mysqli_num_rows($cekUsername) > 0) {
    echo json_encode([
        "status" => "error",
        "message" => "username sudah digunakan"
    ]);
    exit;
}

/* REGISTER SEBAGAI USER BIASA */
$role = 'user';

$sql = "INSERT INTO users (email, username, password, role)
        VALUES ('$email', '$username', '$password', '$role')";

if (mysqli_query($conn, $sql)) {
    echo json_encode([
        "status" => "sukses",
        "message" => "registrasi user berhasil"
    ]);
} else {
    echo json_encode([
        "status" => "error",
        "message" => mysqli_error($conn)
    ]);
}
?>