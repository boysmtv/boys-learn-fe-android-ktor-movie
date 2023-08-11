package com.kotlin.learn.core.network.source

import com.kotlin.learn.core.common.util.network.ResultSpring
import com.kotlin.learn.core.model.BaseResponse
import com.kotlin.learn.core.model.RegisterReqModel
import com.kotlin.learn.core.model.RegisterRespModel
import kotlinx.coroutines.flow.Flow

interface RegisterDataSource {

    suspend fun postRegister(model: RegisterReqModel): BaseResponse<RegisterRespModel>

}