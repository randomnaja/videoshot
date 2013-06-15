package videoshot;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 */
public class Main2 {

    public static void main(String[] args) throws FileNotFoundException {
        ApplicationContext appCtx = initAppCtx();
        final NamedParameterJdbcTemplate jdbcTemplate = appCtx.getBean(NamedParameterJdbcTemplate.class);

        final String path = "/home/tone/Musics/MV/Zidane All in the touch - Hala Madrid I.mp4";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        //"INSERT INTO VIDEO (path) VALUES (?)", new Object[]{path}
        jdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("INSERT INTO video (path) VALUES (?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, path);

                return ps;
            }}, keyHolder);

        Number key = keyHolder.getKey();
        final long videoId = key.longValue();

        GenerateScreenShotUtil
                .generateScreenShot(new File(path),
                        new GenerateScreenShotUtil.OnImageWrite() {
                            @Override
                            public void write(BufferedImage image, long timeStampInMicroSec) {
                                try {
                                    String imgPath = "/home/tone/Desktop/video/ss_" + timeStampInMicroSec + ".png";

                                    double ratio = (double) image.getWidth() / 320.0;
                                    int newHeight = (int) (image.getHeight() * ratio);
                                    // resize buffered image
                                    final BufferedImage bufferedImage =
                                            new BufferedImage(320, newHeight, BufferedImage.TYPE_INT_RGB);
                                    final Graphics2D graphics2D = bufferedImage.createGraphics();
                                    graphics2D.setComposite(AlphaComposite.Src);
                                    graphics2D.drawImage(image, 0, 0, 320, newHeight, null);
                                    graphics2D.dispose();

                                    Sanselan.writeImage(bufferedImage, new File(imgPath),
                                            ImageFormat.IMAGE_FORMAT_PNG, null);

                                    jdbcTemplate.getJdbcOperations().update("INSERT INTO screenshot (timescene, imagepath, video_id) " +
                                            "VALUES (?, ?, ?)", new Object[]{timeStampInMicroSec, imgPath, videoId});
                                } catch (ImageWriteException | IOException e) {
                                    throw new RuntimeException("Cannot save image, " + e.getMessage(), e);
                                }
                            }
                        });


//        ChopVideoUtil.chopVideo(240039800L, 250018433L,
//                new File("/home/tone/Musics/MV/Zidane All in the touch - Hala Madrid I.mp4"),
//                new File("/tmp/chop.mp4"));
    }

    private static ApplicationContext initAppCtx() {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("/spring-context.xml");

        return ctx;
    }
}
