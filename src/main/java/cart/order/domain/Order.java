package cart.order.domain;

import cart.coupon.domain.Coupon;
import cart.member.domain.Member;
import cart.value_object.Money;

public class Order {

  private Long id;

  private Member member;

  private Money deliveryFee;

  private Coupon coupon;

  public Order(final Long id, final Member member, final Money deliveryFee, final Coupon coupon) {
    this.id = id;
    this.member = member;
    this.deliveryFee = deliveryFee;
    this.coupon = coupon;
  }

  public boolean isNotMyOrder(final Long memberId) {
    return !this.member.isMe(memberId);
  }

  public Long getId() {
    return id;
  }
}
