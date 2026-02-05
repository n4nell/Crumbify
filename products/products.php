<?php
header("Content-Type: application/json");
include "../config/connect.php";

$where = [];

if (isset($_GET['category'])) {
    $category = mysqli_real_escape_string($conn, $_GET['category']);
    $where[] = "c.name = '$category'";
}

if (isset($_GET['search'])) {
    $search = mysqli_real_escape_string($conn, $_GET['search']);
    $where[] = "p.name LIKE '%$search%'";
}

// 🔥 Tambahan: hanya tampilkan produk yang stoknya ada
$where[] = "p.stock > 0";

$sql = "
    SELECT 
        p.id,
        p.name,
        p.description,
        p.price,
        p.stock,
        p.image,
        p.created_at,
        c.name AS category_name
    FROM products p
    JOIN categories c ON p.category_id = c.id
";

if (!empty($where)) {
    $sql .= " WHERE " . implode(" AND ", $where);
}

$sql .= " ORDER BY p.created_at DESC";

$result = mysqli_query($conn, $sql);

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
    "total" => count($data),
    "data" => $data
]);
