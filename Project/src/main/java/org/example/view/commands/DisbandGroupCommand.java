package org.example.view.commands;

import org.example.logic.structures.GroupMatched;
import org.example.logic.structures.MatchingRepository;
import org.example.logic.structures.PairMatched;
import org.example.view.UIAction;

import java.util.List;

public class DisbandGroupCommand implements UIAction {

    private final MatchingRepository matchingRepository;
    private final GroupMatched groupMatched;

    public DisbandGroupCommand(MatchingRepository matchingRepository, GroupMatched groupMatched) {
        this.matchingRepository = matchingRepository;
        this.groupMatched = groupMatched;
    }

    @Override
    public void run() {
        matchingRepository.disbandGroup(groupMatched);
    }

    @Override
    public void undo() {
        List<PairMatched> pairs = groupMatched.getPairList();
        pairs.forEach(matchingRepository.pairSuccessors::remove);
        matchingRepository.getMatchedGroupsCollection().add(groupMatched);
    }

    @Override
    public void redo() {
        run();
    }
}
