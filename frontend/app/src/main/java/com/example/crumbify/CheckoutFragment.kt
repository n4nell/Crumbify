package com.example.crumbify

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class CheckoutFragment : Fragment() {

    private lateinit var rvCheckout: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnContinue: MaterialButton
    private lateinit var tvOrderId: TextView

    companion object {
        fun newInstance(cartList: ArrayList<Cart>, orderId: String): CheckoutFragment {
            val fragment = CheckoutFragment()
            val args = Bundle()
            args.putParcelableArrayList("cart_items", cartList)
            args.putString("order_id", orderId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_checkout, container, false)

        rvCheckout = view.findViewById(R.id.recyclerViewCheckOut)
        tvTotal = view.findViewById(R.id.textView16)
        btnContinue = view.findViewById(R.id.button2)
        tvOrderId = view.findViewById(R.id.textView12)

        val cartItems = arguments?.getParcelableArrayList<Cart>("cart_items") ?: arrayListOf()
        val orderId = arguments?.getString("order_id") ?: "-"

        tvOrderId.text = "#$orderId"

        setupRecyclerView(cartItems)
        calculateTotal(cartItems)

        btnContinue.setOnClickListener {
            val sharedPref =
                requireContext().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            val userId = sharedPref.getInt("id", -1)

            btnContinue.isEnabled = false

            lifecycleScope.launch(Dispatchers.IO) {
                if (userId != -1) {
                    val params = HashMap<String, String>()
                    params["user_id"] = userId.toString()
                    RequestHandler().sendPostRequest(Konfigurasi.URL_DELETE_CART, params)
                }

                withContext(Dispatchers.Main) {
                    if (isAdded) {
                        showSuccessDialog()
                    }
                }
            }
        }

        return view
    }

    private fun showSuccessDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())

        builder.setTitle("Order Successful")
        builder.setMessage("\nYour delicious order is being processed.\n")
        builder.setCancelable(false)

        builder.setPositiveButton("CONTINUE SHOPPING") { _, _ ->
            parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

            (activity as? HomeActivity)?.let { home ->
                home.goToHomeTab()
            }
        }

        val dialog = builder.create()
        dialog.show()

        val messageView = dialog.findViewById<TextView>(android.R.id.message)
        messageView?.gravity = android.view.Gravity.CENTER
        messageView?.textSize = 18f
    }

    private fun setupRecyclerView(items: List<Cart>) {
        val adapter = CheckoutAdapter(items)
        rvCheckout.layoutManager = LinearLayoutManager(context)
        rvCheckout.adapter = adapter
    }

    private fun calculateTotal(items: List<Cart>) {
        var total = 0.0
        for (item in items) {
            total += item.total.toDoubleOrNull() ?: 0.0
        }
        tvTotal.text = "Total : ${formatRupiah(total)}"
    }

    private fun formatRupiah(number: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        return format.format(number).replace("Rp", "Rp ").replace(",00", "")
    }
}