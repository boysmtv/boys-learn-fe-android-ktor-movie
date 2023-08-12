package com.kotlin.learn.feature.auth.presentation.ui

import androidx.fragment.app.viewModels
import com.kotlin.learn.core.common.util.network.Result
import com.kotlin.learn.core.common.util.network.SpringParser
import com.kotlin.learn.core.common.base.BaseFragment
import com.kotlin.learn.core.common.util.JsonUtil
import com.kotlin.learn.core.common.util.invokeDataStoreEvent
import com.kotlin.learn.core.common.util.network.invokeSpringParser
import com.kotlin.learn.core.common.util.network.parseResultError
import com.kotlin.learn.core.model.BaseResponse
import com.kotlin.learn.core.model.LoginReqModel
import com.kotlin.learn.core.model.LoginRespModel
import com.kotlin.learn.core.model.UserModel
import com.kotlin.learn.core.nav.navigator.AuthNavigator
import com.kotlin.learn.core.utilities.Constant
import com.kotlin.learn.core.utilities.extension.launch
import com.kotlin.learn.feature.auth.databinding.FragmentAuthBinding
import com.kotlin.learn.feature.auth.presentation.viewmodel.AuthViewModel
import com.kotlin.learn.feature.auth.presentation.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    private val viewModelRegister: RegisterViewModel by viewModels()

    @Inject
    lateinit var authNavigator: AuthNavigator

    @Inject
    lateinit var jsonUtil: JsonUtil

    private var userModel: UserModel = UserModel()


    override fun setupView() {
        subscribeLogin()
        setupListener()
    }

    private fun subscribeLogin() = with(viewModel) {
        postAuthorization.launch(this@AuthFragment) { result ->
            when (result) {
                Result.Waiting -> {}

                is Result.Loading -> showHideProgress(isLoading = true)

                is Result.Success -> authorizationSuccessHandler(result)

                is Result.Error -> authorizationErrorHandler(result.throwable)
            }
        }

        storeFirestore.launch(this@AuthFragment) {

        }
    }

    private fun setupListener() = with(binding) {
        btnLogin.setOnClickListener {
            viewModel.postAuthorization(
                LoginReqModel(
                    email = etEmail.text.toString(),
                    password = etPassword.text.toString()
                )
            )
        }

        tvRegister.setOnClickListener {
            authNavigator.fromAuthToRegister(this@AuthFragment)
        }

        ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        etEmail.setText("Boys.mtv@gmail.com")
        etPassword.setText("123456789")
    }

    private fun authorizationSuccessHandler(result: Result.Success<BaseResponse<LoginRespModel>>) {
        showHideProgress(isLoading = false)
        invokeSpringParser(result.data).launch(lifecycleOwner = this@AuthFragment) {
            when (it) {
                is SpringParser.Success -> {
                    if (it.data != null) {
                        viewModelRegister.storeUserToDatastore(
                            jsonUtil.toJson(
                                userModel.apply {
                                    id = it.data!!.id ?: Constant.EMPTY_STRING
                                    displayName = it.data!!.fullName ?: Constant.EMPTY_STRING
                                }
                            )
                        ).launch(this@AuthFragment) { event ->
                            invokeDataStoreEvent(event,
                                isFetched = {},
                                isStored = {
                                    authNavigator.fromAuthToHome(fragment = this@AuthFragment)
                                }
                            )
                        }
                    } else showDialogGeneralError("Register Error", "Error store data google register")
                }

                is SpringParser.Error -> {
                    showDialogGeneralError(
                        title = "Login Error",
                        message = "Check your connection"
                    )
                }
            }
        }
    }

    private fun authorizationErrorHandler(throwable: Throwable) {
        parseResultError(throwable).launch(this@AuthFragment) { parser ->
            when (parser) {
                is SpringParser.Success -> {
                    showDialogGeneralError("Login failed", parser.data.toString())
                }

                is SpringParser.Error -> {
                    showDialogGeneralError("Login error", throwable.message.toString())
                }
            }
        }
    }
}