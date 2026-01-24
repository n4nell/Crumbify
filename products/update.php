<?php
header("Content-Type: application/json");
include "../config/connect.php";

$id          = $_POST['id'];
$name        = $_POST['name'];
$description = $_POST['description'];
$price       = $_POST['price'];
$category_id = $_POST['category_id'];

if (isset($_FILES['image'])) {
    $fileName = time() . "_" . basename($_FILES["image"]["name"]);
    move_uploaded_file($_FILES["image"]["tmp_name"], "../uploads/" . $fileName);

    mysqli_query($conn, "
        UPDATE products 
        SET name='$name', description='$description', price=$price, category_id=$category_id, image='$fileName'
        WHERE id=$id
    ");
} else {
    mysqli_query($conn, "
        UPDATE products 
        SET name='$name', description='$description', price=$price, category_id=$category_id
        WHERE id=$id
    ");
}

echo json_encode(["status"=>true,"message"=>"Produk berhasil diupdate"]);
