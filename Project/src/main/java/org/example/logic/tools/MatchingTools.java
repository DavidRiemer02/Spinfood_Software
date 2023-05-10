package org.example.logic.tools;

import org.example.data.enums.FoodPreference;

public class MatchingTools {

    public static int getAgeRange(int age){
        if(age <= 17 && age >= 0) {
            return 0;
        } else if (age <= 23 && age >= 18){
            return 1;
        }
        else if (age <= 27 && age >= 24){
            return 2;
        }
        else if (age <= 30 && age >= 28){
            return 3;
        }
        else if (age <= 35 && age >= 31){
            return 4;
        }
        else if (age <= 41 && age >= 36){
            return 5;
        }
        else if (age <= 46 && age >= 42){
            return 6;
        }
        else if (age <= 56 && age >= 47){
            return 7;
        }
        else if (age >= 57){
            return 8;
        }
        throw new IllegalStateException("Unknonw age: " + age);
    }


    public static int getFoodPreference(FoodPreference foodPreference){
        if(foodPreference.equals(FoodPreference.NONE)){
            return 0;
        }
        else if(foodPreference.equals(FoodPreference.MEAT)){
            return 0;
        }
        else if(foodPreference.equals(FoodPreference.VEGGIE)){
            return 1;
        }
        else if(foodPreference.equals(FoodPreference.VEGAN)){
            return 2;
        }
        throw new IllegalStateException("Unknonw foodPreference: " + foodPreference);
    }
}
