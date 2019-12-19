package com.highspot.assignment.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.highspot.assignment.service.SyncService;

public class RemovePlaylistActionDTO extends ActionDTO {
    @JsonProperty("playlist_id")
    private Long playlistId;

    public Long getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public boolean execute() {
        return SyncService.singleton.removePlaylistById(playlistId);
    }
}
