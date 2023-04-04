package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j //slf4j 로그를 의미함. 여러가지 로그 타입 중 하나임.
public class HomeController {

    @RequestMapping("/")//첫번째 화면이 잡힌다. 기본 화면 매핑을 의미함
    public String Home(){
        log.info("home controller");//Slf4j에 포함되어 있는 메서드로 이 메서드가 실행되는 로그를 뽑아준다.
        return "home";
    }
}
