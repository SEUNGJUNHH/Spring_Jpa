package jpabook.jpashop.api;


//xtoOne 과 관련된 예시
//order의 delivery와 member만 끌어다 사용할 것


import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.SimpleOrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    //엔티티를 직접 노출시키면 유지보수 및 관리가 어려워 dto로 반환하는게 올바름
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();//getMember까지는 프록시 객체인데 getName은 끌고 와야되는 거라 강제로 lazy시켜준다.
            order.getDelivery().getAddress();//getMember까지는 프록시 객체인데 getName은 끌고 와야되는 거라 강제로 lazy시켜준다.
        }
        return all;
    }

    //dto로 감싸서 나가긴 하지만 쿼리가 너무 많이 나감
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return result;
    }
    // join fetch를 사용해서 관련 엔티티를 한번에 가져와서 쿼리양이 준다.
    // v3의 경우 엔티티에서 dto로 변환 후 dto로 반환하는 방식이다. 받은 정보를 재활용할 수 있다
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> dtoList = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
        return dtoList;
    }
    //v4의 경우 엔티티를 조회하지 않고 바로 dto를 끌어다 사용하는 방식이다.
    //v4의 경우 핏한 데이터만 가져오기 때문에 다른 api에서 재활용하기 어렵다. api스팩을 위한 코드가 repository에 추가된다. v3,v4는 장단점이 존재 상황에 맞춰 사용하는 것이 좋다.(내 생각엔 v3가 더 활용적이라고 생각)
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4(){

        return orderRepository.findOrderDtos();
    }


    //v1~v3까지 해당 dto를 사용하고 v4는 repository에 생성된 dto를 사용한다.
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); //LAZY 초기화
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();//LAZY 초기화
        }
    }
}
