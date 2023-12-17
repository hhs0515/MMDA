package com.csed404.mmda.ui.home;

import com.csed404.mmda.BuildConfig;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class GptClient {
    private static final String OPENAI_API_KEY = BuildConfig.api_key;

    private static final OkHttpClient client = new OkHttpClient();

    public String generateTxt(String log){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String result = "";

        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            baseAi.put("role", "user");
            baseAi.put("content", "일기로 써줘.");

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
                .url("https://api.openai.com/v1/chat/completions")  //url 경로 수정됨
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
        } catch (IOException ignored){
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
