package com.example.budjet

//helper class for goals unit tests
object GoalUtils {

    fun isValidGoal(minGoal: Double, maxGoal: Double): Boolean {
        return minGoal >= 0 && maxGoal >= 0 && minGoal < maxGoal
    }

    fun calculateGoalProgress(
        minGoal: Double,
        maxGoal: Double,
        totalSpent: Double
    ): Int {
        return when {
            totalSpent < minGoal -> {
                ((totalSpent / minGoal) * 100).toInt().coerceIn(0, 100)
            }

            totalSpent > maxGoal -> {
                100
            }

            else -> {
                ((totalSpent - minGoal) / (maxGoal - minGoal) * 100)
                    .toInt()
                    .coerceIn(0, 100)
            }
        }
    }

    fun getGoalStatus(
        minGoal: Double,
        maxGoal: Double,
        totalSpent: Double
    ): String {
        return when {
            totalSpent < minGoal -> "BELOW_MINIMUM"
            totalSpent > maxGoal -> "OVER_MAXIMUM"
            else -> "WITHIN_GOAL"
        }
    }
}