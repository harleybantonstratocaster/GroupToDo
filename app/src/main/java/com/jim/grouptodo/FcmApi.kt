package com.jim.grouptodo
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {

    @POST("/broadcast")
    suspend fun broadcast(
        @Body body: SendMessageDto
    )

}