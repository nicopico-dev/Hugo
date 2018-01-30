package fr.nicopico.hugo.remote

import com.google.firebase.firestore.FirebaseFirestore
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.utils.Result
import kotlin.coroutines.experimental.suspendCoroutine

interface RemoteService {
    companion object {
        val INSTANCE: RemoteService by lazy { FirebaseRemoteService }
    }

    suspend fun fetch(): Result<List<Timeline.Entry>>
    suspend fun addEntry(entry: Timeline.Entry)
    suspend fun replaceEntry(oldEntry: Timeline.Entry, newEntry: Timeline.Entry)
    suspend fun removeEntry(entry: Timeline.Entry)
}

private object FirebaseRemoteService : RemoteService {

    private val db by lazy { FirebaseFirestore.getInstance() }

    override suspend fun fetch(): Result<List<Timeline.Entry>> = suspendCoroutine {
        db.collection("users")
    }

    override suspend fun addEntry(entry: Timeline.Entry) {
        TODO("saveEntry is not implemented")
    }

    override suspend fun replaceEntry(oldEntry: Timeline.Entry, newEntry: Timeline.Entry) {
        TODO("replaceEntry is not implemented")
    }

    override suspend fun removeEntry(entry: Timeline.Entry) {
        TODO("removeEntry is not implemented")
    }

}