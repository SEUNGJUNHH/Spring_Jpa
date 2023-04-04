package jpabook.jpashop.domain;
//domain에 핵심 엔티티들을 깔것이다.

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter@Setter
public class Member {

    @Id@GeneratedValue//시퀀스 값으로 id값을 생성해준다.
    @Column(name = "member_id")
    private Long Id;

    @NotEmpty
    private String name;

    @Embedded//내장 타입을 사용한다는 것을 의미한다.
    private Address address;

    @JsonIgnore//api에서 해당 order을 빼준다.양방향이여서 무한루프에 빠지기 때문에 한쪽은 ignore을 걸어줘야 한다.
    @OneToMany(mappedBy = "member")//읽기전용이 되어버림 주인이 아닌 거울이 되버린다. 주인은 order의 member 여기에 값을 넣어도 FK의 값이 변경되지 않는다.
    private List<Order> orders = new ArrayList<>();



}
