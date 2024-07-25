package com.arextest.common.saas.utils;

import com.arextest.common.saas.model.Constants;
import com.arextest.common.utils.TenantContextUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

/**
 * @author wildeslam.
 * @create 2024/7/9 15:27
 */
public class TenantUtil {
  public static String extractTenantCode(HttpServletRequest request) {
    String tenantCode = TenantContextUtil.getTenantCode();
    if (StringUtils.isNotEmpty(tenantCode)) {
      return TenantContextUtil.getTenantCode();
    }
    return extractTenantCodeFromRequest(request);
  }


  private static String extractTenantCodeFromRequest(HttpServletRequest request) {
    String res = "";
    String serverName = request.getServerName();
    if (StringUtils.isNotEmpty(serverName)) {
      Pattern pattern = Pattern.compile("^(.*?)\\.arextest\\.com$");
      Matcher matcher = pattern.matcher(serverName);
      if (matcher.find()) {
        res = matcher.group(1);
      }
    }

    if (StringUtils.isEmpty(res)) {
      String orgHeader = request.getHeader(Constants.AREX_TENANT_CODE);
      res = orgHeader == null ? Strings.EMPTY : orgHeader;
    }
    return res;
  }
}
