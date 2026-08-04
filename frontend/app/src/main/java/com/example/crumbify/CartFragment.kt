package com.example.crumbify

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment(), CartAdapter.OnCartChangeListener {
    private lateinit var adapter: CartAdapter
    private lateinit var rvCart: RecyclerView
    private lateinit var btnOrderNow: MaterialButton

    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var mainLayout: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        rvCart = view.findViewById(R.id.recyclerView3)
        btnOrderNow = view.findViewById(R.id.button4)
        progressBar = view.findViewById(R.id.progressBarCart)
        mainLayout = view.findViewById(R.id.mainLayoutCart)

        setupRecyclerView()
        tampilkanCart()

        btnOrderNow.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("id", -1)

            if (userId == -1) {
                Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cartList = adapter.getData()
            if (cartList.isNotEmpty()) {
                btnOrderNow.isEnabled = false

                val dataToSend = ArrayList(cartList)

                lifecycleScope.launch(Dispatchers.IO) {
                    val params = HashMap<String, String>()
                    params["user_id"] = userId.toString()

                    val rh = RequestHandler()
                    val result = rh.sendPostRequest(Konfigurasi.URL_ORDER_CHECKOUT, params)

                    withContext(Dispatchers.Main) {
                        try {
                            val jo = JSONObject(result)
                            if (jo.getBoolean("status")) {
                                val orderIdFromServer = jo.getString("order_id")
                                val checkoutFragment = CheckoutFragment.newInstance(dataToSend, orderIdFromServer)

                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container2, checkoutFragment)
                                    .addToBackStack(null)
                                    .commit()
                            } else {
                                btnOrderNow.isEnabled = true
                                Toast.makeText(context, jo.getString("message"), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            btnOrderNow.isEnabled = true
                            e.printStackTrace()
                            Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }


    private fun setupRecyclerView() {
        adapter = CartAdapter(mutableListOf(), this)
        rvCart.layoutManager = LinearLayoutManager(context)
        rvCart.adapter = adapter
    }

    private fun tampilkanCart() {
        val sharedPref = requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("id", -1)

        if (userId == -1) return

        progressBar.visibility = View.VISIBLE
        mainLayout.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            val rh = RequestHandler()
            val json = rh.sendGetRequestParam(Konfigurasi.URL_INDEX_CART, "?user_id=$userId")

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                mainLayout.visibility = View.VISIBLE

                try {
                    val jo = JSONObject(json)
                    if (jo.getBoolean("status")) {
                        val result = jo.getJSONArray("items")
                        val listCart = mutableListOf<Cart>()
                        for (i in 0 until result.length()) {
                            val obj = result.getJSONObject(i)
                            listCart.add(Cart(
                                obj.getString("id"),
                                obj.getString("name"),
                                obj.getString("price"),
                                obj.getString("qty"),
                                obj.getString("total"),
                                obj.getString("image")
                            ))
                        }
                        adapter.updateData(listCart)
                        val grandTotal = jo.optDouble("grand_total", 0.0)
                        btnOrderNow.text = "Order Now : ${formatRupiah(grandTotal)}"
                    } else {
                        adapter.updateData(mutableListOf())
                        btnOrderNow.text = "Cart is Empty"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    mainLayout.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    override fun onChange(itemId: String, newQty: Int) {
        if (newQty <= 0) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            builder.setTitle("Remove Item")
            builder.setMessage("Are you sure you want to remove this item from your cart?")

            builder.setPositiveButton("Yes, Remove") { _, _ ->
                updateCartAtServer(itemId, 0)
            }

            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                tampilkanCart()
            }

            builder.show()
        } else {
            updateCartAtServer(itemId, newQty)
        }
    }

    private fun updateCartAtServer(itemId: String, qty: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val params = HashMap<String, String>()
            params["item_id"] = itemId
            params["qty"] = qty.toString()

            val rh = RequestHandler()
            val result = rh.sendPostRequest(Konfigurasi.URL_UPDATE_CART, params)

            withContext(Dispatchers.Main) {
                try {
                    val jo = JSONObject(result)
                    if (jo.getBoolean("status")) {
                        tampilkanCart()
                    } else {
                        Toast.makeText(context, jo.getString("message"), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}