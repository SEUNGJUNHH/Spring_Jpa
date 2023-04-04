package jpabook.jpashop.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
//도메인과 컨트롤로에서 원하는 멤버가 다를 수 있다. 해당 컨트롤러에서 원하는 멤버 폼을 만들어 구현하는 것이 바람직하다.(도메인 멤버에는 필요없는 메서드나 어노테이션이 존재할 수 있다.)
public class MemberForm {

    @NotEmpty(message = "회원이름은 필수 입니다.")//해당하는 변수는 NotEmpty 해야된다는 것을 의미. 만일 비어지면 오류 메세지가 뜬다. 나머지 변수들은 입력 안받아도 정상적으로 동작ㄷ
    private String name;

    private String city;
    private String street;
    private String zipcode;

}
