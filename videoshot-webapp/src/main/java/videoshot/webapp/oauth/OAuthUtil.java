package videoshot.webapp.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.util.Scanner;

/**
 *
 */
public class OAuthUtil {

    private static final String AUTHORIZE_URL = "https://www.google.com/accounts/OAuthAuthorizeToken?oauth_token=";
    private static final String PROTECTED_RESOURCE_URL = "http://uploads.gdata.youtube.com/feeds/api/users/default/uploads";

    public static void main(String[] args) {
//        OAuthService service = new ServiceBuilder()
//                .provider(GoogleApi.class)
//                .apiKey("AI39si7F9-EGljyXWxTJ3BSu7gZJZ9nIZZWeC7QOAU8qpecDYonqJ_eSold_KeD8FIHMyqV9U17YhWCF4smEQ4dbmTGLD67e8Q")
//                .apiSecret("")
//                .build();

        OAuthService service = new ServiceBuilder()
                .provider(GoogleApi.class)
                .apiKey("570239286540-7o885qls7km48sshg36cfq9ltkr2qjh7.apps.googleusercontent.com")
                .apiSecret("SZ6TZK7c9yl0gW2B-alzRLaE")
                .scope("http://gdata.youtube.com")
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== " + "Google" + "'s OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println("(if your curious it looks like this: " + requestToken + " )");
        System.out.println();

        System.out.println("Now go and authorize Scribe here:");
        System.out.println(AUTHORIZE_URL + requestToken.getToken());
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(requestToken, verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        request.addHeader("GData-Version", "3.0");
        Response response = request.send();
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");

    }
}
