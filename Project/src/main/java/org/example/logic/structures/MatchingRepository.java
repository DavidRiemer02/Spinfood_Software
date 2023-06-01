package org.example.logic.structures;

import org.example.data.DataManagement;
import org.example.data.structures.Pair;
import org.example.data.structures.Solo;
import org.example.logic.structures.GroupMatched;
import org.example.logic.structures.PairMatched;
import org.example.logic.tools.MatchingSystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Holds all necessary data that is used in the logic layer of the project.
 * Data can be queried and modified with methods in this class
 * @author Paul Groß
 *
 */
public class MatchingRepository {

    public DataManagement dataManagement;
    Collection<Solo> soloDataCollection = new ArrayList<>();
    Collection<Pair> pairDataCollection = new ArrayList<>();

    Collection<PairMatched> matchedPairsCollection = new ArrayList<>();
    Collection<GroupMatched> matchedGroupsCollection =  new ArrayList<>();

    public Collection<Solo> soloSuccessors =  new ArrayList<>();
    public Collection<PairMatched> pairSuccessors = new ArrayList<>();

    /**
     * If a matching repository is created with a data management object, the data of the solo and pair participants are read in.
     * PairMatched pairs are created, which are defined by pair registrations.
     * A PairMatching algorithm is called to define further pairs.
     * After that, a GroupMatching algorithm is called to group the pairs.
     * After the matching algorithms are run, the successor solos and pairs are stored in lists.
     * @author Paul Groß
     * @param dataManagement object that contains data about registered solo participants and pair participants
     */
    public MatchingRepository(DataManagement dataManagement){
        this.dataManagement = dataManagement;
        addSoloCollection(dataManagement.soloParticipants);
        addPairCollection(dataManagement.pairParticipants);

        createAndAddPrematchedPairs();

        Collection<PairMatched> matchedPairs = MatchingSystem.matchPairs((List<Solo>) this.getSoloDataCollection());
        this.addMatchedPairsCollection(matchedPairs);

        setDistanceToPartyLocationForPairs();

        Collection<GroupMatched> matchedGroups = MatchingSystem.matchGroups((List<PairMatched>) this.getMatchedPairsCollection());
        this.addMatchedGroupsCollection(matchedGroups);

        UpdateSoloSuccessors();
        UpdatePairSuccessors();
        
    }

    /**
     * The pairs from the DataManagement object are transferred into PairMatched objects
     * @author Paul Groß
     */
    public void createAndAddPrematchedPairs() {
        Collection<PairMatched> preMatchedPairs = new ArrayList<>();
        for (Pair pair : pairDataCollection) {
            PairMatched pairMatched = new PairMatched(pair);
            preMatchedPairs.add(pairMatched);
        }
        addMatchedPairsCollection(preMatchedPairs);
    }

    /**
     * The distance from each pair to the party location is set
     * @author Paul Groß
     */
    public void setDistanceToPartyLocationForPairs(){
        Collection<PairMatched> pairs = getMatchedPairsCollection();
        for (PairMatched pair : pairs){
            pair.setDistanceToPartyLocation(this.dataManagement.partyLocation);
        }
    }

    /**
     * Find the solos that haven't been matched
     * @author Paul Groß
     */
    private void UpdateSoloSuccessors(){
        Collection<Solo> solos = new ArrayList<>(getSoloDataCollection());
        Collection<PairMatched> pairMatchedList = new ArrayList<>(matchedPairsCollection);

        for (Solo solo : soloDataCollection) {

            for (PairMatched pair : pairMatchedList) {
                if(solo.person.equals(pair.getPersonA()) || solo.person.equals(pair.getPersonB())){
                    solos.remove(solo);
                }
            }
        }
        soloSuccessors = solos;
    }

    /**
     * Find the pairs that haven't been matched and add them to a list
     * @author Paul Groß
     */
    public void UpdatePairSuccessors() {

        Collection<PairMatched> unmatchedPairs = new ArrayList<>(matchedPairsCollection);

        for (PairMatched pair : matchedPairsCollection) {

            for (GroupMatched group : matchedGroupsCollection) {
                if(group.containsPair(pair)){
                    unmatchedPairs.remove(pair);
                }
            }
        }
        pairSuccessors = unmatchedPairs;
    }


    public void printFoodPreferencesOfPairs(){
        System.out.println("Now printing foodPreferences of matched pairs");
        for (PairMatched pair :
                getMatchedPairsCollection()) {
            System.out.println(pair.getFoodPreference());
        }
    }

    //Code for handling the un-registration of EventParticipants

    /**
     * Removes a solo and tries to find a successor
     * @param solo a solo that unregistered from the event
     */
    public void removeSolo(Solo solo){
        //If solo is not matched, just remove it
        if(soloSuccessors.contains(solo)){
            soloDataCollection.remove(solo);
            soloSuccessors.remove(solo);
            return;
        }

        //Find the pair that is affected by the removing of a solo
        PairMatched affectedPair = getMatchedPairsCollection().stream().filter(p -> p.containsPerson(solo.person)).toList().get(0);

        //Find a replacement
        Solo newSolo = findReplacement(solo);

        //If a replacement solo is found, replace it in the affected pair
        if(newSolo != null) {
            if (affectedPair.getPersonA().equals(solo.person)) {
                affectedPair.setPersonA(newSolo.person);
            } else if (affectedPair.getPersonB().equals(solo.person)) {
                affectedPair.setPersonB(newSolo.person);
            }
            return;
        }

        //If there is no replacement, the pair has to be deleted and the remaining solo needs to be added to the
        //successor list
        Solo remainingSolo = null;
        if (affectedPair.getPersonA().equals(solo.person)) {
            remainingSolo = new Solo(affectedPair.getPersonB(), affectedPair.getPersonBFoodPreference(), affectedPair.getKitchen());
        } else if (affectedPair.getPersonB().equals(solo.person)) {
            remainingSolo = new Solo(affectedPair.getPersonA(), affectedPair.getPersonAFoodPreference(), affectedPair.getKitchen());
        }

        soloSuccessors.add(remainingSolo);

        //remove the pair
        removePair(affectedPair);
    }

