package me.gzj.elephant.net;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 站点页面
 *
 * @author ghijnuuz
 */
public interface SitePage {
    /**
     * 视频列表
     * @param page
     * @return
     */
    @GET("/v.php?next=watch")
    Call<ResponseBody> videoList(@Query("page") int page);

    /**
     * 查看视频
     * @param viewkey
     * @return
     */
    @GET("/view_video.php")
    Call<ResponseBody> viewVideo(@Query("viewkey") String viewkey);

    /**
     * 下载
     * @param url
     * @return
     */
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);
}
