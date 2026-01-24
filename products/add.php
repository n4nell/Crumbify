<?php

error_reporting(E_ALL);
ini_set('display_errors', 1);

header("Content-Type: application/json");
include "../config/connect.php";

if (
    !isset($_POST['name']) ||
    !isset($_POST['price']) ||
    !isset($_POST['category_id']) ||
    !isset($_FILES['image'])
) {
    echo json_encode(["status"=>false,"message"=>"Data tidak lengkap"]);
    exit;
}

$name        = mysqli_real_escape_string($conn, $_POST['name']);
$description = mysqli_real_escape_string($conn, $_POST['description'] ?? '');
$price       = $_POST['price'];
$category_id = $_POST['category_id'];

/* Upload gambar */
$targetDir = "../uploads/";
$fileName  = time() . "_" . basename($_FILES["image"]["name"]);
$targetFile = $targetDir . $fileName;

move_uploaded_file($_FILES["image"]["tmp_name"], $targetFile);

/* Simpan ke DB */
mysqli_query($conn, "
    INSERT INTO products (category_id, name, description, price, image, created_at)
    VALUES ($category_id, '$name', '$description', $price, '$fileName', NOW())
");

echo json_encode(["status"=>true,"message"=>"Produk berhasil ditambahkan"]);
