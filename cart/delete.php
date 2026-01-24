<?php
header("Content-Type: application/json");
include "../config/connect.php";

$item_id = $_POST['item_id'];

mysqli_query($conn, "DELETE FROM cart_items WHERE id=$item_id");

echo json_encode([
    "status" => true,
    "message" => "Item dihapus dari cart"
]);
