package fr.nicopico.hugo.domain.model

sealed class Care(val type: CareType)

object UmbilicalCord : Care(CareType.HEALTH_HYGIENE)
object Face : Care(CareType.HEALTH_HYGIENE)
object Bath : Care(CareType.HEALTH_HYGIENE)
object Vitamins : Care(CareType.HEALTH_HYGIENE)

object Pee : Care(CareType.CHANGE)
object Poo : Care(CareType.CHANGE)

data class BreastFeeding(
        val breast: Breast,
        /** duration in minutes */
        val duration: Int
) : Care(CareType.FOOD)

data class BreastExtraction(
        val volume: Int
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