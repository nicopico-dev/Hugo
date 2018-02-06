package fr.nicopico.hugo.service

import com.google.firebase.firestore.DocumentChange
import fr.nicopico.hugo.model.Baby
import fr.nicopico.hugo.model.User
import fr.nicopico.hugo.utils.HugoLogger
import fr.nicopico.hugo.utils.verbose

interface BabyService : FetcherService<Baby> {
    companion object {
        fun create(): BabyService = FirebaseBabyService()
    }

    var user: User?
}

private class FirebaseBabyService : FirebaseFetcherService<Baby>(), BabyService, HugoLogger {

    override var user: User? = null

    override val collectionPath
        get() = "/users/${user!!.uid}/babies"

    override fun remoteId(entry: Baby): String?
            = entry.key
    override fun convert(entry: Baby): Map<String, Any?>
            = BabySerializer.serialize(entry)
    override fun convert(change: DocumentChange): Baby
            = BabySerializer.deserialize(change.document.data)
}

private object BabySerializer : HugoLogger {

    private const val KEY_SCHEMA = "schema"
    private const val KEY_KEY = "key"
    private const val KEY_NAME = "name"

    fun serialize(baby: Baby, schema: Int = 1): Map<String, *> {
        verbose { "Serializing $baby to Firebase (schema $schema)" }

        if (schema == 1) {
            return baby.run {
                mapOf(
                        KEY_SCHEMA to schema,
                        KEY_KEY to baby.key,
                        KEY_NAME to baby.name
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
                    data[KEY_NAME] as String
            )
        }
        throw UnsupportedOperationException("De-serialization of schema $schema is not supported")
    }
}