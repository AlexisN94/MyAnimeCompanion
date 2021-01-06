package com.alexis.myanimecompanion.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alexis.myanimecompanion.data.RemoteDataSource

class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TODO("If user is logged in, proceed. Otherwise, offer button to redirect to login fragment")
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
