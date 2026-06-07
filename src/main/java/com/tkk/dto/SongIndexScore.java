package com.tkk.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class SongIndexScore {

    public Long id;

    public String title;

    public String transliteration;

    public Float score;

}
