package me.gzj.elephant.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author ghijnuuz
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ViewVideo extends BaseVideo {
    /**
     * 下载地址
     */
    private String downloadUrl = "";
}
