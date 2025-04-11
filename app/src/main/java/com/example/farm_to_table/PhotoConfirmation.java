package com.example.farm_to_table;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm_to_table.databinding.ActivityPhotoConfirmationBinding;
import com.example.farm_to_table.databinding.ActivityTrackOrderBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class PhotoConfirmation extends AppCompatActivity {

    private RecyclerView messageRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ImageButton imageButton;
    private MessageAdapter adapter;
    private ArrayList<Message> messageList;
    private Uri photoUri;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private ActivityPhotoConfirmationBinding binding;


    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                    messageList.add(new Message(photoUri.toString(), true, timestamp));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    messageRecyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                    messageList.add(new Message(selectedImage.toString(), true, timestamp));
                    adapter.notifyItemInserted(messageList.size() - 1);
                    messageRecyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoConfirmationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupMessageRecyclerView();
        setupButtons();
    }

    private void setupMessageRecyclerView() {
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        binding.messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.messageRecyclerView.setAdapter(adapter);
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, TrackOrder.class));
            finish();
        });

        binding.CameraButton.setOnClickListener(v -> showImagePickerDialog());

        binding.sendButton.setOnClickListener(v -> {
            String text = binding.messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                String timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                messageList.add(new Message(text, true, timestamp));
                adapter.notifyItemInserted(messageList.size() - 1);
                binding.messageEditText.setText("");
                binding.messageRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Photo")
                .setItems(new String[]{"Take Photo", "Choose from Gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        launchCamera();
                    } else {
                        openGallery();
                    }
                });
        builder.show();
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                photoUri = FileProvider.getUriForFile(this,
                        "com.example.farm_to_table.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraLauncher.launch(takePictureIntent);
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}