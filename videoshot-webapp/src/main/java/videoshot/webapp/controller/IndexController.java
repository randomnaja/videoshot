package videoshot.webapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import videoshot.ChopVideoUtil;
import videoshot.webapp.dao.UploadVideoDao;
import videoshot.webapp.dao.VideoDao;
import videoshot.webapp.job.UploadVideoJob;
import videoshot.webapp.model.ScreenshotModel;
import videoshot.webapp.model.UploadVideoModel;
import videoshot.webapp.model.VideoModel;
import videoshot.webapp.service.HazelcastEntryService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private UploadVideoDao uploadVideoDao;

    @Autowired
    private HazelcastEntryService hazelcastEntryService;

    @RequestMapping("/index.html")
    public String entry(Model model) {
        List<VideoModel> allVideo = videoDao.findAll();

        model.addAttribute("allVideos", allVideo);

        return "index";
    }

    @RequestMapping("/ss.html")
    public String ssPage(Model model, @RequestParam(value = "id", required = true) Long videoId,
            @RequestParam(value = "periodInSec", required = false, defaultValue = "5") Long periodInSec,
            @RequestParam(value = "startMicroSec", required = false) Long startMicroSec,
            @RequestParam(value = "endMicroSec", required = false) Long endMicroSec)
            throws IOException {
        List<ScreenshotModel> ssList = videoDao.findByVideoId(videoId);

        model.addAttribute("ssList", ssList);
        model.addAttribute("periodInSec", periodInSec);
        model.addAttribute("startMicroSec", startMicroSec);
        model.addAttribute("endMicroSec", endMicroSec);

        return "ss";
    }

    @RequestMapping(value = "/chop.html")
    @ResponseBody
    public String chopVideo(@RequestParam(value = "id", required = true) Long videoId,
            @RequestParam(value = "startMicroSec", required = true) Long startMicroSec,
            @RequestParam(value = "endMicroSec", required = true) Long endMicroSec) {

        VideoModel byId = videoDao.findById(videoId);

        if (byId == null) {
            throw new IllegalArgumentException("Video id " + videoId + " is not found");
        }

        String videoPath = byId.getSourceVideoPath();

        File videoFile = new File(videoPath);

        if (! videoFile.exists() || ! videoFile.isFile()) {
            throw new IllegalArgumentException("Video file " + videoFile + " is not existed or not a file");
        }

        File tempOutputFile = new File("/tmp/" + System.currentTimeMillis() + ".mp4");

        log.info("Chopping video, source = {}, startMicroSec = {}, endMicroSec = {}, outputTo = {}",
                videoFile, startMicroSec, endMicroSec, tempOutputFile);
        long start = System.currentTimeMillis();
        ChopVideoUtil.chopVideo(startMicroSec, endMicroSec, videoFile, tempOutputFile);
        long finish = (System.currentTimeMillis() - start);

        // Insert to upload video
        UploadVideoModel uploadVideoModel = new UploadVideoModel();
        uploadVideoModel.setPath(tempOutputFile.getAbsolutePath());
        uploadVideoModel.setUploaded(false);
        uploadVideoDao.save(uploadVideoModel);

        return "<h2>Finish!!! in (ms)" + finish + "</h2>" +
                "<h3>File is at " + tempOutputFile + "</h3>";
    }

    @RequestMapping(value = "/upload.html")
    @ResponseBody
    public String uploadVideo(@RequestParam(value = "id", required = true) Long uploadVideoId)
            throws InterruptedException {

        UploadVideoModel byId = uploadVideoDao.findById(uploadVideoId);

        if (byId == null) {
            throw new IllegalArgumentException("upload video id = " + uploadVideoId + " is not found");
        }

        hazelcastEntryService.getUploadVideoQueue().put(new UploadVideoJob(uploadVideoId));

        return "<h2>Upload job added !!!" + byId.getPath() + "</h2>";
    }
}
