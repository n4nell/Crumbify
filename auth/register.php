<?php
header("Content-Type: application/json");
include "../config/connect.php";

if (
    !isset($_POST['username']) ||
    !isset($_POST['name']) ||
    !isset($_POST['email']) ||
    !isset($_POST['password'])
) {
    echo json_encode([
        "status" => false,
        "message" => "Username, nama, email, dan password wajib diisi"
    ]);
    exit;
}

$username = mysqli_real_escape_string($conn, $_POST['username']);
$name     = mysqli_real_escape_string($conn, $_POST['name']);
$email    = mysqli_real_escape_string($conn, $_POST['email']);
$password = $_POST['password'];

/* Cek username sudah dipakai atau belum */
$checkUsername = mysqli_query($conn, "SELECT id FROM users WHERE username = '$username'");
if (mysqli_num_rows($checkUsername) > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Username sudah digunakan"
    ]);
    exit;
}

/* (Opsional tapi bagus) Cek email juga */
$checkEmail = mysqli_query($conn, "SELECT id FROM users WHERE email = '$email'");
if (mysqli_num_rows($checkEmail) > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Email sudah terdaftar"
    ]);
    exit;
}

/* Hash password */
$hashedPassword = password_hash($password, PASSWORD_DEFAULT);

/* Simpan user */
$insert = mysqli_query($conn, "
    INSERT INTO users (username, name, email, password, created_at)
    VALUES ('$username', '$name', '$email', '$hashedPassword', NOW())
");

if (!$insert) {
    echo json_encode([
        "status" => false,
        "message" => mysqli_error($conn)
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Registrasi berhasil"
]);
