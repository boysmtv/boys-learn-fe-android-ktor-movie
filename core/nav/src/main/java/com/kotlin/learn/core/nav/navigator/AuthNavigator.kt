package com.kotlin.learn.core.nav.navigator

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.kotlin.learn.feature.auth.presentation.ui.AuthFragmentDirections
import com.kotlin.learn.feature.auth.presentation.ui.GreetingsFragmentDirections
import com.kotlin.learn.feature.auth.presentation.ui.RegisterFragmentDirections
import com.kotlin.learn.feature.movie.presentation.ui.SettingFragmentDirections
import com.kotlin.learn.feature.splash.presentation.ui.SplashFragmentDirections

class AuthNavigator {

    fun fromSplashToGreetings(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            SplashFragmentDirections.actionSplashFragmentToGreetingsFragment()
        )
    }

    fun fromSplashToMenu(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            SplashFragmentDirections.actionSplashFragmentToMenuFragment()
        )
    }

    fun fromGreetingsToRegister(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            GreetingsFragmentDirections.actionGreetingsFragmentToRegisterFragment()
        )
    }

    fun fromGreetingsToAuth(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            GreetingsFragmentDirections.actionGreetingsFragmentToAuthFragment()
        )
    }

    fun fromGreetingsToMenu(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            GreetingsFragmentDirections.actionGreetingsFragmentToMenuFragment()
        )
    }

    fun fromRegisterToGreetings(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            RegisterFragmentDirections.actionRegisterFragmentToGreetingsFragment()
        )
    }

    fun fromRegisterToAuth(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            RegisterFragmentDirections.actionRegisterFragmentToAuthFragment()
        )
    }

    fun fromAuthToGreetings(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            AuthFragmentDirections.actionAuthFragmentToGreetingsFragment()
        )
    }

    fun fromAuthToRegister(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            AuthFragmentDirections.actionAuthFragmentToRegisterFragment()
        )
    }

    fun fromAuthToMenu(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            AuthFragmentDirections.actionAuthFragmentToMenuFragment()
        )
    }

    fun fromSettingToGreetings(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(
            SettingFragmentDirections.actionSettingFragmentToGreetingsFragment()
        )
    }

}