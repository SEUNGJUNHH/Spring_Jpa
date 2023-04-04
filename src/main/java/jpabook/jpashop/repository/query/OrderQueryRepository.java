package jpabook.jpashop.repository.query;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    //N+1번 문제가 발생한다.
    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findorders(); //쿼리1번 ->2개
        //loop를 돌린다.
        result.forEach(o -> {
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());//쿼리 N번
            o.setOrderItems(orderItems);
        });
        return result;
    }

    //일대다 인 컬럭테인 경우 하나씩 다 넣어줘야함 loop 돌면서 orderitem을 가져온다, 그 외 xtoone은 findorders로 해준다.
    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                " from OrderItem oi" +
                                " join oi.item i" +
                                " where oi.order.id = : orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    //xtoone문제는 기본적으로 불러온다.
    private List<OrderQueryDto> findorders() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                        + " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class).getResultList();
    }
    public List<OrderQueryDto> findAllByDto_optimization() {

        List<OrderQueryDto> result = findorders();
        //아이디 추출하는 코드 -> 쿼리랑 상관 x
        List<Long> OrderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        //주문 데이터만큼 메모리의 map에 올려 쿼리를 단순화한다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(OrderIds);
        //메모리 멥에 꽂아넣어 쿼리2번으로 최적화가 가능하다.
        result.forEach(o->o.setOrderItems(orderItemMap.get(o.getOrderId())));
        return result;
    }

    private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> OrderIds) {
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        " from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :orderIds", OrderItemQueryDto.class)
                .setParameter("orderIds", OrderIds)
                .getResultList();

        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream().collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));
        return orderItemMap;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery(
                "select new jpabook.jpashop.repository.query.OrderFlatDto(o.id, m.name, o.orderDate, d.address,  o.status, i.name, oi.orderPrice, oi.count)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d" +
                        " join o.orderItems oi" +
                        " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
