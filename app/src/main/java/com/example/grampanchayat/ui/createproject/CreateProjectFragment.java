package com.example.grampanchayat.ui.createproject;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.grampanchayat.databinding.FragmentCreateProjectBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.example.grampanchayat.HomeActivity;
import com.example.grampanchayat.data.model.News;
import com.example.grampanchayat.databinding.FragmentCreateProjectBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreateProjectFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private String imageUrl;
    private TextView postNews;
    private EditText newsTitle, newsDescription,newsBudget,newsTimeline;
    private ImageView newsImage;

    private AppCompatButton addImage;

    private FragmentCreateProjectBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCreateProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        postNews = binding.postNews;
        newsTitle = binding.newsTitle;
        newsDescription = binding.newsDescription;
        newsBudget=binding.budget;
        newsTimeline=binding.timeline;
        newsImage = binding.newsImage;
        addImage = binding.addImage;

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open file chooser when "Add Image" button is clicked
                openFileChooser();
            }
        });

        postNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = newsTitle.getText().toString();
                String description = newsDescription.getText().toString();
                String budget = newsBudget.getText().toString();
                String timeline = newsTimeline.getText().toString();

                if (title.isEmpty()) {
                    newsTitle.setError("Title is required");
                    newsTitle.requestFocus();
                    return;
                } else {
                    newsTitle.setError(null);
                }

                if (description.isEmpty()) {
                    newsDescription.setError("Description is required");
                    newsDescription.requestFocus();
                    return;
                } else {
                    newsDescription.setError(null);
                }
                if (budget.isEmpty()) {
                    newsBudget.setError("Budget is required");
                    newsBudget.requestFocus();
                    return;
                } else {
                    newsTitle.setError(null);
                }
                if (timeline.isEmpty()) {
                    newsTimeline.setError("Timeline is required");
                    newsTimeline.requestFocus();
                    return;
                } else {
                    newsTitle.setError(null);
                }

                uploadNews();
            }
        });

        return root;
    }

    private void openFileChooser() {
        // Intent to open image file chooser
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the file chooser result
            imageUri = data.getData();
            // Set the selected image to the ImageView
            newsImage.setImageURI(imageUri);
        } else {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNews() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            StorageReference filePath = FirebaseStorage.getInstance().getReference("News")
                    .child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            StorageTask uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("News");

                        String newsId = reference.push().getKey();

                        // Getting current date
                        SimpleDateFormat SDFormat = new SimpleDateFormat();
                        Calendar cal = Calendar.getInstance();
                        String curr_date = SDFormat.format(cal.getTime());

                        News news = new News(newsId, newsTitle.getText().toString(),
                                newsDescription.getText().toString(),newsBudget.getText().toString(),newsTimeline.getText().toString(), imageUrl, curr_date);

                        reference.child(newsId).setValue(news);

                        pd.dismiss();
                        Toast.makeText(getContext(), "Project Uploaded", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), HomeActivity.class));
                        getActivity().finish();
                    } else {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        String[] uriParts = uri.toString().split("\\.");
        return uriParts[uriParts.length - 1];
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
