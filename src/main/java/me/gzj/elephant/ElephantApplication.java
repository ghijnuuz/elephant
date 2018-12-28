package me.gzj.elephant;

import me.gzj.core.common.ServiceResult;
import me.gzj.core.util.JsonUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author ghijnuuz
 */
@SpringBootApplication(scanBasePackages = {"me.gzj.elephant"})
@MapperScan("me.gzj.elephant.mapper")
public class ElephantApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext context = SpringApplication.run(ElephantApplication.class, args);
            ElephantService elephantService = context.getBean(ElephantService.class);

            if (args.length < 1) {
                System.out.println("Please run with args.");
            } else {
                String command = args[0];
                ServiceResult result = null;
                switch (command) {
                    case "update":
                        result = elephantService.updateAllArchiveVideo();
                        System.out.println(JsonUtil.writeValueAsString(result));
                        break;
                    case "download":
                        if (args.length < 2) {
                            System.out.println("Download miss type.");
                        } else {
                            String type = args[1];
                            result = elephantService.downloadArchiveVideo(type);
                            System.out.println(JsonUtil.writeValueAsString(result));
                        }
                        break;
                    default:
                        System.out.println(String.format("Command:%s not found.", command));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

}

