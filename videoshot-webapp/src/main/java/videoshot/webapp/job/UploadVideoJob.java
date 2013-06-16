package videoshot.webapp.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import videoshot.webapp.dao.UploadVideoDao;
import videoshot.webapp.model.UploadVideoModel;
import videoshot.webapp.service.SpringApplicationContextService;
import videoshot.webapp.service.YouTubeUploadService;

import java.io.File;
import java.io.Serializable;

public class UploadVideoJob implements Serializable, IJob {

    private static final Logger log = LoggerFactory.getLogger(UploadVideoJob.class);

    private Long uploadVideoId;

    public UploadVideoJob(Long uploadVideoId) {
        this.uploadVideoId = uploadVideoId;
    }

    @Override
    public void runJob() {
        ApplicationContext appCtx = SpringApplicationContextService.getApplicationContext();
        UploadVideoDao uploadVideoDao = appCtx.getBean(UploadVideoDao.class);

        UploadVideoModel byId = uploadVideoDao.findById(this.uploadVideoId);

        String videoFilePath = byId.getPath();

        YouTubeUploadService uploadService = appCtx.getBean(YouTubeUploadService.class);
        long start = System.currentTimeMillis();
        uploadService.uploadVideo(new File(videoFilePath), "TODO add column video title");
        long el = System.currentTimeMillis() - start;
        log.info("Finish uploading video file path = {}, in {} ms", videoFilePath, el);

        //TODO Tone.6/16/13, update uploaded flag in uploadvideo
    }
}
