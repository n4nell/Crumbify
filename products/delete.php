<?php
header("Content-Type: application/json");
include "../config/connect.php";

$id = $_POST['id'];

$q = mysqli_query($conn, "SELECT image FROM products WHERE id=$id");
$data = mysqli_fetch_assoc($q);

if ($data && file_exists("../uploads/".$data['image'])) {
    unlink("../uploads/".$data['image']);
}

mysqli_query($conn, "DELETE FROM products WHERE id=$id");

echo json_encode(["status"=>true,"message"=>"Produk dihapus"]);
