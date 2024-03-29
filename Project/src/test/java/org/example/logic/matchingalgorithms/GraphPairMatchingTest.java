package org.example.logic.matchingalgorithms;

import org.example.data.DataManagement;
import org.example.data.enums.FoodPreference;
import org.example.logic.structures.MatchingRepository;
import org.example.logic.structures.PairMatched;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to check if correct pair matchings are produced
 */
class GraphPairMatchingTest {
    private static MatchingRepository matchingRepository;


    @BeforeAll
    static void setup() {
        String filePathParticipants = "src/main/java/org/example/artifacts/teilnehmerListe.csv";
        String filePathLocation = "src/main/java/org/example/artifacts/partylocation.csv";
        DataManagement dataManagement = new DataManagement(filePathParticipants, filePathLocation);
        matchingRepository = new MatchingRepository(dataManagement);

    }

    @Test
    void match() {
        List<PairMatched> matchedPairs = (List<PairMatched>) matchingRepository.getMatchedPairsCollection();

        boolean pairsAreFeasible = true;

        for (PairMatched pair : matchedPairs) {
            if (!IsFeasible(pair)) {
                pairsAreFeasible = false;
            }
        }

        Assertions.assertTrue(pairsAreFeasible);
    }

    private boolean IsFeasible(PairMatched pair) {
        boolean foodPreferenceIsFeasible;
        boolean kitchenIsFeasible;
        foodPreferenceIsFeasible = checkFoodPreference(pair);

        kitchenIsFeasible = checkKitchen(pair);

        return foodPreferenceIsFeasible && kitchenIsFeasible;
    }

    private boolean checkKitchen(PairMatched pair) {
        return pair.getKitchen() != null;
    }

    private boolean checkFoodPreference(PairMatched pair) {
        FoodPreference personAFoodPreference = pair.getSoloA().getFoodPreference();
        FoodPreference personBFoodPreference = pair.getSoloB().getFoodPreference();

        boolean personAIsMeat = personAFoodPreference.equals(FoodPreference.MEAT);
        boolean personBIsVeggie = personBFoodPreference.equals(FoodPreference.VEGGIE);
        boolean personBisVegan = personBFoodPreference.equals(FoodPreference.VEGAN);

        return !(personAIsMeat && (personBIsVeggie || personBisVegan));
    }
}