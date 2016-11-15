package com.nodhan.firebaseassgn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private SharedPreferences sharedPreferences;
    private final int REQUEST_CODE = 1234;
    private ImageView imageView;
    private String SHARED_PREF = "uploads";
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageView = (ImageView) findViewById(R.id.selected_image);


        findViewById(R.id.select_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        Intent.createChooser(
                                new Intent().setType("image/*") // creating intent and setting type of file
                                        .setAction(Intent.ACTION_GET_CONTENT // setting action of intent
                                        ), "Select an image") // setting a title
                        , REQUEST_CODE);
            }
        });

        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this); //creating progress dialog
            progressDialog.setTitle("Uploading"); // setting title for progress dialog
            progressDialog.show(); // showing progress dialog

            sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); // getting shared preferences
            final int count = sharedPreferences.getInt("count", 0); //getting count

            StorageReference storageReference = mStorageRef.child(count + ".jpg"); // setting path/filename to be saved in server

            storageReference.putFile(filePath) // adding file
                    //called when file upload is success
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            SharedPreferences.Editor editor = sharedPreferences.edit(); //getting shared preference file
                            editor.putString("uri" + count, String.valueOf(downloadUrl)); //adding url of uploaded image
                            showToast("File Uploaded!"); // showing toast
                            editor.putInt("count", count + 1); // incrementing count in shared preferences
                            editor.apply(); // commit changes
                            imageView.setImageDrawable(null); //setting image view to empty
                            progressDialog.dismiss(); // dismissing progress dialog
                        }
                    })
                    //called when upload fails
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss(); // dismissing progress dialog
                            showToast("Upload failed! Reason: " + exception.getMessage()); // showing toast
                        }
                    })
                    //called when upload progresses
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded (" + progress + "%)"); // setting message on how much has been uploaded
                        }
                    });
        } else {
            showToast("Upload failed!"); // showing toast
        }
    }

    /**
     * Displays a toast
     *
     * @param s message to be displayed
     */
    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    //called when an start activity for result execution is completed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu); //creating menu
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_images:
                sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE); //getting shared preferences
                final int count = sharedPreferences.getInt("count", 0); //getting count
                if (count > 0) {
                    startActivity(new Intent(getApplicationContext(), UploadedActivity.class)); //starting activity
                } else {
                    showToast("Upload an image to view!"); // showing toast
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
