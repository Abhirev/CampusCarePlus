package com.abhi.ltcecampuscare.ui;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query; // Import Query

// You might not need @Path at all if the base URL already includes the common path
// and you only change the model or specific action.

// in GeminiApi.java
public interface GeminiApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent") // <--- CHANGE THIS LINE
    Call<GeminiResponse> generateText(
            @Query("key") String apiKey,
            @Body GeminiRequest body
    );
}