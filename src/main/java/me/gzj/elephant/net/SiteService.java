package me.gzj.elephant.net;

import me.gzj.core.common.ServiceResult;
import me.gzj.core.util.DateTimeUtil;
import me.gzj.elephant.ElephantProperties;
import me.gzj.elephant.model.BaseVideo;
import me.gzj.elephant.model.ViewVideo;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ghijnuuz
 */
@Service
public class SiteService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SitePage sitePage;

    @Autowired
    public SiteService(ElephantProperties properties, HeaderInterceptor headerInterceptor) {
        Proxy proxy = null;
        if (properties.getProxy().getType() != Proxy.Type.DIRECT) {
            InetSocketAddress socketAddress = new InetSocketAddress(properties.getProxy().getHostname(), properties.getProxy().getPort());
            proxy = new Proxy(properties.getProxy().getType(), socketAddress);
        }

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(headerInterceptor);
        if (proxy != null) {
            clientBuilder.proxy(proxy);
        }
        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder().
                client(client).
                baseUrl(properties.getSite().getBaseUrl()).
                build();

        this.sitePage = retrofit.create(SitePage.class);
    }

    /**
     * 获取视频列表
     * @param page
     * @return
     */
    public ServiceResult<List<BaseVideo>> getBaseVideoList(int page) {
        if (page <= 0) {
            return ServiceResult.createParameterErrorResult();
        }

        try {
            Call<ResponseBody> call = sitePage.videoList(page);
            Response<ResponseBody> response;
            try {
                response = call.execute();
            } catch (Exception ex) {
                // 异常时等待1秒重试
                Thread.sleep(1000);
                response = call.clone().execute();
            }
            if (!response.isSuccessful()) {
                return ServiceResult.createFailResult();
            }

            ResponseBody body = response.body();
            Document document = Jsoup.parse(body.string());

            List<BaseVideo> baseVideoList = new ArrayList<>();
            Elements elementsVideo = document.select("#videobox .listchannel");
            elementsVideo.forEach(element -> {
                BaseVideo baseVideo = new BaseVideo();

                Elements elementsLink = element.select("a");
                if (!elementsLink.isEmpty()) {
                    Element elementLink = elementsLink.first();

                    HttpUrl httpUrl = HttpUrl.parse(elementLink.attr("href").trim());
                    if (httpUrl == null) {
                        return;
                    }
                    String viewkey = httpUrl.queryParameter("viewkey");
                    if (StringUtils.isNotEmpty(viewkey)) {
                        baseVideo.setViewkey(viewkey);
                    }

                    Elements elementsTitle = elementsLink.select("img");
                    String title = elementsTitle.attr("title").trim();
                    if (StringUtils.isNotEmpty(title)) {
                        baseVideo.setTitle(title);
                    }
                }

                Elements elementsInfo = element.select(".info");
                if (elementsInfo.size() >= 7) {
                    String runtime = elementsInfo.get(0).nextSibling().toString().trim();
                    if (StringUtils.isNotEmpty(runtime)) {
                        baseVideo.setRuntime(runtime);
                    }
                    String from = elementsInfo.get(2).nextSibling().toString().trim();
                    if (StringUtils.isNotEmpty(from)) {
                        baseVideo.setFrom(from);
                    }
                    int views = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(3).nextSibling().toString())));
                    baseVideo.setViews(views);
                    int favorites = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(4).nextSibling().toString()).trim()));
                    baseVideo.setFavorites(favorites);
                    int comments = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(5).nextSibling().toString()).trim()));
                    baseVideo.setComments(comments);
                    int point = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(6).nextSibling().toString()).trim()));
                    baseVideo.setPoint(point);
                }

                baseVideoList.add(baseVideo);
            });

            return ServiceResult.createSuccessResult(baseVideoList);
        } catch (Exception ex) {
            logger.error("page:{}", page, ex);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 获取视频
     * @param viewkey
     * @return
     */
    public ServiceResult<ViewVideo> getViewVideo(String viewkey) {
        if (StringUtils.isEmpty(viewkey)) {
            return ServiceResult.createParameterErrorResult();
        }

        try {
            Call<ResponseBody> call = sitePage.viewVideo(viewkey);
            Response<ResponseBody> response;
            try {
                response = call.execute();
            } catch (Exception ex) {
                // 异常时等待1秒重试
                Thread.sleep(1000);
                response = call.clone().execute();
            }
            if (!response.isSuccessful()) {
                return ServiceResult.createFailResult();
            }

            HttpUrl currentHttpUrl = response.raw().request().url();
            String path = currentHttpUrl.encodedPath();
            String type = currentHttpUrl.queryParameter("type");
            if (StringUtils.equals(path, "/error.php") && StringUtils.equals(type, "video_missing")) {
                return ServiceResult.create(-1000, "视频不存在");
            }

            ResponseBody body = response.body();
            Document document = Jsoup.parse(body.string());

            ViewVideo viewVideo = new ViewVideo();
            viewVideo.setViewkey(viewkey);

            Elements elementsTitle = document.select("#viewvideo-title");
            if (!elementsTitle.isEmpty()) {
                String title = elementsTitle.text().trim();
                if (StringUtils.isNotEmpty(title)) {
                    viewVideo.setTitle(title);
                }
            }

            Elements elementsInfo = document.select("#useraction .info");
            if (elementsInfo.size() >= 5) {
                String runtime = elementsInfo.get(0).nextSibling().toString().trim();
                if (StringUtils.isNotEmpty(runtime)) {
                    viewVideo.setRuntime(runtime);
                }
                int views = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(1).nextSibling().toString())));
                viewVideo.setViews(views);
                int comments = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(2).nextSibling().toString())));
                viewVideo.setComments(comments);
                int favorites = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(3).nextSibling().toString())));
                viewVideo.setFavorites(favorites);
                int point = NumberUtils.toInt(StringUtils.normalizeSpace(StringEscapeUtils.unescapeHtml4(elementsInfo.get(4).nextSibling().toString())));
                viewVideo.setPoint(point);
            }

            Elements elementsDay = document.select("#videodetails-content .title");
            if (elementsDay.size() > 2) {
                LocalDate added = DateTimeUtil.toLocalDate(elementsDay.get(1).text().trim(), "yyyy-MM-dd");
                viewVideo.setAdded(added);
            }

            Elements elementsVideo = document.select("video source");
            if (!elementsVideo.isEmpty()) {
                String downloadUrl = elementsVideo.attr("src").trim();
                if (StringUtils.isNotEmpty(downloadUrl)) {
                    viewVideo.setDownloadUrl(downloadUrl);
                }
            }

            return ServiceResult.createSuccessResult(viewVideo);
        } catch (Exception ex) {
            logger.error("viewkey:{}", viewkey, ex);
        }
        return ServiceResult.createFailResult();
    }

    /**
     * 下载
     * @param url
     * @param filename
     * @return
     */
    public ServiceResult<Long> download(String url, String filename) {
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(filename)) {
            return ServiceResult.createParameterErrorResult();
        }

        try {
            Call<ResponseBody> call = sitePage.download(url);
            Response<ResponseBody> response = call.execute();
            if (!response.isSuccessful()) {
                return ServiceResult.createFailResult();
            }

            long byteCount = 0;
            try (FileOutputStream output = new FileOutputStream(filename)) {
                byteCount = IOUtils.copyLarge(response.body().byteStream(), output);
            }

            return ServiceResult.createSuccessResult(byteCount);
        } catch (Exception ex) {
            logger.error("url:{}, filename:{}", url, filename, ex);
        }
        return ServiceResult.createFailResult();
    }
}
