package videoshot.webapp.model;

import java.io.Serializable;
import java.util.List;

public class VideoModel implements Serializable {

    private Long id;

    private String sourceVideoPath;

    private List<ScreenshotModel> ssList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceVideoPath() {
        return sourceVideoPath;
    }

    public void setSourceVideoPath(String sourceVideoPath) {
        this.sourceVideoPath = sourceVideoPath;
    }

    public List<ScreenshotModel> getSsList() {
        return ssList;
    }

    public void setSsList(List<ScreenshotModel> ssList) {
        this.ssList = ssList;
    }
}
