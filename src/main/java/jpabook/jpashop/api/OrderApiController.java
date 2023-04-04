package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.query.OrderFlatDto;
import jpabook.jpashop.repository.query.OrderItemQueryDto;
import jpabook.jpashop.repository.query.OrderQueryDto;
import jpabook.jpashop.repository.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
//주문 조회 api
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    //엔티티를 직접 노출시키는 방법(바람직하지 않음)
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();//강제 초기화
            order.getDelivery().getAddress();//강제 초기화
            List<OrderItem> orderItems = order.getOrderItems();//여기는 OrderItem이 핵심이다.
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }

        }
        return all;
    }
    //엔티티를 dto로 감싸 반환했지만 최적화가 되지 않음(시간 많이걸림)
    @GetMapping("/api/v2/orders")
    public List<OrderDto> orderV2(){
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream().map(o -> new OrderDto(o)).collect(toList());

        return collect;
    }
    //패치 조인으로 v2의 최적화를 진행하는 방법
    //일대다인 관계에서는 다에 맞춰서 데이터를 끌어와서 뻥튀기된다.
    @GetMapping("/api/v3/orders")
    public List<OrderDto> orderV3(){
        List<Order> order = orderRepository.findAllWithItem();
        List<OrderDto> collect = order.stream().map(o -> new OrderDto(o)).collect(toList());

        return collect;
    }
    //엔티티를 dto로 감싸서 반환하는 방법(최적화 된 메소드) - 내 생각에는 해당 방법이 가장 적절하다고 생각
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> orderV3_page(@RequestParam(value = "offset",defaultValue = "0") int offset,@RequestParam(value = "limit",defaultValue = "100") int limit){
        List<Order> order = orderRepository.findAllWithMemberDelivery(offset,limit);//to one관계 패치조인은 페이징에 영향을 주지 않는다.
        List<OrderDto> collect = order.stream().map(o -> new OrderDto(o)).collect(toList());

        return collect;
    }
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){

        return orderQueryRepository.findAllByDto_optimization();
    }

    //flat하게 개발
    //쿼리가 한방에 나감 but order기준으로 페이징은 불가능하다.
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6(){
        //OrderFlatDto를 반환타입으로 하면 orderQueryRepository.findAllByDto_flat()을 리턴하면 되지만 api스팩이 달라짐
        //스펙에 맞춰 OrderQueryDto로 반환을 하면 OrderFlatDto의 중복을 발라내어 OrderQueryDto로 만들어 리턴을 해주어야 한다.
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());

    }


    //껍데기만 싸는게 아니라 안에 있는 엔티티도 노출하면 안된다. 그럼으로 내부에 있는 엔티티형태의 데이터는 dto로 변환한 뒤 다시 반환해준다.
    @Data
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate; //주문시간
         private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;//dto안에 엔티티가 있어서 올바르지 못함, 엔티티에 대한 의존을 끊어야 한다.엔티티가 외부에 노출됨
                                              //OrderItem을 OrderItemDto로 변환하여 엔티티 노출을 막는다.

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem)).collect(toList());
        }
    }
    @Data
    static class OrderItemDto{
        private String itemName;//상품 명
         private int orderPrice;
         private int count; //주문 수량
        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }

    }
}
