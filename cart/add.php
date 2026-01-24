<?php
header("Content-Type: application/json");
include "../config/connect.php";

$user_id    = $_POST['user_id'];
$product_id = $_POST['product_id'];
$qty        = $_POST['qty'] ?? 1;

$cart = mysqli_query($conn, "SELECT id FROM carts WHERE user_id = $user_id");

if (mysqli_num_rows($cart) == 0) {
    mysqli_query($conn, "INSERT INTO carts (user_id) VALUES ($user_id)");
    $cart_id = mysqli_insert_id($conn);
} else {
    $cart_id = mysqli_fetch_assoc($cart)['id'];
}

$item = mysqli_query(
    $conn,
    "SELECT id, qty FROM cart_items WHERE cart_id=$cart_id AND product_id=$product_id"
);

if (mysqli_num_rows($item) > 0) {
    $row = mysqli_fetch_assoc($item);
    $newQty = $row['qty'] + $qty;
    mysqli_query(
        $conn,
        "UPDATE cart_items SET qty=$newQty WHERE id=".$row['id']
    );
} else {
    mysqli_query(
        $conn,
        "INSERT INTO cart_items (cart_id, product_id, qty)
         VALUES ($cart_id, $product_id, $qty)"
    );
}

echo json_encode([
    "status" => true,
    "message" => "Produk berhasil ditambahkan ke cart"
]);
