package com.example.myapplication.demo_api;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
implementation 'com.squareup.retrofit2:retrofit:2.0.2'
implementation 'com.squareup.retrofit2:converter-gson:2.0.2'
implementation 'com.google.code.gson:gson:2.4'
implementation 'com.squareup.okhttp3:logging-interceptor:4.3.1'
implementation 'com.squareup.retrofit2:converter-jackson:2.7.1'
implementation 'com.squareup.okhttp3:okhttp:4.3.1'
*/

public class RetrofitApiCall extends AppCompatActivity {
    private static final String TAG = "ApiCall";
    private RecyclerView recyclerView;
    private Button checkBucket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_call);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        checkBucket=(Button)findViewById(R.id.checkBucket);

        checkBucket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RetrofitApiCall.this,BucketActivity.class);
                startActivity(intent);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        retrofitAPI.createPost(2).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(@NonNull Call<Example> call, @NonNull Response<Example> response) {
                Example example = response.body();
                if (example != null) {
                    UserAdapter userAdapter = new UserAdapter(RetrofitApiCall.this, example.getData());
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetrofitApiCall.this, LinearLayoutManager.VERTICAL, false);
                    recyclerView.setAdapter(userAdapter);
                    recyclerView.setLayoutManager(layoutManager);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + call.toString() + ": " + t.getMessage());
            }
        });
    }


    public interface RetrofitAPI {
        @GET("users")
        Call<Example> createPost(@Query("page") int page);
    }

public interface ApiInterface {
    @Headers({"Content-Type: application/json"})
    @POST("v1/chat/completions")
    Call<CompletionResponse> chatCompletion(@Header("Authorization") String authorization, @Body CompletionRequest request);
}
}
