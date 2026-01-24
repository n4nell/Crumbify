<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php"; // sesuaikan kalau nama file koneksi beda

$query = "SELECT 
            orders.id,
            users.username AS customer_name,
            orders.total_price,
            orders.created_at
          FROM orders
          JOIN users ON users.id = orders.user_id
          WHERE orders.created_at >= NOW() - INTERVAL 30 DAY
          ORDER BY orders.created_at DESC";

$result = mysqli_query($conn, $query);

if (!$result) {
    echo json_encode([
        "status" => false,
        "message" => mysqli_error($conn)
    ]);
    exit;
}

$data = [];

while ($row = mysqli_fetch_assoc($result)) {
    $data[] = $row;
}

echo json_encode([
    "status" => true,
    "orders" => $data
]);
