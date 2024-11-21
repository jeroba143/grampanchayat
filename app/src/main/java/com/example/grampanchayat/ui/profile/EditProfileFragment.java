package com.example.grampanchayat.ui.profile;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.example.grampanchayat.HomeActivity;
import com.example.grampanchayat.databinding.FragmentEditProfileBinding;

import java.util.Calendar;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding binding;

    private ImageView id_profileImage;
    private TextInputLayout id_name, id_address, id_aadhar, id_contact, id_pincode, id_DOB;
    private TextInputEditText id_dateOfBirth;
    private MaterialRadioButton id_male, id_female, id_other;
    private Button id_profileUpdate;
    private ProgressBar id_progressBar;

    private Uri imageUri;

    StorageReference storageReference;

    FirebaseAuth auth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        id_name = binding.idUserName;
        id_address = binding.idAddress;
        id_aadhar = binding.idAadharno;
        id_contact = binding.idContactNo;
        id_pincode = binding.idPincode;
        id_DOB = binding.idDob;
        id_dateOfBirth = binding.idDobEdittext;
        id_male = binding.idMale;
        id_female = binding.idFemale;
        id_other = binding.idOther;
        id_profileUpdate = binding.idUpdateProfile;
        id_progressBar = binding.progressBar2;
        id_profileImage = binding.profileImage;

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // When user clicks on the date of birth edit text then date picker dialog will open
        id_dateOfBirth.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this.getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
                id_dateOfBirth.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1);
            }, year, month, day);

            datePickerDialog.show();
        });

        // Open the file chooser when profile image is clicked
        id_profileImage.setOnClickListener(v -> {
            Intent selectImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(selectImage, 1000);
        });

        id_profileUpdate.setOnClickListener(v -> {
            String name = id_name.getEditText().getText().toString();
            String address = id_address.getEditText().getText().toString();
            String aadhar = id_aadhar.getEditText().getText().toString();
            String contact = id_contact.getEditText().getText().toString();
            String pincode = id_pincode.getEditText().getText().toString();
            String dob = id_dateOfBirth.getText().toString();
            String gender = "";

            // Validations
            if (name.isEmpty()) {
                id_name.setError("Name is required");
                id_name.requestFocus();
                return;
            } else {
                id_name.setError(null);
            }

            if (contact.isEmpty()) {
                id_contact.setError("Contact is required");
                id_contact.requestFocus();
                return;
            } else if (contact.length() != 10) {
                id_contact.setError("Contact must be 10 digits");
                id_contact.requestFocus();
                return;
            } else {
                id_contact.setError(null);
            }

            if (dob.isEmpty()) {
                id_DOB.setError("Date of Birth is required");
                id_DOB.requestFocus();
                return;
            } else {
                id_DOB.setError(null);
            }

            if (aadhar.isEmpty()) {
                id_aadhar.setError("Aadhar is required");
                id_aadhar.requestFocus();
                return;
            } else if (aadhar.length() != 12) {
                id_aadhar.setError("Aadhar must be 12 digits");
                id_aadhar.requestFocus();
                return;
            } else {
                id_aadhar.setError(null);
            }

            if (id_male.isChecked()) {
                gender = "Male";
            } else if (id_female.isChecked()) {
                gender = "Female";
            } else if (id_other.isChecked()) {
                gender = "Other";
            }

            if (address.isEmpty()) {
                id_address.setError("Address is required");
                id_address.requestFocus();
                return;
            } else {
                id_address.setError(null);
            }

            if (pincode.isEmpty()) {
                id_pincode.setError("Pincode is required");
                id_pincode.requestFocus();
                return;
            } else if (pincode.length() != 6) {
                id_pincode.setError("Pincode must be 6 digits");
                id_pincode.requestFocus();
                return;
            } else {
                id_pincode.setError(null);
            }

            id_progressBar.setVisibility(View.VISIBLE);
            updateProfile(name, address, aadhar, contact, pincode, dob, gender);
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle image selection result
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                id_profileImage.setImageURI(imageUri);
            }
        } else {
            Toast.makeText(getContext(), "Error, Try Again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String name, String address, String aadhar, String contact, String pincode, String dob, String gender) {
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        user.child("name").setValue(name);
        user.child("address").setValue(address);
        user.child("aadhar").setValue(aadhar);
        user.child("contact").setValue(contact);
        user.child("pincode").setValue(pincode);
        user.child("dateOfBirth").setValue(dob);
        user.child("gender").setValue(gender);

        if (imageUri != null) { // Ensure that imageUri is not null before uploading
            StorageReference fileRef = storageReference.child("profileImages").child(auth.getCurrentUser().getUid());
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                user.child("imageUrl").setValue(imageUrl);
                Picasso.get().load(imageUrl).into(id_profileImage); // Load the image into the ImageView
            })).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show());
        }

        id_progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(getContext(), HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        id_name = binding.idUserName;
        id_address = binding.idAddress;
        id_aadhar = binding.idAadharno;
        id_contact = binding.idContactNo;
        id_pincode = binding.idPincode;
        id_dateOfBirth = binding.idDobEdittext;
        id_male = binding.idMale;
        id_female = binding.idFemale;
        id_other = binding.idOther;

        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        // Set Profile Image from Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("profileImages/" + auth.getCurrentUser().getUid());
        profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(id_profileImage);
        });

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String address = snapshot.child("address").getValue().toString();
                String aadhar = snapshot.child("aadhar").getValue().toString();
                String contact = snapshot.child("contact").getValue().toString();
                String pincode = snapshot.child("pincode").getValue().toString();
                String gender = snapshot.child("gender").getValue().toString();
                String dateOfBirth = snapshot.child("dateOfBirth").getValue().toString();

                id_name.getEditText().setText(name);
                id_address.getEditText().setText(address);
                id_aadhar.getEditText().setText(aadhar);
                id_contact.getEditText().setText(contact);
                id_pincode.getEditText().setText(pincode);
                id_dateOfBirth.setText(dateOfBirth);

                if (gender.equals("Male")) {
                    id_male.setChecked(true);
                } else if (gender.equals("Female")) {
                    id_female.setChecked(true);
                } else {
                    id_other.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
