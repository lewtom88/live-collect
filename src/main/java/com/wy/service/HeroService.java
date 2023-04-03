package com.wy.service;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.wy.mapper.HeroMapper;
import com.wy.model.vo.Hero;
import com.wy.model.vo.HeroRole;
import com.wy.model.vo.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HeroService {

    Logger logger = LoggerFactory.getLogger(HeroService.class);

    @Autowired
    private HeroMapper heroMapper;

    private List<Hero> heros;

    private List<Hero> heroList;

    private Map<String, List<Hero>> keyHeroMap;

    private Map<String, Hero> pyHeroMap;

    private Map<String, Hero> aliasHeroMap;

    private static Map<Character, String[]> uniquePY = new HashMap<>();

    static {
        uniquePY.put('阿', new String[] {"a"});
        uniquePY.put('大', new String[] {"da"});
        uniquePY.put('信', new String[] {"xin"});
        uniquePY.put('女', new String[] {"nv"});
        uniquePY.put('孙', new String[] {"sun"});
        uniquePY.put('扁', new String[] {"bian"});
        uniquePY.put('露', new String[] {"lu"});
        uniquePY.put('娜', new String[] {"na"});
        uniquePY.put('思', new String[] {"si"});
        uniquePY.put('艾', new String[] {"ai"});
        uniquePY.put('莫', new String[] {"mo"});
        uniquePY.put('奇', new String[] {"qi"});
        uniquePY.put('摩', new String[] {"mo"});
        uniquePY.put('可', new String[] {"ke"});
        uniquePY.put('约', new String[] {"yue"});
        uniquePY.put('伽', new String[] {"jia"});
        uniquePY.put('奥', new String[] {"ao"});
        uniquePY.put('沈', new String[] {"shen"});
        uniquePY.put('哪', new String[] {"na", "ne"});
        uniquePY.put('邪', new String[] {"xie", "ye"});
        uniquePY.put('不', new String[] {"bu"});
        uniquePY.put('弹', new String[] {"dan"});
//        uniquePY.put("", "");
//        uniquePY.put("", "");
    }

    /**
     * 三层过滤
     * 1. 官方名字/别名 匹配
     * 2. 带音标的拼音匹配
     * 3. 不带拼音的匹配
     *
     */
    private Map<String, String> allMatchNameMap = new HashMap<>();

    private Map<String, String> tonePYNameMap = new HashMap<>();

    @PostConstruct
    private void initialize() {
        Map<String, List<String>> heroRoleMap = new HashMap<>();
        heroList = heroMapper.findAllHeros();

        Map<String, String> heroAlias = heroList.stream()
                                                .filter(h -> h.getAlias() != null)
                                                .collect(Collectors.toMap(Hero::getName, Hero::getAlias));

        Map<Integer, String> heroMap = heroList.stream()
                    .collect(Collectors.toMap(Hero::getId, Hero::getName));

        Map<Integer, String> roleMap  = heroMapper.findAllRoles().stream()
                    .collect(Collectors.toMap(Role::getId, Role::getRoleName));

        List<HeroRole> heroRoleList = heroMapper.findAllHeroRoles();
        for (HeroRole heroRole : heroRoleList) {
            String heroName = heroMap.get(heroRole.getHeroId());
            List<String> roles = heroRoleMap.getOrDefault(heroName, new ArrayList<>());
            heroRoleMap.put(heroName, roles);
            String role = roleMap.get(heroRole.getRoleId());
            roles.add(role);
        }

        heros = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : heroRoleMap.entrySet()) {
            Hero hero = new Hero();
            hero.setName(entry.getKey());
            hero.setAlias(heroAlias.get(entry.getKey()));
            hero.setRoles(String.join("/", entry.getValue()));
            heros.add(hero);
        }

        logger.info("Initializing map for matching hero.");
        for (Hero hero : heroList) {
            String name = hero.getName();
            logger.debug("Init Hero Map 1 {} -> {}", name, name);
            allMatchNameMap.put(name, name);
            initPYMapping(name, name);
            if (hero.getAlias() != null) {
                String[] aliasArray = hero.getAlias().split(",");
                for (String alias : aliasArray) {
                    logger.debug("Init Hero Map 2 {} -> {}", alias, name);
                    allMatchNameMap.put(alias, name);
                    initPYMapping(alias, name);
                }
            }
        }
    }

    private void initPYMapping(String key, String name) {
        List<String> pyNames = generatePYPossible(key);
        for (String pyName : pyNames) {
            if (allMatchNameMap.containsKey(pyName)) {
                String samePYName = allMatchNameMap.get(pyName);
                allMatchNameMap.remove(pyName);
                List<String> nameTones = generatePYPossible(key, true);
                List<String> samePYTones = generatePYPossible(samePYName, true);
                for (String nameTone : nameTones) {
                    if (tonePYNameMap.containsKey(nameTone)) {
                        throw new RuntimeException("两个英雄的发音完全相同，无法处理。" + key);
                    }
                    logger.warn("Init Hero PYMap {} -> {}", nameTone, name);
                    tonePYNameMap.put(nameTone, name);

                }
                for (String samePYTone : samePYTones) {
                    if (tonePYNameMap.containsKey(samePYTone)) {
                        throw new RuntimeException("两个英雄的发音完全相同，无法处理。" + key);
                    }
                    logger.warn("Init Hero PYMap {} -> {}", samePYTone, samePYName);
                    tonePYNameMap.put(samePYTone, samePYName);
                }
            } else {
                logger.debug("Init Hero Map 3 {} -> {}", pyName, name);
                allMatchNameMap.put(pyName, name);
            }
        }
    }

    public String matchHero(String keyword) {
        //search by name
        String result = allMatchNameMap.get(keyword);
        if (result != null) {
            return result;
        }

        //search by PY
        List<String> pyNames = generatePYPossible(keyword);
        for (String pyName : pyNames) {
            result = allMatchNameMap.get(pyName);
            if (result != null) {
                return  result;
            }
        }

        //search by PY with tone
        List<String> pyWithTone = generatePYPossible(keyword, true);
        for (String pyTone : pyWithTone) {
            result = tonePYNameMap.get(pyTone);
            if (result != null) {
                return  result;
            }
        }

        return null;
    }

    public synchronized List<Hero> getHeros(String keyword) {
        if (keyword == null || "".equals(keyword.trim())) {
            return heros;
        }

        keyword = keyword.trim();

        if (keyHeroMap == null) {
            keyHeroMap = new HashMap<>();
            for (Hero hero : heros) {
                keyHeroMap.put(hero.getName(), Arrays.asList(hero));
                String alias = hero.getAlias();
                if (alias != null) {
                    keyHeroMap.put(alias, Arrays.asList(hero));
                }
                String[] roles = hero.getRoles().split("/");
                for (String role : roles) {
                    List<Hero> list = keyHeroMap.getOrDefault(role, new ArrayList<>());
                    keyHeroMap.put(role, list);
                    list.add(hero);
                }
            }
        }

        return keyHeroMap.getOrDefault(keyword, new ArrayList<>());
    }

    public static void main1(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("/Users/Tom/Documents/softwares/live-collect/target/classes/db_scripts/init.sql"));
        String s = reader.readLine();
        while (s != null) {
            if (s.indexOf("t_live_hero (ID") > 0) {
                String[] ss = s.split("'");
                String name_py = PinyinHelper.convertToPinyinString(ss[1], "", PinyinFormat.WITHOUT_TONE);
                String alias_py = PinyinHelper.convertToPinyinString(ss[3], "", PinyinFormat.WITHOUT_TONE);
                String sql = ss[0] + "'" + ss[1] + "'" + ss[2] + "'" + ss[3] + "', '" + name_py + "', '" + alias_py + "'" + ss[4];
                System.out.println(sql);
            }

            s = reader.readLine();;
        }

    }

    public static void main(String[] args) {
        HeroService service = new HeroService();
        String py1 = service.generatePYPossible("瑶", true).get(0);
        String py2 = service.generatePYPossible("曜", true).get(0);
        System.out.println(py1);
        System.out.println(py2);
        System.out.println(py1.equals(py2));
    }

    public List<String> generatePYPossible(String s) {
        return generatePYPossible(s, false);
    }

    public List<String> generatePYPossible(String s, boolean withTone) {
        char[] sc = s.toCharArray();
        List<String> result = new ArrayList<>();
        for (char ss : sc) {
            String[] strings = getPossiblePY(ss, withTone);
            if (result.isEmpty()) {
                for (String string : strings) {
                    result.add(string);
                }
            } else {
                List<String> nr = new ArrayList<>();
                for (String string : strings) {
                    for (String rr : result) {
                        nr.add(rr + string);
                    }
                }
                result = nr;
            }

        }
        return result;
    }

    private String[] getPossiblePY(char c, boolean withTone) {
        String[] possible = uniquePY.get(c);
        if (possible != null) {
            return possible;
        }
        if (withTone) {
            return PinyinHelper.convertToPinyinArray(c);
        } else {
            return PinyinHelper.convertToPinyinArray(c, PinyinFormat.WITHOUT_TONE);
        }
    }
}
