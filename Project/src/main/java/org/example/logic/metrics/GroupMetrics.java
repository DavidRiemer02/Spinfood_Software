package org.example.logic.metrics;

import org.example.data.enums.Sex;
import org.example.logic.enums.MealType;
import org.example.logic.structures.GroupMatched;
import org.example.logic.structures.PairMatched;
import org.example.logic.tools.MatchingTools;

import java.util.List;

public class GroupMetrics {

    /**
     * The age difference of a group is the average of the age differences of each pair in the group
     * @param group a GroupMatched object
     * @return the age difference of the group
     * @throws IllegalArgumentException if the group has no pairs
     */
    public static double calcAgeDifference(GroupMatched group) {
        return group.getPairList().stream()
                .mapToDouble(PairMetrics::calcAgeDifference)
                .average()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * The gender diversity of a group is the total deviation of the number of women in relation to
     * the total number of people in the group from the ideal value of 0.5 (3 women in a group of 6 people).
     * @param group a GroupMatched object
     * @return the gender diversity of the group
     * @throws IllegalArgumentException if the group has no pairs
     */
    public static double calcGenderDiversity(GroupMatched group) {
        double countFemale = 0;

        for (PairMatched pair : group.getPairList()) {
            if (pair.getSoloA().getPerson().sex().equals(Sex.FEMALE)) countFemale++;
            if (pair.getSoloB().getPerson().sex().equals(Sex.FEMALE)) countFemale++;
        }

        double ratio = countFemale / (GroupMatched.groupSize * PairMatched.pairSize);
        return Math.abs(ratio - MetricTools.idealGenderRatio);
    }

    /**
     * The preference deviation of a group is the average of the preference deviations of each pair in the group
     * @param group a GroupMatched object
     * @return the average food preference deviation of the group
     * @throws IllegalArgumentException if the group has no pairs
     */
    public static double calcPreferenceDeviation(GroupMatched group) {
        return group.getPairList().stream()
                .mapToDouble(PairMetrics::calcPreferenceDeviation)
                .average()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * A group is valid when:
     * 1. the group consists of three pairs
     * 2. each pair of the group is valid
     * 3. the group has a valid food preference composition
     * 4. the cook for the group was set
     * 5. the meal type of the group was set
     * @param group a GroupMatched object
     * @return true if the group is valid, otherwise false
     */
    public static boolean isValid(GroupMatched group) {
        return group.getPairList().size() == GroupMatched.groupSize
                && PairListMetrics.isValid (group.getPairList())
                && hasValidPreferenceComposition(group.getPairList())
                && group.getCook() != null
                && !group.getMealType().equals(MealType.NONE);
    }

    /**
     * The preference composition of a group is valid, there the group doesn't contain two none ore meat pairs and
     * one veggie or vegan pair
     * @param pairs the pairs of a group
     * @return true if the preference composition of the group is invalid, otherwise false
     */
    public static boolean hasValidPreferenceComposition(List<PairMatched> pairs) {
        // counter[0] = none, counter[1] = meat, counter[2] = veggie, counter[3] = vegan
        int[] counter = new int[4];

        for (PairMatched pair: pairs) {
            int value = MatchingTools.getIntValueFoodPreference(pair.getFoodPreference());
            counter[value]++;
        }

       return !((counter[0] + counter[1] == 2) && (counter[2] + counter[3] == 1));
    }
}
