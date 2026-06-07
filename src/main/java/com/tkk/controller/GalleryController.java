package com.tkk.controller;

import com.tkk.entity.Category;
import com.tkk.entity.Gallery;
import com.tkk.service.GalleryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService){
        this.galleryService = galleryService;
    }


    @GetMapping("/gallery_upload")
    public String gallery(Model model){
        model.addAttribute("activity", Category.values());
        model.addAttribute("gallery", new Gallery());
        return "gallery_upload";
    }

    @PostMapping("/gallery_upload")
    public String galleryUpload(@ModelAttribute("gallery") @Valid Gallery gallery,BindingResult bindingResult,@RequestParam("file") MultipartFile file, Model model) throws IOException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", "Please resolve the Issue.");
            return "gallery_upload";
        }

        galleryService.uploadFile(gallery.getCategory(), gallery.getMetaData(), gallery.getDate(), file);


        //Todo check this after not showing success at frontend.
        model.addAttribute("successMessage", "Upload completed successfully.");

        return "redirect:/gallery_upload?success=true";
    }

    @GetMapping("/gallery/{category}")
    public String result(@PathVariable String category, Model model){
        model.addAttribute("gallery", galleryService.getObjectsBy(category));
        return "gallery";
    }


    /*    @ResponseBody
    @GetMapping("/random")
    public List<Gallery> random(){
        Pageable pageable = PageRequest.of(0,5);
        return galleryService.getCategoryObjects(String.valueOf(Category.EVENTS), 7);
    }*/

}