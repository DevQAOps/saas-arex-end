package ai.softprobe.saas.api.service;

import ai.softprobe.saas.api.model.contract.ClientDownloadResponse;
import ai.softprobe.saas.api.model.contract.ClientOauthRequest;
import ai.softprobe.saas.api.model.contract.ClientOauthResponse;

/**
 * @author wildeslam.
 * @create 2024/5/20 19:44
 */
public interface ClientService {

  ClientDownloadResponse getBrowserDownloadUrl();

  ClientOauthResponse clientLogin(ClientOauthRequest clientOauthRequest);
}
