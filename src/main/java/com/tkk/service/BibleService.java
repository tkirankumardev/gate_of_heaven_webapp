package com.tkk.service;

import com.tkk.dto.BibleIndex;
import com.tkk.dto.VerseIndex;
import com.tkk.repo.BibleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BibleService {

    private final BibleRepository bibleRepository;

    public BibleService(BibleRepository bibleRepository){
        this.bibleRepository = bibleRepository;
    }

    public List<BibleIndex> bibleIndex(){
        return bibleRepository.getIndex();
    }

    public List<VerseIndex> getVerses(int bookId, int chapterId){
        return bibleRepository.getVerses(bookId, chapterId);
    }

    public List<VerseIndex> getCertainVerse(int bookId, int chapterId, int verseNumber){
        return bibleRepository.getCertainVerse(bookId, chapterId, verseNumber);
    }
    public List<VerseIndex> getCertainVerses(int bookId, int chapterId, int from, int to){
        return bibleRepository.getCertainVerses(bookId, chapterId, from, to);
    }

}
