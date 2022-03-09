package com.alexis.myanimecompanion.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.Error
import com.alexis.myanimecompanion.di.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject lateinit var animeRepository: AnimeRepository
    lateinit var appComponent: AppComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        appComponent = DaggerAppComponent.factory().create(
            DataSourceModule(),
            RepositoryModule(),
            ContextModule(this)
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bottom_nav).apply {
            setupWithNavController(navController)
            setOnItemReselectedListener { reselectedDestination ->
                navController.popBackStack(reselectedDestination.itemId, inclusive = false)
            }
        }
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
            val result = animeRepository.requestToken(authorizationCode)
            if (result.isFailure) {
                val toastMessage = when (result.errorOrNull()!!) {
                    Error.Network -> "A network error occurred while logging you in"
                    Error.Authorization -> "Your authentication was not successful"
                    else -> "An unexpected error occurred while logging you in"
                }

                Toast.makeText(applicationContext, toastMessage, Toast.LENGTH_LONG).show()
            } else {
                withContext(Dispatchers.IO) {
                    animeRepository.postLogin()
                }
            }
        }
    }
}
