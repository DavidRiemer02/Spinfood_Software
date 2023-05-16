package org.example.logic.tools;

import org.example.data.enums.FoodPreference;
import org.example.data.enums.KitchenType;
import org.example.data.enums.Sex;
import org.example.data.structures.Solo;
import org.example.logic.graph.UndirectedGraph;


import java.util.ArrayList;
import java.util.List;

public class PairMatchingAlgorithm {

    private static final float genderCost = 1f;
    private static final float kitchenCost = 1f;
    private static final float foodPreferenceCost = 1f;
    private static final float ageCost = 1f;
    private static final float defaultLimitMultiplier = 1f;

    public static List<PairMatched> match(List<Solo> solos) {
        return match(solos, CostCoefficients.getDefault(), defaultLimitMultiplier);
    }

    public static List<PairMatched> match(List<Solo> solos, CostCoefficients coefficients) {
        return match(solos, coefficients, defaultLimitMultiplier);
    }

    public static List<PairMatched> match(List<Solo> solos, float limitMultiplier) {
        return match(solos, CostCoefficients.getDefault(), limitMultiplier);
    }

    public static List<PairMatched> match(List<Solo> solos, CostCoefficients coefficients, float limitMultiplier) {
        List<PairMatched> pairMatched = new ArrayList<>();
        UndirectedGraph undirectedGraph = createGraph(solos, coefficients, limitMultiplier);

        for (int i = 0 ; i < solos.size() / 2; i++) {
            try {
                Solo soloA = undirectedGraph.getVertexWithLeastEdges();
                Solo soloB = undirectedGraph.getEdgeWithLeastWeight(soloA).solo;

                pairMatched.add(new PairMatched(soloA, soloB));

                undirectedGraph.removeVertex(soloA);
                undirectedGraph.removeVertex(soloB);
            } catch (NullPointerException e) {
                break;
            }
        }

        return pairMatched;
    }

    private static UndirectedGraph createGraph(List<Solo> solos, CostCoefficients coefficients, float limitMultiplier) {
        UndirectedGraph undirectedGraph = new UndirectedGraph();
        float maxCosts = calcMaxCost(coefficients);

        for (int i = 0; i < solos.size(); i++) {
            for (int j = i + 1; j < solos.size(); j++) {
                Solo soloA = solos.get(i);
                Solo soloB = solos.get(j);

                if (fulfillsHardCriteria(soloA, soloB)) {
                    float weight = calcValue(soloA, soloB, coefficients);
                    if (weight <= maxCosts * limitMultiplier) {
                        undirectedGraph.addEdge(soloA, soloB, weight);
                    }
                }
            }
        }
        return undirectedGraph;
    }

    private static boolean fulfillsHardCriteria(Solo solo1, Solo solo2) {
        boolean s1HasNoKitchen = solo1.kitchen.kitchenType.equals(KitchenType.NO);
        boolean s2HasNoKitchen = solo2.kitchen.kitchenType.equals(KitchenType.NO);
        boolean noKitchenAvailable = s1HasNoKitchen && s2HasNoKitchen;

        boolean s1Tos2 = isFoodPreferenceIncompatible(solo1, solo2);
        boolean s2Tos1 = isFoodPreferenceIncompatible(solo2, solo1);
        boolean unFittingFoodPreference = s1Tos2 || s2Tos1;

        return !(noKitchenAvailable || unFittingFoodPreference);
    }

    private static float calcMaxCost(CostCoefficients coefficients) {
        return kitchenCost * coefficients.kitchenWeight()
                + genderCost * coefficients.genderWeight()
                + foodPreferenceCost * coefficients.foodPreferenceWeight()
                + ageCost * coefficients.ageWeight();
    }

    private static float calcValue(Solo soloA, Solo soloB, CostCoefficients coefficients) {
        return calcKitchenCost(soloA, soloB, coefficients)
                + calcGenderCost(soloA, soloB, coefficients)
                + calcAgeCost(soloA, soloB, coefficients)
                + calcFoodPreferenceCost(soloA, soloB, coefficients);
    }

    private static float calcKitchenCost(Solo soloA, Solo soloB, CostCoefficients coefficients) {
        boolean soloAHasKitchen = soloA.kitchen.kitchenType.equals(KitchenType.YES);
        boolean soloBHasKitchen = soloB.kitchen.kitchenType.equals(KitchenType.YES);

        if (soloAHasKitchen && soloBHasKitchen) {
            return 1 * kitchenCost * coefficients.kitchenWeight();
        } else {
            return 0;
        }
    }

    private static float calcGenderCost(Solo soloA, Solo soloB, CostCoefficients coefficients) {
        Sex soloASex = soloA.person.sex();
        Sex soloBSex = soloB.person.sex();

        if (!soloASex.equals(Sex.OTHER) && soloASex.equals(soloBSex)) {
            return 1 * genderCost * coefficients.genderWeight();
        } else {
            return 0;
        }
    }

    private static float calcAgeCost(Solo soloA, Solo soloB, CostCoefficients coefficients) {
        float ageRangeA = MatchingTools.getAgeRange(soloA.person.age());
        float ageRangeB = MatchingTools.getAgeRange(soloB.person.age());
        float difference = Math.abs(ageRangeA - ageRangeB);
        return (difference / 8) * ageCost * coefficients.ageWeight();
    }

    private static float calcFoodPreferenceCost(Solo soloA, Solo soloB, CostCoefficients coefficients) {
        int soloAValue = MatchingTools.getFoodPreference(soloA.foodPreference);
        int soloBValue = MatchingTools.getFoodPreference(soloB.foodPreference);
        float difference = Math.abs(soloAValue - soloBValue);
        return (difference / 2) * foodPreferenceCost * coefficients.foodPreferenceWeight();
    }

    private static boolean isFoodPreferenceIncompatible(Solo soloA, Solo soloB) {
        boolean soloAIsMeat = soloA.foodPreference.equals(FoodPreference.MEAT);
        boolean soloBIsVeggie = soloB.foodPreference.equals(FoodPreference.VEGGIE);
        boolean soloBIsVegan = soloB.foodPreference.equals(FoodPreference.VEGAN);

        return soloAIsMeat && (soloBIsVeggie || soloBIsVegan);
    }
}