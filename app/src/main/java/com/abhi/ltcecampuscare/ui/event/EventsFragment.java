package com.abhi.ltcecampuscare.ui.event;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;



import android.widget.Toast;

import com.abhi.ltcecampuscare.R;


import com.abhi.ltcecampuscare.ui.GeminiApi;
import com.abhi.ltcecampuscare.ui.GeminiRequest;
import com.abhi.ltcecampuscare.ui.GeminiResponse;
import com.abhi.ltcecampuscare.ui.OnGeminiResult;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventsFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICK = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;

    EditText subjectEdit, bodyEdit;
    ImageView eventImage;
    Button btnSelectImage, btnPost;
    Uri imageUri;

    String TAG ="debug";
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    FirebaseAuth auth;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        subjectEdit = view.findViewById(R.id.eventSubject);
        bodyEdit = view.findViewById(R.id.eventBody);
        eventImage = view.findViewById(R.id.eventImage);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnPost = view.findViewById(R.id.btnPostEvent);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnSelectImage.setOnClickListener(v -> showImagePickerDialog());

        btnPost.setOnClickListener(v -> {
            String subject = subjectEdit.getText().toString().trim();
            String rawBody = bodyEdit.getText().toString().trim();

            if (subject.isEmpty() || rawBody.isEmpty()) {
                Toast.makeText(getContext(), "Please enter subject and description", Toast.LENGTH_SHORT).show();
                return;
            }

            formatWithGeminiREST(rawBody, new OnGeminiResult() {
                @Override
                public void onFormatted(String formattedText) {
                    saveEventToFirestore(subject, formattedText); // 👈 only this now
                }

                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(getContext(), "Gemini error: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        });



        return view;
    }

    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};
        new AlertDialog.Builder(getContext())
                .setTitle("Select Image From")
                .setItems(options, (dialog, which) -> {
                    if(which == 0){
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null){
            if(requestCode == REQUEST_IMAGE_PICK){
                imageUri = data.getData();
                eventImage.setImageURI(imageUri);
            } else if(requestCode == REQUEST_IMAGE_CAPTURE){
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageUri = getImageUriFromBitmap(photo);
                eventImage.setImageBitmap(photo);
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }


    private void saveEventToFirestore(String subject, String formattedBody) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String email = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = user.getUid();
        db.collection("Users").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("name");
                        String role = document.getString("role");
                        if (role == null) role = "User"; // default fallback

                        Map<String, Object> event = new HashMap<>();
                        event.put("subject", subject);
                        event.put("body", formattedBody);
                        event.put("timestamp", new Timestamp(new Date()));
                        event.put("postedBy", name != null ? name : email);
                        event.put("role", role);

                        db.collection("Events")
                                .add(event)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(getContext(), "Event Posted Successfully!", Toast.LENGTH_SHORT).show();
                                    clearForm();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void formatWithGeminiREST(String userText, OnGeminiResult callback) {
        btnPost.setEnabled(false); // Disable the button while processing

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GeminiApi api = retrofit.create(GeminiApi.class);

        // Instruction + user input
        String fullPrompt = "Format the following user announcement professionally for a college society app and give only one message not the additional message with that, that you always add before and after the message and no need of anything bold because the ** are visible that does not look good ok and elaborate it little and can include emojis:\n\n" + userText;

        // Prepare the content
        List<GeminiRequest.Part> parts = new ArrayList<>();
        parts.add(new GeminiRequest.Part(fullPrompt));

        List<GeminiRequest.Content> contents = new ArrayList<>();
        contents.add(new GeminiRequest.Content("user", parts));

        GeminiRequest body = new GeminiRequest();
        body.contents = contents;

        String apiKey = BuildConfig.GEMINI_API_KEY;

        // Call Gemini API
        Call<GeminiResponse> call = api.generateText(apiKey, body);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                btnPost.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String formattedText = response.body().candidates.get(0).content.parts.get(0).text;
                        callback.onFormatted(formattedText);
                    } catch (Exception e) {
                        callback.onError("Parsing error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onError("Gemini failed: HTTP " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                btnPost.setEnabled(true);
                callback.onError("Request failed: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }




    private void clearForm() {
        subjectEdit.setText("");
        bodyEdit.setText("");
        eventImage.setImageResource(R.drawable.ic_menu_slideshow);
        imageUri = null;
    }
}
