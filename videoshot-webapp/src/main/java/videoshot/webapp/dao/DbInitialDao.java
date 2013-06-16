package videoshot.webapp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DbInitialDao implements InitializingBean {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        //Init DB tables

        try {
            jdbcTemplate.getJdbcOperations().execute("SELECT COUNT(*) FROM video");
        } catch (DataAccessException e) {
            log.info("Creating table video");
            createVideoTable();
        }

        try {
            jdbcTemplate.getJdbcOperations().execute("SELECT COUNT(*) FROM screenshot");
        } catch (DataAccessException e) {
            log.info("Creating table screenshot");
            createScreenshotTable();
        }

        try {
            jdbcTemplate.getJdbcOperations().execute("SELECT COUNT(*) FROM uploadvideo");
        } catch (DataAccessException e) {
            log.info("Creating table uploadvideo");
            createUploadVideoTable();
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

    private void createUploadVideoTable() {
        String sql = "CREATE TABLE uploadvideo (id BIGINT AUTO_INCREMENT NOT NULL, " +
                "path VARCHAR(1024) NOT NULL, " +
                "title VARCHAR(1024) NULL, " +
                "isuploaded INT NOT NULL DEFAULT 0, " +
                " PRIMARY KEY (id) " +
                ") ENGINE=INNODB  CHARSET=UTF8 COLLATE utf8_general_ci";

        jdbcTemplate.getJdbcOperations().execute(sql);
    }
}
