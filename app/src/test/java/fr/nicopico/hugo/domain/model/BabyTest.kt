package fr.nicopico.hugo.domain.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.startsWith
import org.junit.Test

class BabyTest {
    @Test
    fun keyForSimpleName() {
        val baby = Baby("Hugo")
        assertThat(baby, has(Baby::key, startsWith("HUGO")))
    }

    @Test
    fun keyForComposedName1() {
        val baby = Baby("Anne-Cécile")
        assertThat(baby, has(Baby::key, startsWith("ANNE_CECILE")))
    }

    @Test
    fun keyForComposedName2() {
        val baby = Baby("Jean François")
        assertThat(baby, has(Baby::key, startsWith("JEAN_FRANCOIS")))
    }
}