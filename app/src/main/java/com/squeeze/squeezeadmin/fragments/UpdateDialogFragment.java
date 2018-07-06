package com.squeeze.squeezeadmin.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.EmployeeBean;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;
import com.squeeze.squeezeadmin.ui.SuccessDialog;
import com.squeeze.squeezeadmin.utils.DisplayUtils;
import com.squeeze.squeezeadmin.utils.NetworkUtils;

import java.util.HashMap;
import java.util.Map;

public class UpdateDialogFragment extends BottomSheetDialogFragment {
    private static final String TAG = UpdateDialogFragment.class.getSimpleName();

    private UpdateDialogListener mListener;

    /* UI */
    private TextInputEditText mFirstName;
    private TextInputEditText mLastName;
    private TextInputEditText mEmail;
    private TextInputEditText mDateBeg;
    private TextInputEditText mDateEnd;

    private ImageView mDismissBtn;
    private ImageView mAuthorizeBtn;

    private Button mUpdateBtn;

    private EmployeeBean mEmployee;
    private boolean isAuth;

    private SharedPreferences mSharedPrefs;
    private SuccessDialog mSuccessDialog;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallBack = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public UpdateDialogFragment() {

    }

    public static UpdateDialogFragment newInstance() {
        return new UpdateDialogFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateDialogListener) {
            mListener = (UpdateDialogListener) context;
            mSharedPrefs = context.getSharedPreferences(context.getString(R.string.shared_prefs_file_key), context.MODE_PRIVATE);
        } else
            throw new RuntimeException(context.toString() + " must implement UpdateDialogListener");
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_update_employee, null);
        dialog.setContentView(contentView);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallBack);
        }

        /* TextInputEditTexts */
        mFirstName = (TextInputEditText) contentView.findViewById(R.id.firstNameBottomSheet);
        mLastName = (TextInputEditText) contentView.findViewById(R.id.lastNameBottomSheet);
        mEmail = (TextInputEditText) contentView.findViewById(R.id.emailBottomSheet);
        mDateBeg = (TextInputEditText) contentView.findViewById(R.id.dateBegBottomSheet);
        mDateEnd = (TextInputEditText) contentView.findViewById(R.id.dateEndBottomSheet);
        /* Images */
        mDismissBtn = (ImageView) contentView.findViewById(R.id.quitBottomSheet);
        mAuthorizeBtn = (ImageView) contentView.findViewById(R.id.authorizeBtnBtm);
        /* Button */
        mUpdateBtn = (Button) contentView.findViewById(R.id.updateEmployeeBtn);

        /* Setting values */
        mEmployee = mListener.getEmployeeBean();
        loadEmployeeValues();

        /* Business logic*/
        mDismissBtn.setOnClickListener((view) -> dismiss());

        mAuthorizeBtn.setOnClickListener((view) -> {
            if (isAuth) {
                ((ImageView) view).setImageDrawable(getContext().getDrawable(R.drawable.ic_lock_closed_white_24px));
                isAuth = false;
            } else {
                ((ImageView) view).setImageDrawable(getContext().getDrawable(R.drawable.ic_lock_open_white_24px));
                isAuth = true;
            }
        });

        mUpdateBtn.setOnClickListener((view) -> {
            sendEmployee();
        });

    }

    private void loadEmployeeValues() {
        if (mEmployee == null) return;
        mFirstName.setText(mEmployee.getFirstName());
        mLastName.setText(mEmployee.getLastName());
        mEmail.setText(mEmployee.getEmail());
        mEmail.setFocusable(false);
        mEmail.setClickable(false);
        mDateBeg.setText(DisplayUtils.getTimestampDate(mEmployee.getAuthStarting()));
        mDateEnd.setText(DisplayUtils.getTimestampDate(mEmployee.getAuthEnding()));

        isAuth = mEmployee.isAuthorized();
        if (mEmployee.isAuthorized()) {
            mAuthorizeBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_lock_open_white_24px));
        } else {
            mAuthorizeBtn.setImageDrawable(getContext().getDrawable(R.drawable.ic_lock_closed_white_24px));
        }
    }

    private void sendEmployee() {
        final EmployeeBean updateEmployee = new EmployeeBean();

        updateEmployee.setFirstName(mFirstName.getText().toString());
        updateEmployee.setLastName(mLastName.getText().toString());
        updateEmployee.setAuthorized(isAuth);
        updateEmployee.setEmail(mEmail.getText().toString());
        updateEmployee.setAuthStarting(DisplayUtils.getEquivalentTimestamp(mDateBeg.getText().toString()));
        updateEmployee.setAuthEnding(DisplayUtils.getEquivalentTimestamp(mDateEnd.getText().toString()));
        updateEmployee.setFrequency(0);

        RequestQueue queue = NetworkRequestsSingleton.getInstance(getContext()).getRequestQueue();

        Uri.Builder builder = new Uri.Builder().scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_UPDATE);
        StringRequest request = new StringRequest(Request.Method.PUT, builder.build().toString(),
                (response) -> {
                    dismiss();
                    if (mSuccessDialog == null) {
                        mSuccessDialog = new SuccessDialog(getActivity(), "Success updating data");
                        mSuccessDialog.show();
                    } else {
                        if (mSuccessDialog.isShowing()) mSuccessDialog.dismiss();
                    }

                    mListener.notifyNetworkResponse();
                }, (error) -> {
            Toast.makeText(getContext(), "Error while performing network request", Toast.LENGTH_LONG).show();
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + NetworkUtils.getJwtFromSharedPrefs(getContext(), mSharedPrefs));
                headers.put("Content-Type", "application/json");

                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                Gson gson = new Gson();
                JsonObject data = new JsonObject();
                data.add("data", gson.fromJson(gson.toJson(updateEmployee), JsonElement.class));
                Log.d(TAG, data.toString());
                return data.toString().getBytes();
            }
        };

        queue.add(request);
    }

    public interface UpdateDialogListener {
        EmployeeBean getEmployeeBean();

        void notifyNetworkResponse();
    }
}
