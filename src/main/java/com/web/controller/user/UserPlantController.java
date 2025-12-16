package com.web.controller.user;

import com.web.entity.Plant;
import com.web.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UserPlantController {

    @Autowired
    private PlantService plantService;

    @RequestMapping(value = {"/plant-detail/{slug}"}, method = RequestMethod.GET)
    public String plantDetail(Model model, @PathVariable String slug) {
        Plant plant = plantService.findBySlug(slug);
        if(plant == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "plant not found");
        }
        model.addAttribute("plant", plant);
        return "user/plant-detail.html";
    }
}
