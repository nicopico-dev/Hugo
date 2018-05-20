package fr.nicopico.hugo.android.services

import fr.nicopico.hugo.BuildConfig
import fr.nicopico.hugo.android.HugoLogger
import fr.nicopico.hugo.android.verbose
import fr.nicopico.hugo.domain.model.Baby
import fr.nicopico.hugo.domain.model.FoodType
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

    override fun remoteId(entry: Baby): String? = entry.key
    override fun convert(remoteId: String, entry: Baby): Map<String, Any?> = BabySerializer.serialize(entry)
    override fun convert(data: Map<String, Any?>): Baby = BabySerializer.deserialize(data)
}

private object BabySerializer : HugoLogger {

    private const val KEY_SCHEMA = "schema"
    private const val KEY_KEY = "key"
    private const val KEY_NAME = "name"
    private const val KEY_DISABLED_FOOD_TYPES = "disabledFoodTypes"

    private const val LATEST_SCHEMA = 2

    fun serialize(baby: Baby): Map<String, *> {
        verbose { "Serializing $baby to Firebase (schema $LATEST_SCHEMA)" }

        return baby.run {
            mapOf(
                    KEY_SCHEMA to LATEST_SCHEMA,
                    KEY_KEY to baby.key,
                    KEY_NAME to baby.name,
                    KEY_DISABLED_FOOD_TYPES to baby.disabledFoodTypes.map {
                        FoodTypes.getCode(it)
                    }
            )
        }
    }

    fun deserialize(data: Map<String, Any?>): Baby {
        val schema = data[KEY_SCHEMA].fsLong()
        verbose { "De-serializing from Firebase $data (schema $schema)" }

        val babyKey = data[KEY_KEY].fsString()
        val babyName = data[KEY_NAME].fsString()
        val disabledFoods: Set<FoodType> = when(schema) {
            1L -> emptySet()
            2L -> data[KEY_DISABLED_FOOD_TYPES].fsList()
                    .map { FoodTypes.getType(it.fsString()) }
                    .toSet()
            else -> throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
        }
        return Baby(babyKey, babyName, disabledFoods)
    }
}