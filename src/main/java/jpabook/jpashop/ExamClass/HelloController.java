package jpabook.jpashop.ExamClass;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model){//컨트롤러가 model에 값을 넣어다가 view에 넘겨줄 수 있다.
        model.addAttribute("data","hello!!");//model에 값을 넣어준다.네임에 값을 정해줘서 넘겨준다.
        return "hello";//화면이름을 의 .html은 자동적으로 붙음
    }
}
