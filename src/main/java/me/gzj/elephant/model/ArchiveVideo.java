package me.gzj.elephant.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author ghijnuuz
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArchiveVideo extends BaseVideo {
    /**
     * 视频状态
     * 0 存在
     * 1 不存在
     */
    private int status = 0;
    /**
     * 下载状态
     * 0 未下载
     * 1 已下载
     */
    private int download = 0;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public ArchiveVideo(BaseVideo baseVideo) {
        this.setViewkey(baseVideo.getViewkey());
        this.setTitle(baseVideo.getTitle());
        this.setRuntime(baseVideo.getRuntime());
        this.setAdded(baseVideo.getAdded());
        this.setFrom(baseVideo.getFrom());
        this.setViews(baseVideo.getViews());
        this.setFavorites(baseVideo.getFavorites());
        this.setComments(baseVideo.getComments());
        this.setPoint(baseVideo.getPoint());
    }
}
