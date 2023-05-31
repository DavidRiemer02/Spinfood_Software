package org.example.logic.enums;

/**
 * Enum class to hold the criteria for the matching algorithm
 * @author Paul Groß
 * @version 1.0
 * @see org.example.logic.tools.MatchingSystem
 * @see org.example.logic.matchingalgorithms.MatchCosts
 */
public enum Criteria {
    IDENTICAL_FOOD_PREFERENCE,
    AGE_DIFFERENCE,
    GENDER_DIFFERENCE,
    PATH_LENGTH,
    MATCH_COUNT,
}
