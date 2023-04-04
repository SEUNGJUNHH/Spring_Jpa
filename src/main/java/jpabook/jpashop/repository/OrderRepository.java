package jpabook.jpashop.repository;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;//RequiredArgsConstructor을 이용하기 위해 파이널로 생성

    public void save(Order order){
        em.persist(order);
    }
    public Order findOne(Long id){
        return em.find(Order.class,id);
    }


    //language=JPAQl로 구현하는 방법  --- 오류날 가능성 많고 코드가 복잡
public List<Order> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
    TypedQuery<Order> query = em.createQuery(jpql, Order.class) .setMaxResults(1000); //최대 1000건
    if (orderSearch.getOrderStatus() != null) {
        query = query.setParameter("status", orderSearch.getOrderStatus());
    }
    if (StringUtils.hasText(orderSearch.getMemberName())) {
        query = query.setParameter("name", orderSearch.getMemberName());
    }
    return query.getResultList();
}
//    public List<Order> findAll(OrderSearch orderSearch) {
//
//
//        QOrder order = QOrder.order;
//        QMember member = QMember.member;
//
//        return query
//                .select(order)
//                .from(order)
//                .join(order.member, member)
//                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
//                .limit(1000)
//                .fetch();
//    }
//        private BooleanExpression statusEq(OrderStatus statusCond) {
//            if (statusCond == null) {
//                return null;
//            }
//            return QOrder.order.status.eq(statusCond);
//        }
//        private BooleanExpression nameLike(String nameCond) {
//            if (!StringUtils.hasText(nameCond)) {
//                return null;
//            }
//            return QMember.member.name.like(nameCond);
//        }


        //jpa criteria로 해결하는 방법 - 알고만 있기 실무에서 사용 x(유지보수가 너무 어렵다. 코드 가독성 낮음)
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
//주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
//회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }

    //xtoone관계에서는 패치 조인을 해도 row수가 증가하지 않는다.(toone관계는 아무리 패치조인을 많이해도 페이징이 가능하다.)
    public List<Order> findAllWithMemberDelivery() {
        //order을 가져오면서 멤버와 딜리버리를 한번에 같이 가져온다.
        //join fetch은 sql에는 존재하지 않고 jpa에만 존재하는 문법이다.
       return em.createQuery("select o from Order o " + "join fetch o.member m " + "join fetch o.delivery d", Order.class).getResultList();

    }

    //repository에 컨트롤러 의존관계를 형성하면 꼬이기 때문에 해당 dto는 repository에 생성해준다.
    //jpql 띄어쓰기 주의할 것
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.SimpleOrderQueryDto(o.id,m.name,o.orderDate,o.status,d.address)"
                +"from Order o "
                +"join o.member m "
                +"join o.delivery d",SimpleOrderQueryDto.class).getResultList();
    }


    public List<Order> findAllWithItem() {
        //distinct로 중복을 방지해준다.
        //DB에서는 distinct가 완전 동일한 것만 제외해주지만 jpa에서는 완전 동일하지 않아도(같은 아이디를 갖는 엔티티를 불러오는경우) 중복방지를 해준다.
        //컬렉션을 패치 조인으로 만들경우 페이징이 불가능하다.메모리단계에서 페이징처리 해버림(패치와 페이징이 연관됌)
        return em.createQuery("select distinct o from Order o " +
                "join fetch o.member m " +
                "join fetch o.delivery d "+
                "join fetch o.orderItems oi "+
                "join fetch oi.item i", Order.class).getResultList();

    }
    //페이징 기능을 넣은 메소드
    //xtoone관계에서는 패치 조인을 해도 row수가 증가하지 않는다.(toone관계는 아무리 패치조인을 많이해도 페이징이 가능하다.)
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o " + "join fetch o.member m " + "join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }



//    return  em.createQuery("select o from Order o join o.member m"+
//               "where o.status = :status"+
//               "and m.name like :name"
//               ,Order.class)
//            .setParameter("status",orderSearch.getOrderStatus())
//            .setParameter("name",orderSearch.getMembername())//파라미터 설정해주기
//            .setMaxResults(1000)//1000개까지만 조회 최대 1000건
//            .getResultList();


}
