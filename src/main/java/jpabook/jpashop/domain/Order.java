package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")//관례로 order이 되어버리기 때문에 테이블 이름을 바꾸어준다.
@Getter@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)//createorder 사용하지 않고 객체를 만들어 개발하는 것을 막아줌. (생성메서드로 개발)
public class Order {
    @Id@GeneratedValue
    @Column(name="order_id")
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")//FK를 member_id로 설정해주는 역할
    private Member member;//애가 연관관게의 주인임

    //@BatchSize(size = 1000)//부분적으로 BatchSize를 거는 방법 (얼마나 추가적으로 끌어올지)
    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)//Cascade는 여러 군대에서 떙겨오는 class의 경우 사용 안하는 것이 바람직
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)//cascade는 persist를 간편하게 해준다
    @JoinColumn(name="delivery_id")//FK를 member_id로 설정해주는 역할
    private Delivery delivery;

    private LocalDateTime orderDate;//주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;//order/ cencel둘중에 하나만 선택 enum타입

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }
    public void addOrderItem(OrderItem orderItem) { orderItems.add(orderItem);
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }


    //==생성메서드==//점점점 문법은 여러개를 넘길 수 있다
    public static Order createorder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem:orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //==비지니스 매서드==//
    //주문취소
    public void cancel(){
        //이미 배송이 되어있을때를 의미한다.
        if(delivery.getStatus()==DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송되었습니다");
        }
        this.setStatus(OrderStatus.CENCEL);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
    //==조회로직==//
    //전체주문가격조회
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderitem:orderItems){
            totalPrice+=orderitem.getTotalPrice();
        }
        return totalPrice;
    }
}
