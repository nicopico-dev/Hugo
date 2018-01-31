package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.redux.ENTRY_ADDED
import fr.nicopico.hugo.redux.ENTRY_MODIFIED
import fr.nicopico.hugo.redux.ENTRY_REMOVED
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.Result
import fr.nicopico.hugo.utils.warn
import redux.api.Dispatcher
import kotlin.coroutines.experimental.suspendCoroutine

interface RemoteService {
    companion object {
        val INSTANCE: RemoteService by lazy { FirebaseRemoteService }
    }

    suspend fun fetchTimeline(dispatcher: Dispatcher): Result<List<Timeline.Entry>>
    suspend fun saveEntry(entry: Timeline.Entry)
    suspend fun removeEntry(entry: Timeline.Entry)
}

private object FirebaseRemoteService : RemoteService, HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }

    override suspend fun fetchTimeline(dispatcher: Dispatcher): Result<List<Timeline.Entry>> =
            suspendCoroutine {
                // TODO Do not hardcode path
                db.collection("/users/sgIdPDnelqvAoH4JbFiL/babies/hugo/timeline")
                        .addSnapshotListener { querySnapshot, exception ->
                            if (exception != null) {
                                warn(exception) { "Firebase error" }
                                return@addSnapshotListener
                            }

                            @Suppress("IMPLICIT_CAST_TO_ANY")
                            val action = querySnapshot.documentChanges.map {
                                val entry = it.document.data.toTimelineEntry()
                                when (it.type) {
                                    DocumentChange.Type.ADDED -> ENTRY_ADDED(entry)
                                    DocumentChange.Type.MODIFIED -> ENTRY_MODIFIED(entry, entry)
                                    DocumentChange.Type.REMOVED -> ENTRY_REMOVED(entry)
                                }
                            }
                            dispatcher.dispatch(action)
                        }
            }

    override suspend fun saveEntry(entry: Timeline.Entry) {
        TODO("saveEntry is not implemented")
    }

    override suspend fun removeEntry(entry: Timeline.Entry) {
        TODO("removeEntry is not implemented")
    }

}

private fun Map<String, Any?>.toTimelineEntry(): Timeline.Entry {
    TODO()
}