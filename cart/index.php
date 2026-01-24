<?php
header("Content-Type: application/json");
include "../config/connect.php";

$user_id = $_GET['user_id'];

$sql = "
SELECT 
    ci.id,
    p.name,
    p.price,
    ci.qty,
    (p.price * ci.qty) AS total
FROM cart_items ci
JOIN carts c ON ci.cart_id = c.id
JOIN products p ON ci.product_id = p.id
WHERE c.user_id = $user_id
";

$result = mysqli_query($conn, $sql);

$data = [];
$grandTotal = 0;

while ($row = mysqli_fetch_assoc($result)) {
    $grandTotal += $row['total'];
    $data[] = $row;
}

echo json_encode([
    "status" => true,
    "grand_total" => $grandTotal,
    "items" => $data
]);
