package fr.nicopico.hugo.model

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import org.junit.Test

class BabyTest {
    @Test
    fun keyForSimpleName() {
        val baby = Baby("Hugo")
        assertThat(baby, has(Baby::key, equalTo("HUGO")))
    }

    @Test
    fun keyForComposedName1() {
        val baby = Baby("Anne-Cécile")
        assertThat(baby, has(Baby::key, equalTo("ANNE_CECILE")))
    }

    @Test
    fun keyForComposedName2() {
        val baby = Baby("Jean François")
        assertThat(baby, has(Baby::key, equalTo("JEAN_FRANCOIS")))
    }
}