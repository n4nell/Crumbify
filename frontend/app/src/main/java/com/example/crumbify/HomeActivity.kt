package com.example.crumbify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tabLayout = findViewById(R.id.tabLayout)

        // 1. Logika untuk menangani navigasi dari DetailActivity
        val target = intent.getStringExtra("TARGET_FRAGMENT")

        if (target == "CART") {
            // Jika ada perintah buka CART, arahkan ke Fragment Cart (posisi 2)
            openFragment(CartFragment())
            tabLayout.getTabAt(2)?.select()
        } else {
            // Jika tidak ada perintah (login biasa/buka app), ke Home (posisi 0)
            if (savedInstanceState == null) {
                openFragment(HomeFragment())
                tabLayout.getTabAt(0)?.select()
            }
        }

        // 2. Listener untuk perpindahan Tab secara manual oleh user
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> openFragment(HomeFragment())
                    1 -> openFragment(CategoriesFragment())
                    2 -> openFragment(CartFragment())
                    3 -> openFragment(ProfileFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container2, fragment)
            .commit()
    }

    fun goToHomeTab() {
        val tabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout) // Sesuaikan ID-nya
        tabLayout.getTabAt(0)?.select()
    }
}