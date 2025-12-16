package com.web.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @RequestMapping(value = {"/create-article"}, method = RequestMethod.GET)
    public String createArticle() {
        return "admin/create-article.html";
    }

    @RequestMapping(value = {"/create-plant"}, method = RequestMethod.GET)
    public String createPlant() {
        return "admin/create-plant.html";
    }

    @RequestMapping(value = {"/create-user"}, method = RequestMethod.GET)
    public String createUser() {
        return "admin/create-user.html";
    }

    @RequestMapping(value = {"/create-research"}, method = RequestMethod.GET)
    public String createResearch() {
        return "admin/create-research.html";
    }

    @RequestMapping(value = {"/create-expert"}, method = RequestMethod.GET)
    public String createExpert() {
        return "admin/create-expert.html";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index() {
        return "admin/index.html";
    }

    @RequestMapping(value = {"/list-article"}, method = RequestMethod.GET)
    public String listArticle() {
        return "admin/list-article.html";
    }

    @RequestMapping(value = {"/list-diseases"}, method = RequestMethod.GET)
    public String listDiseases() {
        return "admin/list-diseases.html";
    }

    @RequestMapping(value = {"/list-families"}, method = RequestMethod.GET)
    public String listFamilies() {
        return "admin/list-families.html";
    }

    @RequestMapping(value = {"/list-plant"}, method = RequestMethod.GET)
    public String listPlant() {
        return "admin/list-plant.html";
    }

    @RequestMapping(value = {"/list-user"}, method = RequestMethod.GET)
    public String listUser() {
        return "admin/list-user.html";
    }

    @RequestMapping(value = {"/list-research"}, method = RequestMethod.GET)
    public String listResearch() {
        return "admin/list-research.html";
    }

    @RequestMapping(value = {"/list-expert"}, method = RequestMethod.GET)
    public String listExpert() {
        return "admin/list-expert.html";
    }
}