    /**
     * Tries to find a replacement from the successor list
     * @author Paul Groß
     * @param solo a solo that unregistered from the event
     * @return a replacement solo for the unregistered solo participant
     */
    private Solo findReplacement(Solo solo) {
        for (Solo replacementSolo : soloSuccessors) {
            if(replacementSolo.foodPreference.equals(solo.foodPreference)){
                return replacementSolo;
            }
        }
        return null;
    }

    /**
     * Removes a pair that unregistered from the event. Also is called when a solo unregisters from the event
     * and there is no fitting successor.
     * @param pair a pair that unregistered from the event
     */
    public void removePair(PairMatched pair) {
        //If pair is not matched in a group, just remove it
        if (pairSuccessors.contains(pair)) {
            getMatchedPairsCollection().remove(pair);
            pairSuccessors.remove(pair);
            return;
        }

        //Find all affected groups. The pair is a part of 3 groups
        List<GroupMatched> affectedGroups = getMatchedGroupsCollection().stream().filter(g -> g.containsPair(pair)).toList();

        //Try to find a replacement pair
        PairMatched newPair = findReplacement(pair);

        //If a replacement is found, replace the pair in all 3 groups
        //If no replacement is found, the group has to be deleted and the remaining pairs get added to the successor list.
        if (newPair != null) {
            replacePairInGroups(pair, newPair, affectedGroups);
        } else {
            affectedGroups.forEach(g -> {g.removePair(pair);
                                            disbandGroup(g);
                                            getMatchedGroupsCollection().remove(g);});
        }

        getMatchedPairsCollection().remove(pair);
        UpdatePairSuccessors();
    }

    /**
     * Adds all remaining pairs of a group to the successor list
     * @param group a group that needs to be deleted
     */
    private void disbandGroup(GroupMatched group) {
        pairSuccessors.addAll(group.getPairList());
    }

    /**
     * Replaces the unregistered pair of the three groups with the new pair
     * @param pair the pair that unregistered from the event
     * @param newPair the filler pair, that replaces the pair that unregistered
     * @param affectedGroups the three affected groups the unregistered pair was part of
     */
    private void replacePairInGroups(PairMatched pair, PairMatched newPair, List<GroupMatched> affectedGroups) {
        System.out.println("now replacing " + pair + " with " + newPair);
        for (GroupMatched group : affectedGroups) {
            group.switchPairs(pair, newPair);
        }
    }

    /**
     * Tries to find a replacement pair for the pair the unregistered from the event
     * @param pair a pair that unregistered from the event
     * @return a pair that is similar to the unregistered pair that can be used as a replacement
     */
    private PairMatched findReplacement(PairMatched pair) {
        for (PairMatched replacementPair : pairSuccessors) {
            if(replacementPair.getFoodPreference().equals(pair.getFoodPreference())){
                return replacementPair;
            }
        }
        return null;
    }



    public void printFoodPreferenceOfPairPersons(){
        List<PairMatched> pairMatchedList = (List<PairMatched>) getMatchedPairsCollection();

        for (PairMatched pair : pairMatchedList) {
            System.out.println("FoodPreferences");
            System.out.println("A: " + pair.getPersonAFoodPreference() + " B: " + pair.getPersonBFoodPreference());
        }
        
    }

    public void printFoodPreferenceOfUnmatchedPairs(){
        List<PairMatched> pairSuccessors = (List<PairMatched>) this.pairSuccessors;

        for (PairMatched pair : pairSuccessors) {
            System.out.println("FoodPreferences");
            System.out.println(pair.getFoodPreference());;
        }

    }


    //Getters, Setters
    public void addMatchedPairsCollection(Collection<PairMatched> matchedPairsCollection) {
        this.matchedPairsCollection.addAll(matchedPairsCollection);
    }

    public void addMatchedGroupsCollection(Collection<GroupMatched> matchedGroupsCollection) {
        this.matchedGroupsCollection.addAll(matchedGroupsCollection);
    }

    public void addSoloCollection(Collection<Solo> solos) {
        this.soloDataCollection.addAll(solos);
    }

    public void addPairCollection(Collection<Pair> pairs) {
        this.pairDataCollection.addAll(pairs);
    }

    public Collection<Solo> getSoloDataCollection() {
        return soloDataCollection;
    }

    public Collection<Pair> getPairDataCollection() {
        return pairDataCollection;
    }

    public Collection<PairMatched> getMatchedPairsCollection() {
        return matchedPairsCollection;
    }

    public Collection<GroupMatched> getMatchedGroupsCollection() {
        return matchedGroupsCollection;
    }


}