package com.highspot.assignment.dto.action;

import com.highspot.assignment.dto.action.ActionListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.TestCase;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ActionListDTOTests extends TestCase {

    public void testReadWrite() throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI resource = Objects.requireNonNull(classLoader.getResource("changes_example.json")).toURI();

        String contents = new String(Files.readAllBytes(Paths.get(resource)));
        ObjectMapper objectMapper = new ObjectMapper();

        ActionListDTO action = objectMapper.readValue(contents, ActionListDTO.class);
        String actual = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(action);
        assertEquals(actual, contents);
    }
}