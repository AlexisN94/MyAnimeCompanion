package com.alexis.myanimecompanion.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.RemoteDataSource
import com.alexis.myanimecompanion.databinding.FragmentLoginBinding
import com.alexis.myanimecompanion.databinding.FragmentProfileBinding
import com.alexis.myanimecompanion.ui.profile.ProfileViewModel
import com.alexis.myanimecompanion.ui.profile.ProfileViewModelFactory
import androidx.core.content.ContextCompat.startActivity




class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentLoginBinding = FragmentLoginBinding.inflate(inflater)
        val animeRepository = AnimeRepository.getInstance(requireContext())

        val viewModelFactory = LoginViewModelFactory(animeRepository)
        val viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[LoginViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.webView.settings.apply{
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            domStorageEnabled = true
        }
        binding.webView.webViewClient = WebViewController(requireContext())
        
        viewModel.webViewUrl.observe(viewLifecycleOwner, {
            binding.webView.loadUrl(it)
        })

        return binding.root
    }
}

class WebViewController(val context: Context) : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if(url.contains("oauth://callback")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(context, intent, null)
        } else {
            view.loadUrl(url)
        }
        return true
    }
}
