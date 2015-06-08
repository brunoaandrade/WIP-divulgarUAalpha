package login;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by LuisAfonso on 26-05-2015.
 */
public class IdentityUAApi  extends DefaultApi10a {

    /*private static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize?oauth_token=%s";
    private static final String REQUEST_TOKEN_RESOURCE = "api.twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_RESOURCE = "api.twitter.com/oauth/access_token";*/

    public static final String REQUEST_TOKEN_RESOURCE 		= "http://identity.ua.pt/oauth/request_token";
    public static final String ACCESS_TOKEN_RESOURCE 		= "http://identity.ua.pt/oauth/access_token";
    public static final String AUTHORIZE_URL 	= "http://identity.ua.pt/oauth/authorize?oauth_token=%s";
    public static final String GETDATA_URL 	= "http://identity.ua.pt/oauth/get_data?scope=%s&format=json";

    public static final String CONSUMER_KEY 	= "_344025380d028eacea3dd374020ed56f5a6a07306c";
    public static final String CONSUMER_SECRET 	= "_9fbfe4053eb54949f1c46677b4a3beb29d42c0b26c";



    @Override
    public String getRequestTokenEndpoint() {
        return REQUEST_TOKEN_RESOURCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {

        return String.format(AUTHORIZE_URL,requestToken.getToken());
    }


    public static String getDataUrl(String scope) {

        return String.format(GETDATA_URL,scope);
    }

    public static String getConsumerSecret() {
        return CONSUMER_SECRET;
    }

    public static String getConsumerKey() {
        return CONSUMER_KEY;
    }


}
