package me.gzj.elephant.model;

import me.gzj.core.util.DateTimeUtil;

import java.time.LocalDateTime;

/**
 * @author ghijnuuz
 */
public class CommonConst {
    /**
     * MySQL最小LocalDateTime
     */
    public static final LocalDateTime MYSQL_MIN_LOCAL_DATE_TIME = DateTimeUtil.toLocalDateTime(1000);
}