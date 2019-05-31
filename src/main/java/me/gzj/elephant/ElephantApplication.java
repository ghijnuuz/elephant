package me.gzj.elephant;

import me.gzj.commons.core.model.ServiceResult;
import me.gzj.commons.core.util.JsonUtil;
import me.gzj.elephant.service.ElephantService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author ghijnuuz
 */
@SpringBootApplication(scanBasePackages = {"me.gzj.elephant"})
@MapperScan("me.gzj.elephant.mapper")
public class ElephantApplication {

    public static void main(String[] args) {
        try {
            Options options = new Options();

            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse(options, args);

            List<String> argList = line.getArgList();
            if (CollectionUtils.isEmpty(argList)) {
                System.out.println("Commond not found.");
                return;
            }

            ConfigurableApplicationContext context = SpringApplication.run(ElephantApplication.class, args);
            ElephantService elephantService = context.getBean(ElephantService.class);

            String arg1 = argList.get(0);
            String arg2 = null;
            if (argList.size() >= 2) {
                arg2 = argList.get(1);
            }
            String arg3 = null;
            if (argList.size() >= 3) {
                arg3 = argList.get(2);
            }

            if (StringUtils.equalsIgnoreCase("archive", arg1)) {
                if (StringUtils.equalsIgnoreCase("update", arg2)) {
                    ServiceResult<Triple<Integer, Integer, Integer>> result = elephantService.updateAllArchiveVideo();
                    if (result.isSuccess()) {
                        System.out.println(String.format("Update archive video. Total count:%d, new count:%d, update count:%d",
                                result.getData().getLeft(), result.getData().getMiddle(), result.getData().getRight()));
                    } else {
                        System.out.println(JsonUtil.writeValueAsString(result));
                    }
                    return;
                } else if (StringUtils.equalsIgnoreCase("download", arg2)) {
                    if (StringUtils.isEmpty(arg3)) {
                        arg3 = "";
                    }
                    ServiceResult<Integer> result = elephantService.downloadArchiveVideo(arg3);
                    if (result.isSuccess()) {
                        System.out.println(String.format("Download archive video. Download count:%d", result.getData()));
                    } else {
                        System.out.println(JsonUtil.writeValueAsString(result));
                    }
                    return;
                }
            } else if (StringUtils.equalsIgnoreCase("online", arg1)) {
                if (StringUtils.equalsIgnoreCase("download", arg2)) {
                    ServiceResult<Integer> result = elephantService.downloadOnlineVideo();
                    if (result.isSuccess()) {
                        System.out.println(String.format("Download online video. Download count:%d", result.getData()));
                    } else {
                        System.out.println(JsonUtil.writeValueAsString(result));
                    }
                    return;
                }
            }

            System.out.println("Commond not found.");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }
}

