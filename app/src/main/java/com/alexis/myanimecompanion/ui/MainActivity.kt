package com.alexis.myanimecompanion.ui

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).setupWithNavController(navController)
    }

    override fun onResume() {
        super.onResume()

        val data = intent?.data
        val scheme = data?.scheme
        val host = data?.host

        if (scheme == "oauth" && host == "callback") {
            handleMALAuthorizationResponse(data)
        }
    }

    private fun handleMALAuthorizationResponse(data: Uri) {
        val authorizationCode = requireNotNull(data.getQueryParameter("code"))
        lifecycleScope.launch {
            UserRepository.getInstance(applicationContext).onAuthorizationCodeReceived(authorizationCode)
        }
    }
}
