package com.example.budjet

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GoalUtilsTest {

    // Tests that a goal is valid when the minimum amount is less than the maximum amount.
    @Test
    fun validGoal_returnsTrue_whenMinIsLessThanMax() {
        val result = GoalUtils.isValidGoal(300.0, 1000.0)

        assertTrue(result)
    }

    // Tests that a goal is invalid when the minimum and maximum amounts are the same.
    @Test
    fun validGoal_returnsFalse_whenMinEqualsMax() {
        val result = GoalUtils.isValidGoal(500.0, 500.0)

        assertFalse(result)
    }

    // Tests that a goal is invalid when the minimum amount is greater than the maximum amount.
    @Test
    fun validGoal_returnsFalse_whenMinIsGreaterThanMax() {
        val result = GoalUtils.isValidGoal(1000.0, 300.0)

        assertFalse(result)
    }

    // Tests that a goal is invalid when one of the goal values is negative.
    @Test
    fun validGoal_returnsFalse_whenGoalIsNegative() {
        val result = GoalUtils.isValidGoal(-100.0, 1000.0)

        assertFalse(result)
    }

    // Tests that spending below the minimum goal is classified as BELOW_MINIMUM.
    @Test
    fun getGoalStatus_returnsBelowMinimum_whenSpendingIsLessThanMinGoal() {
        val result = GoalUtils.getGoalStatus(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 100.0
        )

        assertEquals("BELOW_MINIMUM", result)
    }

    // Tests that spending between the minimum and maximum goals is classified as WITHIN_GOAL.
    @Test
    fun getGoalStatus_returnsWithinGoal_whenSpendingIsBetweenMinAndMax() {
        val result = GoalUtils.getGoalStatus(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 500.0
        )

        assertEquals("WITHIN_GOAL", result)
    }

    // Tests that spending above the maximum goal is classified as OVER_MAXIMUM.
    @Test
    fun getGoalStatus_returnsOverMaximum_whenSpendingIsMoreThanMaxGoal() {
        val result = GoalUtils.getGoalStatus(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 1200.0
        )

        assertEquals("OVER_MAXIMUM", result)
    }

    // Tests that progress is calculated correctly when spending is below the minimum goal.
    @Test
    fun calculateGoalProgress_returnsCorrectProgress_whenBelowMinimum() {
        val result = GoalUtils.calculateGoalProgress(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 150.0
        )

        assertEquals(50, result)
    }

    // Tests that progress is calculated correctly when spending is within the goal range.
    @Test
    fun calculateGoalProgress_returnsCorrectProgress_whenWithinGoal() {
        val result = GoalUtils.calculateGoalProgress(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 650.0
        )

        assertEquals(50, result)
    }

    // Tests that progress is capped at 100 when spending is above the maximum goal.
    @Test
    fun calculateGoalProgress_returns100_whenOverMaximum() {
        val result = GoalUtils.calculateGoalProgress(
            minGoal = 300.0,
            maxGoal = 1000.0,
            totalSpent = 1200.0
        )

        assertEquals(100, result)
    }
}