package videoshot.webapp.service;

import com.google.gdata.client.authn.oauth.GoogleOAuthParameters;
import com.google.gdata.client.authn.oauth.OAuthException;
import com.google.gdata.client.authn.oauth.OAuthHmacSha1Signer;
import com.google.gdata.client.youtube.YouTubeService;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.data.media.mediarss.MediaCategory;
import com.google.gdata.data.media.mediarss.MediaDescription;
import com.google.gdata.data.media.mediarss.MediaKeywords;
import com.google.gdata.data.media.mediarss.MediaTitle;
import com.google.gdata.data.youtube.VideoEntry;
import com.google.gdata.data.youtube.YouTubeMediaGroup;
import com.google.gdata.data.youtube.YouTubeNamespace;
import com.google.gdata.util.ServiceException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class YouTubeUploadService {

    public static final String DEFAULT_USER = "default";

    public static final String VIDEO_UPLOAD_FEED = "http://uploads.gdata.youtube.com/feeds/api/users/"
            + DEFAULT_USER + "/uploads";

    private String applicationName = "test-youtube-upload";

    private String developerKey = "";
    private String oAuthToken = "";
    private String oAuthTokenSecret = "";
    private String oAuthConsumerKey = "";
    private String oAuthConsumerSecret = "";

    private String mimeType = "video/mp4";

    public void uploadVideo(File videoFile, String videoTitle) {

        YouTubeService service = new YouTubeService(applicationName, developerKey);

        GoogleOAuthParameters oauthParam = new GoogleOAuthParameters();
        oauthParam.setOAuthToken(oAuthToken);
        oauthParam.setOAuthTokenSecret(oAuthTokenSecret);
        oauthParam.setOAuthConsumerKey(oAuthConsumerKey);
        oauthParam.setOAuthConsumerSecret(oAuthTokenSecret);
        oauthParam.setScope("http://gdata.youtube.com");

        try {
            service.setOAuthCredentials(oauthParam, new OAuthHmacSha1Signer());
        } catch (OAuthException e) {
            e.printStackTrace();
            return;
        }

        VideoEntry newEntry = new VideoEntry();

        YouTubeMediaGroup mg = newEntry.getOrCreateMediaGroup();

        mg.addCategory(new MediaCategory(YouTubeNamespace.CATEGORY_SCHEME, "Tech"));
        mg.setTitle(new MediaTitle());
        mg.getTitle().setPlainTextContent(videoTitle);
        mg.setKeywords(new MediaKeywords());
        mg.getKeywords().addKeyword("gdata-test");
        mg.setDescription(new MediaDescription());
        mg.getDescription().setPlainTextContent(videoTitle);
        MediaFileSource ms = new MediaFileSource(videoFile, mimeType);
        newEntry.setMediaSource(ms);

        try {
            service.insert(new URL(VIDEO_UPLOAD_FEED), newEntry);
        } catch (ServiceException se) {
            System.out.println("Sorry, your upload was invalid:");
            System.out.println(se.getResponseBody());
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Video uploaded successfully!");

    }

}
