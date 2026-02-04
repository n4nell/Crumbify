<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php";

$user_id    = $_POST['user_id'];
$product_id = $_POST['product_id'];

$check = mysqli_query($conn, "
    SELECT id FROM wishlist 
    WHERE user_id = $user_id AND product_id = $product_id
");

if (mysqli_num_rows($check) > 0) {
    echo json_encode([
        "status" => false,
        "message" => "Produk sudah ada di wishlist"
    ]);
    exit;
}

$insert = mysqli_query($conn, "
    INSERT INTO wishlist (user_id, product_id, created_at)
    VALUES ($user_id, $product_id, NOW())
");

echo json_encode([
    "status" => true,
    "message" => "Produk ditambahkan ke wishlist"
]);
