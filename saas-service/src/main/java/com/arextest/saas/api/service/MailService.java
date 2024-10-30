package com.arextest.saas.api.service;

/**
 * @author b_yu
 * @since 2024/7/29
 */
public interface MailService {

  boolean sendEmail(String mailBox, String subject, String htmlMsg);
}
