package cart.cart_item.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cart.cart_item.domain.CartItem;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;

@JdbcTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class CartItemDaoTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private CartItemDao cartItemDao;

  @BeforeEach
  void setUp() {
    cartItemDao = new CartItemDao(jdbcTemplate);
  }

  @Test
  @DisplayName("findByIdsIn() : 주어진 id들에 속한 cartItem을 조회할 수 있다.")
  void test_findByIdsIn() throws Exception {
    //given
    final Long memberId = 1L;
    final List<Long> cartItemIds = List.of(1L, 2L, 4L);

    //when
    final List<CartItem> cartItems = cartItemDao.findByIdsIn(cartItemIds, memberId);

    //then
    final List<Long> savedCartItemIds = cartItems.stream()
        .map(CartItem::getId)
        .collect(Collectors.toList());

    Assertions.assertAll(
        () -> assertEquals(3, savedCartItemIds.size()),
        () -> assertThat(savedCartItemIds).containsAnyElementsOf(List.of(1L, 2L, 3L))
    );
  }
}
