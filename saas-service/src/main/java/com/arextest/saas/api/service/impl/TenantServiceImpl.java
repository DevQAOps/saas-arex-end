package com.arextest.saas.api.service.impl;

import com.arextest.common.model.response.GenericResponseType;
import com.arextest.saas.api.service.http.HttpWebClient;
import com.arextest.saas.api.common.enums.ErrorCode;
import com.arextest.saas.api.common.enums.TenantStatus;
import com.arextest.saas.api.common.exceptions.ArexSaasException;
import com.arextest.saas.api.common.utils.CommonUtil;
import com.arextest.saas.api.common.utils.JwtUtil;
import com.arextest.saas.api.configuration.DisabledDomainConfig;
import com.arextest.saas.api.configuration.PackageConfiguration;
import com.arextest.saas.api.configuration.PackageConfiguration.PackageInfo;
import com.arextest.saas.api.mails.SendInvitationEmail;
import com.arextest.saas.api.mails.SendResetPwdEmail;
import com.arextest.saas.api.mails.SendVerificationEmail;
import com.arextest.saas.api.repo.TenantRepository;
import com.arextest.saas.api.service.DevopsServiceHandler;
import com.arextest.saas.api.service.SubscribeService;
import com.arextest.saas.api.service.TenantService;
import com.arextest.saas.api.service.VerificationHandler;
import com.arextest.saas.api.service.VerificationHandler.VerificationEntity;
import com.arextest.saas.api.model.contract.LoginRequest;
import com.arextest.saas.api.model.contract.RegisterRequest;
import com.arextest.saas.api.model.contract.VerifyRequest;
import com.arextest.saas.api.model.contract.external.AddUserRequest;
import com.arextest.saas.api.model.contract.external.FinalizeSaasUserRequest;
import com.arextest.saas.api.model.contract.external.RemoveUserRequest;
import com.arextest.saas.api.model.contract.external.UserType;
import com.arextest.saas.api.model.dao.TenantCollection;
import com.arextest.saas.api.model.dto.TenantDto;
import com.arextest.saas.api.model.dto.TenantDto.UserInfoDto;
import com.arextest.saas.api.model.enums.TenantLevelEnum;
import com.arextest.saas.api.model.vo.TenantVo;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author wildeslam.
 * @create 2024/3/5 15:53
 */
@Slf4j
@Service
public class TenantServiceImpl implements TenantService {

  private static final Long VERIFICATION_CODE_ACTIVE_DURATION = 24 * 60 * 60 * 1000L;

  @Value("${arex.devops.url}")
  private String devopsUrl;

  @Value("${devops.url.usermgnt.add}")
  private String addUserUrl;
  @Value("${devops.url.usermgnt.remove}")
  private String removeUserUrl;

  @Resource
  private VerificationHandler verificationHandler;

  @Autowired
  private TenantRepository tenantRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Resource
  private CompanyTokenServiceImpl companyTokenService;

  @Resource
  private SubscribeService subscribeService;

  @Resource
  private DevopsServiceHandler devopsServiceHandler;

  @Resource
  private DisabledDomainConfig disabledDomainConfig;

  @Resource
  private PackageConfiguration packageConfiguration;

  @Resource
  private SendVerificationEmail sendVerificationEmail;

  @Resource
  private SendInvitationEmail sendInvitationEmail;
  @Autowired
  private SendResetPwdEmail sendResetPwdEmail;
  @Resource
  private HttpWebClient httpWebClient;

  @Override
  public boolean validate(String email) {
    return tenantRepository.queryTenantByEmail(email) == null;
  }

  @Override
  public boolean register(RegisterRequest request) {
    if (!CommonUtil.validateEmail(request.getEmail())) {
      throw new ArexSaasException(ErrorCode.EMAIL_FORMAT_ERROR.getCodeValue(),
          "Email invalid");
    }
    TenantDto existTenant = tenantRepository.queryTenantByEmail(request.getEmail());
    if (existTenant != null && existTenant.isEnabled()) {
      throw new ArexSaasException(ErrorCode.TENANT_EXISTED.getCodeValue(), "Tenant already exists");
    }

    String verificationCode = CommonUtil.generateVerificationCode();

    TenantDto tenant = new TenantDto();
    tenant.setEmail(request.getEmail());
    tenant.setPassword(passwordEncoder.encode(request.getPassword()));
    tenant.setUserLevel(TenantLevelEnum.DISABLED.getCode());
    tenant.setVerificationCode(verificationCode);
    tenant.setVerificationTime(System.currentTimeMillis() + VERIFICATION_CODE_ACTIVE_DURATION);

    tenantRepository.upsertTenant(tenant);

    try {
      return sendVerificationEmail.sendVerificationEmail(tenant.getEmail(), verificationCode);
    } catch (Exception e) {
      LOGGER.error("Failed to send verification code", e);
      tenantRepository.deleteTenantInfo(request.getEmail());
      throw new ArexSaasException(ErrorCode.SEND_EMAIL_FAILED.getCodeValue(), e.getMessage());
    }

  }

