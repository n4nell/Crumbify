<?php
header("Content-Type: application/json");
include "../config/connect.php";

$user_id = (int) $_GET['user_id'];

$result = mysqli_query($conn, "
    SELECT id, total_price, created_at
    FROM orders
    WHERE user_id = $user_id
    ORDER BY created_at DESC
");

$data = [];
while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode([
    "status"=>true,
    "orders"=>$data
]);
