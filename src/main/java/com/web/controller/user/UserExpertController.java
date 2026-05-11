package com.web.controller.user;

import com.web.entity.Article;
import com.web.entity.Expert;
import com.web.repository.ExpertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class UserExpertController {

    @Autowired
    private ExpertRepository expertRepository;

    @RequestMapping(value = {"/expert-detail"}, method = RequestMethod.GET)
    public String expertDetail(Model model, @RequestParam Long id) {
        Optional<Expert> expert = expertRepository.findById(id);
        if(expert.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "expert not found");
        }
        model.addAttribute("expert", expert.get());
        return "user/expert-detail";
    }

}
