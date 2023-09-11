package com.kotlin.learn.feature.menu.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.learn.core.common.util.JsonUtil
import com.kotlin.learn.core.common.util.event.DataStoreCacheEvent
import com.kotlin.learn.core.common.util.security.DataStorePreferences
import com.kotlin.learn.core.domain.ProfileUseCase
import com.kotlin.learn.core.model.ProfileModel
import com.kotlin.learn.core.model.UserModel
import com.kotlin.learn.core.utilities.PreferenceConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val useCase: ProfileUseCase,
    private val dataStore: DataStorePreferences,
    private val jsonUtil: JsonUtil
) : ViewModel() {

    // TODO : start region to datastore
    // ===============================================================

    fun storeProfileToDatastore(data: String) =
        viewModelScope.launch {
            dataStore.setString(
                PreferenceConstants.Authorization.PREF_PROFILE,
                data
            )
        }

    fun fetchProfileFromDatastore() =
        flow {
            val data = dataStore.getString(PreferenceConstants.Authorization.PREF_PROFILE).getOrNull().orEmpty()
            if (data.isNotEmpty() && data.isNotBlank()) {
                emit(
                    DataStoreCacheEvent.FetchSuccess(
                        jsonUtil.fromJson<ProfileModel>(data)
                    )
                )
            } else {
                emit(DataStoreCacheEvent.FetchError)
            }
        }


    // TODO : start region to firestore
    // ===============================================================

    fun <T : Any> storeProfileToFirestore(
        id: String,
        model: T,
        onLoad: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        useCase.storeProfileToFirestore(
            id = id,
            model = model,
            onLoad = onLoad,
            onSuccess = onSuccess,
            onError = onError
        ).launchIn(viewModelScope)
    }

    fun updateProfileToFirestore(
        id: String,
        model: Map<String, String>,
        onLoad: () -> Unit,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        useCase.updateProfileToFirestore(
            id = id,
            model = model,
            onLoad = onLoad,
            onSuccess = onSuccess,
            onError = onError
        ).launchIn(viewModelScope)
    }

    fun <T : Any> fetchProfileFromFirestore(
        filter: Pair<String, String>,
        resources: T,
        onLoad: () -> Unit,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) = useCase.fetchProfileFromFirestore(
        filter = filter,
        resources = resources,
        onLoad = onLoad,
        onSuccess = onSuccess,
        onError = onError
    ).launchIn(viewModelScope)

}