package org.example.logic.structures;

import org.example.data.Coordinate;
import org.example.data.enums.FoodPreference;
import org.example.logic.tools.MatchingTools;
import org.example.logic.enums.MealType;
import org.example.logic.tools.Metricable;

import java.util.ArrayList;
import java.util.List;

public class GroupMatched {

    public static final int groupSize = 3;
    PairMatched cook;
    MealType mealType;
    PairMatched pairA;
    PairMatched pairB;
    PairMatched pairC;
    private List<PairMatched> pairs;

    FoodPreference groupFoodPreference;

    public GroupMatched(PairMatched pairA, PairMatched pairB, PairMatched pairC) {
        this.pairA = pairA;
        this.pairB = pairB;
        this.pairC = pairC;
    }

    public GroupMatched(PairMatched cook, PairMatched guestA, PairMatched guestB, MealType mealType) {
        this.cook = cook;
        this.cook.cooksMealType = mealType;
        this.mealType = mealType;

        this.pairs = new ArrayList<>();
        this.pairs.add(cook);
        this.pairs.add(guestA);
        this.pairs.add(guestB);

        for (PairMatched pair : pairs) {
            pair.addToGroup(this);
        }
    }

    public boolean containsPair(PairMatched pairMatched) {
        return pairs.contains(pairMatched);
    }

    public Coordinate getKitchenCoordinate() {
        return cook.getKitchen().coordinate;
    }

    protected FoodPreference calculateFoodPreference() {
        //Calculate here
        int foodPreferencePairA = MatchingTools.getFoodPreference(pairA.getFoodPreference());
        int foodPreferencePairB = MatchingTools.getFoodPreference(pairB.getFoodPreference());
        int foodPreferencePairC = MatchingTools.getFoodPreference(pairC.getFoodPreference());
        return FoodPreference.parseFoodPreference(Math.max(foodPreferencePairA, Math.max(foodPreferencePairB, foodPreferencePairC)));

        //throw new IllegalStateException("not implemented yet");
    }

    public List<PairMatched> getPairs() {
        return pairs;
    }

    public void deleteGroup() {

    }

    @Override
    public String toString() {
        return "(" + pairs.get(0).toString() + "), ("
                + pairs.get(1).toString() + "), ("
                + pairs.get(2).toString() + ")";
    }

    private boolean validFoodPreferences() {
        int meatNoneCount = 0;
        int veggieVeganCount = 0;

        for (PairMatched pair : pairs) {
            boolean isMeatOrNone = pair.getFoodPreference().equals(FoodPreference.MEAT)
                    || pair.getFoodPreference().equals(FoodPreference.NONE);

            if (isMeatOrNone) meatNoneCount++;
            else veggieVeganCount++;
        }

        return !(meatNoneCount == 1 && veggieVeganCount == 2);
    }

    public void switchPairs(PairMatched thisPair, PairMatched newPair){
        if(thisPair.equals(pairA)){
            pairA = newPair;
        }
        else if(thisPair.equals(pairB)){
            pairB = newPair;
        }
        else if(thisPair.equals(pairC)){
            pairC = newPair;
        }
    }
}
