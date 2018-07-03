package com.squeeze.squeezeadmin.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.squeeze.squeezeadmin.R;

public class SuccessDialog extends Dialog {
    /* UI */
    private TextView mSuccessText;
    private Button mActionButton;

    /* Data */
    private String mDataText;

    /* Optional click listener */
    private View.OnClickListener mBtnClickListener;

    public SuccessDialog(@NonNull Context context, @NonNull String message) {
        super(context);
        mDataText = message;
    }

    public SuccessDialog(@NonNull Context context,@NonNull String message,View.OnClickListener clickAction) {
        super(context);
        mDataText = message;
        mBtnClickListener = clickAction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Requesting dialog */
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        /* Setting layout */
        setContentView(R.layout.success_display_layout);

        /* Instantiating UI components */
        mSuccessText = (TextView) findViewById(R.id.successPopUpMessage);
        mActionButton = (Button) findViewById(R.id.successOkButton);

        /* Setting up button action & text*/
        mSuccessText.setText(mDataText);
        if(mBtnClickListener == null) {
            mActionButton.setOnClickListener((view)-> {
                dismiss();
            });
        } else {
            mActionButton.setOnClickListener(mBtnClickListener);
        }
    }
}
