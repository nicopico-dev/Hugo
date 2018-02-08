package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.verbose
import java.util.*
import kotlin.properties.Delegates

interface TimelineService : FetcherService<Timeline.Entry> {
    companion object {
        fun create(): TimelineService = FirebaseTimelineService()
    }

    var user: User?
    var baby: Baby?
}

private class FirebaseTimelineService : FirebaseFetcherService<Timeline.Entry>(), TimelineService {

    override var user: User? by Delegates.observable(null) { _, _: User?, newValue: User? ->
        ready = newValue != null && baby != null
    }
    override var baby: Baby? by Delegates.observable(null) { _, _: Baby?, newValue: Baby? ->
        ready = newValue != null && user != null
    }

    override val collectionPath
        get() = "/users/${user!!.uid}/babies/${baby!!.key}/timeline"

    override fun remoteId(entry: Timeline.Entry): String?
            = entry.remoteId
    override fun convert(entry: Timeline.Entry): Map<String, Any?>
            = TimelineEntrySerializer.serialize(entry)
    override fun convert(change: DocumentChange): Timeline.Entry
            = TimelineEntrySerializer.deserialize(change.document.id, change.document.data)
}

private object TimelineEntrySerializer : HugoLogger {

    private const val KEY_SCHEMA = "schema"
    private const val KEY_REMOTE_ID = "remoteId"
    private const val KEY_TYPE = "type"
    private const val KEY_TIME = "time"
    private const val KEY_NOTES = "notes"
    private const val KEY_CARES = "cares"

    private const val KEY_FOOD_TYPE = "foodType"
    private const val FOOD_TYPE_BREAST_FEEDING = "BreastFeeding"
    private const val FOOD_TYPE_BREAST_EXTRACTION = "BreastExtraction"
    private const val FOOD_TYPE_BOTTLE_FEEDING = "BottleFeeding"

    private const val KEY_BREAST = "breast"
    private const val KEY_BREASTS = "breasts"
    private const val KEY_VOLUME = "volume"
    private const val KEY_DURATION = "duration"
    private const val KEY_MATERNAL_MILK = "maternalMilk"


    private const val CARE_UMBILICAL_CORD = "UmbilicalCord"
    private const val CARE_FACE = "Face"
    private const val CARE_BATH = "Bath"
    private const val CARE_VITAMINS = "Vitamins"
    private const val CARE_PEE = "Pee"
    private const val CARE_POO = "Poo"

    @Suppress("IMPLICIT_CAST_TO_ANY")
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
                            when (care) {
                                UmbilicalCord -> CARE_UMBILICAL_CORD
                                Face -> CARE_FACE
                                Bath -> CARE_BATH
                                Vitamins -> CARE_VITAMINS
                                Pee -> CARE_PEE
                                Poo -> CARE_POO
                                is BreastFeeding -> mapOf(
                                        KEY_FOOD_TYPE to FOOD_TYPE_BREAST_FEEDING,
                                        KEY_BREAST to care.breast.name,
                                        KEY_DURATION to care.duration
                                )
                                is BreastExtraction -> mapOf(
                                        KEY_FOOD_TYPE to FOOD_TYPE_BREAST_EXTRACTION,
                                        KEY_BREASTS to care.breasts.map { it.name },
                                        KEY_VOLUME to care.volume
                                )
                                is BottleFeeding -> mapOf(
                                        KEY_FOOD_TYPE to FOOD_TYPE_BOTTLE_FEEDING,
                                        KEY_VOLUME to care.volume,
                                        KEY_MATERNAL_MILK to care.maternalMilk
                                )
                            }
                        }
                )
            }
        } else {
            throw UnsupportedOperationException("Serialization of schema $schema is not supported")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deserialize(remoteId: String?, data: Map<String, Any?>): Timeline.Entry {
        val schema = data[KEY_SCHEMA] as Long
        verbose { "De-serializing from Firebase $data (schema $schema)" }

        if (schema == 1L) {
            return Timeline.Entry(
                    remoteId = remoteId,
                    type = data[KEY_TYPE].asEnum(),
                    time = data[KEY_TIME] as Date,
                    cares = (data[KEY_CARES] as List<*>).map {
                        when (it) {
                            CARE_UMBILICAL_CORD -> UmbilicalCord
                            CARE_FACE -> Face
                            CARE_BATH -> Bath
                            CARE_VITAMINS -> Vitamins
                            CARE_PEE -> Pee
                            CARE_POO -> Poo
                            is Map<*, *> -> when (it[KEY_FOOD_TYPE]) {
                                FOOD_TYPE_BREAST_FEEDING -> BreastFeeding(
                                        breast = it[KEY_BREAST].asEnum(),
                                        duration = it[KEY_DURATION].asInt()
                                )
                                FOOD_TYPE_BREAST_EXTRACTION -> BreastExtraction(
                                        volume = it[KEY_VOLUME].asInt(),
                                        breasts = it[KEY_BREASTS].asBreastSet()
                                )
                                FOOD_TYPE_BOTTLE_FEEDING -> BottleFeeding(
                                        volume = it[KEY_VOLUME].asInt(),
                                        maternalMilk = it[KEY_MATERNAL_MILK] as Boolean
                                )
                                else -> throw UnsupportedOperationException("Unable to deserialize care data $it (food type)")
                            }
                            else -> throw UnsupportedOperationException("Unable to deserialize care data $it")
                        }
                    },
                    notes = data[KEY_NOTES] as String?
            )
        }
        throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
    }

    private fun Any?.asBreastSet() = (this as Iterable<*>)
            .map { it.asEnum<Breast>() }
            .toSet()
}