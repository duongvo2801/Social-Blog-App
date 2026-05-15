package com.example.socialblog

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.example.socialblog.databinding.ActivityMainBinding

import com.example.socialblog.fragment.FeedFragment
import com.example.socialblog.fragment.PostFragment
import com.example.socialblog.fragment.UserFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // DEFAULT

        replaceFragment(
            FeedFragment()
        )

        // BOTTOM NAVIGATION

        binding.bottomNavigation
            .setOnItemSelectedListener {

                when (it.itemId) {

                    R.id.nav_feed -> {

                        replaceFragment(
                            FeedFragment()
                        )

                        true
                    }

                    R.id.nav_post -> {

                        replaceFragment(
                            PostFragment()
                        )

                        true
                    }

                    R.id.nav_user -> {

                        replaceFragment(
                            UserFragment()
                        )

                        true
                    }

                    else -> false
                }
            }
    }

    private fun replaceFragment(
        fragment: Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.frameLayout,
                fragment
            )
            .commit()
    }
}