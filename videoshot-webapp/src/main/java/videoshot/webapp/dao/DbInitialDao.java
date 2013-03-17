package videoshot.webapp.dao;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DbInitialDao implements InitializingBean {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        //Init DB tables

        try {
            jdbcTemplate.getJdbcOperations().execute("SELECT COUNT(*) FROM video");
        } catch (DataAccessException e) {
            createVideoTable();
        }

        try {
            jdbcTemplate.getJdbcOperations().execute("SELECT COUNT(*) FROM screenshot");
        } catch (DataAccessException e) {
            createScreenshotTable();
        }
    }

    private void createVideoTable() {
        String sql = "CREATE TABLE video (id BIGINT AUTO_INCREMENT NOT NULL, path varchar(2048), PRIMARY KEY (id))";

        jdbcTemplate.getJdbcOperations().execute(sql);
    }

    private void createScreenshotTable() {
        String sql = "CREATE TABLE screenshot (id BIGINT AUTO_INCREMENT NOT NULL, timescene BIGINT NOT NULL, " +
                "imagepath varchar(2048), video_id BIGINT NOT NULL, PRIMARY KEY (id), " +
                "Foreign Key (video_id) references video(id) on delete cascade on update cascade)" +
                " ENGINE=INNODB  CHARSET=UTF8 COLLATE utf8_general_ci";

        jdbcTemplate.getJdbcOperations().execute(sql);
    }
}
