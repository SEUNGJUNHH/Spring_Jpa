package jpabook.jpashop.repository;

import jpabook.jpashop.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class OrderSearch {
    private String memberName;//동적쿼리를 위한 변수. where문에 membername가 들어가는 역할을 수행한다.
    private OrderStatus orderStatus;


}
