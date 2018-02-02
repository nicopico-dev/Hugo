package fr.nicopico.hugo.service

import fr.nicopico.hugo.model.*
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.verbose
import java.util.*

object EntrySerializer : HugoLogger {

    private const val KEY_SCHEMA = "schema"
    private const val KEY_REMOTE_ID = "remoteId"
    private const val KEY_TYPE = "type"
    private const val KEY_TIME = "time"
    private const val KEY_NOTES = "notes"
    private const val KEY_CARES = "cares"

    private const val CARE_UMBILICAL_CORD = "UmbilicalCord"
    private const val CARE_FACE = "Face"
    private const val CARE_BATH = "Bath"
    private const val CARE_VITAMINS = "Vitamins"
    private const val CARE_PEE = "Pee"
    private const val CARE_POO = "Poo"

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

    fun deserialize(remoteId: String?, data: Map<String, Any?>): Timeline.Entry {
        val schema = data[KEY_SCHEMA] as Long
        verbose { "De-serializing from Firebase $data (schema $schema)" }

        if (schema == 1L) {
            return Timeline.Entry(
                    remoteId = remoteId,
                    type = CareType.valueOf(data[KEY_TYPE] as String),
                    time = data[KEY_TIME] as Date,
                    cares = (data[KEY_CARES] as List<*>).map {
                        when (it) {
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