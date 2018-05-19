package fr.nicopico.hugo.android.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.info


class MessagingService : FirebaseMessagingService()

class InstanceIdService : FirebaseInstanceIdService(), HugoLogger {
    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        info { "Firebase token: $token" }
    }
}