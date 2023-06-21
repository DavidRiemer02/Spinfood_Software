package org.example.view.commands;

import org.example.data.structures.Solo;
import org.example.logic.structures.MatchingRepository;
import org.example.logic.structures.PairMatched;
import org.example.view.UIAction;

public class DisbandPair implements UIAction {

    MatchingRepository matchingRepository;
    public PairMatched pairToDisband;
    public DisbandPair(MatchingRepository matchingRepository, PairMatched pairToDisband) {
        this.matchingRepository = matchingRepository;
        this.pairToDisband = pairToDisband;
    }

    @Override
    public void run() {
        System.out.println("now disbanding pair " + pairToDisband.getSoloA().getPerson().id() + " " + pairToDisband.getSoloB().getPerson().id());
        matchingRepository.disbandPair(pairToDisband);
    }

    @Override
    public void undo() {
        Solo soloA = pairToDisband.getSoloA();
        Solo soloB = pairToDisband.getSoloB();

        matchingRepository.soloSuccessors.remove(soloA);
        matchingRepository.soloSuccessors.remove(soloB);

        matchingRepository.getMatchedPairsCollection().add(pairToDisband);
    }

    @Override
    public void redo() {
        run();
    }
}
