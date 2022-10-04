package com.esrc.android.extension.marvrus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esrc.sdk.android.ESRCException;
import com.esrc.sdk.android.ESRCFragment;
import com.esrc.sdk.android.ESRCLicense;
import com.esrc.sdk.android.ESRCType;
import com.esrc.sdk.android.extension.marvrus.Marvrus;
import com.esrc.sdk.android.extension.marvrus.MarvrusType;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "MainActivity";
    private static final String APP_ID = "";  // Application ID.

    // Permission
    private static final int PERMISSIONS_REQUEST_CODE = 1000;
    private static final String[] PERMISSIONS = {INTERNET, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    // Property
    private MarvrusType.Property mProperty = new MarvrusType.Property(
            false,  // Whether visualize result or not. It is only valid If you bind the ESRC Fragment (i.e., Step 2).
            true,  // Whether analyze measurement environment or not.
            true,  // Whether detect face or not.
            true,  // Whether detect facial landmark or not. If enableFace is false, it is also automatically set to false.
            true,  // Whether analyze facial action unit or not. If enableFace or enableFacialLandmark is false, it is also automatically set to false.
            true,  // Whether recognize basic facial expression or not. If enableFace is false, it is also automatically set to false.
            true,  // Whether recognize valence facial expression or not. If enableFace is false, it is also automatically set to false.
            true,  // Whether estimate remote hr or not. If enableFace is false, it is also automatically set to false.
            true,  // Whether analyze HRV or not. If enableFace or enableRemoteHR is false, it is also automatically set to false.
            true,  // Whether recognize engagement or not. If enableRemoteHR and enableHRV are false, it is also automatically set to false.
            true,  // Whether print information or not.
            true);  // Whether recognize MEE index or not.

    // Layout variables for FaceBox
    private TextView mFaceBoxText;
    private ImageView mFaceBoxImage;
    private GradientDrawable mFaceBoxDrawable;

    // Layout variables for Basic Facial Expression
    private int[] mBasicFacialExpImageDrawables;
    private View mBasicFacialExpValContainer;
    private ImageView mBasicFacialExpImage;
    private TextView mBasicFacialExpValText;

    // Layout variables for Valence Facial Expression
    private int[] mValenceFacialExpImageDrawables;
    private View mValenceFacialExpValContainer;
    private ImageView mValenceFacialExpImage;
    private TextView mValenceFacialExpValText;

    // Layout variables for Attention
    private View mAttentionValContainer;
    private TextView mAttentionValText;

    // Layout variables for Heart Rate
    private View mHRValContainer;
    private TextView mHRValText;
    private ProgressBar mHRSpinnerDummy;
    private ProgressBar mHRSpinner;

    // Layout variables for Heart Rate Variability
    private View mHRVValContainer;
    private TextView mHRVValText;
    private ProgressBar mHRVSpinnerDummy;
    private ProgressBar mHRVSpinner;

    // Layout variables for Engagement
    private View mEngagementValContainer;
    private TextView mEngagementValText;
    private ProgressBar mEngagementSpinnerDummy;
    private ProgressBar mEngagementSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind ESRCFragment (optional)
        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, ESRCFragment.newInstance())
                    .commit();
        }

        // Set always display on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Request Permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }

        // Initialize layout
        initLayout();

        // Initialize Marvrus
        Marvrus.init(APP_ID, this, new ESRCLicense.ESRCLicenseHandler() {
            @Override
            public void onValidatedLicense() {
                // Start
                start();
            }

            @Override
            public void onInvalidatedLicense() {
                Toast.makeText(getApplicationContext(), "Invalid license", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Stop
        stop();

        super.onDestroy();
    }

    /**
     * Initialize layout.
     */
    private void initLayout() {
        // Initialize layout for FaceBox
        mFaceBoxText = findViewById(R.id.facebox_text);
        mFaceBoxImage = findViewById(R.id.facebox_image);
        mFaceBoxDrawable = (GradientDrawable) mFaceBoxImage.getBackground();

        // Initialize layout for Basic Facial Expression
        mBasicFacialExpImageDrawables = new int[] {
                R.drawable.ic_facial_exp_anger, R.drawable.ic_facial_exp_disgust, R.drawable.ic_facial_exp_fear,
                R.drawable.ic_facial_exp_happy, R.drawable.ic_facial_exp_sad, R.drawable.ic_facial_exp_surprise,
                R.drawable.ic_facial_exp_neutral,
        };
        mBasicFacialExpValContainer = findViewById(R.id.basic_facial_expression_val_container);
        mBasicFacialExpImage = findViewById(R.id.basic_facial_expression_image);
        mBasicFacialExpValText = findViewById(R.id.basic_facial_expression_val_text);

        // Initialize layout for Valence Facial Expression
        mValenceFacialExpImageDrawables = new int[] {
                R.drawable.ic_facial_exp_positive, R.drawable.ic_facial_exp_negative, R.drawable.ic_facial_exp_neutral,
        };
        mValenceFacialExpValContainer = findViewById(R.id.valence_facial_expression_val_container);
        mValenceFacialExpImage = findViewById(R.id.valence_facial_expression_image);
        mValenceFacialExpValText = findViewById(R.id.valence_facial_expression_val_text);

        // Initialize layout for Attention
        mAttentionValContainer = findViewById(R.id.attention_val_container);
        mAttentionValText = findViewById(R.id.attention_val_text);

        // Initialize layout for Heart Rate
        mHRValContainer = findViewById(R.id.hr_val_container);
        mHRValText = findViewById(R.id.hr_val_text);
        mHRSpinnerDummy = findViewById(R.id.hr_spinner_dummy);
        mHRSpinner = findViewById(R.id.hr_spinner);

        // Initialize layout for Heart Rate Variability
        mHRVValContainer = findViewById(R.id.hrv_val_container);
        mHRVValText = findViewById(R.id.hrv_val_text);
        mHRVSpinnerDummy = findViewById(R.id.hrv_spinner_dummy);
        mHRVSpinner = findViewById(R.id.hrv_spinner);

        // Initialize layout for Engagement
        mEngagementValContainer = findViewById(R.id.engagement_val_container);
        mEngagementValText = findViewById(R.id.engagement_val_text);
        mEngagementSpinnerDummy = findViewById(R.id.engagement_spinner_dummy);
        mEngagementSpinner = findViewById(R.id.engagement_spinner);
    }

    /**
     * Starts Marvrus process.
     */
    private void start() {
        // Show dummy spinners
        mHRSpinnerDummy.setVisibility(View.VISIBLE);
        mHRVSpinnerDummy.setVisibility(View.VISIBLE);
        mEngagementSpinnerDummy.setVisibility(View.VISIBLE);
        mHRSpinner.setVisibility(View.GONE);
        mHRVSpinner.setVisibility(View.GONE);
        mEngagementSpinner.setVisibility(View.GONE);

        // Start Marvrus
        Marvrus.start(mProperty, new Marvrus.MarvrusHandler() {

            @Override
            public void onRecognizedESRC(int id, MarvrusType.Face face, MarvrusType.FacialLandmark facialLandmark, MarvrusType.FacialActionUnit facialActionUnit, MarvrusType.BasicFacialExpression basicFacialExpression, MarvrusType.ValenceFacialExpression valenceFacialExpression, MarvrusType.ProgressRatio progressRatioOnRemoteHR, MarvrusType.RemoteHR remoteHR, MarvrusType.ProgressRatio progressRatioOnHRV, MarvrusType.HRV hrv, MarvrusType.Engagement engagement, MarvrusType.MEEIndex meeIndex, String meeJson, ESRCException e) {
                if (e == null) {
                    Log.d(TAG, "onRecognizedESRC: " + meeJson);
                    Log.d(TAG, "onRecognizedFacialLandmark: " + facialLandmark);
                    Log.d(TAG, "onRecognizedFacialActionUnit: " + facialActionUnit);

                    // Whether face is detected or not
                    if (face.getIsDetect()) {  // If face is detected
                        // Show FaceBox
                        int color = getResources().getColor(R.color.primary_color);
                        mFaceBoxText.setTextColor(color);
                        mFaceBoxDrawable.setStroke(8, color);

                        // Show Attention containers
                        mAttentionValContainer.setVisibility(View.VISIBLE);

                        // Set Basic Facial Expression values
                        ESRCType.BasicFacialExpression.Emotion basicEmotion = basicFacialExpression.getEmotion();
                        if (basicEmotion != null) {
                            mBasicFacialExpImage.setImageResource(mBasicFacialExpImageDrawables[basicEmotion.ordinal()]);
                        }
                        mBasicFacialExpValText.setText(basicFacialExpression.getEmotionStr());

                        // Show container for Basic Facial Expression
                        mBasicFacialExpValContainer.setVisibility(View.VISIBLE);

                        // Set Valence Facial Expression values
                        ESRCType.ValenceFacialExpression.Emotion valenceEmotion = valenceFacialExpression.getEmotion();
                        if (valenceEmotion != null) {
                            mValenceFacialExpImage.setImageResource(mValenceFacialExpImageDrawables[valenceEmotion.ordinal()]);
                        }
                        mValenceFacialExpValText.setText(valenceFacialExpression.getEmotionStr());

                        // Show container for Valence Facial Expression
                        mValenceFacialExpValContainer.setVisibility(View.VISIBLE);

                        // Set HR spinner
                        mHRSpinner.setProgress((int) progressRatioOnRemoteHR.getProgress());

                        // Hide dummy spinner
                        if (mHRSpinnerDummy.getVisibility() == View.VISIBLE) {
                            mHRSpinnerDummy.setVisibility(View.GONE);
                            mHRSpinner.setVisibility(View.VISIBLE);
                        }

                        // If HR is estimated
                        if (mHRSpinner.getProgress() >= 100) {
                            // Hide HR spinner
                            if(mHRSpinner.getVisibility() == View.VISIBLE) {
                                mHRSpinner.setVisibility(View.GONE);
                                mHRSpinnerDummy.setVisibility(View.GONE);
                            }

                            // Set HR values
                            mHRValText.setText(Long.toString(Math.round(remoteHR.getHR())));

                            // Show container for HR
                            mHRValContainer.setVisibility(View.VISIBLE);
                        }

                        // Set HRV and Engagement spinner
                        mHRVSpinner.setProgress((int) progressRatioOnHRV.getProgress());
                        mEngagementSpinner.setProgress((int) progressRatioOnHRV.getProgress());

                        // Hide dummy spinner
                        if (mHRVSpinnerDummy.getVisibility() == View.VISIBLE) {
                            mHRVSpinnerDummy.setVisibility(View.GONE);
                            mEngagementSpinnerDummy.setVisibility(View.GONE);
                            mHRVSpinner.setVisibility(View.VISIBLE);
                            mEngagementSpinner.setVisibility(View.VISIBLE);
                        }

                        // If HRV is analyzed
                        if (mHRVSpinner.getProgress() >= 100) {
                            // Hide HRV spinner
                            if(mHRVSpinner.getVisibility() == View.VISIBLE) {
                                mHRVSpinner.setVisibility(View.GONE);
                                mHRVSpinnerDummy.setVisibility(View.GONE);
                            }

                            // Set HRV values
                            mHRVValText.setText(Double.toString((double)Math.round(hrv.getSdnn() * 100) / 100));

                            // Show container for HRV
                            mHRVValContainer.setVisibility(View.VISIBLE);

                            // Hide Engagement spinner
                            if(mEngagementSpinner.getVisibility() == View.VISIBLE) {
                                mEngagementSpinner.setVisibility(View.GONE);
                                mEngagementSpinnerDummy.setVisibility(View.GONE);
                            }

                            // Set Engagement values
                            mEngagementValText.setText(engagement.getEmotionStr());

                            // Show container for Engagement
                            mEngagementValContainer.setVisibility(View.VISIBLE);
                        }
                    } else {  // If face is not detected
                        // Hide FaceBox
                        int color = getResources().getColor(R.color.gray);
                        mFaceBoxText.setTextColor(color);
                        mFaceBoxDrawable.setStroke(4, color);

                        // Hide containers
                        mBasicFacialExpValContainer.setVisibility(View.GONE);
                        mValenceFacialExpValContainer.setVisibility(View.GONE);
                        mAttentionValContainer.setVisibility(View.GONE);
                        mHRValContainer.setVisibility(View.GONE);
                        mHRVValContainer.setVisibility(View.GONE);
                        mEngagementValContainer.setVisibility(View.GONE);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Stop Marvrus process.
     */
    private void stop() {
        // Stop Marvrus
        Marvrus.stop();
    }

    /**
     * Check granted permissions.
     */
    private boolean hasPermissions(String[] permissions) {
        int result;

        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);

            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Request permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("If you reject permission, you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]");
                }
                break;
        }
    }

    /**
     * Show dialog for permission.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Permission");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("DENY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}