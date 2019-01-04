package me.gzj.elephant;

import lombok.extern.slf4j.Slf4j;
import me.gzj.core.common.ServiceResult;
import me.gzj.core.util.DateTimeUtil;
import me.gzj.core.util.JsonUtil;
import me.gzj.elephant.mapper.VideoMapper;
import me.gzj.elephant.model.ArchiveVideo;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.CodeConst;
import me.gzj.elephant.model.ViewVideo;
import me.gzj.elephant.net.SiteService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

/**
 * @author ghijnuuz
 */
@Slf4j
@Service
public class ElephantService {
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
        log.info("Start update all archive video.");
        int totalVideoCount = 0;
        int newVideoCount = 0;
        int updateVideoCount = 0;
        try {
            int page = 1;
            boolean hasNextPage = true;
            while (hasNextPage) {
                ServiceResult<Pair<List<BaseVideo>, Integer>> baseVideoListResult = siteService.getBaseVideoList(page);
                if (!baseVideoListResult.isSuccess()) {
                    log.warn("Get base video list fail. page:{}, result:{}", page, JsonUtil.writeValueAsString(baseVideoListResult));
                    break;
                }

                Pair<List<BaseVideo>, Integer> pair = baseVideoListResult.getData();
                List<BaseVideo> baseVideoList = pair.getLeft();
                int pageCount = pair.getRight();

                int pageVideoCount = baseVideoList.size();
                totalVideoCount += pageVideoCount;

                if (page >= pageCount) {
                    hasNextPage = false;
                } else {
                    page++;
                }

                for (BaseVideo baseVideo : baseVideoList) {
                    String viewkey = baseVideo.getViewkey();

                    ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
                    if (archiveVideo != null) {
                        int updateResult = videoMapper.updateNoAddedVideo(baseVideo);
                        if (updateResult > 0) {
                            updateVideoCount++;
                        } else {
                            log.warn("Update no added video fail. baseVideo:{}", JsonUtil.writeValueAsString(baseVideo));
                        }
                    } else {
                        int addResult = videoMapper.addVideo(baseVideo);
                        if (addResult > 0) {
                            newVideoCount++;
                        } else {
                            log.warn("Add video fail. baseVideo:{}", JsonUtil.writeValueAsString(baseVideo));
                        }
                    }
                }
            }
            return ServiceResult.createSuccessResult(Triple.of(totalVideoCount, newVideoCount, updateVideoCount));
        } catch (Exception ex) {
            log.error("updateAllArchiveVideo error", ex);
        } finally {
            log.info("Update all archive video. Total count:{}, new count:{}, update count:{}",
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
            log.info("Start download video. viewkey:{}", viewkey);
            ServiceResult<ViewVideo> viewVideoResult = siteService.getViewVideo(viewkey);
            if (!viewVideoResult.isSuccess()) {
                if (viewVideoResult.getCode() == CodeConst.VIDEO_NOT_EXIST) {
                    // 视频不存在
                    videoMapper.updateVideoStatus(viewkey, 1);
                    log.info("Video not exist. viewkey:{}", viewkey);
                    return ServiceResult.create(viewVideoResult.getCode(), viewVideoResult.getMessage());
                } else {
                    log.warn("View video fail. viewkey:{}, result:{}", viewkey, JsonUtil.writeValueAsString(viewVideoResult));
                    return ServiceResult.createFailResult();
                }
            }

            ViewVideo viewVideo = viewVideoResult.getData();

            // 更新存档视频
            ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
            if (archiveVideo != null) {
                videoMapper.updateVideo(viewVideo);
                if (archiveVideo.getDownload() == 1) {
                    return ServiceResult.create(CodeConst.VIDEO_ALREADY_DOWNLOAD, "视频已下载");
                }
            } else {
                videoMapper.addVideo(viewVideo);
            }

            // 下载视频
            String downloadUrl = viewVideo.getDownloadUrl();
            if (StringUtils.isEmpty(downloadUrl)) {
                log.warn("Video download restricted. viewkey:{}", viewkey);
                return ServiceResult.create(CodeConst.DOWNLOAD_RESTRICTED, "下载被限制");
            }
            String filename = String.format("%s_%s_%s.mp4", DateTimeUtil.format(viewVideo.getAdded(), "yyyyMMdd"),
                    viewVideo.getViewkey(), viewVideo.getTitle());
            String fullFilename = String.format("%s%s", properties.getDownload().getPath(), filename);
            ServiceResult<Long> downloadResult = siteService.download(downloadUrl, fullFilename);
            if (downloadResult.isSuccess()) {
                long filesize = downloadResult.getData();
                if (filesize >= 100 * 1024) {
                    // 正常视频应该大于100KB
                    log.info("Downlaod video success. viewkey:{}, filename:{}, filesize:{}", viewkey, filename, filesize);
                    videoMapper.updateVideoDownload(viewkey, 1);
                    return ServiceResult.createSuccessResult();
                } else {
                    log.info("Downlaod video fail. viewkey:{}, filename:{}, filesize:{}", viewkey, filename, filesize);
                    FileUtils.forceDelete(new File(fullFilename));
                    return ServiceResult.createFailResult();
                }
            } else {
                log.warn("Download video fail. viewkey:{}", viewkey);
                FileUtils.forceDelete(new File(fullFilename));
                return ServiceResult.createFailResult();
            }
        } catch (Exception ex) {
            log.error("viewkey:{}", viewkey, ex);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 下载存档视频
     * @param order
     * @return
     */
    public ServiceResult<Integer> downloadArchiveVideo(String order) {
        log.info("Start download archive video. order:{}", order);
        int downloadCount = 0;
        try {
            boolean canDownload = true;
            int size = 20;
            while (canDownload) {
                List<ArchiveVideo> archiveVideoList = null;
                switch (order) {
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
                        return ServiceResult.createParameterErrorResult();
                }
                if (CollectionUtils.isNotEmpty(archiveVideoList)) {
                    for (ArchiveVideo archiveVideo : archiveVideoList) {
                        String viewkey = archiveVideo.getViewkey();
                        ServiceResult downloadResult = downloadVideo(viewkey);
                        if (downloadResult.isSuccess()) {
                            downloadCount++;
                        } else {
                            if (downloadResult.getCode() != CodeConst.VIDEO_NOT_EXIST &&
                                    downloadResult.getCode() != CodeConst.VIDEO_ALREADY_DOWNLOAD) {
                                canDownload = false;
                                break;
                            }
                        }
                    }
                }
            }

            return ServiceResult.createSuccessResult(downloadCount);
        } catch (Exception ex) {
            log.error("order:{}", order, ex);
        } finally {
            log.info("Download archive video. Download count:{}", downloadCount);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 下载在线视频
     * @return
     */
    public ServiceResult<Integer> downloadOnlineVideo() {
        log.info("Start download online video.");
        int downloadCount = 0;
        try {
            boolean canDownload = true;
            int page = 1;
            while (canDownload) {
                ServiceResult<Pair<List<BaseVideo>, Integer>> baseVideoListResult = siteService.getBaseVideoList(page);
                if (baseVideoListResult.isSuccess()) {
                    List<BaseVideo> baseVideoList = baseVideoListResult.getData().getLeft();
                    if (CollectionUtils.isEmpty(baseVideoList)) {
                        canDownload = false;
                        continue;
                    }
                    for (BaseVideo baseVideo : baseVideoList) {
                        String viewkey = baseVideo.getViewkey();
                        ServiceResult downloadResult = downloadVideo(viewkey);
                        if (downloadResult.isSuccess()) {
                            downloadCount++;
                        } else {
                            if (downloadResult.getCode() != CodeConst.VIDEO_NOT_EXIST &&
                                    downloadResult.getCode() != CodeConst.VIDEO_ALREADY_DOWNLOAD) {
                                canDownload = false;
                                break;
                            }
                        }
                    }
                    page++;
                }
            }

            return ServiceResult.createSuccessResult(downloadCount);
        } catch (Exception ex) {
            log.error("error", ex);
        } finally {
            log.info("Download online video. Download count:{}", downloadCount);
        }
        return ServiceResult.createFailResult();
    }
}
