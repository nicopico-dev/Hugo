package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.error
import fr.nicopico.hugo.utils.verbose
import fr.nicopico.hugo.utils.warn

abstract class FirebaseFetcherService<T> : FetcherService<T>, HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }
    protected abstract val collectionPath: String

    private var registration: ListenerRegistration? = null

    override fun fetch(fetcher: Fetcher<T>) {
        if (registration != null) {
            warn(m = "Fetching already set, use stopFetching() first to change the fetcher")
            return
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

    override fun stopFetching() {
        registration?.remove()
    }

    override fun addEntry(entry: T) {
        val remoteId = remoteId(entry)
        db.collection(collectionPath)
                .run {
                    if (remoteId == null) {
                        document()
                    } else {
                        document(remoteId)
                    }
                }
                .set(convert(entry))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry added: $entry" }
                    else error(task.exception) { "Unable to add entry $entry" }
                }
    }

    override fun updateEntry(entry: T) {
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

    override fun removeEntry(entry: T) {
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