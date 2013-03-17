package videoshot.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import videoshot.webapp.dao.VideoDao;
import videoshot.webapp.model.ScreenshotModel;
import videoshot.webapp.model.VideoModel;

import java.io.IOException;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    VideoDao videoDao;

    @RequestMapping("/index.html")
    public String entry(Model model) {
        List<VideoModel> allVideo = videoDao.findAll();

        model.addAttribute("allVideos", allVideo);

        return "index";
    }

    @RequestMapping("/ss.html")
    public String ssPage(Model model, @RequestParam(value = "id", required = true) Long videoId)
            throws IOException {
        List<ScreenshotModel> ssList = videoDao.findByVideoId(videoId);

        model.addAttribute("ssList", ssList);

        return "ss";
    }
}
