package com.tkk.controller;

import com.tkk.dto.VerseIndex;
import com.tkk.service.BibleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BibleController {

    private final BibleService bibleService;

    public BibleController(BibleService bibleService) {
        this.bibleService = bibleService;
    }



    // Map this to your main page URL. You can change "/bible" to "/bible-modal" if you prefer.
    @GetMapping("/bible")
    public String biblePage(
            @RequestParam(value = "bookId", required = false) Integer bookId,
            @RequestParam(value = "chapter", required = false) Integer chapterId,
            @RequestParam(value = "verseFrom", required = false) Integer verseFrom,
            @RequestParam(value = "verseTo", required = false) Integer verseTo,
            Model model) {

        // 1. ALWAYS add the books list so the dropdown works, whether it's a fresh load or a search result.
        model.addAttribute("data", bibleService.bibleIndex());

        // 2. Check if the user submitted the form (bookId and chapter are present)
        if (bookId != null && chapterId != null) {
            List<VerseIndex> verses;

            if (verseFrom == null && verseTo == null) {
                // Both are null -> Get whole chapter
                verses = bibleService.getVerses(bookId, chapterId);

            } else if (verseFrom != null && verseTo == null) {
                // Only 'from' is defined -> Get a single verse
                verses = bibleService.getCertainVerse(bookId, chapterId, verseFrom);

            } else if (verseFrom != null && verseTo != null) {
                // Both are defined -> Get range of verses
                verses = bibleService.getCertainVerses(bookId, chapterId, verseFrom, verseTo);

            } else {
                // Fallback just in case only 'to' is defined for some reason
                verses = bibleService.getVerses(bookId, chapterId);
            }

            // 3. Add the fetched verses to the model so Thymeleaf can render them
            model.addAttribute("verses", verses);
        }

        // 4. Return the EXACT SAME template name for both scenarios
        return "bible-modal";
    }
}