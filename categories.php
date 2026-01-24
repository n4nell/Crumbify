<?php
header('Content-Type: application/json');
include 'config/connect.php';

$query = "SELECT * FROM categories";
$result = mysqli_query($conn, $query);

$categories = [];

while ($row = mysqli_fetch_assoc($result)) {
    $categories[] = $row;
}

echo json_encode([
    "status" => "success",
    "data" => $categories
]);
