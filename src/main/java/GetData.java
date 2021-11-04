import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.json.JSONObject;

public class GetData {
  private static final String API_SEARCH = "api/issues/search";
  private static final int PAGE_SIZE = 500;

  private static final String BASE_URL = "https://sonarcloud.io/";

  public static void main(String[] args) throws IOException, InterruptedException {
    collectData("src/main/resources/example", "projectKey");
  }

  private static void collectData(String fileOutput, String projectKey) throws IOException, InterruptedException {
    FileWriter fileWriter = new FileWriter(fileOutput);
    PrintWriter printWriter = new PrintWriter(fileWriter);

    int page = 1;
    String input = collectData(page, projectKey);
    printWriter.println(input);
    JSONObject jsonObject = new JSONObject(input);
    int total = (int) jsonObject.get("total");

    while (page * PAGE_SIZE < total) {
      page++;
      input = collectData(page, projectKey);
      printWriter.println(input);
    }

    printWriter.close();
  }


  private static String collectData(int page, String projectKey) throws IOException, InterruptedException {
    Map<String, String> params = Map.of("projectKeys", projectKey,
      "ps", "" + PAGE_SIZE,
      "p", "" + page);

    HttpClient client = HttpClient.newHttpClient();
    URI uri = URI.create(BASE_URL + API_SEARCH + "?" + getParamsString(params));
    HttpRequest request = HttpRequest.newBuilder()
      .uri(uri)
      .build();
    System.out.println(uri.toString());

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }


  public static String getParamsString(Map<String, String> params) {
    StringBuilder result = new StringBuilder();

    for (Map.Entry<String, String> entry : params.entrySet()) {
      result.append(entry.getKey());
      result.append("=");
      result.append(entry.getValue());
      result.append("&");
    }
    String resultString = result.toString();
    return resultString.substring(0, resultString.length() - 1);
  }

}
