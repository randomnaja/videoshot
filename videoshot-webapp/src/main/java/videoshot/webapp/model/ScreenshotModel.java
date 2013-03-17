package videoshot.webapp.model;

public class ScreenshotModel {

    private Long id;

    private Long timeStampInMicroSec;

    private String imagePath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeStampInMicroSec() {
        return timeStampInMicroSec;
    }

    public void setTimeStampInMicroSec(Long timeStampInMicroSec) {
        this.timeStampInMicroSec = timeStampInMicroSec;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
