package com.squeeze.squeezeadmin.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.squeeze.squeezeadmin.R;
import com.squeeze.squeezeadmin.beans.ImageBean;
import com.squeeze.squeezeadmin.network.NetworkRequestsSingleton;
import com.squeeze.squeezeadmin.network.RequestScheme;
import com.squeeze.squeezeadmin.ui.SuccessDialog;
import com.squeeze.squeezeadmin.utils.DisplayUtils;
import com.squeeze.squeezeadmin.utils.NetworkUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TrainNetworkActivity extends AppCompatActivity {
    private static final String TAG = TrainNetworkActivity.class.getSimpleName();

    /* UI */
    private Toolbar mToolbar;
    private GridLayout mGridLayout;
    private TextView mInstructions;
    private ImageView thumbnails[] = new ImageView[12];
    private TextInputEditText mFullNameEditTxt;
    private Button mButton;
    private SpinKitView mSpinKit;

    /* Data */
    private ArrayList<ImageBean> imagesBean = new ArrayList<>();
    private int imageIndex = 0;
    private boolean isImageSet = false;
    private int imageCount = 0;

    private SharedPreferences mSharedPrefs;

    private SuccessDialog mSuccessDialog;

    private static final int PICKER_IMAGES = 54845;
    public static final String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static Random RANDOM = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_network);

        /* Init UI */
        int imagesId[] = {R.id.firstImageTrain,R.id.secondImageTrain,R.id.thirdImageTrain,
                R.id.fourthImageTrain, R.id.fifthImageTrain,R.id.sixthImageTrain,
                R.id.seventhImageTrain,R.id.eightImageTrain,R.id.ninthImageTrain,
                R.id.tenthImageTrain, R.id.eleventhImageTrain, R.id.twelfthImageTrain};
        mToolbar = (Toolbar) findViewById(R.id.trainNetworkToolbar);
        for(int i=0; i<thumbnails.length;i++)
            thumbnails[i] = (ImageView) findViewById(imagesId[i]);
        mGridLayout = (GridLayout) findViewById(R.id.gridViewTrainNetwork);
        mFullNameEditTxt = (TextInputEditText) findViewById(R.id.namePictureEditText);
        mButton = (Button) findViewById(R.id.trainButton);
        mSpinKit = (SpinKitView) findViewById(R.id.sendImagesProgrss);
        mInstructions = (TextView) findViewById(R.id.instructionsTextView);

        /* Setting up status bar color and toolbar*/
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setTitle(R.string.trainNetTitle);

        /* Init data */
        mSharedPrefs = getSharedPreferences(
                getString(R.string.shared_prefs_file_key),
                MODE_PRIVATE
        );

        /* Setting up click listeners */
        for(int i=0; i< thumbnails.length;i++) {
            final int index = i;
            thumbnails[i].setOnClickListener((view) -> {
                imageIndex = index;
                imageClickListenerAction();
            });
        }

        mButton.setOnClickListener( (view) -> {
            if(!isImageSet) {
                Toast.makeText(TrainNetworkActivity.this,"No image picked",Toast.LENGTH_LONG).show();
            }
            mInstructions.setVisibility(View.GONE);
            mSpinKit.setVisibility(View.VISIBLE);
            sendImagesToServer();
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICKER_IMAGES && resultCode == Activity.RESULT_OK) {
            if(data == null) {
                //TODO: display err
                Log.e(TAG,"Error retrieving picture");
            }
            try {
                if(data.getData() == null) throw new FileNotFoundException();
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                InputStream secondInputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap btm = DisplayUtils.getThumbnail(inputStream);
                thumbnails[imageIndex].setImageBitmap(btm);
                imagesBean.add(DisplayUtils.getImageBeanFromImage(secondInputStream,
                        getRandomFileName(10)+".jpg"));
                isImageSet = true;
                imageCount++;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSuccessDialog != null && mSuccessDialog.isShowing())
            mSuccessDialog.dismiss();
    }

    private void imageClickListenerAction() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICKER_IMAGES);
    }

    private void sendImagesToServer() {
        RequestQueue queue = NetworkRequestsSingleton.getInstance(this).getRequestQueue();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(RequestScheme.HTTP_SCHEME)
                .encodedAuthority(RequestScheme.AUTHORITY)
                .appendPath(RequestScheme.EMPLOYEES_PATH)
                .appendPath(RequestScheme.EMPLOYEES_IMAGE);

        for(int i=0; i<imagesBean.size();i++) {
            final int index = i;
            StringRequest stringRequest = new StringRequest(Request.Method.POST,builder.build().toString(),
                    (response) -> {
                        if(index == imagesBean.size()-1) {
                            if(mSuccessDialog == null) {
                                mSuccessDialog = new SuccessDialog(TrainNetworkActivity.this,
                                        getString(R.string.successSendingImages),
                                        (view)-> {
                                            NetworkUtils.sendTrainingSignal(TrainNetworkActivity.this,
                                                    mSharedPrefs);
                                            mSuccessDialog.dismiss();
                                        });
                                mSuccessDialog.show();
                                imagesBean = new ArrayList<>();
                                mSpinKit.setVisibility(View.GONE);
                                
                            } else {
                                if(mSuccessDialog.isShowing()){
                                    mSuccessDialog.dismiss();
                                    mSuccessDialog.show();
                                }
                            }
                        }
                    }, (error) -> {
                    Toast.makeText(TrainNetworkActivity.this,"Error while performing request",Toast.LENGTH_LONG).show();
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("content-type","application/json");
                    headers.put("Authorization","Bearer "+
                        NetworkUtils.getJwtFromSharedPrefs(TrainNetworkActivity.this,mSharedPrefs));

                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    Gson gson = new Gson();
                    imagesBean.get(index).setName(mFullNameEditTxt.getText().toString().trim());
                    return gson.toJson(imagesBean.get(index)).getBytes();
                }
            };

            queue.add(stringRequest);
        }
    }

    private String getRandomFileName(int len) {
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }
}
