<?php
header("Content-Type: application/json");
include "../config/connect.php";

$wishlist_id = $_POST['wishlist_id'];

mysqli_query($conn, "DELETE FROM wishlist WHERE id = $wishlist_id");

echo json_encode([
    "status" => true,
    "message" => "Produk dihapus dari wishlist"
]);
