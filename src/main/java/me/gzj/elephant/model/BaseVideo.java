package me.gzj.elephant.model;

import lombok.Data;

import java.time.LocalDate;

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
    private LocalDate added = LocalDate.of(1000, 1, 1);
    /**
     * 作者
     */
    private String from = "";
    /**
     * 查看
     */
    private int views;
    /**
     * 收藏
     */
    private int favorites;
    /**
     * 留言
     */
    private int comments;
    /**
     * 积分
     */
    private int point;
}
