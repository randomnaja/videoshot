package videoshot.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import videoshot.webapp.model.UploadVideoModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class UploadVideoDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<UploadVideoModel> findBy(boolean isUploaded) {
        List<UploadVideoModel> res = jdbcTemplate.getJdbcOperations()
                .query("SELECT id, path, title, isuploaded FROM uploadvideo WHERE isuploaded = ?",
                        new Object[]{isUploaded ? 1 : 0}, new UploadVideoRowMapper());

        return res;
    }

    public UploadVideoModel findById(long id) {
        try {
            UploadVideoModel uploadVideoModel = jdbcTemplate.getJdbcOperations()
                    .queryForObject("SELECT id,path,title,isuploaded FROM uploadvideo WHERE id = ?",
                            new Object[]{id}, new UploadVideoRowMapper());

            return uploadVideoModel;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public UploadVideoModel save(UploadVideoModel model) {
        if (model.getId() == null) {
            return insert(model);
        } else {
            return update(model);
        }
    }

    private UploadVideoModel insert(UploadVideoModel model) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.getJdbcOperations().update(new UploadVideoPrepareStatement(model),
                keyHolder);

        model.setId(keyHolder.getKey().longValue());

        return model;
    }

    private UploadVideoModel update(UploadVideoModel model) {
        jdbcTemplate.getJdbcOperations().update("UPDATE uploadvideo SET path = ?, title = ?, isuploaded = ? WHERE id = ?",
                model.getPath(), model.getTitle(), model.getUploaded() ? 1 : 0, model.getId());
        return model;
    }

    private static final class UploadVideoRowMapper implements RowMapper<UploadVideoModel> {

        @Override
        public UploadVideoModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            UploadVideoModel model = new UploadVideoModel();
            model.setId(rs.getLong("id"));
            model.setPath(rs.getString("path"));
            model.setTitle(rs.getString("title"));
            model.setUploaded(rs.getInt("isuploaded") != 0);

            return model;
        }
    }

    private static final class UploadVideoPrepareStatement implements PreparedStatementCreator {

        private UploadVideoModel model;

        private UploadVideoPrepareStatement(UploadVideoModel model) {
            this.model = model;
        }

        @Override
        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
            PreparedStatement ps =
                    con.prepareStatement("INSERT INTO uploadvideo (path,title,isuploaded) VALUES (?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, model.getPath());
            ps.setString(2, model.getTitle());
            ps.setInt(3, model.getUploaded() ? 1 : 0);

            return ps;
        }
    }
}
