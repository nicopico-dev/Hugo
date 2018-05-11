package fr.nicopico.hugo.domain.model

import kotlin.reflect.KClass

sealed class Care(val type: CareType)

enum class CareType {
    CHANGE,
    FOOD,
    HEALTH_HYGIENE
}

//region Health & Hygiene
sealed class HealthCare : Care(CareType.HEALTH_HYGIENE)

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
//endregion

//region Change
sealed class ChangeCare : Care(CareType.CHANGE)

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
//endregion

//region Food
typealias FoodType = KClass<out FoodCare>

object FoodTypes {

    private const val BREAST_FEEDING = "BREAST_FEEDING"
    private const val BREAST_EXTRACTION = "BREAST_EXTRACTION"
    private const val MATERNAL_MILK = "MATERNAL_MILK"
    private const val ARTIFICIAL_MILK = "ARTIFICIAL_MILK"
    private const val DIVERSIFICATION = "DIVERSIFICATION"

    val allTypes: List<FoodType> = listOf(
            BreastFeeding::class,
            BreastExtraction::class,
            BottleFeeding.Maternal::class,
            BottleFeeding.Artificial::class,
            Diversification::class
    )

    fun getCode(foodType: FoodType): String {
        return when(foodType) {
            BreastFeeding::class -> BREAST_FEEDING
            BreastExtraction::class -> BREAST_EXTRACTION
            BottleFeeding.Maternal::class -> MATERNAL_MILK
            BottleFeeding.Artificial::class -> ARTIFICIAL_MILK
            Diversification::class -> DIVERSIFICATION
            else -> throw UnsupportedOperationException("Unsupported FoodType $foodType")
        }
    }

    fun getType(code: String): FoodType {
        return when(code) {
            BREAST_FEEDING -> BreastFeeding::class
            BREAST_EXTRACTION -> BreastExtraction::class
            MATERNAL_MILK -> BottleFeeding.Maternal::class
            ARTIFICIAL_MILK -> BottleFeeding.Artificial::class
            DIVERSIFICATION -> Diversification::class
            else -> throw UnsupportedOperationException("Unknown FoodType code $code")
        }
    }
}

sealed class FoodCare : Care(CareType.FOOD) {
    val foodType: FoodType
        get() = this::class
}

data class BreastFeeding(
        /** duration in minutes */
        val leftDuration: Int?,
        /** duration in minutes */
        val rightDuration: Int?
) : FoodCare()

enum class Breast { LEFT, RIGHT }

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
//endregion