package com.wy;

import com.wy.model.vo.Hero;
import com.wy.service.HeroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class HeroController {

    Logger logger = LoggerFactory.getLogger(HeroController.class);

    @Autowired
    private HeroService heroService;

    private String separator = ",";

    @RequestMapping("/get_all_hero")
    public List<Map<String, String>> getAllHero() {
        List<Map<String, String>> result = new ArrayList<>();
        List<Hero> list = heroService.getHeros(null);
        for (Hero hero : list) {
            List<String> valueList = new ArrayList<>();
            valueList.add(hero.getName());
            valueList.addAll(heroService.generatePYPossible(hero.getName()));
            String alias = hero.getAlias();
            if (alias != null && !alias.equals("")) {
                valueList.add(hero.getAlias());
                for (String s : alias.split(",")) {
                    valueList.addAll(heroService.generatePYPossible(s));
                }
            }
            Map<String, String> entity = new HashMap<>();
            entity.put("label", hero.getName());
            entity.put("value", String.join(",", valueList));
            result.add(entity);
        }
        return result;
    }

//    public static void main(String[] args) {
//        String s = "娜可露露";
//        List<String> result = heroService.genPYPossible(s);
//
//        System.out.println(String.join("   ", result));
//    }

    @RequestMapping("/get_all_py")
    public List<Map<String, List<String>>> getAllPY() {
        List<Map<String, List<String>>> result = new ArrayList<>();
        List<Hero> list = heroService.getHeros(null);
        for (Hero hero : list) {
            Map<String, List<String>> npy = new HashMap<>();
            String name = hero.getAlias();
            if (name != null && !"".equals(name.trim())) {
                List<String> allPossible = heroService.generatePYPossible(name);
                npy.put(name, allPossible);
                result.add(npy);
            }
        }
        return result;
    }



}
