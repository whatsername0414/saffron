package com.saffron.cook.core.data.network

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MealMapperTest {
    @Test
    fun `header-only step lines are dropped`() {
        val instructions =
            """
            STEP 1
            Preheat the oven to 200C.

            STEP 2
            Bake for twenty-five minutes.
            """.trimIndent()

        val steps = instructions.parseSteps()

        assertThat(steps.map { it.instruction })
            .containsExactly(
                "Preheat the oven to 200C.",
                "Bake for twenty-five minutes.",
            ).inOrder()
    }

    @Test
    fun `inline step prefix is stripped`() {
        val instructions =
            """
            Step 1: Chop the onions finely.

            Step 2 - Fry until golden.
            """.trimIndent()

        val steps = instructions.parseSteps()

        assertThat(steps.map { it.instruction })
            .containsExactly(
                "Chop the onions finely.",
                "Fry until golden.",
            ).inOrder()
    }

    @Test
    fun `normal paragraph text is untouched`() {
        val instructions =
            """
            Whisk the eggs and sugar together until pale.

            Fold in the flour gently, then pour into the tin.
            """.trimIndent()

        val steps = instructions.parseSteps()

        assertThat(steps.map { it.instruction })
            .containsExactly(
                "Whisk the eggs and sugar together until pale.",
                "Fold in the flour gently, then pour into the tin.",
            ).inOrder()
    }

    @Test
    fun `mixed real-world sample yields clean contiguous steps`() {
        val instructions =
            """
            STEP 1
            Mix the halloumi with breadcrumbs, egg and herbs in a large bowl.

            STEP 2
            Shape the mixture into four burger patties.

            Step 3: Fry the patties for four minutes on each side until golden.

            STEP 4
            Toast the buns and assemble the burgers with salad and sauce.
            """.trimIndent()

        val steps = instructions.parseSteps()

        assertThat(steps.map { it.title })
            .containsExactly(
                "Step 1",
                "Step 2",
                "Step 3",
                "Step 4",
            ).inOrder()
        assertThat(steps.map { it.instruction })
            .containsExactly(
                "Mix the halloumi with breadcrumbs, egg and herbs in a large bowl.",
                "Shape the mixture into four burger patties.",
                "Fry the patties for four minutes on each side until golden.",
                "Toast the buns and assemble the burgers with salad and sauce.",
            ).inOrder()
    }
}
