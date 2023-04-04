package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepositoryOld;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
//서비스 단에는 위임만 하고 각 엔티티에 비지니스 로직을 활용해서 개발 - 도메인 모델 패턴
@Service
@Transactional(readOnly = true)//서비스단은 조회기능이 많으니 걸어놓고 내가 원하는 매서드만 오버라이딩해서 푼다.
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;
    //주문
    @Transactional//정보를 변경시키는 메서드 임으로 트랜잭션을 붙여준다.
    //누가 어떤 물건을 몇개 사는지 인자를 받는다
    public Long order(Long memberId, Long itemId, int count){
        //엔티티 조회
        Member member = memberRepository.fineOne(memberId);
        Item item = itemRepository.findOne(itemId);

        //배송정보
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());//회원의 주소를 배송 주소로 넣어줌

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createorder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();


    }
    //취소
@Transactional
    public void cancelorder(Long orderId){
    Order order = orderRepository.findOne(orderId);
    order.cancel();
}

    public List<Order> findOrder(OrderSearch orderSerach){
        return orderRepository.findAllByString(orderSerach);
    }
}
