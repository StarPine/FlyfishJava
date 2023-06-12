package fly.fish.report;

public class RequestConfig {
    private String body;
    private String url;

    public RequestConfig(String url, String body) {
        this.body = body;
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "RequestConfig{" +
                "body='" + body + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
