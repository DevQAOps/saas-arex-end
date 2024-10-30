package com.arextest.saas.api.controller;

import com.arextest.common.model.response.Response;
import com.arextest.common.utils.ResponseUtils;
import com.arextest.saas.api.service.ClientService;
import com.arextest.saas.api.model.contract.ClientDownloadResponse;
import com.arextest.saas.api.model.contract.ClientOauthRequest;
import com.arextest.saas.api.model.enums.ClientOauthTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wildeslam.
 * @create 2024/5/20 19:38
 */
@Slf4j
@Controller
@RequestMapping("/api/client")
public class ClientController {

  @Autowired
  private ClientService clientService;

  @PostMapping("/queryClientInfo")
  @ResponseBody
  public Response queryClientInfo(@RequestHeader("User-Agent") String userAgent) {
    ClientDownloadResponse response = clientService.getBrowserDownloadUrl();
    return ResponseUtils.successResponse(response);
  }

  @ResponseBody
  @GetMapping("/clientLogin")
  public Response clientLogin(@RequestParam String code) {
    ClientOauthRequest clientOauthRequest = new ClientOauthRequest();
    clientOauthRequest.setCode(code);
    clientOauthRequest.setOauthType(ClientOauthTypeEnum.OAUTH0.getCode());
    return ResponseUtils.successResponse(
        clientService.clientLogin(clientOauthRequest)
    );
  }

}
