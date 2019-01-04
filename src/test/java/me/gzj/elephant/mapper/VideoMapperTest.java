package me.gzj.elephant.mapper;

import me.gzj.elephant.TestUtil;
import me.gzj.elephant.model.ArchiveVideo;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.CommonConst;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ghijnuuz
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VideoMapperTest {
    @Autowired
    private VideoMapper videoMapper;

    private String viewkey = "viewkey_" + RandomStringUtils.randomNumeric(16);

    private BaseVideo initBaseVideo = null;

    /**
     * 获取随机视频信息
     * @param viewkey
     * @return
     */
    private BaseVideo getBaseVideo(String viewkey) {
        BaseVideo baseVideo = new BaseVideo();
        baseVideo.setViewkey(viewkey);
        baseVideo.setTitle("title_" + RandomStringUtils.randomNumeric(16));
        baseVideo.setRuntime("runtime_" + RandomStringUtils.randomNumeric(4));
        baseVideo.setAdded(CommonConst.MYSQL_MIN_LOCAL_DATE_TIME);
        baseVideo.setFrom("from_" + RandomStringUtils.randomNumeric(16));
        baseVideo.setViews(RandomUtils.nextInt());
        baseVideo.setFavorites(RandomUtils.nextInt());
        baseVideo.setComments(RandomUtils.nextInt());
        baseVideo.setPoint(RandomUtils.nextInt());
        return baseVideo;
    }

    @Before
    public void before() {
        initBaseVideo = getBaseVideo(viewkey);
        int addResult = videoMapper.addVideo(initBaseVideo);
        Assert.assertEquals(addResult, 1);
    }

    @After
    public void after() {
        int deleteResult = videoMapper.deleteVideo(viewkey);
        Assert.assertEquals(deleteResult, 1);
    }

    @Test
    public void addVideo() {
        ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
        Assert.assertTrue(TestUtil.equals(initBaseVideo, archiveVideo, true));
    }

    @Test
    public void updateVideo() {
        BaseVideo baseVideo = getBaseVideo(viewkey);
        int updateResult = videoMapper.updateVideo(baseVideo);
        Assert.assertEquals(updateResult, 1);

        ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
        Assert.assertTrue(TestUtil.equals(baseVideo, archiveVideo, true));
    }

    @Test
    public void updateNoAddedVideo() {
        BaseVideo baseVideo = getBaseVideo(viewkey);
        int updateResult = videoMapper.updateNoAddedVideo(baseVideo);
        Assert.assertEquals(updateResult, 1);

        ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
        Assert.assertTrue(TestUtil.equals(baseVideo, archiveVideo, false));
    }

    @Test
    public void updateVideoStatus() {
        int status = 1;
        int updateResult = videoMapper.updateVideoStatus(viewkey, status);
        Assert.assertEquals(updateResult, 1);

        ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
        Assert.assertEquals(archiveVideo.getStatus(), status);
    }

    @Test
    public void updateVideoDownload() {
        int download = 1;
        int updateResult = videoMapper.updateVideoDownload(viewkey, download);
        Assert.assertEquals(updateResult, 1);

        ArchiveVideo archiveVideo = videoMapper.getVideo(viewkey);
        Assert.assertEquals(archiveVideo.getDownload(), download);
    }
}
