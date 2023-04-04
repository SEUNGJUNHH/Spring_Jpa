package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//pk타입 Long을 넣어준다.
public interface MemberRepository extends JpaRepository<Member, Long> {
   //자동으로 쿼리를 만들어서줘서 메소드 구현을 하지 않아도 정상적으로 작동한다.
    List<Member> findByName(String name);
}
