package me.gzj.elephant;

import me.gzj.core.common.ServiceResult;
import me.gzj.core.util.DateTimeUtil;
import me.gzj.core.util.JsonUtil;
import me.gzj.elephant.mapper.VideoMapper;
import me.gzj.elephant.model.ArchiveVideo;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.ViewVideo;
import me.gzj.elephant.net.SiteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ghijnuuz
 */
@Service
public class ElephantService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ElephantProperties properties;
    private final SiteService siteService;
    private final VideoMapper videoMapper;

    @Autowired
    public ElephantService(ElephantProperties properties, SiteService siteService, VideoMapper videoMapper) {
        this.properties = properties;
        this.siteService = siteService;
        this.videoMapper = videoMapper;
    }

    /**
     * 更新存档视频
     * @return
     */
    public ServiceResult<Triple<Integer, Integer, Integer>> updateAllArchiveVideo() {
        logger.info("Start update all archive video.");
        int totalVideoCount = 0;
        int newVideoCount = 0;
        int updateVideoCount = 0;
        try {
            int page = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                ServiceResult<List<BaseVideo>> baseVideoListResult = siteService.getBaseVideoList(page);
                if (!baseVideoListResult.isSuccess()) {
                    logger.warn("Get base video list fail. page:{}, result:{}", page, JsonUtil.writeValueAsString(baseVideoListResult));
                    hasNextPage = false;
                    break;
                }

                int pageVideoCount = baseVideoListResult.getData().size();
                totalVideoCount += pageVideoCount;
                logger.debug("Get page:{} video count:{}", page, pageVideoCount);

                for (BaseVideo baseVideo : baseVideoListResult.getData()) {
                    String viewkey = baseVideo.getViewkey();

                    ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
                    if (archiveVideo != null) {
                        int updateResult = videoMapper.updateNoAddedVideo(baseVideo);
                        if (updateResult > 0) {
                            updateVideoCount++;
                        } else {
                            logger.warn("Update no added video fail. baseVideo:{}", JsonUtil.writeValueAsString(baseVideo));
                        }
                    } else {
                        int addResult = videoMapper.addVideo(baseVideo);
                        if (addResult > 0) {
                            newVideoCount++;
                        } else {
                            logger.warn("Add video fail. baseVideo:{}", JsonUtil.writeValueAsString(baseVideo));
                        }
                    }
                }

                if (pageVideoCount > 0) {
                    page++;
                } else {
                    hasNextPage = false;
                }
            }
            return ServiceResult.createSuccessResult(Triple.of(totalVideoCount, newVideoCount, updateVideoCount));
        } catch (Exception ex) {
            logger.error("updateAllArchiveVideo error", ex);
        } finally {
            logger.info("Update all archive video. Total count:{}, new count:{}, update count:{}",
                    totalVideoCount, newVideoCount, updateVideoCount);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 下载视频
     * @param viewkey
     * @return
     */
    public ServiceResult downloadVideo(String viewkey) {
        try {
            logger.info("Start download video. viewkey:{}", viewkey);
            ServiceResult<ViewVideo> viewVideoResult = siteService.getViewVideo(viewkey);
            if (!viewVideoResult.isSuccess()) {
                if (viewVideoResult.getCode() == -1000) {
                    // 视频不存在
                    videoMapper.updateVideoStatus(viewkey, 1);
                    logger.info("Video miss. viewkey:{}", viewkey);
                } else {
                    logger.warn("View video fail. viewkey:{}, result:{}", viewkey, JsonUtil.writeValueAsString(viewVideoResult));
                }
                return ServiceResult.createFailResult();
            }

            ViewVideo viewVideo = viewVideoResult.getData();

            // 更新存档视频
            videoMapper.updateVideo(viewVideo);

            // 下载视频
            String downloadUrl = viewVideo.getDownloadUrl();
            if (StringUtils.isEmpty(downloadUrl)) {
                logger.warn("Video download url empty");
                return ServiceResult.createFailResult();
            }
            String filename = String.format("%s_%s_%s.mp4", DateTimeUtil.format(viewVideo.getAdded(), "yyyyMMdd"),
                    viewVideo.getViewkey(), viewVideo.getTitle());
            String fullFilename = String.format("%s%s", properties.getDownload().getPath(), filename);
            ServiceResult<Long> downloadResult = siteService.download(downloadUrl, fullFilename);
            if (downloadResult.isSuccess()) {
                long filesize = downloadResult.getData();
                if (filesize >= 100 * 1024) {
                    // 正常视频应该大于100KB
                    logger.info("Downlaod video success. viewkey:{}, filename:{}, filesize:{}", viewkey, filename, filesize);
                    videoMapper.updateVideoDownload(viewkey, 1);
                    return ServiceResult.createSuccessResult();
                } else {
                    logger.info("Downlaod video fail. viewkey:{}, filename:{}, filesize:{}", viewkey, filename, filesize);
                    return ServiceResult.createFailResult();
                }
            } else {
                logger.warn("Download video fail. viewkey:{}", viewkey);
                return ServiceResult.createFailResult();
            }
        } catch (Exception ex) {
            logger.error("viewkey:{}", viewkey, ex);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 下载存档视频
     * @param type
     * @return
     */
    public ServiceResult<Integer> downloadArchiveVideo(String type) {
        logger.info("Start download archive video. type:{}", type);
        int downloadCount = 0;
        try {
            boolean canDownload = true;
            int size = 20;
            while (canDownload) {
                List<ArchiveVideo> archiveVideoList = null;
                switch (type) {
                    case "views":
                        archiveVideoList = videoMapper.getNormalNotDownloadVideoListOrderByViews(size);
                        break;
                    case "favorites":
                        archiveVideoList = videoMapper.getNormalNotDownloadVideoListOrderByFavorites(size);
                        break;
                    case "comments":
                        archiveVideoList = videoMapper.getNormalNotDownloadVideoListOrderByComments(size);
                        break;
                    case "point":
                        archiveVideoList = videoMapper.getNormalNotDownloadVideoListOrderByPoint(size);
                        break;
                    case "create_time":
                        archiveVideoList = videoMapper.getNormalNotDownloadVideoListOrderByCreateTime(size);
                        break;
                    default:
                }
                if (CollectionUtils.isNotEmpty(archiveVideoList)) {
                    for (ArchiveVideo archiveVideo : archiveVideoList) {
                        String viewkey = archiveVideo.getViewkey();
                        ServiceResult downloadResult = downloadVideo(viewkey);
                        if (downloadResult.isSuccess()) {
                            downloadCount++;
                        } else {
                            canDownload = false;
                            break;
                        }
                    }
                }
            }

            return ServiceResult.createSuccessResult(downloadCount);
        } catch (Exception ex) {
            logger.error("type:{}", type, ex);
        } finally {
            logger.info("Download archive video. Download count:{}", downloadCount);
        }
        return ServiceResult.createFailResult();
    }
}
