package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepositoryOld;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)//junit과 스프링을 엮어서 테스트를 진행하기 위한 어노테이션
@SpringBootTest//스프링 컨테이너 안에서 돌리기 위한 어노테이션 - 이게 없으면 오토 와이어 다 실패
@Transactional//커밋하기 전에 롤백을 시켜 쿼리가 안날라간다.
public class MemberServiceTest {
    @Autowired MemberService memberService;
    @Autowired
    MemberRepositoryOld memberRepository;
    @Autowired EntityManager em;
    @Test
    public void 회원가입() throws Exception {

    //Given
        Member member = new Member();
        member.setName("한승준");
    //When
        //persist된다고 해서 인서트 쿼리가 DB로 나가는게 아니라 commit될때 인서트 쿼리가 나가게 된다.
        Long saveId = memberService.Join(member);
    //Then
        em.flush();//영속성 컨텍스트에 있는 변경이나 등록 내용을 DB에 반영하는 매서드 DB에 강제로 쿼리를 날림 그 후 Transactional이 롤백시켜서 기록에는 안남음(인서트도 롤백됌)

        //같은 영속성 콘텍스트를 사용해서 true가 나온다.
        assertEquals(member, memberRepository.fineOne(saveId));
    }
    @Test(expected = IllegalStateException.class)//IllegalStateException이면 테스트 성공
    public void 중복_회원_예외() throws Exception {

//Given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
//When
        memberService.Join(member1);
        memberService.Join(member2);
//코드가 더러움
//        try{
//            memberService.Join(member2);
//        }catch(IllegalStateException e){
//            return;
//        }

//Then
     fail("예외가 발생하지않았다");
    }

}