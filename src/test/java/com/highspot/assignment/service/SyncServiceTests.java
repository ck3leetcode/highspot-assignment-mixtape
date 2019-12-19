package com.highspot.assignment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highspot.assignment.dto.mixtape.MixtapeDTO;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class SyncServiceTests extends TestCase {

    private Long existID = 1l;
    private Long nonExistID = 0l;

    private MixtapeDTO readFromResource() throws IOException, URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URI resource = Objects.requireNonNull(classLoader.getResource("mixtape-data.json")).toURI();

        String contents = new String(Files.readAllBytes(Paths.get(resource)));
        ObjectMapper objectMapper = new ObjectMapper();

        MixtapeDTO mixtapeDTO = objectMapper.readValue(contents, MixtapeDTO.class);
        return mixtapeDTO;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SyncService.singleton.load(readFromResource());
    }

    public void test_addSongToPlaylistById_succeed() {
        boolean actual = SyncService.singleton.addSongToPlaylistById(existID, existID);
        boolean expected = true;
        assertEquals(actual, expected);
    }

    public void test_addSongToPlaylistById_when_song_does_not_exist() {
        boolean actual = SyncService.singleton.addSongToPlaylistById(nonExistID, existID);
        boolean expected = false;
        assertEquals(actual, expected);
    }

    public void test_addSongToPlaylistById_when_playlist_does_not_exist() {
        boolean actual = SyncService.singleton.addSongToPlaylistById(existID, nonExistID);
        boolean expected = false;
        assertEquals(actual, expected);
    }


    public void test_createPlaylistForUserByIds() {
        boolean actual = SyncService.singleton.createPlaylistForUserByIds(existID, Arrays.asList(existID));
        boolean expected = true;
        assertEquals(actual, expected);
    }

    public void test_createPlaylistForUserByIds_when_songs_are_empty() {
        boolean actual = SyncService.singleton.createPlaylistForUserByIds(existID, Arrays.asList());
        boolean expected = false;
        assertEquals(actual, expected);
    }

    public void test_createPlaylistForUserByIds_when_user_does_not_exist() {
        boolean actual = SyncService.singleton.createPlaylistForUserByIds(nonExistID, Arrays.asList(existID));
        boolean expected = false;
        assertEquals(actual, expected);
    }

    public void test_createPlaylistForUserByIds_when_song_does_not_exist() {
        boolean actual = SyncService.singleton.createPlaylistForUserByIds(existID, Arrays.asList(nonExistID));
        boolean expected = false;
        assertEquals(actual, expected);
    }

    public void test_removePlaylistById() {
        boolean actual = SyncService.singleton.removePlaylistById(existID);
        boolean expected = true;
        assertEquals(actual, expected);
    }

    public void test_removePlaylistById_when_playlist_does_not_exist() {
        boolean actual = SyncService.singleton.removePlaylistById(nonExistID);
        boolean expected = false;
        assertEquals(actual, expected);
    }
}
