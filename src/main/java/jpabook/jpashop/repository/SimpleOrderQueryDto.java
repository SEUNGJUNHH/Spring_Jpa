package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

//dto는 컨트롤러에 생성하면 컨트롤러를 repository로 생각할 수 있기 떄문에 분리를 해주어 repository에 dto클래스를 만들어 준다.
@Data
public class SimpleOrderQueryDto {

       private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderQueryDto(Long orderId,String name, LocalDateTime orderDate, OrderStatus orderStatus,Address address) {
            this.orderId = orderId;
            this.name = name;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
            this.address = address;
        }
}
