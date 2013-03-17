package videoshot.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import videoshot.webapp.model.ScreenshotModel;
import videoshot.webapp.model.VideoModel;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class VideoDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<VideoModel> findAll() {
        List<VideoModel> res = jdbcTemplate.getJdbcOperations().query("SELECT * FROM video",
                new Object[]{}, new VideoRowMapper());

        return res;
    }

    public VideoModel findByPath(String sourceVideoPath) {
        List<VideoModel> res = jdbcTemplate.getJdbcOperations().query("SELECT * FROM video WHERE path = ?",
                new Object[]{sourceVideoPath}, new VideoRowMapper());

        if (res.size() == 1) {
            return res.get(0);
        }

        return null;
    }

    public List<ScreenshotModel> findByVideoId(Long videoId) {
        List<ScreenshotModel> res =
                jdbcTemplate.getJdbcOperations().query("SELECT * FROM screenshot WHERE video_id = ?",
                        new Object[]{videoId}, new ScreenshotRowMapper());

        return res;
    }

    public class VideoRowMapper implements RowMapper<VideoModel> {

        @Override
        public VideoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            VideoModel model = new VideoModel();
            model.setId(rs.getLong("id"));
            model.setSourceVideoPath(rs.getString("path"));

            model.setSsList(findByVideoId(rs.getLong("id")));

            return model;
        }
    }

    public class ScreenshotRowMapper implements RowMapper<ScreenshotModel> {

        @Override
        public ScreenshotModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScreenshotModel model = new ScreenshotModel();
            model.setId(rs.getLong("id"));
            model.setTimeStampInMicroSec(rs.getLong("timescene"));
            model.setImagePath(rs.getString("imagepath"));

            return model;
        }
    }
}
