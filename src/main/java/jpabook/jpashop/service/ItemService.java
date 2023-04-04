package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional//readonly를 제외시키기 위해 오버라이드 함
    public void saveItem(Item item){
        itemRepository.save(item);
    }
//더티체킹(변경감지)이 merge보다 실무에서 잘 쓰인다.(null이 들어갈 수도 있어 원하는 역할 값만 변경하는 것이 좋은 코드이다.)
    @Transactional//더티체킹 하는 방식 commit되는 시점에 더디체킹을 해서 변경을 감지하고 flush를 해준다. = merge와 같은 역할을 한다.(더티체킹은 원하는 속성만 변경할 수 있지만 merge는 모든 속성을 변경시킨다)
    public void updateItem(Long itemId, int price, String name, int stockQuantity){
        //이렇게 하나하나 입력하는 것 보다 하나의 메서드를 구현하여 구현하는 것이 바람직함.
        Item findItem = itemRepository.findOne(itemId);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
        findItem.setPrice(price);

    }


    public List<Item> findItems(){
        return itemRepository.findAll();
    }
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
