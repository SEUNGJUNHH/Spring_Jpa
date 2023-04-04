package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class memberController {
    private final MemberService memberService;//컨트롤러가 주로 서비스를 가져와 사용하기 때문에 선언해준다.

    @GetMapping("/members/new")//get은 화면을 열어보는 것이 주된 목적
    public String creatForm(Model model){
    model.addAttribute("memberForm", new MemberForm());//해당 모델 객체를 뷰로 넘겨준다. 여기에 데이터들을 담아온다.
    return "members/createMemberForm";
    }


    @PostMapping("/members/new")//데이터를 등록하는 것이 목적
    public String create(@Valid MemberForm form, BindingResult result){//@Valid는 MemberForm에 있는 어노테이션 notempty같은 어노테이션 특성을 해당 파라미터에서도 사용할 수 있도록 해준다.

        if(result.hasErrors()){
            return "members/createMemberForm";//에러가 뜨면 해당 form으로 돌려준다. 오류페이지가 아닌 해당 페이지에 내가 설정한 오류메세지가 뜬다.ex)이름은 필수입니다.
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.Join(member); //저장해주는 역할
        return "redirect:/";
    }

    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
       model.addAttribute("members",members);//"members"이게 키값이고 해당 키를 부르면 members리스트가 호출된다.
        return "members/memberList"; //loop를 돌며 회원을 모두 출력해준다.
    }
}
