package ai.softprobe.saas.api.model.contract;

import lombok.Data;

@Data
public class GrantClientTokenResponse {

  private String accessToken;

  private String refreshToken;
}
