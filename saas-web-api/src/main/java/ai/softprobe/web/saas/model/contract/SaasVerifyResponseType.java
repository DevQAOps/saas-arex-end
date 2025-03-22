package ai.softprobe.web.saas.model.contract;

import lombok.Data;

@Data
public class SaasVerifyResponseType {
  private String accessToken;
  private String refreshToken;
}
