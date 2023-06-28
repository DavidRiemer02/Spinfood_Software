package org.example.data.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.data.factory.Kitchen;
import org.example.data.structures.Solo;
import org.example.logic.structures.GroupMatched;
import org.example.logic.structures.MatchingRepository;
import org.example.logic.structures.PairMatched;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Serializer {
    private static final SerializationFeature feature = SerializationFeature.INDENT_OUTPUT;

    public static void writeToFile(String data, String filepath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(data);
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(filepath), jsonNode);
    }

    public static String serializeKitchen(Kitchen kitchen) throws JsonProcessingException {
        KitchenWrapper kitchenWrapper = new KitchenWrapper(kitchen);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(kitchenWrapper);
    }

    public static String serializeSolo(Solo solo) throws JsonProcessingException {
        SoloWrapper soloWrapper = new SoloWrapper(solo);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(soloWrapper);
    }

    public static String serializeSoloList(List<Solo> solos) throws JsonProcessingException {
        List<SoloWrapper> soloWrappers = solos.stream()
                .map(SoloWrapper::new)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(soloWrappers);
    }

    public static String serializePair(PairMatched pairMatched) throws JsonProcessingException {
        PairWrapper pairWrapper = new PairWrapper(pairMatched);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(pairWrapper);
    }

    public static String serializePairList(List<PairMatched> pairs) throws JsonProcessingException {
        List<PairWrapper> pairWrappers = pairs.stream()
                .map(PairWrapper::new)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(pairWrappers);
    }

    public static String serializeGroup(GroupMatched groupMatched) throws JsonProcessingException {
        GroupWrapper groupWrapper = new GroupWrapper(groupMatched);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(groupWrapper);
    }

    public static String serializeGroupList(List<GroupMatched> groups) throws JsonProcessingException {
        List<GroupWrapper> groupWrappers = groups.stream()
                .map(GroupWrapper::new)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(groupWrappers);
    }

    public static String serializeMatchingRepository(MatchingRepository repository) throws JsonProcessingException {
        MatchingRepositoryWrapper repositoryWrapper = new MatchingRepositoryWrapper(repository);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(feature);
        return mapper.writeValueAsString(repositoryWrapper);
    }
}
