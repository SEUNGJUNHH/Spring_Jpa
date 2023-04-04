package jpabook.jpashop.ExamClass;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository //컴포넌트 스캔을 포함하고 있는 어노테이션이다. 자동으로 빈에 등록이 된다.
public class MemberRepositoryExam {
    @PersistenceContext //EntityManager를 주입해주는 역할
    EntityManager em;
    public int save(TestMember member) {
        em.persist(member);//persist는 db에 객체를 저장한다는 의미이다.
        return member.getId();
    }
    public TestMember find(int id) {
        return em.find(TestMember.class, id);
    }
}