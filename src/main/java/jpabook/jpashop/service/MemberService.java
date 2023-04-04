package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service//Service에 component가 포함되어 있기 때문에 자동적으로 스프링 빈에 등록이 된다.
@Transactional(readOnly = true)//회원조회는 읽는 전용 메서드이며 조회 관련 메서드가 많으니 class단위로 readOnly를 걸어놓고 join의 경우 쓰기 형태의 메서드 임으로 해당 메서드에는 새로 Transactional을 넣어줘 readOnly 옵션을 뺴준다.
@RequiredArgsConstructor//final이 붙은 아규먼트만 생성자를 자동적으로 만들어준다.
public class MemberService {

    //변경 될 일이 없기 때문에 final로 설정하는 것을 권장한다. 컴파일 시점에 한번 더 확인할 수 있음
    private final MemberRepository memberRepository;

    //@Autowired를 생략해도 최신 스프링 기술에는 자동으로 Autowired를 해준다(해당 방식은 생성자 주입 방법)
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }
    
    //회원 가입
    @Transactional(readOnly = false)
    public Long Join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    //중복회원 검증 메서드 직접사용되는 메서드가 아니라 join에 상속되어 있는 메서드 임으로 private로 설정한다.
    //이렇게 메서드로 조건을 걸어도 다른 스레드가 동시에 해당 메서드를 호출하면 회원가입이 둘다 된다.
    //DB에서 이름을 유니크 제약조건으로 설정하는 것이 바람직함
    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    //회원 전체 조회
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }

    public Member findMembers(Long memberId){
        return memberRepository.findById(memberId).get();
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findById(id).get();
        member.setName(name);

    }
}
