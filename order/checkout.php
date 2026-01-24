<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php";

$user_id = (int) $_POST['user_id'];

/* Ambil cart user */
$cart = mysqli_query($conn, "SELECT id FROM carts WHERE user_id = $user_id");
if (mysqli_num_rows($cart) == 0) {
    echo json_encode(["status"=>false,"message"=>"Cart kosong"]);
    exit;
}
$cart_id = mysqli_fetch_assoc($cart)['id'];

/* Ambil semua item cart */
$items = mysqli_query($conn, "
    SELECT ci.product_id, ci.qty, p.price
    FROM cart_items ci
    JOIN products p ON ci.product_id = p.id
    WHERE ci.cart_id = $cart_id
");

if (mysqli_num_rows($items) == 0) {
    echo json_encode(["status"=>false,"message"=>"Cart kosong"]);
    exit;
}

/* Hitung total */
$total = 0;
$dataItems = [];

while ($row = mysqli_fetch_assoc($items)) {
    $subtotal = $row['price'] * $row['qty'];
    $total += $subtotal;
    $dataItems[] = $row + ["subtotal"=>$subtotal];
}

/* Simpan ke orders */
mysqli_query($conn, "
    INSERT INTO orders (user_id, total_price, created_at)
    VALUES ($user_id, $total, NOW())
");

$order_id = mysqli_insert_id($conn);

/* Simpan ke order_items */
foreach ($dataItems as $item) {
    mysqli_query($conn, "
        INSERT INTO order_items (order_id, product_id, price, qty, subtotal)
        VALUES ($order_id, {$item['product_id']}, {$item['price']}, {$item['qty']}, {$item['subtotal']})
    ");
}

/* Kosongkan cart */
mysqli_query($conn, "DELETE FROM cart_items WHERE cart_id = $cart_id");

echo json_encode([
    "status" => true,
    "message" => "Order berhasil dibuat",
    "order_id" => $order_id,
    "total" => $total
]);
