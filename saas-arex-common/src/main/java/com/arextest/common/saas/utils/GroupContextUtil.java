package com.arextest.common.saas.utils;

import com.alibaba.ttl.TransmittableThreadLocal;

public class GroupContextUtil {

  private static final TransmittableThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();

  public static void setGroup(String group) {
    CONTEXT.set(group);
  }

  public static String getGroup() {
    return CONTEXT.get();
  }

  public static void clear() {
    CONTEXT.remove();
  }

}
