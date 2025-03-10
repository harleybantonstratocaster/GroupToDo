package com.jim.grouptodo


data class SendMessageDto(
    val notification: NotificationBody
)

data class NotificationBody(
    val title: String,
    val body: String
)
