package org.example.logic.matchingalgorithms;

import org.example.data.Coordinate;
import org.example.data.enums.FoodPreference;
import org.example.data.enums.Sex;
import org.example.logic.enums.MealType;
import org.example.logic.graph.Graph;
import org.example.logic.structures.GroupMatched;
import org.example.logic.structures.PairMatched;

import java.util.ArrayList;
import java.util.List;

public class GraphGroupMatching {


    /**
     * Matches the pairs into groups via a graph solution based on the given importance of matching criteria
     * @author Paul Groß
     * @param matchedPairs list of pairs that need to be matched to groups
     * @param matchCosts object that contains the importance of the soft criteria
     * @return returns a list of the matched groups
     */
    public static List<GroupMatched> match(List<PairMatched> matchedPairs, MatchCosts matchCosts) {

        //Split pairs based on foodPreference
        List<PairMatched> meatNonePairs = getMeatNonePairs(matchedPairs);
        List<PairMatched> veggieVeganPairs = getVeggieVeganPairs(matchedPairs);

        //Create graphs
        Graph<PairMatched> meatGraph = createGraph(meatNonePairs, matchCosts);
        Graph<PairMatched> veganGraph = createGraph(veggieVeganPairs, matchCosts);

        //find superGroups (9 pairs)
        List<List<PairMatched>> superGroups = new ArrayList<>();
        superGroups.addAll(findSuperGroups(meatNonePairs, meatGraph));
        superGroups.addAll(findSuperGroups(veggieVeganPairs, veganGraph));

        //Split superGroups in corresponding dinner groups and assign cook (starters, mainCourse, dessert)
        List<GroupMatched> groupMatchedList;
        groupMatchedList = createDinnerGroupsFromSuperGroups(superGroups);

        return groupMatchedList;
    }


    /**
     * Performs the actual matching of the pairs into groups via a graph
     * @author Paul Groß
     * @param matchedPairs a list of matched pair participants
     * @param graph a graph where Pairs are vertices and weighted edges show the feasibility
     * of a possible group matching
     * @return A list of superGroups (List of 9 pairs) the supergroups get separated into 3 Starter groups, 3 main
     * course groups and 3 dessert groups
     */
    private static List<List<PairMatched>> findSuperGroups(List<PairMatched> matchedPairs, Graph<PairMatched> graph) {
        int superGroupSize = 9;
        List<List<PairMatched>> superGroups = new ArrayList<>();

        for (int i = 0; i < matchedPairs.size(); i++) {
            try {
                List<PairMatched> superGroup = new ArrayList<>();
                PairMatched currentPair = graph.getVertexWithLeastEdges(8);

                for(int j = 0; j < superGroupSize; j++) {
                    PairMatched possibleMatch = graph.getEdgeWithLeastWeight(currentPair).participant;
                    superGroup.add(possibleMatch);
                    graph.removeVertex(possibleMatch);
                }
                superGroups.add(superGroup);
                graph.removeVertex(currentPair);

            } catch (Exception e) {
                break;
            }
        }

        return superGroups;
    }


    /**
     * Splits the supergroups into 9 dinner groups, so that every pair meets two new pairs at every course.
     * Creates 3 starter Groups, 3 main course groups and 3 dessert groups
     * @author Paul Groß
     * @param superGroups The supergroups calculated in the findDinnerGroups() method
     * @return A list containing every single group of 3 pairs and the respective course they eat together
     */
    private static List<GroupMatched> createDinnerGroupsFromSuperGroups(List<List<PairMatched>> superGroups) {
        List<GroupMatched> dinnerGroups = new ArrayList<>();
        for (List<PairMatched> superGroup : superGroups){
            PairMatched pairA = superGroup.get(0);
            PairMatched pairB = superGroup.get(1);
            PairMatched pairC = superGroup.get(2);
            PairMatched pairD = superGroup.get(3);
            PairMatched pairE = superGroup.get(4);
            PairMatched pairF = superGroup.get(5);
            PairMatched pairG = superGroup.get(6);
            PairMatched pairH = superGroup.get(7);
            PairMatched pairI = superGroup.get(8);

            //starters groups
            GroupMatched starterGroupA = new GroupMatched(pairA, pairD, pairG, MealType.STARTER);
            GroupMatched starterGroupB = new GroupMatched(pairB, pairE, pairH, MealType.STARTER);
            GroupMatched starterGroupC = new GroupMatched(pairC, pairF, pairI, MealType.STARTER);

            //main course groups
            GroupMatched mainCourseGroupA = new GroupMatched(pairF, pairA, pairH, MealType.MAIN);
            GroupMatched mainCourseGroupB = new GroupMatched(pairD, pairB, pairI, MealType.MAIN);
            GroupMatched mainCourseGroupC = new GroupMatched(pairE, pairC, pairG, MealType.MAIN);

            //dessert groups
            GroupMatched dessertGroupA = new GroupMatched(pairI, pairE, pairA, MealType.DESSERT);
            GroupMatched dessertGroupB = new GroupMatched(pairG, pairF, pairB, MealType.DESSERT);
            GroupMatched dessertGroupC = new GroupMatched(pairH, pairD, pairC, MealType.DESSERT);

            dinnerGroups.addAll(List.of(
                    starterGroupA, starterGroupB, starterGroupC,
                    mainCourseGroupA, mainCourseGroupB, mainCourseGroupC,
                    dessertGroupA, dessertGroupB, dessertGroupC));
        }
        return dinnerGroups;
    }


