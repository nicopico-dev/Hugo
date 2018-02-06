package fr.nicopico.hugo.model

import java.text.Normalizer
import java.util.*

data class Baby(
        val key: String,
        val name: String
) {

    companion object {
        private fun createKey(value: String): String {
            return Normalizer.normalize(value, Normalizer.Form.NFKD)
                    .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
                    .toUpperCase(Locale.ROOT)
                    .replace("[^A-Z0-9]".toRegex(), "_")
        }
    }

    constructor(name: String) : this(createKey(name), name)
}