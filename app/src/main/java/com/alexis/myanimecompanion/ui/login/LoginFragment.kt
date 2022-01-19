package com.alexis.myanimecompanion.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alexis.myanimecompanion.data.RemoteDataSource
import com.alexis.myanimecompanion.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentLoginBinding = FragmentLoginBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    private fun startLogin() {
        val authorizationURL = RemoteDataSource.getInstance(requireContext()).getAuthorizationURL()
        val browse = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationURL))
        startActivity(browse)
    }
}
