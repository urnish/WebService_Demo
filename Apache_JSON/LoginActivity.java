package com.nationalhandloomcorp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nationalhandloomcorp.R;
import com.nationalhandloomcorp.dbhandler.DBHandler;
import com.nationalhandloomcorp.util.Constant;
import com.nationalhandloomcorp.util.Global;
import com.nationalhandloomcorp.util.RequestMethod;
import com.nationalhandloomcorp.util.RestClient;
import com.nationalhandloomcorp.util.Tools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    TextView signup,signin1,forgotpass,showhide;
    EditText etemailphone,etPassword;
    Global global;
    ProgressDialog progressDialog;
    Context context;
    SharedPreferences mprefs;
    RelativeLayout loginroot;
    DBHandler dbHandler;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getWindow().setBackgroundDrawableResource(R.drawable.bg_screen);
        context = this;
        global = new Global(context);
        dbHandler = new DBHandler(context);
        mprefs = getSharedPreferences(Constant.PREFS_NAME,MODE_PRIVATE);
        inticomp();
        Tools.systemBarLolipop(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // checkpermission();
            insertDummyContactWrapper();

        }

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent it = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(it);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        showhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showhide.getText().equals("Hide"))
                {
                    showhide.setText("Show");
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                else if(showhide.getText().equals("Show"))
                {
                    showhide.setText("Hide");
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
        signin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateEmailPhone()) {
                    return;
                }

                if(global.isNetworkAvailable())
                {
                    hideKeyboard();
                    new loginAsync().execute();
                }
                else {
                    retryInternet();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void inticomp()
    {
        signup = (TextView)findViewById(R.id.signup);
        signin1 = (TextView)findViewById(R.id.tvSignup);
        etemailphone = (EditText) findViewById(R.id.etemailphone);
        etPassword = (EditText) findViewById(R.id.etPassword);
        forgotpass= (TextView)findViewById(R.id.forgotpass);
        showhide= (TextView)findViewById(R.id.showhide);

        loginroot = (RelativeLayout) findViewById(R.id.loginroot);
    }


    public void retryInternet()
    {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.layout_nointernet);
        Button btnRetryinternet = (Button) dialog.findViewById(R.id.btnRetryinternet);
        btnRetryinternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(global.isNetworkAvailable())
                {
                    dialog.dismiss();
                    hideKeyboard();
                    new loginAsync().execute();
                }
                else {
                    Toast.makeText(context,R.string.nonetwork,Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }


    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private boolean validateEmailPhone() {
        if (etemailphone.getText().toString().trim().isEmpty())
        {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            Toast.makeText(LoginActivity.this,"Enter mobile number",Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }

    private boolean validatePassword() {

        if (etPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class loginAsync extends AsyncTask<String,Void,String> {

        JSONObject jsonObjectList;
        String resMessage = "";
        String resCode =  "";
        String stremailphone = "";
        String strPassword = "";
        String resId = "";
        String resName = "";
        String resEmail = "";
        String resPhone = "";
        String resImage = "";
        String resWishCount = "";
        String resCity = "";
        String strWishlist = "";
        String otp ="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            stremailphone = etemailphone.getText().toString().trim().replaceAll(" ","%20");

        }

        @Override
        protected String doInBackground(String... params) {

            String strLogincall = Constant.BASE_URL + Constant.LOGIN_URL_TWO +stremailphone;
            String strLogincalltrim = strLogincall.replaceAll(" ","%20");
            Log.d("strLogincalltrim", strLogincalltrim);
            try
            {
                RestClient restClient=new RestClient(strLogincalltrim);
                try {
                    restClient.Execute(RequestMethod.GET);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                String VerifyUser=restClient.getResponse();
                Log.e("VerifyUser", VerifyUser);

                if(VerifyUser!=null && VerifyUser.length()!=0)
                {
                    jsonObjectList=new JSONObject(VerifyUser);
                    if(jsonObjectList.length()!=0) {
                        resMessage = jsonObjectList.getString("message");
                        resCode = jsonObjectList.getString("msgcode");
                        otp = jsonObjectList.getString("otp");
                        if(resCode.equalsIgnoreCase("0"))
                        {
                            JSONArray jsonArray = jsonObjectList.getJSONArray("detail");
                            {
                                JSONObject jsonObjectList=jsonArray.getJSONObject(0);

                                resId = jsonObjectList.getString("userID");
                                resName = jsonObjectList.getString("name");
                                resEmail = jsonObjectList.getString("email");
                                resPhone = jsonObjectList.getString("phone");
                                resCity= jsonObjectList.getString("city");
                                resImage = jsonObjectList.getString("image");
                                resWishCount = jsonObjectList.getString("wish_count");

                                JSONArray jsonArray1 = jsonObjectList.getJSONArray("wish_productID_list");
                                {
                                    if(jsonArray1!=null && jsonArray1.length()!=0);
                                    {
                                        for(int i=0;i<jsonArray1.length();i++)
                                        {
                                            JSONObject jsonObjectListWish=jsonArray1.getJSONObject(i);
                                            strWishlist = jsonObjectListWish.getString("productID");
                                            dbHandler.addProduct(strWishlist);
                                        }
                                    }
                                }
                            }

                        }

                    }

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog.isShowing() && progressDialog!=null)
            {
                progressDialog.dismiss();
                if(resCode.equalsIgnoreCase("0"))
                {

                    Intent intent = new Intent(context,OTPActivity.class);
                    intent.putExtra("otp",otp);
                    intent.putExtra("phonenumber",stremailphone);

                    intent.putExtra("resId",resId);
                    intent.putExtra("resName",resName);
                    intent.putExtra("resPhone",resPhone);
                    intent.putExtra("resImage",resImage);
                    intent.putExtra("resEmail",resEmail);
                    intent.putExtra("resCity",resCity);
                    intent.putExtra("wishlistsize",dbHandler.getAllProduct().size());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                }
                else if(resCode.equalsIgnoreCase("1"))
                {
                    Toast.makeText(context,resMessage,Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_SMS))
            permissionsNeeded.add("Read SMS");

        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Contacts");

        if (permissionsList.size() > 0)
        {
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        //  insertDummyContact();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();


                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                //  && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                if (perms.get(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                        )
                {

                    //   checkGPS();
                }
                else
                {
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private long exitTime = 0;

    //   @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void doExitApp()
    {
        if ((System.currentTimeMillis() - exitTime) > 2000)
        {
            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            // finishAffinity();

        }
        else
        {
            finish();
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
        }

    }
    @Override
    public void onBackPressed()
    {
        doExitApp();
    }



}
