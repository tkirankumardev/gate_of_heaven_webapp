package com.tkk.controller;

import com.tkk.entity.Category;
import com.tkk.service.GalleryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {

    private final GalleryService galleryService;

    public MainController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }


    @GetMapping({"/", "/index", "/home"})
    public String home(Model model){
        model.addAttribute("catList", Category.values());
        model.addAttribute("events", galleryService.getCategoryObjects(String.valueOf(Category.EVENTS), 7));
        model.addAttribute("charity", galleryService.getCategoryObjects(String.valueOf(Category.CHARITY), 7));
        model.addAttribute("preachings", galleryService.getCategoryObjects(String.valueOf(Category.PREACHINGS), 7));
        return "index";
    }

    @GetMapping("/terms")
    public String terms(){
        return "terms-policy";
    }

}
