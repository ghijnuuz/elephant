package me.gzj.elephant.net;

import me.gzj.core.common.ServiceResult;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.CodeConst;
import me.gzj.elephant.TestUtil;
import me.gzj.elephant.model.ViewVideo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author ghijnuuz
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SiteServiceTest {
    @Autowired
    private SiteService siteService;

    @Test
    public void getBaseVideoList_FirstPage() {
        ServiceResult<Pair<List<BaseVideo>, Integer>> baseVideoListResult = siteService.getBaseVideoList(1);
        Assert.assertTrue(baseVideoListResult.isSuccess());
        Pair<List<BaseVideo>, Integer> pair = baseVideoListResult.getData();
        List<BaseVideo> baseVideoList = pair.getLeft();
        Assert.assertTrue(CollectionUtils.isNotEmpty(baseVideoList));
        BaseVideo baseVideo = baseVideoList.get(0);
        TestUtil.assertBaseVideo(baseVideo);
        int pageCount = pair.getRight();
        Assert.assertTrue(pageCount > 0);
    }

    @Test
    public void getBaseVideoList_LastPage() {
        ServiceResult<Pair<List<BaseVideo>, Integer>> baseVideoListResult = siteService.getBaseVideoList(100000000);
        Assert.assertTrue(baseVideoListResult.isSuccess());
        Pair<List<BaseVideo>, Integer> pair = baseVideoListResult.getData();
        int pageCount = pair.getRight();
        Assert.assertTrue(pageCount > 0);
    }

    @Test
    public void getViewVideo_Exist() {
        String viewkey = "b5781e9ea815cdd633e5";
        ServiceResult<ViewVideo> viewVideoResult = siteService.getViewVideo(viewkey);
        Assert.assertTrue(viewVideoResult.isSuccess());
        ViewVideo viewVideo = viewVideoResult.getData();
        TestUtil.assertViewVideo(viewVideo);
    }

    @Test
    public void getViewVideo_NotExist() {
        String viewkey = "test";
        ServiceResult<ViewVideo> viewVideoResult = siteService.getViewVideo(viewkey);
        Assert.assertFalse(viewVideoResult.isSuccess());
        Assert.assertEquals(viewVideoResult.getCode(), CodeConst.VIDEO_NOT_EXIST);
    }

    @Test
    public void download() {
        // todo
    }
}
