package cart.order.application;

import cart.member.domain.Member;
import cart.order.application.dto.RegisterOrderRequest;
import cart.order.dao.OrderDao;
import cart.order.dao.entity.OrderEntity;
import cart.order.domain.Order;
import cart.order.exception.CanNotDeleteNotMyOrderException;
import cart.order.exception.NotFoundOrderException;
import cart.order.exception.NotSameTotalPriceException;
import cart.order_item.application.OrderItemCommandService;
import cart.order_item.domain.OrderedItems;
import cart.value_object.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderCommandService {

  private final OrderDao orderDao;
  private final OrderItemCommandService orderItemCommandService;

  public OrderCommandService(
      final OrderDao orderDao,
      final OrderItemCommandService orderItemCommandService
  ) {
    this.orderDao = orderDao;
    this.orderItemCommandService = orderItemCommandService;
  }

  public Long registerOrder(final Member member, final RegisterOrderRequest registerOrderRequest) {

    final OrderEntity orderEntity = new OrderEntity(member.getId(),
        registerOrderRequest.getDeliveryFee());

    final Long savedOrderId = orderDao.save(orderEntity);

    final Order order = new Order(
        savedOrderId,
        member,
        new Money(registerOrderRequest.getDeliveryFee())
    );

    final OrderedItems orderedItems = orderItemCommandService.registerOrderItem(
        registerOrderRequest.getCartItemIds(),
        order,
        member
    );

    validateSameTotalPrice(orderedItems.calculateAllItemPrice(), registerOrderRequest);

    return savedOrderId;
  }

  private void validateSameTotalPrice(
      final Money totalPrice,
      final RegisterOrderRequest registerOrderRequest
  ) {
    final Money other = new Money(registerOrderRequest.getTotalPrice());

    if (totalPrice.isNotSame(other)) {
      throw new NotSameTotalPriceException("주문된 총액이 올바르지 않습니다.");
    }
  }

  public void deleteOrder(final Member member, final Long orderId) {
    final OrderEntity orderEntity = orderDao.findByOrderId(orderId)
        .orElseThrow(() -> new NotFoundOrderException("해당 주문은 존재하지 않습니다."));

    final Order order = new Order(
        orderEntity.getId(),
        member,
        new Money(orderEntity.getDeliveryFee())
    );

    validateOrderOwner(order, orderEntity.getMemberId());

    orderDao.deleteByOrderId(orderId);
  }

  private void validateOrderOwner(final Order order, final Long memberId) {
    if (order.isNotMyOrder(memberId)) {
      throw new CanNotDeleteNotMyOrderException("사용자의 주문 목록 이외는 삭제할 수 없습니다.");
    }
  }
}
