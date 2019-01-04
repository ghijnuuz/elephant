package me.gzj.elephant;

import me.gzj.elephant.model.ArchiveVideo;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.CommonConst;
import me.gzj.elephant.model.ViewVideo;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

import java.time.LocalDateTime;

/**
 * @author ghijnuuz
 */
public class TestUtil {
    public static void assertBaseVideo(BaseVideo baseVideo) {
        Assert.assertNotNull(baseVideo);
        Assert.assertTrue(StringUtils.isNotEmpty(baseVideo.getViewkey()));
        Assert.assertTrue(StringUtils.isNotEmpty(baseVideo.getTitle()));
        Assert.assertTrue(StringUtils.isNotEmpty(baseVideo.getRuntime()));
        Assert.assertTrue(StringUtils.isNotEmpty(baseVideo.getFrom()));
        Assert.assertTrue(baseVideo.getViews() >= 0);
        Assert.assertTrue(baseVideo.getFavorites() >= 0);
        Assert.assertTrue(baseVideo.getComments() >= 0);
        Assert.assertTrue(baseVideo.getPoint() >= 0);
    }

    public static void assertViewVideo(ViewVideo viewVideo) {
        assertBaseVideo(viewVideo);
        Assert.assertTrue(CommonConst.MYSQL_MIN_LOCAL_DATE_TIME.isBefore(viewVideo.getAdded()));
        Assert.assertTrue(StringUtils.isNotEmpty(viewVideo.getDownloadUrl()));
    }

    public static void assertArchiveVideo(ArchiveVideo archiveVideo) {
        assertBaseVideo(archiveVideo);
        LocalDateTime added = archiveVideo.getAdded();
        boolean isAddedCorrect = CommonConst.MYSQL_MIN_LOCAL_DATE_TIME.isEqual(added) || CommonConst.MYSQL_MIN_LOCAL_DATE_TIME.isBefore(added);
        Assert.assertTrue(isAddedCorrect);
    }

    public static boolean equals(BaseVideo baseVideo, ArchiveVideo archiveVideo, boolean checkAdded) {
        if (baseVideo == null || archiveVideo == null) {
            return false;
        }
        if (!StringUtils.equals(archiveVideo.getViewkey(), baseVideo.getViewkey())) {
            return false;
        }
        if (!StringUtils.equals(archiveVideo.getTitle(), baseVideo.getTitle())) {
            return false;
        }
        if (!StringUtils.equals(archiveVideo.getRuntime(), baseVideo.getRuntime())) {
            return false;
        }
        if (checkAdded) {
            if (!archiveVideo.getAdded().isEqual(baseVideo.getAdded())) {
                return false;
            }
        }
        if (!StringUtils.equals(archiveVideo.getFrom(), baseVideo.getFrom())) {
            return false;
        }
        if (archiveVideo.getViews() != baseVideo.getViews()) {
            return false;
        }
        if (archiveVideo.getFavorites() != baseVideo.getFavorites()) {
            return false;
        }
        if (archiveVideo.getComments() != baseVideo.getComments()) {
            return false;
        }
        if (archiveVideo.getPoint() != baseVideo.getPoint()) {
            return false;
        }
        return true;
    }
}
