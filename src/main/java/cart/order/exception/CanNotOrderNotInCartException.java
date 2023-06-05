package cart.order.exception;

import cart.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class CanNotOrderNotInCartException extends BusinessException {

  public CanNotOrderNotInCartException(final String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
