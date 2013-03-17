package videoshot.webapp;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class CommonsUtil {

    public static File getFileByPath(String path) {
        return FileUtils.getFile(path);
    }
}
