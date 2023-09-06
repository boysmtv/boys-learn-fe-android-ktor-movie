package com.kotlin.learn.feature.menu.presentation.ui

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.kotlin.learn.core.common.base.BaseFragment
import com.kotlin.learn.core.nav.data.ITransactionNavigationParent
import com.kotlin.learn.core.nav.navigator.AuthNavigator
import com.kotlin.learn.feature.menu.R
import com.kotlin.learn.feature.menu.databinding.FragmentMenuBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MenuFragment : BaseFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate), ITransactionNavigationParent {

    private val tag = this::class.java.simpleName

    @Inject
    lateinit var authNavigator: AuthNavigator

    override fun setupView() {
        setupInit()
    }

    private fun setupInit() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_menu_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun navigateToGreetings() {
        authNavigator.fromMenuToGreetings(this@MenuFragment)
    }

}