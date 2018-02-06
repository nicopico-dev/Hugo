package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fr.nicopico.hugo.utils.*

abstract class FirebaseFetcherService<T> : HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }
    protected abstract val collectionPath: String

    private var registration: ListenerRegistration? = null

    fun fetch(fetcher: Fetcher<T>) {
        registration?.let {
            info("Remove previous fetch listener")
            it.remove()
        }

        registration = db.collection(collectionPath)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        fetcher.onError(exception)
                        return@addSnapshotListener
                    }

                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    for (change in querySnapshot.documentChanges) {
                        val entry = convert(change)
                        when (change.type) {
                            DocumentChange.Type.ADDED -> fetcher.onEntryAdded(entry)
                            DocumentChange.Type.MODIFIED -> fetcher.onEntryModified(entry)
                            DocumentChange.Type.REMOVED -> fetcher.onEntryRemoved(entry)
                        }
                    }
                }
    }

    fun stopFetching() {
        registration?.remove()
    }

    fun addEntry(entry: T) {
        db.collection(collectionPath)
                .add(convert(entry))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry added: $entry" }
                    else error(task.exception) { "Unable to add entry $entry" }
                }
    }

    fun updateEntry(entry: T) {
        val remoteId = remoteId(entry)
        if (remoteId == null) {
            warn { "Entry $entry cannot be updated (no remoteId)" }
            return
        }

        db.collection(collectionPath)
                .document(remoteId)
                .set(convert(entry))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry updated: $entry" }
                    else error(task.exception) { "Unable to update entry $entry" }
                }
    }

    fun removeEntry(entry: T) {
        val remoteId = remoteId(entry)
        if (remoteId == null) {
            warn { "Entry $entry cannot be removed (no remoteId)" }
            return
        }

        db.collection(collectionPath)
                .document(remoteId)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry removed: $entry" }
                    else error(task.exception) { "Unable to remove entry $entry" }
                }
    }

    protected abstract fun remoteId(entry: T): String?
    protected abstract fun convert(entry: T): Map<String, Any?>
    protected abstract fun convert(change: DocumentChange): T
}