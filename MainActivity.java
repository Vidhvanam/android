package com.beautycamera.demoproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beautycamera.demoproject.ApiCall.RetrofitAPICall;
import com.beautycamera.demoproject.model.Example;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<GridItem> gridItemList;
    private GridAdapter gridAdapter;
    int gridSize = 7;
    private static final int FILE_SELECT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate: ");

        //   callApi();

     //   showFileChooser();

        setAdapter();
    }

    private void setAdapter() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridSize));
        gridItemList = generateGridItems(gridSize);
        gridAdapter = new GridAdapter(this, gridItemList, position -> {
            highlightRowAndColumn(position);
            Toast.makeText(this, "Item clicked: " + position, Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(gridAdapter);
    }

    private void showFileChooser() {

        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
            Uri imageUri = data.getData();
            Log.e(TAG, "onActivityResult: " + imageUri);
            Log.e(TAG, "onActivityResult: " + getRealPathFromURI(imageUri));

            File file = new File(getRealPathFromURI(imageUri));

            callApi(file);
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void callApi(File file) {

        Log.e(TAG, "callApi: file : " + file.exists());

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("name", "abcd")
                .addFormDataPart("email", "viraj123@gmail.com")
                .addFormDataPart("age", "abcd")
                .addFormDataPart("nick_name", "abcd")
                .addFormDataPart("gender", "abcd")
                .addFormDataPart("age", "19")
                .addFormDataPart("about_me", "abcd")
                .addFormDataPart("latitude", "abcd")
                .addFormDataPart("longitude", "abcd")
                .addFormDataPart("food", "abcd")
                .addFormDataPart("drink", "abcd")
                .addFormDataPart("smoke", "abcd")
                .addFormDataPart("intrested_in", "abcd")
                .addFormDataPart("looking_for", "abcd")
                .addFormDataPart("hobbies", "abcd")
                .addFormDataPart("profile_image", file.getName(), RequestBody.create(MultipartBody.FORM, file));


        RequestBody requestBody = builder.build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://68.183.84.213/dating_app/API/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPICall retrofitAPI = retrofit.create(RetrofitAPICall.class);
        Call<Example> call = retrofitAPI.UserProfile(requestBody);

        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(@NonNull Call<Example> call, @NonNull Response<Example> response) {
                Log.e(TAG, "onResponse: success");
                Example example = response.body();
                if (example != null) {
                    Log.e(TAG, "onResponse: " + example.getData().getProfileImage());
                    for (int i = 0; i < example.getData().getProfileImageList().size(); i++) {
                        Log.e(TAG, "onResponse: " + example.getData().getProfileImageList().get(i));
                    }
                } else {
                    Log.e(TAG, "onResponse: null example");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Example> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void highlightRowAndColumn(int position) {
        clearSelections();

        int clickPoint = position+1;
        int userInput = gridSize;
        int clickPointMod = getMod(clickPoint, userInput);

        int start = clickPoint;
        int startPointMod = 0;

        while (start > 0) {
            // System.out.println(start);
          //  matrix.put(start, "1");

            gridItemList.get(start-1).setSelected(true);
            start -= userInput + 1;
            startPointMod = getMod(start, userInput);
            if (start < 1 || startPointMod >= clickPointMod) {
                break;
            }
        }
        // show();

        start = clickPoint;
        while (start > 0) {
            // System.out.println(start);
            //matrix.put(start, "2");

            gridItemList.get(start-1).setSelected(true);
            start -= userInput - 1;
            startPointMod = getMod(start, userInput);
            if (start < 1 || startPointMod <= clickPointMod) {
                break;
            }
        }
        // show();

        start = clickPoint;
        while (start > 0) {
            // System.out.println(start);
            //matrix.put(start, "3");

            gridItemList.get(start-1).setSelected(true);
            start += userInput - 1;
            startPointMod = getMod(start, userInput);
            if (start > userInput * userInput || startPointMod >= clickPointMod) {
                break;
            }
        }
        // show();

        start = clickPoint;
        while (start > 0) {
            // System.out.println(start);
            //matrix.put(start, "4");

            gridItemList.get(start-1).setSelected(true);
            start += userInput + 1;
            startPointMod = getMod(start, userInput);
            if (start > userInput * userInput || startPointMod <= clickPointMod) {
                break;
            }
        }

        gridAdapter.notifyDataSetChanged();
    }

    public int getMod(int number, int modVal) {
        int ans = number % modVal;
        if (ans == 0) {
            return modVal;
        }
        return ans;
    }

    private List<GridItem> generateGridItems(int gridSize) {
        List<GridItem> items = new ArrayList<>();
        for (int i = 0; i < gridSize * gridSize; i++) {
            items.add(new GridItem(String.valueOf(i)));
        }
        return items;
    }

    /*private void highlightRowAndColumn(int position) {
        int row = position / gridSize;
        int col = position % gridSize;

        Log.e(TAG, "highlightRowAndColumn row: "+row );
        Log.e(TAG, "highlightRowAndColumn col: "+col );

        clearSelections();

        for (int i = 0; i < gridSize; i++) {
            int rowPosition = row * gridSize + i;
            int colPosition = i * gridSize + col;

            gridItemList.get(rowPosition).setSelected(true);
            gridItemList.get(colPosition).setSelected(true);

            Log.e(TAG, "highlightRowAndColumn rowPosition: "+rowPosition);
            Log.e(TAG, "highlightRowAndColumn colPosition: "+colPosition);
        }

        gridAdapter.notifyDataSetChanged();
    }*/


   /* private void highlightRowAndColumn(int position) {
        int row = position / gridSize;
        int col = position % gridSize;

        Log.e(TAG, "highlightRowAndColumn row: " + row);
        Log.e(TAG, "highlightRowAndColumn col: " + col);

        clearSelections();

        for (int i = 0; i < gridSize; i++) {
            int rowPosition = row * gridSize + i;
            int colPosition = i * gridSize + col;
            int diagonal1Position = i * gridSize + i; // Diagonal from top-left to bottom-right
            int diagonal2Position = (gridSize - 1 - i) * gridSize + i; // Diagonal from top-right to bottom-left

            gridItemList.get(rowPosition).setSelected(true);
            gridItemList.get(colPosition).setSelected(true);
            gridItemList.get(diagonal1Position).setSelected(true);
            gridItemList.get(diagonal2Position).setSelected(true);


            Log.e(TAG, "highlightRowAndColumn rowPosition: " + rowPosition);
            Log.e(TAG, "highlightRowAndColumn colPosition: " + colPosition);
            Log.e(TAG, "highlightRowAndColumn mainDiagonalPosition: " + diagonal1Position);
            Log.e(TAG, "highlightRowAndColumn antiDiagonalPosition: " + diagonal2Position);
        }


        gridAdapter.notifyDataSetChanged();
    }*/

    private void clearSelections() {
        for (GridItem item : gridItemList) {
            item.setSelected(false);
        }
    }
}