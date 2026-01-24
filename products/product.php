<?php
header("Content-Type: application/json");
include "../config/connect.php";

// cek apakah id dikirim
if (!isset($_GET['id'])) {
    echo json_encode([
        "status" => false,
        "message" => "Product ID is required"
    ]);
    exit;
}

$id = $_GET['id'];

$query = "SELECT * FROM products WHERE id = $id";
$result = mysqli_query($conn, $query);

if (mysqli_num_rows($result) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Product not found"
    ]);
    exit;
}

$product = mysqli_fetch_assoc($result);

echo json_encode([
    "status" => true,
    "data" => $product
]);
