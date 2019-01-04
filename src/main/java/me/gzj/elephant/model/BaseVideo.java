package me.gzj.elephant.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ghijnuuz
 */
@Data
public class BaseVideo {
    /**
     * ViewKey
     */
    private String viewkey = "";
    /**
     * 标题
     */
    private String title = "";
    /**
     * 时长
     */
    private String runtime = "";
    /**
     * 添加时间
     */
    private LocalDateTime added = CommonConst.MYSQL_MIN_LOCAL_DATE_TIME;
    /**
     * 作者
     */
    private String from = "";
    /**
     * 查看
     */
    private int views = -1;
    /**
     * 收藏
     */
    private int favorites = -1;
    /**
     * 留言
     */
    private int comments = -1;
    /**
     * 积分
     */
    private int point = -1;
}
