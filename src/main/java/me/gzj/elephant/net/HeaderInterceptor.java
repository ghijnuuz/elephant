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
                .removeHeader("User-Agent").header("User-Agent", properties.getHeader().getUserAgent())
                .removeHeader("Accept-Language").header("Accept-Language", properties.getHeader().getAcceptLanguage())
                .build();
        return chain.proceed(request);
    }
}
