package com.example.grampanchayat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    Button addUserButton, viewUserButton, addComplaintButton, viewComplaintButton, addServicesButton, viewServicesButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize buttons
        addUserButton = findViewById(R.id.id_addUser);
        viewUserButton = findViewById(R.id.id_viewUser);
        addComplaintButton = findViewById(R.id.id_addComplaint);
        viewComplaintButton = findViewById(R.id.id_viewComplaint);
        addServicesButton = findViewById(R.id.id_addServices);
        viewServicesButton = findViewById(R.id.id_viewServices);
        logoutButton = findViewById(R.id.id_logout);

        // Set click listeners for each button to navigate to respective activities
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        viewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewUserActivity.class);
                startActivity(intent);
            }
        });

        addComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AddComplaintActivity.class);
                startActivity(intent);
            }
        });

        viewComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewComplaintActivity.class);
                startActivity(intent);
            }
        });

        addServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, AddServicesActivity.class);
                startActivity(intent);
            }
        });

        viewServicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ViewServicesActivity.class);
                startActivity(intent);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log the user out and navigate to LoginActivity (for example)
                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                startActivity(intent);
                finish(); // Close current activity
            }
        });
    }
}
