package com.csed404.mmda.ui.home;

import okhttp3.*;

import java.io.IOException;

public class GptClient {
    private static final String OPENAI_API_KEY = System.getenv("MY_OPENAI_KEY");
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/engines/davinci-codex/completions";

    public String generateText(String prompt) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("prompt", prompt)
                .build();

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                assert response.body() != null;
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
