package org.example.logic.structures;

import org.example.data.Coordinate;
import org.example.data.enums.FoodPreference;
import org.example.data.enums.KitchenType;
import org.example.data.enums.Sex;
import org.example.data.factory.Kitchen;
import org.example.data.factory.Person;
import org.example.data.structures.Pair;
import org.example.data.structures.Solo;
import org.example.logic.tools.MatchingTools;
import org.example.logic.enums.MealType;
import org.example.logic.tools.Metricable;

public class PairMatched implements Comparable<PairMatched>, Metricable {

    private Person personA;
    private Person personB;
    private final FoodPreference foodPreference;

    private FoodPreference personAFoodPreference;
    private FoodPreference personBFoodPreference;

    private final Kitchen kitchen;
    public final boolean preMatched;

    private Coordinate partyLocation;
    private Double distanceToPartyLocation;
    public MealType cooksMealType;
    private GroupMatched starterGroup;
    private GroupMatched mainGroup;
    private GroupMatched dessertGroup;


    private final int foodPreferenceDeviation;
    private final int ageRangeDeviation;

    public PairMatched(Pair pair){
        this.personA = pair.personA;
        this.personB = pair.personB;
        this.foodPreference = pair.foodPreference;
        this.personAFoodPreference = pair.foodPreference;
        this.personBFoodPreference = pair.foodPreference;
        this.kitchen = pair.kitchen;

        this.foodPreferenceDeviation = 0;
        this.ageRangeDeviation = MatchingTools.calculateAgeRangeDeviation(personA, personB);
        this.preMatched = true;
    }

    public PairMatched(Solo soloA, Solo soloB){
        this.personA = soloA.person;
        this.personB = soloB.person;
        this.foodPreference = calculateFoodPreference(soloA, soloB);
        this.personAFoodPreference = soloA.foodPreference;
        this.personBFoodPreference = soloB.foodPreference;
        this.kitchen = calculateKitchen(soloA, soloB);

        this.foodPreferenceDeviation = MatchingTools.calculateFoodPreferenceDeviation(soloA.foodPreference, soloB.foodPreference);
        this.ageRangeDeviation = MatchingTools.calculateAgeRangeDeviation(personA, personB);
        this.preMatched = false;
    }


    public Person getPersonA() {
        return personA;
    }

    public Person getPersonB() {
        return personB;
    }

    public FoodPreference getFoodPreference() {
        return foodPreference;
    }



    public Kitchen getKitchen() {
        return kitchen;
    }

    @Override
    public double getPathLength() {
        Coordinate starter = starterGroup.getKitchenCoordinate();
        Coordinate main = mainGroup.getKitchenCoordinate();
        Coordinate dessert = dessertGroup.getKitchenCoordinate();

        double distanceStarterToMain = Coordinate.getDistance(starter, main);
        double distanceMainToDessert = Coordinate.getDistance(main, dessert);
        double distanceDessertToParty = Coordinate.getDistance(dessert, partyLocation);

        return  distanceStarterToMain + distanceMainToDessert + distanceDessertToParty;
    }

    @Override
    public double getGenderDeviation() {
        double countFemale = 0;
        if (personA.sex().equals(Sex.FEMALE)) countFemale++;
        if (personB.sex().equals(Sex.FEMALE)) countFemale++;
        return countFemale / 2;
    }

    @Override
    public double getAgeRangeDeviation() {
        return ageRangeDeviation;
    }

    @Override
    public double getFoodPreferenceDeviation() {
        return foodPreferenceDeviation;
    }

    @Override
    public boolean isValid() {
        return !personA.equals(personB);
    }

    public void addGroup(GroupMatched groupMatched) {
        switch (groupMatched.mealType) {
            case NONE -> throw new IllegalArgumentException();
            case STARTER -> starterGroup = groupMatched;
            case MAIN ->  mainGroup = groupMatched;
            case DESSERT -> dessertGroup = groupMatched;
        }
    }

    public boolean isNotInGroup() {
        return starterGroup == null && mainGroup == null && dessertGroup == null;
    }

    private FoodPreference calculateFoodPreference(Solo soloA, Solo soloB) {
        int foodValueA = MatchingTools.getIntValueFoodPreference(soloA.foodPreference);
        int foodValueB = MatchingTools.getIntValueFoodPreference(soloB.foodPreference);
        return FoodPreference.parseFoodPreference(Math.max(foodValueA, foodValueB));
    }

    private Kitchen calculateKitchen(Solo soloA, Solo soloB) {
        Kitchen kitchenA = soloA.kitchen;
        Kitchen kitchenB = soloB.kitchen;

        if (kitchenA.kitchenType.equals(KitchenType.YES)) return kitchenA;
        else if (kitchenB.kitchenType.equals(KitchenType.YES)) return kitchenB;
        else if (kitchenA.kitchenType.equals(KitchenType.MAYBE)) return kitchenA;
        else if (kitchenB.kitchenType.equals(KitchenType.MAYBE)) return kitchenB;
        else throw new IllegalStateException(this + " has no kitchen");
    }

    public void setDistanceToPartyLocation(Coordinate partyLocation) {
        this.partyLocation = partyLocation;
        this.distanceToPartyLocation = Coordinate.getDistance(getKitchen().coordinate, partyLocation);

    }

    public double getDistanceToPartyLocation() {
        if (this.distanceToPartyLocation == null) {
            throw new IllegalStateException("Distance to party location isn't set");
        }
        return this.distanceToPartyLocation;
    }

    public boolean containsPerson(Person person) {
        return getPersonA().equals(person) || getPersonB().equals(person);
    }

    @Override
    public int compareTo(PairMatched o) {
        return Double.compare(this.getDistanceToPartyLocation(), o.getDistanceToPartyLocation());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PairMatched other)){
            return false;
        }
        return this.personA.equals(other.getPersonA()) && this.personB.equals(other.getPersonB());
    }

    @Override
    public String toString() {
        return getPersonA().name() + ", " + getPersonB().name() + ", " + getFoodPreference();
    }

    public void setPersonA(Person person) {
        this.personA = person;
    }

    public void setPersonB(Person person) {
        this.personB = person;
    }

    public FoodPreference getPersonAFoodPreference() {
        return personAFoodPreference;
    }

    public FoodPreference getPersonBFoodPreference() {
        return personBFoodPreference;
    }
}
