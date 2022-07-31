package Study.Board.oauth.provider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
}
