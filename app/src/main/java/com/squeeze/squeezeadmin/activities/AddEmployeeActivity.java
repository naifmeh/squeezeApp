package com.squeeze.squeezeadmin.activities;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.EmployeeBean;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;
import com.squeeze.squeezeadmin.ui.SuccessDialog;
import com.squeeze.squeezeadmin.utils.DisplayUtils;

import java.util.HashMap;
import java.util.Map;

public class AddEmployeeActivity extends AppCompatActivity {
    private static final String TAG = AddEmployeeActivity.class.getSimpleName();

    /* UI */
    private Toolbar mToolbar;
    private TextInputEditText firstNameEditTxt;
    private TextInputEditText lastNameEditTxt;
    private TextInputEditText emailEditTxt;
    private Switch authorizationSwitch;
    private TextInputEditText dateBegEditTxt;
    private TextInputEditText dateEndEditTxt;
    private Button sendDataBtn;

    /* Data */
    private EmployeeBean employeeBean;
    private SharedPreferences mSharedPrefs;

    /* Dialog */
    private SuccessDialog mSuccessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        /* Initializing UI */
        mToolbar = (Toolbar) findViewById(R.id.addEmployeeToolbar);
        firstNameEditTxt = (TextInputEditText) findViewById(R.id.firstNameEditText);
        lastNameEditTxt = (TextInputEditText) findViewById(R.id.lastNameEditText);
        emailEditTxt = (TextInputEditText) findViewById(R.id.emailEditText);
        authorizationSwitch = (Switch) findViewById(R.id.authorizedSwitch);
        dateBegEditTxt = (TextInputEditText) findViewById(R.id.dateBeginningAuthEditText);
        dateEndEditTxt = (TextInputEditText) findViewById(R.id.dateEndingAuthEditText);
        sendDataBtn = (Button) findViewById(R.id.sendEmployeeButton);

        /* Setting up status bar color and toolbar*/
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* Setting listener */
        sendDataBtn.setOnClickListener((view) -> {
            boolean areEmpty = verifyFields();
            if(areEmpty == true) {
                Toast.makeText(this,"One or more fields are empty",Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            employeeBean = new EmployeeBean(firstNameEditTxt.getText().toString(),
                    lastNameEditTxt.getText().toString(),
                    emailEditTxt.getText().toString(),
                    authorizationSwitch.isChecked(),
                    0,
                    DisplayUtils.getEquivalentTimestamp(dateBegEditTxt.getText().toString()),
                    DisplayUtils.getEquivalentTimestamp(dateEndEditTxt.getText().toString())
                    );
            Toast.makeText(this,"Sending employee...",Toast.LENGTH_LONG).show();
            sendEmployee();

        });

        mSharedPrefs = getSharedPreferences(getString(R.string.shared_prefs_file_key),MODE_PRIVATE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verifyFields() {
        TextInputEditText listEdits[] = {firstNameEditTxt,lastNameEditTxt,
            emailEditTxt, dateBegEditTxt, dateEndEditTxt};
        boolean isEmpty = DisplayUtils.areEditTextsEmpty(listEdits);
        return isEmpty;
    }

    private void sendEmployee() {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(this).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_REGISTER);
        StringRequest addEmployeeRequest = new StringRequest(Request.Method.POST,
                builder.build().toString(),(response) -> {
            mSuccessDialog = new SuccessDialog(AddEmployeeActivity.this,getString(R.string.successAddingEmployee));
            if(mSuccessDialog.isShowing()) mSuccessDialog.dismiss();
            mSuccessDialog.show();
        }, (error) -> {
            Toast.makeText(AddEmployeeActivity.this,"Error while performing request",Toast.LENGTH_LONG).show();
            //TODO: Error dialog
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("content-type","application/json");
                if(mSharedPrefs.contains(getString(R.string.shared_prefs_jwt_key))) {
                    headers.put("Authorization","Bearer "+mSharedPrefs.getString(getString(R.string.shared_prefs_jwt_key),""));
                }
                 return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Gson gson = new Gson();
                JsonObject json = new JsonObject();
                JsonObject jsonEmployee = gson.fromJson(gson.toJson(employeeBean),JsonObject.class);
                json.add("data",jsonEmployee);
                return json.toString().getBytes();
            }
        };

        queue.add(addEmployeeRequest);
    }
}
