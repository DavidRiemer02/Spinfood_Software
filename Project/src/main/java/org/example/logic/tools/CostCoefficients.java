package org.example.logic.tools;

public record CostCoefficients(float ageWeight, float foodPreferenceWeight, float genderWeight, float kitchenWeight) {
    static CostCoefficients getDefault() {
        return new CostCoefficients(1,1,1,1);
    }
}