    /**
     * Filters the vegan and veggie pairs of all matched pairs
     * @author Paul Groß
     * @param matchedPairs a list of pairs that have been matched
     * @return the pairs that have a vegan or veggie food preference
     */
    private static List<PairMatched> getVeggieVeganPairs(List<PairMatched> matchedPairs) {
        List<PairMatched> veggieVeganPairs = new ArrayList<>();
        for (PairMatched pair : matchedPairs) {
            if (pair.getFoodPreference().equals(FoodPreference.VEGGIE) || pair.getFoodPreference().equals(FoodPreference.VEGAN)) {
                veggieVeganPairs.add(pair);
            }
        }
        return veggieVeganPairs;

    }

    /**
     * Filters the meat and none pairs of all matched pairs
     * @author Paul Groß
     * @param matchedPairs a list of pairs that have been matched
     * @return the pairs that have a meat or none food preference
     */
    private static List<PairMatched> getMeatNonePairs(List<PairMatched> matchedPairs) {
        List<PairMatched> meatNonePairs = new ArrayList<>();
        for (PairMatched pair : matchedPairs) {
            if (pair.getFoodPreference().equals(FoodPreference.MEAT) || pair.getFoodPreference().equals(FoodPreference.NONE)) {
                meatNonePairs.add(pair);
            }
        }
        return meatNonePairs;
    }

    /**
     * Creates a graph based on the pairs as vertices. Calculates the costs to match two pairs and adds these costs as
     * the edge weight. If a pair can be matched with another pair, they have an edge
     * @param matchedPairs a list of pairs that have been matched
     * @param matchCosts an object containing the importance of the soft criteria
     * @return a graph with pairs as vertices. If a pair can be matched to another pair, they have an edge
     */
    public static Graph<PairMatched> createGraph(List<PairMatched> matchedPairs, MatchCosts matchCosts) {
        Graph<PairMatched> graph = new Graph<>();

        PairMatched furthestPair = GetFurthestPair(matchedPairs);
        double maxDistanceToPartyLocation = furthestPair.getDistanceToPartyLocation();

        for (int i = 0; i < matchedPairs.size(); i++) {

            for (int j = i + 1; j < matchedPairs.size(); j++) {
                PairMatched pairA = matchedPairs.get(i);
                PairMatched pairB = matchedPairs.get(j);

                if(fullfillsHardCriteria(pairA,pairB)){
                   double costs = calcMatchingCosts(pairA, pairB, maxDistanceToPartyLocation, matchCosts);
                   graph.addEdge(pairA, pairB, (float) costs);
                }
            }
        }
        return graph;
    }

    /**
     * Calculates the cost and thus, the likeability of two pairs being matched.
     * @param pairA A single matched pair.
     * @param pairB Another matched pair.
     * @param maxDistanceToPartyLocation Distance from the pair, farthest from the party location.
     * @param matchCosts an object containing the importance of the soft criteria.
     * @return the cost of matching PairA and PairB.
     */
    private static double calcMatchingCosts(PairMatched pairA, PairMatched pairB, double maxDistanceToPartyLocation, MatchCosts matchCosts) {
        double costs = 0;
        costs += calcPathCosts(pairA, pairB, maxDistanceToPartyLocation, matchCosts);
        costs += calcAgeCosts(pairA, pairB, matchCosts);
        costs += calcSexCosts(pairA, pairB, matchCosts);
        costs += calcFoodPreferenceCosts(pairA, pairB, matchCosts);
        return costs;
    }

