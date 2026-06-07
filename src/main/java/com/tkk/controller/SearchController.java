package com.tkk.controller;

import com.tkk.service.SongService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final SongService songService;

    public SearchController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/search-song")
    public String search(@RequestParam("search") String term, Model model){
        System.out.println(term);
        System.out.println(songService.search(term));
        model.addAttribute("results",songService.search(term));
        model.addAttribute("last_uploads", songService.lastUploads(7));
        return  "results-page";
    }

}
