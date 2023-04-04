package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter@Setter
public class OrderItem {
    @Id@GeneratedValue
    @Column(name="order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore//양방향 참조여서 한쪽에는 ignore을 달아줘야 무한루프를 방지할 수 있다.
    @ManyToOne(fetch = FetchType.LAZY)//orderitem기준으로는 다대일이다.
    @JoinColumn(name = "order_id")//FK를 member_id로 설정해주는 역할
    private Order order;

    private int orderPrice;//주문가격
    private int count;//주문 수량
    //OrderItem객체를 생성해서 하나씩 값을 주어지도록 코드를 설계하면 유지보수가 어렵다. 그러므로 createOrderItem을 통해 값을 넣을 수 있도록 protected로 막아준다.
    protected OrderItem(){

    }

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        item.removeStock(count);
        return orderItem;
    }

    //==비지니스 로직==//
    public void cancel() {
        getItem().addStock(count);
    }
//==조회로직==//
    public int getTotalPrice() {
        return getOrderPrice()*getCount();
    }
}
