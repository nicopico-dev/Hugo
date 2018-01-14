package fr.nicopico.hugo.domain.model

sealed class Care(val type: CareType)

object UmbilicalCord : Care(CareType.HEALTH_HYGIENE) {
    override fun toString(): String {
        return "UmbilicalCord"
    }
}
object Face : Care(CareType.HEALTH_HYGIENE) {
    override fun toString(): String {
        return "Face"
    }
}
object Bath : Care(CareType.HEALTH_HYGIENE) {
    override fun toString(): String {
        return "Bath"
    }
}
object Vitamins : Care(CareType.HEALTH_HYGIENE) {
    override fun toString(): String {
        return "Vitamins"
    }
}

object Pee : Care(CareType.CHANGE) {
    override fun toString(): String {
        return "Pee"
    }
}
object Poo : Care(CareType.CHANGE) {
    override fun toString(): String {
        return "Poo"
    }
}

data class BreastFeeding(
        val breast: Breast,
        /** duration in minutes */
        val duration: Int
) : Care(CareType.FOOD)

data class BreastExtraction(
        val volume: Int,
        val breasts: Set<Breast>
) : Care(CareType.FOOD)

data class BottleFeeding(
        /** volume in milliliters */
        val volume: Int,
        /** `true` if the milk used was maternal milk extracted earlier */
        val maternalMilk: Boolean = false
) : Care(CareType.FOOD)

enum class Breast { LEFT, RIGHT }

enum class CareType {
    CHANGE,
    FOOD,
    HEALTH_HYGIENE
}