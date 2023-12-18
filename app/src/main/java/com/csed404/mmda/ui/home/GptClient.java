package com.csed404.mmda.ui.home;

import com.csed404.mmda.BuildConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GptClient {
    private static final String OPENAI_API_KEY = BuildConfig.api_key;

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    public String generateTxt(String log, String content){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String result = "";

        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            baseAi.put("role", "system");
            baseAi.put("content", "Automated diary writer를 찾아줘서 고마워! 어떤 것을 도와줄까?");
            baseAi.put("role", "user");
            if(content.isEmpty()){
                baseAi.put("content", "생각, 느낌, 경험을 표현하는 오늘의 일기를 써줘. 일기는 개인적이고 통찰력있게 작성해줘\n" +
                        " 시간 로그는 포함하지 말아줘");
            }
            else baseAi.put("content", content);

            userMsg.put("role", "user");
            userMsg.put("content", log);

            arr.put(baseAi);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {

            object.put("model", "gpt-3.5-turbo");
            object.put("messages", arr);
        } catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer "+ OPENAI_API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(response.isSuccessful()){
                assert response.body() != null;
                JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
            }
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return result;
    }

    public String generatePic(String keyword){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String image = "";

        JSONObject object = new JSONObject();
        try {
            object.put("model", "dall-e-3");
            object.put("prompt", keyword);
            object.put("n", 1);
            object.put("size", "1024x1024"); // Image size
            object.put("response_format", "b64_json");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer "+ OPENAI_API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(response.isSuccessful()){
                assert response.body() != null;
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray dataArray = jsonObject.getJSONArray("data");
                if (dataArray.length() > 0) {
                    JSONObject dataObject = dataArray.getJSONObject(0);
                    image = dataObject.getString("b64_json");
                } else {
                }
            }
        } catch (IOException | JSONException e){
            e.printStackTrace();
        }

        return image;
    }
}
