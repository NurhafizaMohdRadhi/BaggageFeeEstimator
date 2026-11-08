package com.example.individualassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etWeight, etRate;
    private Spinner spTicketClass;
    private TextView tvExcessWeight, tvExcessCharge, tvTotalPayable;
    private Button btnCalculate, btnClear, btnAbout;

    private static final String GITHUB_REPO_URL = "https://github.com/nurhafiza/BaggageFeeEstimator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind and Set Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Bind Form Views
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);
        spTicketClass = findViewById(R.id.spTicketClass);
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvExcessCharge = findViewById(R.id.tvExcessCharge);
        tvTotalPayable = findViewById(R.id.tvTotalPayable);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear = findViewById(R.id.btnClear);
        btnAbout = findViewById(R.id.btnAbout);

        // Setup Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.ticket_classes,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTicketClass.setAdapter(adapter);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateFee();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });
    }

    private void calculateFee() {
        String weightStr = etWeight.getText().toString().trim();
        String rateStr = etRate.getText().toString().trim();

        // 1. Validate Empty Baggage Weight
        if (weightStr.isEmpty()) {
            etWeight.setError("Baggage weight is required");
            etWeight.requestFocus();
            return;
        }

        // 2. Validate Empty Excess Rate
        if (rateStr.isEmpty()) {
            etRate.setError("Excess rate per kg is required");
            etRate.requestFocus();
            return;
        }

        double baggageWeight = Double.parseDouble(weightStr);
        double ratePerKg = Double.parseDouble(rateStr);

        // 3. Validate Non-Positive Inputs
        if (baggageWeight <= 0) {
            etWeight.setError("Baggage weight must be greater than 0");
            etWeight.requestFocus();
            return;
        }

        if (ratePerKg <= 0) {
            etRate.setError("Excess rate must be greater than 0");
            etRate.requestFocus();
            return;
        }

        // Perform Allowance Calculation
        int selectedPosition = spTicketClass.getSelectedItemPosition();
        double allowance = (selectedPosition == 0) ? 20.0 : 40.0;

        double excessWeight = baggageWeight - allowance;
        if (excessWeight < 0) {
            excessWeight = 0;
        }

        double excessCharge = excessWeight * ratePerKg;
        double totalPayable = excessCharge + (excessCharge * 0.08);

        // Display Outputs
        tvExcessWeight.setText(String.format(Locale.getDefault(), "%.2f kg", excessWeight));
        tvExcessCharge.setText(String.format(Locale.getDefault(), "RM %.2f", excessCharge));
        tvTotalPayable.setText(String.format(Locale.getDefault(), "RM %.2f", totalPayable));

        // 4. Feedback Notice Toast
        if (excessWeight == 0) {
            Toast.makeText(this, "Within allowance limit! No excess fee required.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Calculation complete (includes 8% tax).", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetFields() {
        etWeight.setText("");
        etRate.setText("");
        spTicketClass.setSelection(0);
        tvExcessWeight.setText("0.00 kg");
        tvExcessCharge.setText("RM 0.00");
        tvTotalPayable.setText("RM 0.00");
        etWeight.clearFocus();
        etRate.clearFocus();
        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show();
    }

    // Menu Inflater for Toolbar Share Action
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ICT602 Excess Baggage Estimator");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the Baggage Fee Estimator application source repository: " + GITHUB_REPO_URL);
            startActivity(Intent.createChooser(shareIntent, "Share Application via"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}