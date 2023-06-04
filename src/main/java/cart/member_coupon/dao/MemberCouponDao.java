package cart.member_coupon.dao;

import cart.coupon.dao.CouponDao;
import cart.coupon.domain.Coupon;
import cart.coupon.exception.NotFoundCouponException;
import cart.member.dao.MemberDao;
import cart.member.domain.Member;
import cart.member_coupon.domain.MemberCoupon;
import cart.member_coupon.domain.UsedStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberCouponDao {

  private final JdbcTemplate jdbcTemplate;

  private final MemberDao memberDao;

  private final CouponDao couponDao;

  public MemberCouponDao(
      final JdbcTemplate jdbcTemplate,
      final MemberDao memberDao,
      final CouponDao couponDao
  ) {
    this.jdbcTemplate = jdbcTemplate;
    this.memberDao = memberDao;
    this.couponDao = couponDao;
  }

  public List<MemberCoupon> findByMemberId(final Long memberId, final String usedCondition) {
    final String sql = "SELECT * "
        + "FROM MEMBER_COUPON MC "
        + "WHERE MC.member_id = ? "
        + "and MC.used_yn = ?";

    return jdbcTemplate.query(sql, getRowMapper(), memberId, usedCondition);
  }

  private RowMapper<MemberCoupon> getRowMapper() {
    return (rs, rowNum) -> {
      final long savedMemberId = rs.getLong("member_id");
      final Member member = memberDao.getMemberById(savedMemberId);
      final long savedCouponId = rs.getLong("coupon_id");
      final Coupon coupon = couponDao.findById(savedCouponId)
          .orElseThrow(() -> new NotFoundCouponException("해당 쿠폰은 존재하지 않습니다"));
      final String usedYn = rs.getString("used_yn");
      return new MemberCoupon(member, coupon, UsedStatus.mapToUsedStatus(usedYn));
    };
  }

  public void updateMemberCoupon(final Long couponId, final Long memberId, final String usedYn) {
    final String sql = "UPDATE MEMBER_COUPON MC "
        + "SET used_yn = ? "
        + "WHERE MC.coupon_id = ? "
        + "AND MC.member_id = ?";

    jdbcTemplate.update(sql, usedYn, couponId, memberId);
  }

  public Optional<MemberCoupon> findByMemberAndCouponId(final Long couponId, final Long memberId) {
    final String sql = "SELECT * "
        + "FROM MEMBER_COUPON MC "
        + "WHERE MC.member_id = ? "
        + "AND MC.coupon_id = ?";

    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(sql, getRowMapper(), memberId, couponId));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }
}
