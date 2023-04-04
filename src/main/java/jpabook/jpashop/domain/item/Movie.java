package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity//데이터베이스에 테이블을 만들어주는 역할
@DiscriminatorValue("M")//이걸로 구분을 할 수 있음
@Getter@Setter
public class Movie extends Item{
    private String director;
    private String actor;
}
