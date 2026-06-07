package com.tkk.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter

public class BibleIndex {
    public long bookId;

    public String bookName;

    public String totalChapters;

}
