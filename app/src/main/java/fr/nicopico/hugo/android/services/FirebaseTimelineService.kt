package fr.nicopico.hugo.android.services

import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.verbose
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.Bath
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.Breast
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.Care
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.Face
import fr.nicopico.hugo.domain.model.Pee
import fr.nicopico.hugo.domain.model.Poo
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.model.UmbilicalCord
import fr.nicopico.hugo.domain.model.User
import fr.nicopico.hugo.domain.model.Vitamins
import fr.nicopico.hugo.domain.services.TimelineService
import kotlin.properties.Delegates


class FirebaseTimelineService : FirebaseFetcherService<Timeline.Entry>(), TimelineService {

    override var user: User? by Delegates.observable(null) { _, _: User?, newValue: User? ->
        ready = newValue != null && baby != null
    }
    override var baby: Baby? by Delegates.observable(null) { _, _: Baby?, newValue: Baby? ->
        ready = newValue != null && user != null
    }

    override val collectionPath
        get() = "${BuildConfig.FIRESTORE_ROOT}/${user!!.uid}/babies/${baby!!.key}/timeline"

    override fun remoteId(entry: Timeline.Entry): String? = entry.remoteId
    override fun convert(remoteId: String, entry: Timeline.Entry): Map<String, Any?> = TimelineEntrySerializer.serialize(remoteId, entry)
    override fun convert(data: Map<String, Any?>): Timeline.Entry = TimelineEntrySerializer.deserialize(data)
}

private object TimelineEntrySerializer : HugoLogger {

    private const val LATEST_SCHEMA = 2

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
    private const val FOOD_TYPE_DIVERSIFICATION = "Diversification"

    private const val KEY_BREASTS = "breasts"
    private const val KEY_VOLUME = "volume"
    private const val KEY_LEFT_DURATION = "leftDuration"
    private const val KEY_RIGHT_DURATION = "rightDuration"
    private const val KEY_BOTTLE_CONTENT = "content"
    private const val KEY_ALIMENT = "aliment"
    private const val KEY_QUANTITY = "quantity"

    private const val BOTTLE_CONTENT_MATERNAL_MILK = "MATERNAL_MILK"
    private const val BOTTLE_CONTENT_ARTIFICIAL_MILK = "ARTIFICIAL_MILK"

    private const val CARE_UMBILICAL_CORD = "UmbilicalCord"
    private const val CARE_FACE = "Face"
    private const val CARE_BATH = "Bath"
    private const val CARE_VITAMINS = "Vitamins"
    private const val CARE_PEE = "Pee"
    private const val CARE_POO = "Poo"

    @Suppress("IMPLICIT_CAST_TO_ANY")
    fun serialize(remoteId: String, entry: Timeline.Entry): Map<String, *> {
        verbose { "Serializing $entry to Firebase (schema $LATEST_SCHEMA)" }

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
                                    KEY_LEFT_DURATION to care.leftDuration,
                                    KEY_RIGHT_DURATION to care.rightDuration
                            )
                            is BreastExtraction -> mapOf(
                                    KEY_FOOD_TYPE to FOOD_TYPE_BREAST_EXTRACTION,
                                    KEY_BREASTS to care.breasts.map { it.name },
                                    KEY_VOLUME to care.volume
                            )
                            is BottleFeeding -> mapOf(
                                    KEY_FOOD_TYPE to FOOD_TYPE_BOTTLE_FEEDING,
                                    KEY_VOLUME to care.volume,
                                    KEY_BOTTLE_CONTENT to when (care) {
                                        is BottleFeeding.Maternal -> BOTTLE_CONTENT_MATERNAL_MILK
                                        is BottleFeeding.Artificial -> BOTTLE_CONTENT_ARTIFICIAL_MILK
                                        is BottleFeeding.Other -> care.content
                                    }
                            )
                            is Diversification -> mapOf(
                                    KEY_FOOD_TYPE to FOOD_TYPE_DIVERSIFICATION,
                                    KEY_ALIMENT to care.aliment,
                                    KEY_QUANTITY to care.quantity
                            )
                        }
                    }
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun deserialize(data: Map<String, Any?>): Timeline.Entry {
        val schema = data[KEY_SCHEMA].fsLong()
        verbose { "De-serializing from Firebase $data (schema $schema)" }

        if (schema == 1L) {
            return Timeline.Entry(
                    remoteId = data[KEY_REMOTE_ID].fsStringOrNull(),
                    type = data[KEY_TYPE].fsEnum(),
                    time = data[KEY_TIME].fsDate(),
                    cares = data[KEY_CARES].fsList().map {
                        when (it) {
                            CARE_UMBILICAL_CORD -> UmbilicalCord
                            CARE_FACE -> Face
                            CARE_BATH -> Bath
                            CARE_VITAMINS -> Vitamins
                            CARE_PEE -> Pee
                            CARE_POO -> Poo
                            is FsMap -> when (it[KEY_FOOD_TYPE]) {
                                FOOD_TYPE_BREAST_FEEDING -> BreastFeeding(
                                        leftDuration = it[KEY_LEFT_DURATION].fsIntOrNull(),
                                        rightDuration = it[KEY_RIGHT_DURATION].fsIntOrNull()
                                )
                                FOOD_TYPE_BREAST_EXTRACTION -> BreastExtraction(
                                        volume = it[KEY_VOLUME].fsInt(),
                                        breasts = it[KEY_BREASTS].fsBreastSet()
                                )
                                FOOD_TYPE_BOTTLE_FEEDING -> {
                                    val bottleContent = it[KEY_BOTTLE_CONTENT].fsString()
                                    val volume = it[KEY_VOLUME].fsInt()
                                    when (bottleContent) {
                                        BOTTLE_CONTENT_MATERNAL_MILK -> BottleFeeding.Maternal(volume)
                                        BOTTLE_CONTENT_ARTIFICIAL_MILK -> BottleFeeding.Artificial(volume)
                                        else -> BottleFeeding.Other(bottleContent, volume)
                                    }
                                }
                                FOOD_TYPE_DIVERSIFICATION -> Diversification(
                                        aliment = it[KEY_ALIMENT].fsString(),
                                        quantity = it[KEY_QUANTITY].fsInt()
                                )
                                else -> throw UnsupportedOperationException("Unable to deserialize care data $it " +
                                        "(food type)")
                            }
                            else -> throw UnsupportedOperationException("Unable to deserialize care data $it")
                        }
                    },
                    notes = data[KEY_NOTES].fsStringOrNull()
            )
        }
        throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
    }

    private fun Any?.fsBreastSet() = this.fsList()
            .map { it.fsEnum<Breast>() }
            .toSet()
}