    /**
     * Calculates the cost for matching the pairs based on their food preference.
     * @param pairA A single matched pair.
     * @param pairB Another matched pair.
     * @param matchCosts an object containing the importance of the soft criteria.
     * @return the cost of matching PairA and PairB based on the food preference.
     */
    private static double calcFoodPreferenceCosts(PairMatched pairA, PairMatched pairB, MatchCosts matchCosts) {
        double costs = 0;
        if(pairA.getFoodPreference() != pairB.getFoodPreference()){
            costs += matchCosts.getFoodPreferenceCosts();
        }
        return costs;
    }

    private static boolean fullfillsHardCriteria(PairMatched pairA, PairMatched pairB) {
        return !pairA.getKitchen().coordinate.equals(pairB.getKitchen().coordinate);
    }

    /**
     * Calculates the cost for matching the pairs based on their distance to one another.
     * @param pairA A single matched pair.
     * @param pairB Another matched pair.
     * @param matchCosts an object containing the importance of the soft criteria.
     * @return the cost of matching PairA and PairB based on their distance to one another.
     */
    public static double calcPathCosts(PairMatched pairA, PairMatched pairB, double maxDistanceToPartyLocation, MatchCosts matchCosts) {
        double pathCosts =  Coordinate.getDistance(pairA.getKitchen().coordinate, pairB.getKitchen().coordinate) / (maxDistanceToPartyLocation * 2);
        return pathCosts * matchCosts.getPathLengthCosts();
    }


    /**
     * Calculates the cost for matching the pairs based on their age.
     * @param pairA A single matched pair.
     * @param pairB Another matched pair.
     * @param matchCosts an object containing the importance of the soft criteria.
     * @return the cost of matching PairA and PairB based on their age.
     */
    public static double calcAgeCosts(PairMatched pairA, PairMatched pairB, MatchCosts matchCosts){
        int ageRangeA = pairA.getPersonA().age();
        int ageRangeB = pairA.getPersonB().age();
        int meanAgePairA = (ageRangeA + ageRangeB) / 2;

        int ageRangeC = pairB.getPersonA().age();
        int ageRangeD = pairB.getPersonB().age();
        int meanAgePairB = (ageRangeC + ageRangeD) / 2;

        int ageRangeDeviation = Math.abs(meanAgePairA - meanAgePairB);

        return ageRangeDeviation * matchCosts.getAgeCosts();
    }

    /**
     * Calculates the cost for matching the pairs based on their gender.
     * @param pairA A single matched pair.
     * @param pairB Another matched pair.
     * @param matchCosts an object containing the importance of the soft criteria.
     * @return the cost of matching PairA and PairB based on their gender.
     */
    public static double calcSexCosts(PairMatched pairA, PairMatched pairB, MatchCosts matchCosts) {

        double costs = 0;

        Sex sexA = pairA.getPersonA().sex();
        Sex sexB = pairA.getPersonB().sex();

        Sex sexC = pairB.getPersonA().sex();
        Sex sexD = pairB.getPersonB().sex();

        if(sexA.equals(sexC)){
            costs++;
        }
        if(sexA.equals(sexD)){
            costs++;
        }

        if(sexB.equals(sexC)){
            costs++;
        }
        if(sexB.equals(sexD)){
            costs++;
        }

        return costs * matchCosts.getGenderCosts();
    }

    /**
     * Finds the pair that is the furthest away from the party location.
     * This pair is later used, to calculate the distance from the furthest pair to the party location, this distance is used to normalize the
     * distance value from every other pair to receive a value between zero and one for the costs
     * @param pairs a list of all matched pairs
     * @return the pair that is the furthest away from the party location
     */
    public static PairMatched GetFurthestPair(List<PairMatched> pairs) {
        pairs.sort((pairA, pairB) -> {
            return Double.compare(pairB.getDistanceToPartyLocation(), pairA.getDistanceToPartyLocation());
        });
        return pairs.get(0);
    }
}