package com.example.grampanchayat.ui.complaint;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.grampanchayat.HomeActivity;
import com.example.grampanchayat.data.model.Complaint;
import com.example.grampanchayat.databinding.FragmentFileComplaintBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileComplaintFragment extends Fragment {

    private Uri imageUri;
    private ImageView complaintImage;
    private TextView subject;
    private TextView description;
    private AppCompatButton addImage;
    private AppCompatButton submitComplaint;

    private FragmentFileComplaintBinding binding;
    private StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFileComplaintBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize UI components
        complaintImage = binding.complaintImage;
        subject = binding.subject;
        description = binding.description;
        addImage = binding.addImage;
        submitComplaint = binding.submitComplaint;

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("Complaints");

        // Set onClickListener for the addImage button
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to open the file chooser
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Only allow image files
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 100);
            }
        });

        // Set onClickListener for the submitComplaint button
        submitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String subjectText = subject.getText().toString();
                String descriptionText = description.getText().toString();

                if (subjectText.isEmpty()) {
                    subject.setError("Subject is required");
                    subject.requestFocus();
                    return;
                } else {
                    subject.setError(null);
                }
                if (descriptionText.isEmpty()) {
                    description.setError("Description is required");
                    description.requestFocus();
                    return;
                } else {
                    description.setError(null);
                }

                // Upload complaint to the database
                uploadComplaint();
            }
        });

        return root;
    }

    // Handle the result of the image picker (file chooser)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the image was selected successfully
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            imageUri = data.getData();
            // Display the selected image in the ImageView
            complaintImage.setImageURI(imageUri);
        } else {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to upload the complaint to Firebase
    private void uploadComplaint() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Filing Complaint");
        pd.show();

        if (imageUri != null) {
            // Get a reference for the image upload path in Firebase Storage
            StorageReference filePath = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            // Upload the selected image to Firebase Storage
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Get the download URL of the uploaded image
                    Uri downloadUri = (Uri) task.getResult();
                    String imageUrl = downloadUri.toString();

                    // Proceed to save the complaint data with the image URL
                    saveComplaintData(pd, imageUrl);
                } else {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Image upload failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no image was selected, use a default placeholder image URL
            String defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/gram-panchayat-services.appspot.com/o/News%2FPlaceholder_view.jpg?alt=media&token=5ca6caac-8be3-457d-8837-107b551a7875";
            saveComplaintData(pd, defaultImageUrl);
        }
    }

    // Method to save complaint data to Firebase
    private void saveComplaintData(ProgressDialog pd, String imageUrl) {
        DatabaseReference refComplaint = FirebaseDatabase.getInstance().getReference("Complaints");
        String complaintId = refComplaint.push().getKey();

        // Getting current date
        SimpleDateFormat SDFormat = new SimpleDateFormat();
        Calendar cal = Calendar.getInstance();
        String curr_date = SDFormat.format(cal.getTime());

        // Get user details from Firebase
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String phone = snapshot.child("contact").getValue().toString();
                String aadhar = snapshot.child("aadhar").getValue().toString();

                // Create a new complaint object with the image URL
                Complaint complaint = new Complaint(complaintId, curr_date, subject.getText().toString(),
                        description.getText().toString(), "In Progress", email, name, aadhar, phone, "null", imageUrl);

                // Save the complaint to Firebase
                refComplaint.child(complaintId).setValue(complaint);

                pd.dismiss();
                Toast.makeText(getContext(), "Complaint Filed Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), HomeActivity.class));
                getActivity().finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pd.dismiss();
                Toast.makeText(getContext(), "Failed to file complaint", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get the file extension from the selected image URI
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