  @Override
  public String verify(VerifyRequest request) {

    String upnString = JwtUtil.getUpnString(request.getUpn());
    if (StringUtils.isEmpty(upnString)) {
      throw new ArexSaasException(ErrorCode.VERIFY_FAILED.getCodeValue(), "Upn is empty");
    }

    VerificationEntity verificationEntity = verificationHandler.getVerificationInfoByToken(
        request.getUpn());
    if (verificationEntity == null) {
      throw new ArexSaasException(ErrorCode.CONVERT_VERIFICATION_INFO_ERROR.getCodeValue(),
          "Verification info invalid");
    }

    Long currentTime = System.currentTimeMillis();
    if (!CommonUtil.validateEmail(verificationEntity.getEmail())) {
      throw new ArexSaasException(ErrorCode.EMAIL_FORMAT_ERROR.getCodeValue(), "Email invalid");
    }
    TenantDto tenant = tenantRepository.queryTenantByEmail(verificationEntity.getEmail());
    if (tenant == null) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "Tenant not found");
    }
    if (!tenant.getVerificationCode()
        .equals(verificationEntity.getVerificationCode())) {
      throw new ArexSaasException(ErrorCode.VERIFY_FAILED.getCodeValue(),
          "The verification code is incorrect");
    } else {
      // reset verification code after verified successfully
      tenant.setVerificationCode(CommonUtil.generateVerificationCode());
      tenantRepository.upsertTenant(tenant);
    }

    if (tenant.getVerificationTime() < currentTime) {
      throw new ArexSaasException(ErrorCode.VERIFY_FAILED.getCodeValue(),
          "Verification code expired");
    }

    return JwtUtil.makeAccessTokenWithEmail(verificationEntity.getEmail());
  }

  @Override
  public boolean deleteTenant(String email) {
    return tenantRepository.deleteTenantInfo(email);
  }

  @Override
  public String login(LoginRequest request) {
    String email = request.getEmail();
    String password = request.getPassword();

    if (!CommonUtil.validateEmail(email)) {
      throw new ArexSaasException(ErrorCode.EMAIL_FORMAT_ERROR.getCodeValue(), "Email invalid");
    }

    TenantDto tenant = tenantRepository.queryTenantByEmail(email);
    if (tenant == null || !tenant.isEnabled()) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "Tenant not found");
    }
    if (tenant.getPassword() == null || !passwordEncoder.matches(password, tenant.getPassword())) {
      throw new ArexSaasException(ErrorCode.PASSWORD_ERROR.getCodeValue(), "Password error");
    }
    if (tenant.getTenantName() == null) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_BOUND.getCodeValue(), "Tenant not bind");
    }
    return JwtUtil.makeAccessTokenWithTenantCode(tenant.getTenantCode());
  }

  @Override
  public boolean bind(String email, String tenantName, String tenantCode) {
    if (!tenantCode.matches("^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?$")
        || disabledDomainConfig.getDisabled().contains(tenantCode)) {
      throw new ArexSaasException(ErrorCode.COMPANY_CODE_ERROR.getCodeValue(),
          "Company code invalid");
    }

    if (!CommonUtil.validateEmail(email)) {
      throw new ArexSaasException(ErrorCode.EMAIL_FORMAT_ERROR.getCodeValue(), "Email invalid");
    }

    TenantDto oldTenant = tenantRepository.queryTenant(tenantCode);
    if (oldTenant != null) {
      LOGGER.error("Company code already bound, tenantCode: {}, email: {}", tenantCode, email);
      throw new ArexSaasException(ErrorCode.COMPANY_CODE_BOUND.getCodeValue(),
          "Company code already bound");
    }

    TenantDto tenant = tenantRepository.queryTenantByEmail(email);
    if (tenant == null) {
      LOGGER.error("User not found, email: {}", email);
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "User not found");
    }
    if (tenant.isEnabled()) {
      LOGGER.error("User already bind, email: {}", email);
      throw new ArexSaasException(ErrorCode.USER_BOUND.getCodeValue(), "User already bind");
    }

    String tenantToken;
    try {
      tenantToken = companyTokenService.generateToken(tenantCode);
    } catch (Exception exception) {
      LOGGER.error("Failed to generate company token, exception: {}, tenantCode: {}",
          exception.getMessage(), tenantCode);
      throw new ArexSaasException(ErrorCode.COMPANY_TOKEN_ERROR.getCodeValue(),
          "User already bind");
    }

    PackageInfo packageInfo = packageConfiguration.getPackageInfoById(0);

    tenant.setUserLevel(TenantLevelEnum.NORMAL.getCode());
    tenant.setTenantName(tenantName);
    tenant.setTenantCode(tenantCode);
    tenant.setTenantToken(tenantToken);
    tenant.setPackageEffectiveTime(System.currentTimeMillis());
    tenant.setExpireTime(packageInfo.calculateExpireTimestamp(tenant.getPackageEffectiveTime()));
    tenant.setMemberLimit(packageInfo.getMemberLimit());
    tenant.setTrafficLimit(packageInfo.getTrafficLimit());

    UserInfoDto userInfoDto = new UserInfoDto();
    userInfoDto.setEmail(email);
    userInfoDto.setProviderUids(tenant.getProviderUids());
    tenant.setUserInfos(Collections.singleton(userInfoDto));

    try {
      if (!subscribeService.initDataBySubscription(tenant)) {
        return false;
      }
    } catch (RuntimeException exception) {
      LOGGER.error("Failed to init tenant, exception: {}, tenantCode: {}", exception.getMessage(),
          tenantCode);
      throw new ArexSaasException(ErrorCode.INIT_USER_ERROR.getCodeValue(), "init tenant error");
    }

    tenantRepository.upsertTenant(tenant);
    return true;
  }

  @Override
  public TenantVo getTenant(String accessToken) {
    String tenantCode = JwtUtil.getUserNameByUserToken(accessToken);
    if (tenantCode != null) {
      return convertUserDtoToVo(tenantRepository.queryTenant(tenantCode));
    }
    return null;
  }

  @Override
  public boolean addUsers(String tenantCode, Set<String> emails) {
    TenantDto tenantDto = tenantRepository.queryTenant(tenantCode);

    if (tenantDto.getUserInfos() != null) {
      Set<UserInfoDto> userInfoDtos = tenantDto.getUserInfos();
      Set<String> existEmails = userInfoDtos.stream().map(UserInfoDto::getEmail)
          .collect(Collectors.toSet());
      if (CollectionUtils.containsAny(existEmails, emails)) {
        throw new ArexSaasException(ErrorCode.USER_ALREADY_EXISTED.getCodeValue(),
            "User already existed");
      }
    }

    if (memberExceedLimit(tenantDto, emails)) {
      throw new ArexSaasException(ErrorCode.MEMBER_EXCEED_LIMIT.getCodeValue(),
          "Member exceed limit");
    }

    List<UserType> users = new ArrayList<>(emails.size());
    emails.forEach(email -> {
      UserType userType = new UserType();
      userType.setUserName(email);
      userType.setVerificationCode(CommonUtil.generateVerificationCode());
      users.add(userType);
    });

    // add users to xxxx_arex_storage_db
    AddUserRequest addUserRequest = new AddUserRequest();
    addUserRequest.setTenantCode(tenantCode);
    addUserRequest.setEmails(users);

    if (StringUtils.isEmpty(devopsUrl)) {
      LOGGER.error("devopsUrl is empty");
      return false;
    }
    String url = devopsUrl + addUserUrl;
    GenericResponseType response = httpWebClient.jsonPost(url, addUserRequest,
        GenericResponseType.class);
    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to add user");
    }

    // add users to saas_dbr
    Set<UserInfoDto> userInfoDtos = Optional.ofNullable(tenantDto.getUserInfos())
        .orElse(new HashSet<>(emails.size()));
    for (String email : emails) {
      UserInfoDto userInfoDto = new UserInfoDto();
      userInfoDto.setEmail(email);
      userInfoDto.setProviderUids(new HashSet<>());
      userInfoDtos.add(userInfoDto);
    }
    tenantRepository.upsertTenant(tenantDto);
    return true;
  }

  @Override
  public boolean removeUsers(String tenantCode, Set<String> emails) {
    TenantDto tenantDto = tenantRepository.queryTenant(tenantCode);
    if (tenantDto.getUserInfos() == null) {
      LOGGER.info("no users to remove");
      return false;
    }

    if (StringUtils.isEmpty(devopsUrl)) {
      LOGGER.error("devopsUrl is empty");
      return false;
    }
    String url = devopsUrl + removeUserUrl;

    RemoveUserRequest removeUserRequest = new RemoveUserRequest();
    removeUserRequest.setTenantCode(tenantCode);
    removeUserRequest.setEmails(emails);

    GenericResponseType response = httpWebClient.jsonPost(url, removeUserRequest,
        GenericResponseType.class);
    if (response == null || response.getResponseStatusType().hasError()
        || response.getBody() == null) {
      throw new RuntimeException("Failed to remove user");
    }

    tenantDto.getUserInfos().removeIf(userInfoDto -> emails.contains(userInfoDto.getEmail()));
    tenantRepository.upsertTenant(tenantDto);
    return true;
  }

  @Override
  public List<TenantVo> queryTenantsByEmail(String email) {
    return tenantRepository.queryTenantsByEmail(email)
        .stream()
        .map(this::displayUserLoginDetails)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public Set<String> queryUserEmailsByCode(String tenantCode) {
    return tenantRepository.queryTenant(tenantCode).getUserInfos().stream()
        .map(UserInfoDto::getEmail)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean logOff(String tenantCode) {
    TenantDto existTenant = tenantRepository.queryTenant(tenantCode);
    if (existTenant == null) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(), "Tenant not found");
    }
    deleteTenant(existTenant.getEmail());

    FinalizeSaasUserRequest request = new FinalizeSaasUserRequest();
    request.setTenantCode(tenantCode);
    return devopsServiceHandler.finalizeUserRepo(request);
  }

  @Override
  public boolean sendResetPwdEmail(String email) {
    TenantDto tenantDto = tenantRepository.queryTenantByEmail(email);
    if (tenantDto == null) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(),
          "Tenant not existed");
    }

    String verificationCode = CommonUtil.generateVerificationCode();
    tenantDto.setVerificationCode(verificationCode);
    tenantDto.setVerificationTime(System.currentTimeMillis() + VERIFICATION_CODE_ACTIVE_DURATION);
    tenantRepository.upsertTenant(tenantDto);

    return sendResetPwdEmail.sendResetPwdEmail(email, verificationCode);
  }

  @Override
  public boolean resetPassword(String email, String password) {
    TenantDto tenantDto = tenantRepository.queryTenantByEmail(email);
    if (tenantDto == null) {
      throw new ArexSaasException(ErrorCode.TENANT_NOT_EXISTED.getCodeValue(),
          "Tenant not existed");
    }

    tenantDto.setPassword(passwordEncoder.encode(password));
    tenantDto.setVerificationCode(CommonUtil.generateVerificationCode());
    tenantRepository.upsertTenant(tenantDto);
    return true;
  }


  // userdto 转 userVo
  private TenantVo convertUserDtoToVo(TenantDto tenant) {
    if (tenant == null) {
      return null;
    }
    TenantVo userVo = new TenantVo();
    userVo.setEmail(tenant.getEmail());
    userVo.setTenantName(tenant.getTenantName());
    userVo.setUserLevel(tenant.getUserLevel());
    userVo.setTenantCode(tenant.getTenantCode());
    userVo.setTenantToken(tenant.getTenantToken());
    userVo.setTenantStatus(tenant.getExpireTime() < System.currentTimeMillis() ?
        TenantStatus.INACTIVE.getStatus() : TenantStatus.ACTIVE.getStatus());
    userVo.setExpireTime(tenant.getExpireTime());
    return userVo;
  }

  private TenantVo displayUserLoginDetails(TenantCollection dao) {
    if (dao == null) {
      return null;
    }
    TenantVo userVo = new TenantVo();
    userVo.setEmail(dao.getEmail());
    userVo.setTenantName(dao.getTenantName());
    userVo.setUserLevel(dao.getUserLevel());
    userVo.setTenantCode(dao.getTenantCode());
    userVo.setTenantStatus(dao.getExpireTime() < System.currentTimeMillis() ?
        TenantStatus.INACTIVE.getStatus() : TenantStatus.ACTIVE.getStatus());
    userVo.setExpireTime(dao.getExpireTime());
    return userVo;
  }


  private boolean memberExceedLimit(TenantDto tenantDto, Set<String> emails) {
    Set<UserInfoDto> userInfoDtos =
        Optional.ofNullable(tenantDto.getUserInfos()).orElse(Collections.emptySet());
    int futureUserSize = userInfoDtos.size() + emails.size();
    return futureUserSize > tenantDto.getMemberLimit();
  }
}
