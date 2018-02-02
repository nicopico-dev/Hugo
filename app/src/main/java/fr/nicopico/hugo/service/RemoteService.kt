package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import fr.nicopico.hugo.model.Timeline
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.error
import fr.nicopico.hugo.utils.verbose
import fr.nicopico.hugo.utils.warn

interface RemoteService {
    companion object {
        fun create(): RemoteService = FirebaseRemoteService()
    }

    fun fetchTimeline(fetcher: TimelineFetcher)
    fun addEntry(entry: Timeline.Entry)
    fun updateEntry(entry: Timeline.Entry)
    fun removeEntry(entry: Timeline.Entry)
}

interface TimelineFetcher {
    fun onEntryAdded(entry: Timeline.Entry)
    fun onEntryModified(entry: Timeline.Entry)
    fun onEntryRemoved(entry: Timeline.Entry)
    fun onError(exception: Exception)
}

private class FirebaseRemoteService : RemoteService, HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val timelinePath = "/users/sgIdPDnelqvAoH4JbFiL/babies/hugo/timeline"

    override fun fetchTimeline(fetcher: TimelineFetcher) {
        db.collection(timelinePath)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        fetcher.onError(exception)
                        return@addSnapshotListener
                    }

                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    for (change in querySnapshot.documentChanges) {
                        val entry = change.toEntry()
                        when (change.type) {
                            DocumentChange.Type.ADDED -> fetcher.onEntryAdded(entry)
                            DocumentChange.Type.MODIFIED -> fetcher.onEntryModified(entry)
                            DocumentChange.Type.REMOVED -> fetcher.onEntryRemoved(entry)
                        }
                    }
                }
    }

    override fun addEntry(entry: Timeline.Entry) {
        db.collection(timelinePath)
                .add(entry.toDocument())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry added: $entry" }
                    else error(task.exception) { "Unable to add entry $entry" }
                }
    }

    override fun updateEntry(entry: Timeline.Entry) {
        if (entry.remoteId == null) {
            warn { "Entry $entry cannot be updated (no remoteId)" }
            return
        }

        db.collection(timelinePath)
                .document(entry.remoteId)
                .set(entry.toDocument())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry updated: $entry" }
                    else error(task.exception) { "Unable to update entry $entry" }
                }
    }

    override fun removeEntry(entry: Timeline.Entry) {
        if (entry.remoteId == null) {
            warn { "Entry $entry cannot be removed (no remoteId)" }
            return
        }

        db.collection(timelinePath)
                .document(entry.remoteId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry removed: $entry" }
                    else error(task.exception) { "Unable to remove entry $entry" }
                }
    }

    private fun DocumentChange.toEntry(): Timeline.Entry
            = EntrySerializer.deserialize(this.document.id, this.document.data)

    private fun Timeline.Entry.toDocument(): Map<String, Any?>
            = EntrySerializer.serialize(this)
}