package ai.softprobe.saas.api.common.exceptions;

/**
 * @author wildeslam.
 * @create 2024/3/21 15:31
 */
public class SpSaasException extends RuntimeException {

  private int code;

  public SpSaasException(int code, String message) {
    super(message);
    this.code = code;
  }

  public SpSaasException(int code, Throwable cause) {
    super(cause);
    this.code = code;
  }

  public SpSaasException(int code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public int getCode() {
    return code;
  }
}
