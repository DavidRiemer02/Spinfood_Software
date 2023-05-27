package org.example.logic.tools;

import org.example.data.enums.FoodPreference;
import org.example.data.factory.Person;

public class MatchingTools {

    public static int getAgeRange(int age){
        if (age < 0) throw new IllegalStateException("Unknown age: " + age);
        if (age <= 17) return 0;
        if (age <= 23) return 1;
        if (age <= 27) return 2;
        if (age <= 30) return 3;
        if (age <= 35) return 4;
        if (age <= 41) return 5;
        if (age <= 46) return 6;
        if (age <= 56) return 7;
        else return 8;
    }

    public static int getFoodPreference(FoodPreference foodPreference){
        return switch (foodPreference) {
            case NONE -> 0;
            case MEAT -> 1;
            case VEGGIE -> 2;
            case VEGAN -> 3;
        };
    }

    public static FoodPreference mapIntToFoodPreference(int value) {
        return switch (value) {
            case 0 -> FoodPreference.MEAT;
            case 1 -> FoodPreference.VEGGIE;
            case 3 -> FoodPreference.VEGAN;
            default -> throw new IllegalArgumentException("int " + value + "cant be mapped to food preference");
        };
    }

    public static int calculateFoodPreferenceDeviation(FoodPreference foodPreferenceA, FoodPreference foodPreferenceB) {
        int foodValueA = MatchingTools.getFoodPreference(foodPreferenceA);
        int foodValueB = MatchingTools.getFoodPreference(foodPreferenceB);
        return Math.abs(foodValueA - foodValueB);
    }

    public static int calculateAgeRangeDeviation(Person personA, Person personB) {
        int ageValueA = MatchingTools.getAgeRange(personA.age());
        int ageValueB = MatchingTools.getAgeRange(personB.age());
        return  Math.abs(ageValueA - ageValueB);
    }
}
