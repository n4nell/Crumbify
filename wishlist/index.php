<?php
header("Content-Type: application/json");
include "../config/connect.php";

$user_id = $_GET['user_id'];

$query = mysqli_query($conn, "
    SELECT 
        w.id,
        p.id AS product_id,
        p.name,
        p.price,
        p.image
    FROM wishlist w
    JOIN products p ON w.product_id = p.id
    WHERE w.user_id = $user_id
    ORDER BY w.created_at DESC
");

$data = [];
while ($row = mysqli_fetch_assoc($query)) {
    $data[] = $row;
}

echo json_encode([
    "status" => true,
    "wishlist" => $data
]);