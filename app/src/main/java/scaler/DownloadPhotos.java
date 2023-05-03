package scaler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DownloadPhotos {
    public static void main(String[] args) throws Exception {
        System.out.println(">>>>>> DownloadPhotos....");

        RestClient restClient = new RestClient();
        // a hashmap to store 2 photos per album by albumId
        Map<Integer, List<Photo>> albums = new HashMap();

        try {

            var apiResponse = restClient.getPhotosAPI().getPhotos().execute();
            apiResponse.body().forEach(photo -> {
                if (photo.getAlbumId() <= 10) {
                    addPhoto(albums, photo);
                    // System.out.println(">>>>> " + photo);
                }
            });
            // download the photos stored in the hashmap
            for (List<Photo> albumPhotos : albums.values()) {
                for (Photo photo : albumPhotos) {
                    System.out.println(">>>>> downloading" + photo);
                    restClient.downloadPhoto(photo);
                }
            }
        } finally {
            restClient.close();
            System.out.println("Retrofit call completed. Terminating the program...");
        }

    }

    public static void addPhoto(Map<Integer, List<Photo>> photoAlbum, Photo photo) {
        // check if the album exists in the photoAlbum dictionary
        int albumId = photo.getAlbumId();
        if (photoAlbum.containsKey(albumId)) {
            // if the album exists, check the size of the list of photos
            List<Photo> albumPhotos = photoAlbum.get(albumId);
            if (albumPhotos.size() < 2) {
                albumPhotos.add(photo);
                System.out.println(photo);
            }
        } else {
            // if the album doesn't exist, create a new list of photos and add the photo to
            // it
            List<Photo> albumPhotos = new ArrayList<>();
            albumPhotos.add(photo);
            System.out.println(photo.getAlbumId() + ", " + photo.getId());
            photoAlbum.put(albumId, albumPhotos);
        }
    }
}

interface PhotosAPI {
    @GET("/photos")
    Call<List<Photo>> getPhotos();
}

class RestClient {
    private PhotosAPI photosAPI = null;
    private OkHttpClient okHttpClient = null;

    public RestClient() {
        okHttpClient = new OkHttpClient();
        final String BASE_URL = "https://jsonplaceholder.typicode.com";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        photosAPI = retrofit.create(PhotosAPI.class);
    }

    public PhotosAPI getPhotosAPI() {
        return photosAPI;
    }

    public void close() {
        okHttpClient.dispatcher().executorService().shutdown();
        okHttpClient.connectionPool().evictAll();
    }

    public void downloadPhoto(Photo photo) {
        try {
            // Create folder for album if it doesn't exist
            File folder = new File("app/albums", "Album " + photo.getAlbumId());
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Build request with photo URL
            Request request = new Request.Builder()
                    .url(photo.getUrl())
                    .build();

            // Execute request and save response to file
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                File file = new File(folder, photo.getId() + ".jpg");
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                sink.writeAll(response.body().source());
                sink.close();
                System.out.println("Downloaded photo " + photo.getId() + " to " + file.getAbsolutePath());
            } else {
                System.out.println("Failed to download photo " + photo.getId() + ": " + response.code() + " "
                        + response.message());
            }
        } catch (IOException e) {
            System.out.println("Failed to download photo " + photo.getId() + ": " + e.getMessage());
        }
    }
}

class Photo {
    int albumId;
    int id;
    String title;
    String url;
    String thumbnailUrl;

    public int getAlbumId() {
        return albumId;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String toString() {
        return "Photo{" +
                "albumId=" + albumId +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", thumbnailUrl='" + thumbnailUrl +
                '}';
    }
}