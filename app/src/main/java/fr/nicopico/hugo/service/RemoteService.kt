package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.error
import fr.nicopico.hugo.utils.verbose
import fr.nicopico.hugo.utils.warn
import java.util.*

interface RemoteService {
    companion object {
        val INSTANCE: RemoteService by lazy { FirebaseRemoteService }
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

private object FirebaseRemoteService : RemoteService, HugoLogger {

    private val db by lazy { FirebaseFirestore.getInstance() }
    private const val timelinePath = "/users/sgIdPDnelqvAoH4JbFiL/babies/hugo/timeline"

    override fun fetchTimeline(fetcher: TimelineFetcher) {
        db.collection(timelinePath)
                .addSnapshotListener { querySnapshot, exception ->
                    if (exception != null) {
                        fetcher.onError(exception)
                        return@addSnapshotListener
                    }

                    @Suppress("IMPLICIT_CAST_TO_ANY")
                    for (change in querySnapshot.documentChanges) {
                        val entry = Serializer.deserialize(change.document)
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
                .add(Serializer.serialize(entry))
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
                .set(Serializer.serialize(entry))
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

    private object Serializer {

        const val KEY_SCHEMA = "schema"
        const val KEY_REMOTE_ID = "remoteId"
        const val KEY_TYPE = "type"
        const val KEY_TIME = "time"
        const val KEY_NOTES = "notes"
        const val KEY_CARES = "cares"

        const val CARE_UMBILICAL_CORD = "UmbilicalCord"
        const val CARE_FACE = "Face"
        const val CARE_BATH = "Bath"
        const val CARE_VITAMINS = "Vitamins"
        const val CARE_PEE = "Pee"
        const val CARE_POO = "Poo"

        fun serialize(entry: Timeline.Entry, schema: Int = 1): Map<String, *> {
            verbose { "Serializing $entry to Firebase (schema $schema)" }

            if (schema == 1) {
                return entry.run {
                    mapOf(
                            KEY_SCHEMA to 1,
                            KEY_REMOTE_ID to remoteId,
                            KEY_TYPE to type.toString(),
                            KEY_TIME to time,
                            KEY_NOTES to notes,
                            KEY_CARES to cares.map { care: Care ->
                                when(care) {
                                    UmbilicalCord -> CARE_UMBILICAL_CORD
                                    Face -> CARE_FACE
                                    Bath -> CARE_BATH
                                    Vitamins -> CARE_VITAMINS
                                    Pee -> CARE_PEE
                                    Poo -> CARE_POO
                                    is BreastFeeding -> TODO()
                                    is BreastExtraction -> TODO()
                                    is BottleFeeding -> TODO()
                                }
                            }
                    )
                }
            } else {
                throw UnsupportedOperationException("Serialization of schema $schema is not supported")
            }
        }

        fun deserialize(document: DocumentSnapshot): Timeline.Entry {
            val id = document.id
            val data = document.data
            val schema = data[KEY_SCHEMA] as Long
            verbose { "De-serializing from Firebase $data (schema $schema)" }

            if (schema == 1L) {
                return Timeline.Entry(
                        remoteId = id,
                        type = CareType.valueOf(data[KEY_TYPE] as String),
                        time = data[KEY_TIME] as Date,
                        cares = (data[KEY_CARES] as List<*>).map {
                            when(it) {
                                CARE_UMBILICAL_CORD -> UmbilicalCord
                                CARE_FACE -> Face
                                CARE_BATH -> Bath
                                CARE_VITAMINS -> Vitamins
                                CARE_PEE -> Pee
                                CARE_POO -> Poo
                                else -> throw UnsupportedOperationException("Unable to deserialize care data $it")
                            }
                        },
                        notes = data[KEY_NOTES] as String?
                )
            }
            throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
        }
    }
}