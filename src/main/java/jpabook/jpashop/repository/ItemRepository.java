package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor//생성자 주입을 위한 어노테이션
public class ItemRepository {
    private final EntityManager em;

    public void save(Item item){
        //item.getId()==null이라는 것은 새로 생성된 객체라는 의미이다.
        if(item.getId()==null){
            em.persist(item);//신규 등록
        }else{
            Item merge = em.merge(item);//준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능 / merge가 영속성 context로 관리 item가 저장되는 것은 아님
        }
    }

    public Item findOne(Long id){
        Item find = em.find(Item.class, id);
        return find;
    }
    public List<Item> findAll(){
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
