package fr.nicopico.hugo.android.services

import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.verbose
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.FoodTypes
import fr.nicopico.hugo.domain.model.User
import fr.nicopico.hugo.domain.services.BabyService
import kotlin.properties.Delegates

class FirebaseBabyService : FirebaseFetcherService<Baby>(), BabyService, HugoLogger {

    override var user: User? by Delegates.observable(null) { _, _: User?, newValue: User? ->
        ready = newValue != null
    }

    override val collectionPath
        get() = "${BuildConfig.FIRESTORE_ROOT}/${user!!.uid}/babies"

    override fun remoteId(entry: Baby): String?
            = entry.key
    override fun convert(remoteId: String, entry: Baby): Map<String, Any?>
            = BabySerializer.serialize(entry)
    override fun convert(data: Map<String, Any?>): Baby
            = BabySerializer.deserialize(data)
}

private object BabySerializer : HugoLogger {

    private const val KEY_SCHEMA = "schema"
    private const val KEY_KEY = "key"
    private const val KEY_NAME = "name"
    private const val KEY_DISABLED_FOOD_TYPES = "disabledFoodTypes"

    fun serialize(baby: Baby, schema: Int = 1): Map<String, *> {
        verbose { "Serializing $baby to Firebase (schema $schema)" }

        if (schema == 1) {
            return baby.run {
                mapOf(
                        KEY_SCHEMA to schema,
                        KEY_KEY to baby.key,
                        KEY_NAME to baby.name,
                        KEY_DISABLED_FOOD_TYPES to baby.disabledFoodTypes.map {
                            FoodTypes.getCode(it)
                        }
                )
            }
        }
        throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
    }

    fun deserialize(data: Map<String, Any?>): Baby {
        val schema = data[KEY_SCHEMA] as Long
        verbose { "De-serializing from Firebase $data (schema $schema)" }

        if (schema == 1L) {
            return Baby(
                    data[KEY_KEY] as String,
                    data[KEY_NAME] as String,
                    (data[KEY_DISABLED_FOOD_TYPES] as List<*>).map {
                        FoodTypes.getType(it as String)
                    }.toSet()
            )
        }
        throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
    }
}