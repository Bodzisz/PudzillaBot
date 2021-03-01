package bodzisz.commands.audio;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;

public class Youtube {
    private YouTube youtube;
    private final String apiKey = "AIzaSyCsOoJisH_SFE7EXBCwKXDNpbSo5fUqBaI";
    YouTube.Search.List search;


    public Youtube() throws GeneralSecurityException, IOException {
        youtube = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
            }}).setApplicationName("youtube-video-url-getter").build();

        search = youtube.search().list("id,snippet");
        search.setKey(apiKey);
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setMaxResults((long)3);
    }

    public String getVideoId(String keyword) throws IOException {
        search.setQ(keyword);
        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();
        Iterator<SearchResult> itsearch = searchResultList.iterator();

        SearchResult singleVideo = itsearch.next();
        ResourceId rId = singleVideo.getId();

        return rId.getVideoId();
    }

}
