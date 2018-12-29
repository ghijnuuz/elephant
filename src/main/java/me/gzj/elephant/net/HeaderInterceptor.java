package me.gzj.elephant.net;

import me.gzj.elephant.ElephantProperties;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author ghijnuuz
 */
@Component
public class HeaderInterceptor implements Interceptor {
    private final ElephantProperties properties;

    @Autowired
    public HeaderInterceptor(ElephantProperties properties) {
        this.properties = properties;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .removeHeader("Accept").header("Accept", properties.getHeader().getAccept())
                .removeHeader("Accept-Encoding").header("Accept-Encoding", properties.getHeader().getAcceptEncoding())
                .removeHeader("Accept-Language").header("Accept-Language", properties.getHeader().getAcceptLanguage())
                .removeHeader("Cache-Control").header("Cache-Control", properties.getHeader().getCacheControl())
                .removeHeader("Cookie").header("Cookie", properties.getHeader().getCookie())
                .removeHeader("Proxy-Connection").header("Proxy-Connection", properties.getHeader().getProxyConnection())
                .removeHeader("Upgrade-Insecure-Requests").header("Upgrade-Insecure-Requests", properties.getHeader().getUpgradeInsecureRequests())
                .removeHeader("User-Agent").header("User-Agent", properties.getHeader().getUserAgent())
                .build();
        return chain.proceed(request);
    }
}
