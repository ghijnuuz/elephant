package me.gzj.elephant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ghijnuuz
 */
@Component
@ConfigurationProperties(
        prefix = "elephant"
)
@Data
public class ElephantProperties {

    /**
     * 站点配置
     */
    private Site site = new Site();

    @Data
    public static class Site {
        /**
         * 网址
         * http[s]://www.example.com/
         */
        private String baseUrl = "";
    }

    /**
     * 头信息
     */
    private Header header = new Header();

    @Data
    public static class Header {
        /**
         * Accept
         */
        private String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8";
        /**
         * Accept-Encoding
         */
        private String acceptEncoding = "gzip, deflate";
        /**
         * Accept-Language
         */
        private String acceptLanguage = "zh-CN,zh;q=0.9";
        /**
         * Cache-Control
         */
        private String cacheControl = "max-age=0";
        /**
         * Cookie
         */
        private String cookie = "";
        /**
         * Proxy-Connection
         */
        private String proxyConnection = "keep-alive";
        /**
         * Upgrade-Insecure-Requests
         */
        private String upgradeInsecureRequests = "1";
        /**
         * User-Agent
         */
        private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    }

    /**
     * 代理配置
     */
    private Proxy proxy = new Proxy();

    @Data
    public static class Proxy {
        /**
         * 代理类型
         */
        private java.net.Proxy.Type type = java.net.Proxy.Type.DIRECT;
        /**
         * 主机名
         */
        private String hostname = "";
        /**
         * 端口
         */
        private int port = 0;
    }

    /**
     * 下载配置
     */
    private Download download = new Download();

    @Data
    public static class Download {
        /**
         * 下载路径
         */
        private String path = "";
    }
}
