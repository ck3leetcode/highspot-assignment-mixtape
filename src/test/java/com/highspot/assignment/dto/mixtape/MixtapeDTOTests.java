package com.highspot.assignment.dto.mixtape;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highspot.assignment.dto.mixtape.MixtapeDTO;
import junit.framework.TestCase;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class MixtapeDTOTests extends TestCase {

    public void testReadWrite() throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI resource = Objects.requireNonNull(classLoader.getResource("mixtape-data.json")).toURI();

        String contents = new String(Files.readAllBytes(Paths.get(resource)));
        ObjectMapper objectMapper = new ObjectMapper();

        MixtapeDTO mixtapeDTO = objectMapper.readValue(contents, MixtapeDTO.class);
        String actual = objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(mixtapeDTO);
        assertEquals(actual, contents);
    }
}