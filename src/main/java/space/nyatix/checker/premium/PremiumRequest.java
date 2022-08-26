package space.nyatix.checker.premium;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import space.nyatix.checker.common.Callback;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

/**
 * @author Nyatix
 * @since 26.08.2022 - 12:54
 **/
@RequiredArgsConstructor
public class PremiumRequest {

    private final ExecutorService executorService;

    private final Gson gson = new GsonBuilder().disableHtmlEscaping()
            .registerTypeAdapter(
                    String.class,
                    (JsonDeserializer<String>) (jsonElement, type, jsonDeserializationContext) ->
                            new String(jsonElement.getAsString().getBytes(), StandardCharsets.UTF_8)
            )
            .create();

    public void sendRequest(String username, Callback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                var url = new URL("https://mcapi.cloudprotected.net/uuid/" + username);

                var httpClient = HttpClients.createDefault();
                var httpGet = new HttpGet(url.toString());
                var response = httpClient.execute(httpGet);
                var scanner = new Scanner(response.getEntity().getContent());
                var responseBuilder = new StringBuilder();

                while (scanner.hasNext()) {
                    responseBuilder.append(scanner.nextLine());
                }

                var jsonObject = gson.fromJson(responseBuilder.toString(), JsonObject.class);
                var obj = jsonObject.get("result").getAsJsonArray();
                callback.accept(!(obj.get(0).getAsJsonObject().get("uuid") instanceof JsonNull));

                response.close();
                httpClient.close();
            } catch (IOException e) {
                callback.onFailure(e);
            }
        });
    }

}
