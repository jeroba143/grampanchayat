package com.example.grampanchayat.ui.complaint;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.example.grampanchayat.R;
import com.example.grampanchayat.data.model.Admin;
import com.example.grampanchayat.ui.home.HomeFragment;

import java.util.Objects;

public class ReadComplaintFragment extends Fragment {

    String subject, description, image, date, status, name, mobile, email, aadhar, feedback, complaintId;

    Admin currentUser;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public ReadComplaintFragment() {
        // Required empty public constructor
    }

    public ReadComplaintFragment(String complaintId, String subject, String description, String image, String date, String status, String name, String mobile, String email, String aadhar, String feedback){
        this.complaintId = complaintId;
        this.subject = subject;
        this.description = description;
        this.image = image;
        this.date = date;
        this.status = status;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.aadhar = aadhar;
        this.feedback = feedback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read_complaint, container, false);

        // Initialize UI components
        TextView subjectText = view.findViewById(R.id.subject);
        TextView dateText = view.findViewById(R.id.date);
        TextView descriptionText = view.findViewById(R.id.description);
        TextView nameText = view.findViewById(R.id.name);
        TextView emailText = view.findViewById(R.id.email);
        TextView phoneText = view.findViewById(R.id.phone);
        TextView aadharText = view.findViewById(R.id.aadhar);
        TextView statusText = view.findViewById(R.id.status);
        TextView feedbackText = view.findViewById(R.id.feedback);
        ImageView imageView = view.findViewById(R.id.image);

        TextView selectStatus = view.findViewById(R.id.selectStatus);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        RadioButton inProgress = view.findViewById(R.id.inProgress);
        RadioButton rejected = view.findViewById(R.id.rejected);
        RadioButton resolved = view.findViewById(R.id.resolved);
        EditText feedbackEditText = view.findViewById(R.id.feedbackText);
        AppCompatButton giveFeedback = view.findViewById(R.id.giveFeedback);

        // Set text values to the UI components
        subjectText.setText("Subject: " + this.subject);
        dateText.setText(this.date);
        descriptionText.setText(this.description);
        nameText.setText("Name: " + this.name);
        emailText.setText("Email: " + this.email);
        phoneText.setText("Phone: " + this.mobile);
        aadharText.setText("Aadhar: " + this.aadhar);
        statusText.setText("Status: " + this.status);

        // Handle feedback
        if (this.feedback.equals("null")) {
            this.feedback = "No feedback yet";
        }
        feedbackText.setText("Feedback: " + this.feedback);

        // Debugging: Log the image URL to check if it's valid
        Log.d("ImageURL", "Image URL: " + this.image);

        // Load the image with Picasso and handle any errors
        if (this.image != null && !this.image.isEmpty()) {
            Picasso.get()
                    .load(this.image)
                    .placeholder(R.drawable.profile)  // Placeholder image during loading
                    .error(R.drawable.edit_profile)              // Error image if loading fails
                    .into(imageView);
        } else {
            Log.e("ImageLoading", "Image URL is null or empty");
            imageView.setImageResource(R.drawable.edit_profile);  // Set error image if no URL
        }

        // Initialize the Admin and FirebaseAuth
        currentUser = new Admin();
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if the current user is an admin
        if (currentUser.isAdminUsingMail(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail())) {
            // Show feedback and status change options for admin
            giveFeedback.setVisibility(View.VISIBLE);
            selectStatus.setVisibility(View.VISIBLE);
            inProgress.setVisibility(View.VISIBLE);
            resolved.setVisibility(View.VISIBLE);
            rejected.setVisibility(View.VISIBLE);
            feedbackEditText.setVisibility(View.VISIBLE);
            radioGroup.setVisibility(View.VISIBLE);
        }

        // Handle feedback submission by the admin
        giveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update status and feedback of the complaint in the Firebase database
                String updatedStatus = "";

                if (inProgress.isChecked()) {
                    updatedStatus = "In Progress";
                } else if (resolved.isChecked()) {
                    updatedStatus = "Resolved";
                } else if (rejected.isChecked()) {
                    updatedStatus = "Rejected";
                }

                // Update the status and feedback in the Firebase database
                databaseReference = FirebaseDatabase.getInstance().getReference("Complaints").child(complaintId);
                databaseReference.child("status").setValue(updatedStatus);
                databaseReference.child("feedback").setValue(feedbackEditText.getText().toString());

                // Navigate back to HomeFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_content_home, new HomeFragment()).commit();
            }
        });

        return view;
    }

    // Handle back press to navigate to the previous fragment
    public void onBackPress() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_content_home, new UsersComplaintFragment()).commit();
    }

    // This method will be called when the fragment is paused
    @Override
    public void onPause() {
        super.onPause();
    }
}
