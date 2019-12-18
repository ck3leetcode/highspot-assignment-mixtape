package com.highspot.assignment.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.highspot.assignment.service.SyncService;

import java.util.List;

public class CreatePlaylistActionDTO extends ActionDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("song_ids")
    private List<Long> songIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }

    public boolean execute() {
        return SyncService.singleton.createPlaylistForUserByIds(userId, songIds);
    }
}