package com.highspot.assignment.dto.mixtape;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class PlaylistDTO {

    private Long id;
    private Long userId;
    private List<Long> songIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("user_id")
    public Long getUserId() {
        return userId;
    }

    @JsonProperty("user_id")
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @JsonProperty("song_ids")
    public List<Long> getSongIds() {
        return songIds;
    }

    @JsonProperty("song_ids")
    public void setSongIds(List<Long> songIds) {
        this.songIds = songIds;
    }
}