package me.gzj.elephant.mapper;

import me.gzj.elephant.model.ArchiveVideo;
import me.gzj.elephant.model.BaseVideo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ghijnuuz
 */
@Repository
public interface VideoMapper {
    @Insert("insert into `video` " +
            "(`viewkey`,`title`,`runtime`,`added`,`from`,`views`,`favorites`,`comments`,`point`) " +
            "values " +
            "(#{viewkey},#{title},#{runtime},#{added},#{from},#{views},#{favorites},#{comments},#{point})")
    int addVideo(BaseVideo video);

    @Update("update `video` " +
            "set `title`=#{title},`runtime`=#{runtime},`added`=#{added},`from`=#{from},`views`=#{views},`favorites`=#{favorites},`comments`=#{comments},`point`=#{point} " +
            "where `viewkey`=#{viewkey}")
    int updateVideo(BaseVideo video);

    @Update("update `video` " +
            "set `title`=#{title},`runtime`=#{runtime},`from`=#{from},`views`=#{views},`favorites`=#{favorites},`comments`=#{comments},`point`=#{point} " +
            "where `viewkey`=#{viewkey}")
    int updateNoAddedVideo(BaseVideo video);

    @Update("update `video` set `status`=#{status} where `viewkey`=#{viewkey}")
    int updateVideoStatus(@Param("viewkey") String viewkey, @Param("status") int status);

    @Update("update `video` set `download`=#{download} where `viewkey`=#{viewkey}")
    int updateVideoDownload(@Param("viewkey") String viewkey, @Param("download") int download);

    @Results(id = "video", value = {
            @Result(column = "viewkey", property = "viewkey", id = true),
            @Result(column = "title", property = "title"),
            @Result(column = "runtime", property = "runtime"),
            @Result(column = "added", property = "added"),
            @Result(column = "from", property = "from"),
            @Result(column = "views", property = "views"),
            @Result(column = "favorites", property = "favorites"),
            @Result(column = "comments", property = "comments"),
            @Result(column = "point", property = "point"),
            @Result(column = "status", property = "status"),
            @Result(column = "download", property = "download"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime")
    })
    @Select("select * from `video` where `viewkey`=#{viewkey}")
    ArchiveVideo getVideo(@Param("viewkey") String viewkey);

    @ResultMap("video")
    @Select("select * from `video` where `status`=0 and `download`=0 order by `views` desc limit #{size}")
    List<ArchiveVideo> getNormalNotDownloadVideoListOrderByViews(@Param("size") int size);

    @ResultMap("video")
    @Select("select * from `video` where `status`=0 and `download`=0 order by `favorites` desc limit #{size}")
    List<ArchiveVideo> getNormalNotDownloadVideoListOrderByFavorites(@Param("size") int size);

    @ResultMap("video")
    @Select("select * from `video` where `status`=0 and `download`=0 order by `comments` desc limit #{size}")
    List<ArchiveVideo> getNormalNotDownloadVideoListOrderByComments(@Param("size") int size);

    @ResultMap("video")
    @Select("select * from `video` where `status`=0 and `download`=0 order by `point` desc limit #{size}")
    List<ArchiveVideo> getNormalNotDownloadVideoListOrderByPoint(@Param("size") int size);

    @ResultMap("video")
    @Select("select * from `video` where `status`=0 and `download`=0 order by `create_time` desc limit #{size}")
    List<ArchiveVideo> getNormalNotDownloadVideoListOrderByCreateTime(@Param("size") int size);
}
