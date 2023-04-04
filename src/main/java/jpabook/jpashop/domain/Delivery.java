package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @JsonIgnore//양방향 참조여서 한쪽에는 ignore을 달아줘야 무한루프를 방지할 수 있다.
    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)//eum이면 Enumerated을 붙여줘야 한다. ordinal이면 숫자로 구분되는 eum을 의미한다.
    private DeliveryStatus status; //ENUM [READY(준비), COMP(배송)]
}