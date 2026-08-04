package com.example.crumbify

object Konfigurasi {
    private const val BASE_URL = "http://10.0.2.2/crumbify/"

    // --- AUTH ---
    const val URL_LOGIN = BASE_URL + "auth/login.php"
    const val URL_REGISTER = BASE_URL + "auth/register.php"

    // --- CART ---
    const val URL_ADD_CART = BASE_URL + "cart/add.php"
    const val URL_DELETE_CART = BASE_URL + "cart/delete.php"
    const val URL_INDEX_CART = BASE_URL + "cart/index.php"
    const val URL_UPDATE_CART = BASE_URL + "cart/update.php"

    // --- ORDER ---
    const val URL_ORDER_ADMIN_DETAIL = BASE_URL + "order/admin_detail.php"
    const val URL_ORDER_ADMIN_HISTORY = BASE_URL + "order/admin_history.php"
    const val URL_ORDER_CHECKOUT = BASE_URL + "order/checkout.php"
    const val URL_ORDER_DETAIL = BASE_URL + "order/detail.php"
    const val URL_ORDER_HISTORY = BASE_URL + "order/history.php"

    // --- PRODUCTS ---
    const val URL_PRODUCT_ADD = BASE_URL + "products/add.php"
    const val URL_PRODUCT_DELETE = BASE_URL + "products/delete.php"
    const val URL_PRODUCT_SINGLE = BASE_URL + "products/product.php"
    const val URL_PRODUCT_LIST = BASE_URL + "products/products.php"
    const val URL_PRODUCT_UPDATE = BASE_URL + "products/update.php"

    // --- WISHLIST ---
    const val URL_WISHLIST_ADD = BASE_URL + "wishlist/add.php"
    const val URL_WISHLIST_DELETE = BASE_URL + "wishlist/delete.php"
    const val URL_WISHLIST_INDEX = BASE_URL + "wishlist/index.php"

    // --- NOTIFICATIONS ---
    const val URL_SEND_NOTIF = BASE_URL + "notifications/send.php"
    const val URL_GET_NOTIF = BASE_URL + "notifications/get.php"

    // --- LAIN-LAIN ---
    const val URL_CATEGORIES = BASE_URL + "categories.php"

    // --- KEY UNTUK KIRIM DATA (POST/GET) ---
    const val KEY_ID_USER = "id_user"
    const val KEY_ID_PRODUK = "id_produk"
    const val KEY_QTY = "qty"
    const val KEY_HARGA = "harga"
    const val KEY_NAMA_PRODUK = "nama_produk"
    const val URL_IMAGE = BASE_URL + "img/"

    // --- TAG UNTUK PARSING JSON ---
    const val TAG_JSON_ARRAY = "result"
    const val TAG_SUCCESS = "status"
    const val TAG_MESSAGE = "message"
}