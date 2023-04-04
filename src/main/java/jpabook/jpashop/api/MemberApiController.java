package jpabook.jpashop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
 * 문제점
 * - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
 * - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
 * - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를
 위한 모든 요청 요구사항을 담기는 어렵다.
 * - 엔티티가 변경되면 API 스펙이 변한다.
 * 결론
 * - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다. */

@RequiredArgsConstructor
@RestController //ResponseBody + controller가 포함되어 있는 어노테이션이다.
public class MemberApiController {

    private final MemberService memberService;

    //api를 개발할때는 엔티티를 파람으로 받아서도 외부에 노출하는 것도 부적절하다.
    //(@RequestBody는 json으로 온 데이터를 Member에 넣어준다
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){

        Long id = memberService.Join(member);
        return new CreateMemberResponse(id);
    }
    //조회
    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        List<Member> members = memberService.findMembers();
        return members;
    }

    //엔티티를 절대 직접 밖으로 노출시키지 말것 반드시 dto로 변환시켜 반환할 것
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        List<Member> findmembers = memberService.findMembers();
        List<MemberDto> collect = findmembers.stream().map(m -> new MemberDto(m.getName())).collect(Collectors.toList());
        return new Result(collect.size(), collect);


    }
    //배열반환이 아니라 데이터 타입으로 반환하여 유연성을 늘릴 수 있다./ 원하는 변수만 내보낼 수 있다.
    @Data
    @AllArgsConstructor
    static class Result<T>{
        private int count;
        private T data;
    }


    @Data
    @AllArgsConstructor
    static class MemberDto{
        private String name;
    }

    //유지보수에 편리한 장점이 있음, 엔티티와 api스팩 분리를 할 수 있다.
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){

          Member member = new Member();
          member.setName(request.getName());
        Long join = memberService.Join(member);
        return new CreateMemberResponse(join);
    }

    //수정
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(@PathVariable("id") Long id, @RequestBody @Valid UpdateMemberRequest request){

        memberService.update(id, request.getName());
        Member members = memberService.findMembers(id);
        return new UpdateMemberResponse(members.getId(),members.getName());
    }


    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse{

        private Long id;
        private String name;
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }


    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberResponse {
        private Long id;
        public CreateMemberResponse(Long id){
            this.id =id;
        }
    }
}
