package com.tkk.controller;

import com.tkk.entity.Song;
import com.tkk.repo.SongRepository;
import com.tkk.service.SongService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
public class SongController {

    private final SongService songService;
    private final SongRepository songRepository;

    public SongController(SongService songService, SongRepository songRepository) {
        this.songService = songService;
        this.songRepository = songRepository;
    }

    @GetMapping({"/lyrics", "/list", "/songs-list", "/songs"})
    public String index(Model model){
        model.addAttribute("list", songService.getTitles());
        model.addAttribute("list2", songService.getTransliterations());
        return "song-index";
    }

    @GetMapping("/song/{id}")
    public String song(@PathVariable long id, Model model){
        if(!songRepository.existsById(id)){
            throw new EntityNotFoundException("The song lyrics which is begin requested doesn't exists in database.");
        }
        model.addAttribute("song", songService.songById(id));
        model.addAttribute("last_uploads", songService.lastUploads(7));
        return "song";
    }

    @GetMapping({"/post", "/upload_song", "upload_lyrics"})
    public String post(Model model){
        model.addAttribute("song", new Song());
        return "post";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("song") Song song, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "post";
        }
        String resultMessage = songService.saveSong(song);
        if (resultMessage.equals("error")) {
            // Updated error message to reflect the title-only check
            model.addAttribute("errorMessage", "A song with this Telugu title already exists in the database!");
            return "post";
        }
        return "redirect:/post?success";
    }

    @GetMapping({"/remove/{id}","/delete/{id}"})
    public String remove(@PathVariable long id){
        if(!songRepository.existsById(id)){
            throw new EntityNotFoundException("The song with this id doesn't exits, may be removed or deleted by admin.");
        }
        songService.remove(id);
        return "redirect:/songs-list";
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public String handler(EntityNotFoundException e, Model model){
        model.addAttribute("message", e.getMessage());
        model.addAttribute("code", HttpStatus.NOT_FOUND);
        return "error-page";
    }
}
