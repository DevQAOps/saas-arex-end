package com.arextest.saas.api.service;

import com.arextest.saas.api.model.contract.ClientDownloadResponse;
import com.arextest.saas.api.model.contract.ClientOauthRequest;
import com.arextest.saas.api.model.contract.ClientOauthResponse;

/**
 * @author wildeslam.
 * @create 2024/5/20 19:44
 */
public interface ClientService {

  ClientDownloadResponse getBrowserDownloadUrl();

  ClientOauthResponse clientLogin(ClientOauthRequest clientOauthRequest);
}
