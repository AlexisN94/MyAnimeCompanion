package com.alexis.myanimecompanion.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.RemoteDataSource
import com.alexis.myanimecompanion.databinding.FragmentLoginBinding
import com.alexis.myanimecompanion.databinding.FragmentProfileBinding
import com.alexis.myanimecompanion.ui.profile.ProfileViewModel
import com.alexis.myanimecompanion.ui.profile.ProfileViewModelFactory

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
        binding.webView.webViewClient = WebViewController()
        
        viewModel.webViewUrl.observe(viewLifecycleOwner, {
            binding.webView.loadUrl(it)
        })
        return binding.root

    }
}

class WebViewController : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}
