<?php
header("Content-Type: application/json");
include "../config/connect.php";

$order_id = (int) $_GET['order_id'];

$query = mysqli_query($conn, "
    SELECT p.name, oi.price, oi.qty, oi.subtotal
    FROM order_items oi
    JOIN products p ON oi.product_id = p.id
    WHERE oi.order_id = $order_id
");

$items = [];
while ($row = mysqli_fetch_assoc($query)) {
    $items[] = $row;
}

echo json_encode([
    "status" => true,
    "items" => $items
]);
