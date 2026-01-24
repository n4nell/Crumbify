<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php";

if (!isset($_POST['item_id']) || !isset($_POST['qty'])) {
    echo json_encode([
        "status" => false,
        "message" => "item_id dan qty wajib"
    ]);
    exit;
}

$item_id = (int) $_POST['item_id'];
$qty     = (int) $_POST['qty'];

if ($qty <= 0) {
    $delete = mysqli_query($conn, "DELETE FROM cart_items WHERE id = $item_id");

    if (!$delete) {
        echo json_encode([
            "status" => false,
            "message" => mysqli_error($conn)
        ]);
        exit;
    }

    echo json_encode([
        "status" => true,
        "message" => "Item dihapus"
    ]);
    exit;
}

$update = mysqli_query(
    $conn,
    "UPDATE cart_items SET qty = $qty WHERE id = $item_id"
);

if (!$update) {
    echo json_encode([
        "status" => false,
        "message" => mysqli_error($conn)
    ]);
    exit;
}

echo json_encode([
    "status" => true,
    "message" => "Qty diperbarui"
]);
