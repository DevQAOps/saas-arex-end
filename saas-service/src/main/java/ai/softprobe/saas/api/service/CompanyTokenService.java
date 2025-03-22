package ai.softprobe.saas.api.service;

public interface CompanyTokenService {

  String generateToken(String tenantCode) throws Exception;

}
