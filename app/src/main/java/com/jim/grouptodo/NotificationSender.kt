package com.jim.grouptodo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object NotificationSender {
    private val api: FcmApi = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FcmApi::class.java)

    // Using a custom CoroutineScope; be sure to cancel the job when no longer needed
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    fun sendMessage(title: String, text: String) {
        scope.launch {
            val messageDto = SendMessageDto(NotificationBody(title, text))
            api.broadcast(messageDto)
        }
    }

    // Optionally cancel ongoing jobs
    fun cancel() {
        job.cancel()
    }
}
