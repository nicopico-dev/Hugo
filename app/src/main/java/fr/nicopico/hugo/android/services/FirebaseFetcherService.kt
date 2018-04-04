package fr.nicopico.hugo.android.services

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.error
import fr.nicopico.hugo.android.info
import fr.nicopico.hugo.android.verbose
import fr.nicopico.hugo.android.warn
import fr.nicopico.hugo.domain.services.Fetcher
import fr.nicopico.hugo.domain.services.FetcherService
import kotlin.coroutines.experimental.suspendCoroutine
import kotlin.properties.Delegates

abstract class FirebaseFetcherService<T> : FetcherService<T>, HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }
    protected abstract val collectionPath: String
    protected var ready: Boolean by Delegates.observable(false) { _, _, newValue ->
        val fetcher = shouldFetcher
        if (newValue && fetcher != null) {
            this.fetch(fetcher)
        }
    }

    private var registration: ListenerRegistration? = null
    // Fetcher that should be used once the service is ready
    private var shouldFetcher: Fetcher<T>? = null

    override suspend fun get(remoteId: String): T = suspendCoroutine { cont ->
        db.collection(collectionPath)
                .document(remoteId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(convert(task.result.data))
                    } else {
                        cont.resumeWithException(task.exception!!)
                    }
                }
    }

    override fun fetch(fetcher: Fetcher<T>) {
        if (!ready) {
            info("Defer fetching until the service is ready")
            if (shouldFetcher != null && shouldFetcher != fetcher) {
                warn("Previous fetcher $shouldFetcher will be dropped")
            }
            shouldFetcher = fetcher
        }

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
        shouldFetcher = null
        registration?.remove()
        registration = null
    }

    override fun addEntry(entry: T) {
        if (!ready) {
            warn(m = "Service is not ready")
            return
        }

        lateinit var remoteId: String
        db.collection(collectionPath)
                .run {
                    remoteId(entry)
                            .let { currentRemoteId ->
                                when (currentRemoteId) {
                                    null -> document()
                                    else -> document(currentRemoteId)
                                }
                            }
                            .apply {
                                // Retrieve the document id (new or existing)
                                remoteId = id
                            }
                }
                .set(convert(remoteId, entry))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry added: $entry" }
                    else error(task.exception) { "Unable to add entry $entry" }
                }
    }

    override fun updateEntry(entry: T) {
        if (!ready) {
            warn(m = "Service is not ready")
            return
        }

        val remoteId = remoteId(entry)
        if (remoteId == null) {
            warn { "Entry $entry cannot be updated (no remoteId)" }
            return
        }

        db.collection(collectionPath)
                .document(remoteId)
                .set(convert(remoteId, entry))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) verbose { "Entry updated: $entry" }
                    else error(task.exception) { "Unable to update entry $entry" }
                }
    }

    override fun removeEntry(entry: T) {
        if (!ready) {
            warn(m = "Service is not ready")
            return
        }

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
    protected abstract fun convert(remoteId: String, entry: T): Map<String, Any?>
    protected abstract fun convert(data: Map<String, Any?>): T

    private fun convert(change: DocumentChange): T {
        return convert(change.document.data)
    }
}