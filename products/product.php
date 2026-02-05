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

$sql = "
    SELECT 
        p.id,
        p.name,
        p.description,
        p.price,
        p.stock,
        p.image,
        p.created_at,
        c.name AS category_name
    FROM products p
    JOIN categories c ON p.category_id = c.id
    WHERE p.id = $id
";

$query = mysqli_query($conn, $sql);

if (!$query) {
    echo json_encode([
        "status" => false,
        "message" => mysqli_error($conn)
    ]);
    exit;
}

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
