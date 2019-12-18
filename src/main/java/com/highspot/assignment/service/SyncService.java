package com.highspot.assignment.service;

import com.highspot.assignment.dto.action.ActionDTO;
import com.highspot.assignment.dto.action.ActionListDTO;
import com.highspot.assignment.dto.mixtape.MixtapeDTO;
import com.highspot.assignment.dto.mixtape.PlaylistDTO;
import com.highspot.assignment.dto.mixtape.SongDTO;
import com.highspot.assignment.dto.mixtape.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SyncService {

    public static final SyncService singleton = new SyncService();

    private static Logger logger = LoggerFactory.getLogger(SyncService.class);

    private Map<Long, UserDTO> usersLookup;
    private Map<Long, SongDTO> songsLookup;
    private Map<Long, PlaylistDTO> playlistsLookup;
    private long nextPlaylistId = 0;

    private SyncService() {
        this.usersLookup = new HashMap<>();
        this.songsLookup = new HashMap<>();
        this.playlistsLookup = new HashMap<>();
        this.nextPlaylistId = 0;
    }

    /**
     * Apply list of action (changes)
     *
     * @param mixtapeDTO
     * @param actionListDTO
     * @return
     */
    public static MixtapeDTO process(MixtapeDTO mixtapeDTO, ActionListDTO actionListDTO) {
        singleton.load(mixtapeDTO);
        for (ActionDTO action : actionListDTO.getActions()) {
            if (!action.execute()) {
                logger.error("process failed.");
                throw new IllegalStateException();
            };
        }
        return singleton.generate();
    }

    /**
     * load the mixtape to the sync service
     *
     * @param mixtapeDTO
     */
    public void load(MixtapeDTO mixtapeDTO) {
        List<UserDTO> users = mixtapeDTO.getUsers();
        List<SongDTO> songs = mixtapeDTO.getSongs();
        List<PlaylistDTO> playlists = mixtapeDTO.getPlaylists();

        this.usersLookup = users.stream()
                .collect(Collectors.toMap(UserDTO::getId, Function.identity()));
        this.songsLookup = songs.stream()
                .collect(Collectors.toMap(SongDTO::getId, Function.identity()));
        this.playlistsLookup = playlists.stream()
                .collect(Collectors.toMap(PlaylistDTO::getId, Function.identity()));
        this.nextPlaylistId = 1L + (playlists.isEmpty() ? 0 :
                playlists.stream().max(Comparator.comparingLong(PlaylistDTO::getId)).get().getId());
    }

    /**
     * Add a new playlist for an existing user; the playlist should contain at least one existing song.
     *
     * @param userId
     * @param songIds
     * @return
     */
    public boolean createPlaylistForUserByIds(Long userId, List<Long> songIds) {
        logger.info("Create playlist for user", userId);
        if (!usersLookup.containsKey(userId)) {
            logger.error("Cannot create playlist for user. Reason: user does not exist", userId);
            return false;
        }

        if (songIds.isEmpty()) {
            logger.error("Cannot create playlist for user. Reason: songs cannot be empty");
            return false;
        }

        for (Long songId : songIds) {
            if (!songsLookup.containsKey(songId)) {
                logger.error("Cannot create playlist for user. Reason: song does not exist", songId);
                return false;
            }
        }

        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(this.nextPlaylistId++);
        dto.setUserId(userId);
        dto.setSongIds(songIds);
        playlistsLookup.put(dto.getId(), dto);
        return true;
    }

    /**
     * Add an existing song to an existing playlist.
     *
     * @param songId
     * @param playlistId
     * @return
     */
    public boolean addSongToPlaylistById(Long songId, Long playlistId) {
        logger.info("Add song {} to playlist {}", songId, playlistId);
        if (!songsLookup.containsKey(songId)) {
            logger.error("Cannot add song: {} to playlist: {}. Reason: Song does not exist", songId, playlistId);
            return false;
        }

        if (!playlistsLookup.containsKey(playlistId)) {
            logger.error("Cannot add song: {} to playlist: {}. Reason: Playlist does not exist", songId, playlistId);
            return false;
        }

        return playlistsLookup.get(playlistId).getSongIds().add(songId);
    }


    /**
     * Remove an existing playlist.
     *
     * @param playlistId
     * @return
     */
    public boolean removePlaylistById(Long playlistId) {
        logger.info("Remove playlist {}", playlistId);
        if (!playlistsLookup.containsKey(playlistId)) {
           logger.error("Cannot remove playlist: {}. Reason: Playlist does not exist", playlistId);
           return false;
        }

        playlistsLookup.remove(playlistId);
        return true;
    }

    /**
     *
     * @return
     */
    public MixtapeDTO generate() {
        MixtapeDTO mixtapeDTO = new MixtapeDTO();
        mixtapeDTO.setUsers(new ArrayList<>(usersLookup.values()));
        mixtapeDTO.setSongs(new ArrayList<>(songsLookup.values()));
        mixtapeDTO.setPlaylists(new ArrayList<>(playlistsLookup.values()));
        return mixtapeDTO;
    }
}
