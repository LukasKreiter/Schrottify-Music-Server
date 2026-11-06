package auk.spl.schrottify.SubsonicAPI.TestStubs;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.UUID;

public class SubsonicClientStub {
    private final String baseUrl;
    private final String username;
    private final String password;
    private final HttpClient http = HttpClient.newHttpClient();

    public SubsonicClientStub(String baseUrl, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
    }

    public String md5(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public String request(String method, String... params) throws Exception {
        String salt = UUID.randomUUID().toString().substring(0, 12);
        String token = md5(password + salt);

        StringBuilder url = new StringBuilder(baseUrl + "/rest/" + method + ".view?");
        url.append("u=").append(username);
        url.append("&t=").append(token);
        url.append("&s=").append(salt);
        url.append("&v=1.15.0");
        url.append("&c=MyJavaClient");
        url.append("&f=json");

        for (String p : params) url.append("&").append(p);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url.toString()))
                .GET()
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }

    public static void main(String[] args) throws Exception {
        SubsonicClientStub client = new SubsonicClientStub("http://localhost:13013/airsonic", "admin", "admin");
        System.out.println(client.request("ping"));
        System.out.println(client.request("getIndexes"));
    }

}

