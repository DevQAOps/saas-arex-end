package ai.softprobe.saas.api.model.dto.login;

import lombok.Data;

@Data
public class OauthResult {

  private String email;

  private String providerUid;

}
