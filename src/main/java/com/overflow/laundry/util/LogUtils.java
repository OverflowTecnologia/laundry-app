package com.overflow.laundry.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LogUtils {

  private static final String LOG_PREFIX = "[LAUNDRY-APP] ";
  public static final Logger log = LoggerFactory.getLogger(LogUtils.class);

  public static void logInfo(String message) {
    log.info("{} {}", LOG_PREFIX + "INFO", message);
  }

  public static void logError(String message, Exception ex) {
    log.error("{} {}", LOG_PREFIX + "ERROR", message, ex);
  }

  public static void logWarn(String message, Exception ex) {
    log.warn("{} {}", LOG_PREFIX + "WARN", message, ex);
  }
}
