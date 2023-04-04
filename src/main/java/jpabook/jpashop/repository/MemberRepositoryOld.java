package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository//컴포넌트 스캔을 통해 자동으로 스프링 빈으로 등록이 된다.
@RequiredArgsConstructor//lombok이 지원해주는 어노테이션이다
public class MemberRepositoryOld {

    //PersistenceContext
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);//persist가 DB에 저장한다는 의미를 담고있음 comit이 되는 시점에 insert쿼리가 날라간다.
    }

    //단건 조회
    public Member fineOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        //sql은 테이블을 대상으로 쿼리를 짜는데 jpql은 객체를 대상으로 쿼리를 짠다.
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}