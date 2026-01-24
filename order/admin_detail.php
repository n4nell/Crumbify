<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php";

if (!isset($_GET['order_id'])) {
    echo json_encode([
        "status" => false,
        "message" => "order_id wajib"
    ]);
    exit;
}

$order_id = (int) $_GET['order_id'];


// 🔹 Ambil info order + nama user
$order_query = mysqli_query($conn, "
    SELECT 
        orders.id,
        users.username AS customer_name,
        orders.total_price,
        orders.created_at
    FROM orders
    JOIN users ON users.id = orders.user_id
    WHERE orders.id = $order_id
");

if (mysqli_num_rows($order_query) == 0) {
    echo json_encode([
        "status" => false,
        "message" => "Order tidak ditemukan"
    ]);
    exit;
}

$order = mysqli_fetch_assoc($order_query);


// 🔹 Ambil daftar barang di order itu
$items_query = mysqli_query($conn, "
    SELECT 
        products.name AS product_name,
        order_items.price,
        order_items.qty,
        order_items.subtotal
    FROM order_items
    JOIN products ON products.id = order_items.product_id
    WHERE order_items.order_id = $order_id
");

$items = [];
while ($row = mysqli_fetch_assoc($items_query)) {
    $items[] = $row;
}


// 🔹 Output final
echo json_encode([
    "status" => true,
    "order" => $order,
    "items" => $items
]);
