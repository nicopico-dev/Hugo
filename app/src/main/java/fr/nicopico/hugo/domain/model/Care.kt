package fr.nicopico.hugo.domain.model

sealed class Care(val type: CareType)
sealed class HealthCare : Care(CareType.HEALTH_HYGIENE)
sealed class ChangeCare : Care(CareType.CHANGE)
sealed class FoodCare : Care(CareType.FOOD)

object UmbilicalCord : HealthCare() {
    override fun toString(): String {
        return "UmbilicalCord"
    }
}
object Face : HealthCare() {
    override fun toString(): String {
        return "Face"
    }
}
object Bath : HealthCare() {
    override fun toString(): String {
        return "Bath"
    }
}
object Vitamins : HealthCare() {
    override fun toString(): String {
        return "Vitamins"
    }
}

object Pee : ChangeCare() {
    override fun toString(): String {
        return "Pee"
    }
}
object Poo : ChangeCare() {
    override fun toString(): String {
        return "Poo"
    }
}

data class BreastFeeding(
        /** duration in minutes */
        val leftDuration: Int?,
        /** duration in minutes */
        val rightDuration: Int?
) : FoodCare()

data class BreastExtraction(
        val volume: Int,
        val breasts: Set<Breast>
) : FoodCare()

sealed class BottleFeeding(open val content: String) : FoodCare() {
    /** Volume in milliliters */
    abstract val volume: Int

    companion object {
        const val CONTENT_MATERNAL_MILK = "maternal"
        const val CONTENT_ARTIFICIAL_MILK = "artificial"

        fun create(content: String, volume: Int): BottleFeeding = when(content) {
            CONTENT_MATERNAL_MILK -> Maternal(volume)
            CONTENT_ARTIFICIAL_MILK -> Artificial(volume)
            else -> Other(content, volume)
        }
    }

    data class Maternal(override val volume: Int) : BottleFeeding(CONTENT_MATERNAL_MILK)
    data class Artificial(override val volume: Int) : BottleFeeding(CONTENT_ARTIFICIAL_MILK)
    data class Other(override val content: String, override val volume: Int) : BottleFeeding(content)
}

data class Diversification(
        /** quantity in grams */
        val quantity: Int,
        val aliment: String
) : FoodCare()

enum class Breast { LEFT, RIGHT }

enum class CareType {
    CHANGE,
    FOOD,
    HEALTH_HYGIENE
}