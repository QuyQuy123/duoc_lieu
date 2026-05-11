package com.web.controller.user;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController {

    @RequestMapping(value = {"/about"}, method = RequestMethod.GET)
    public String about(Model model) {
        return "user/about";
    }

    @RequestMapping(value = {"/articles"}, method = RequestMethod.GET)
    public String articles(Model model) {
        return "user/articles";
    }

    @RequestMapping(value = {"/confirm"}, method = RequestMethod.GET)
    public String confirm(Model model) {
        return "user/confirm";
    }

    @RequestMapping(value = {"/experts"}, method = RequestMethod.GET)
    public String experts(Model model) {
        return "user/experts";
    }

    @RequestMapping(value = {"/forgot"}, method = RequestMethod.GET)
    public String forgot(Model model) {
        return "user/forgot";
    }

    @RequestMapping(value = {"/","/index"}, method = RequestMethod.GET)
    public String index(Model model) {
        return "user/index";
    }

    @RequestMapping(value = {"/login"}, method = RequestMethod.GET)
    public String login(Model model) {
        return "user/login";
    }

    @RequestMapping(value = {"/reset-password"}, method = RequestMethod.GET)
    public String resetpassword(Model model) {
        return "user/reset-password";
    }

    @RequestMapping(value = {"/my-account"}, method = RequestMethod.GET)
    public String myAccount(Model model) {
        return "user/my-account";
    }

    @RequestMapping(value = {"/plant"}, method = RequestMethod.GET)
    public String plant(Model model) {
        return "user/plant";
    }

    @RequestMapping(value = {"/regis"}, method = RequestMethod.GET)
    public String regis(Model model) {
        return "user/regis";
    }

    @RequestMapping(value = {"/research"}, method = RequestMethod.GET)
    public String research(Model model) {
        return "user/research";
    }

}
