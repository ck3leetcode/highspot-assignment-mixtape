package com.highspot.assignment.dto.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AddSongActionDTO.class, name = ActionDTO.addSong),
        @JsonSubTypes.Type(value = CreatePlaylistActionDTO.class, name = ActionDTO.createPlaylist),
        @JsonSubTypes.Type(value = RemovePlaylistActionDTO.class, name = ActionDTO.removePlaylist)
})
public abstract class ActionDTO {
    static final String addSong = "add_song";
    static final String removePlaylist = "remove_playlist";
    static final String createPlaylist = "create_playlist";

    public abstract boolean execute();
}