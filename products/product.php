<?php
header("Content-Type: application/json");
include "../config/connect.php";

if (!isset($_GET['id'])) {
    echo json_encode([
        "status" => false,
        "message" => "Product ID is required"
    ]);
    exit;
}

$id = (int) $_GET['id'];

$query = mysqli_query($conn, "
    SELECT 
        id,
        category_id,
        name,
        description,
        price,
        image,
        created_at
    FROM products 
    WHERE id = $id
");

if (mysqli_num_rows($query) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Product not found"
    ]);
    exit;
}

$product = mysqli_fetch_assoc($query);

echo json_encode([
    "status" => true,
    "data" => $product
]);
