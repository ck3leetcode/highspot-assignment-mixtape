package com.highspot.assignment.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.highspot.assignment.service.SyncService;

public class AddSongActionDTO extends ActionDTO {

    @JsonProperty("song_id")
    private Long songId;
    @JsonProperty("playlist_id")
    private Long playlistId;

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    @Override
    public boolean execute() {
        return SyncService.singleton.addSongToPlaylistById(songId, playlistId);
    }
}