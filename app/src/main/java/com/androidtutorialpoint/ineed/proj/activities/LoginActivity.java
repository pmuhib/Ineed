package com.androidtutorialpoint.ineed.proj.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.androidtutorialpoint.ineed.proj.activities.SignUpActivity.selection;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.androidtutorialpoint.ineed.R;
import com.androidtutorialpoint.ineed.proj.Utils.AppGlobal;
import com.androidtutorialpoint.ineed.proj.Utils.Utillity;
import com.androidtutorialpoint.ineed.proj.models.LoginData;
import com.androidtutorialpoint.ineed.proj.webservices.ApiList;
import com.androidtutorialpoint.ineed.proj.webservices.CustomRequest;
import com.androidtutorialpoint.ineed.proj.webservices.VolleySingelton;
import com.google.gson.Gson;
import com.mukesh.tinydb.TinyDB;

import org.json.JSONObject;

import java.util.HashMap;


public class LoginActivity extends AppCompatActivity {
    TextView txtSignUp,txt_forget,txt_login,txt_skip;
    TextInputEditText et_email,et_password;
    TinyDB sharpref;
    String language,Email,Password, stauts, jobseekerid;
    Gson gson = new Gson();
    LoginData loginData;
    AppGlobal appGlobal = AppGlobal.getInstancess();
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        initialize
        sharpref=new TinyDB(getApplicationContext());
        language=sharpref.getString("language_id");
        appGlobal.context=getApplicationContext();
        loginData = new LoginData();
        requestQueue= VolleySingelton.getsInstance().getmRequestQueue();

//        find jobseekerid
        txt_forget= (TextView) findViewById(R.id.txt_forgetpass);
        txtSignUp= (TextView) findViewById(R.id.txt_sign_up);
        et_email= (TextInputEditText) findViewById(R.id.tiet_email);
        et_password=(TextInputEditText) findViewById(R.id.tiet_password);
        //   txt_login= (TextView) findViewById(R.jobseekerid.login);
        txt_skip= (TextView) findViewById(R.id.txt_skp);
        if (getIntent().hasExtra("search")){
            stauts = getIntent().getStringExtra("search");
            jobseekerid = getIntent().getStringExtra("id");
        }

//        set click listener
        txt_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(LoginActivity.this,Search.class);
                it.putExtra("Login","login");
                startActivity(it);
            }
        });
    /*    txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            }
        });*/


        txt_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordsActivity.class));
            }
        });

        if(language!=null && !language.isEmpty())
        {
            if (language.equals("en"))
            {
                String noAccount="DON'T HAVE AN ACCOUNT? SIGN UP";
                int i=noAccount.indexOf("SI");
                int j=noAccount.indexOf("P");
                txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
                txtSignUp.setText(noAccount, TextView.BufferType.SPANNABLE);
                Spannable spannable= (Spannable) txtSignUp.getText();
                final ClickableSpan clickableSpan=new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        setupoverlay();
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(Color.BLACK);
                        ds.setUnderlineText(false);
                    }
                };

                spannable.setSpan(clickableSpan,i,j+1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if(language.equals("ar"))
            {
                String noAccount="هل لديك حساب بالفعل؟: سجل";
                int i=noAccount.indexOf(" س");
                int j=noAccount.indexOf("جل");
                txtSignUp.setMovementMethod(LinkMovementMethod.getInstance());
                txtSignUp.setText(noAccount, TextView.BufferType.SPANNABLE);
                Spannable spannable= (Spannable) txtSignUp.getText();
                ClickableSpan clickableSpan=new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(Color.BLACK);
                        ds.setUnderlineText(false);
                    }
                };
                spannable.setSpan(clickableSpan,i,j+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


    public void loginclick(View v)
    {
        Email=et_email.getText().toString().trim();
        Password=et_password.getText().toString().trim();
        if(!Email.isEmpty() && !Password.isEmpty())
        {
            if(!Email.isEmpty())
            {
                if (Utillity.isNetworkConnected(this))
                {
                    HashMap<String,String> params=new HashMap<>();
                    params.put("username",Email);
                    params.put("password",Password);
                    params.put("language",language);
                    CustomRequest customRequest=new CustomRequest(Request.Method.POST, ApiList.LOGIN,params,
                            this.success(),this.error());
                    customRequest.setRetryPolicy(new DefaultRetryPolicy(300000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(customRequest);
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            getResources().getString(R.string.internetConnection), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.appbasecolor));
                    snackbar.show();
                }
            }
            else
            {
                Utillity.message(this,getResources().getString(R.string.validemail));
            }
        }
        else
        {
            Utillity.message(this,getResources().getString(R.string.fieldsmeand));
        }
    }


    private Response.Listener<JSONObject> success()
    {   Utillity.showloadingpopup(this);
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Utillity.hidepopup();
                if (response!=null){
                    Log.d("TAG", "onResponse: "+response.toString());
                    loginData = gson.fromJson(response.toString(), LoginData.class);
                    if (loginData.isStatus()== true){
                        appGlobal.setLoginData(response.toString());
                        Utillity.message(getApplicationContext(), loginData.getMsg());
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        if (stauts!=null){
                            if (stauts.equals("search")){

                                startActivity(new Intent(LoginActivity.this,JobseekerDetailActivity.class)
                                        .putExtra("id", jobseekerid));
                            }
                        }
                        finish();
                    } else {
                        Utillity.message(getApplicationContext(), loginData.getMsg());
                    }
                }
            }
        };
    }
    private Response.ErrorListener error()
    {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: "+error.toString());

                Utillity.message(getApplicationContext(), "Connection error ");
                Utillity.hidepopup();

            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        Utillity.hideSoftKeyboard(LoginActivity.this);
    }

    private void setupoverlay() {
        final Dialog dialog=new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.overlay_popup);
        RelativeLayout relativeLayout=(RelativeLayout)dialog.findViewById(R.id.rela_backgrnd);
        TextView textView=(TextView)dialog.findViewById(R.id.txt_msg);
        final Button job=(Button)dialog.findViewById(R.id.overjob);
        Button emp=(Button)dialog.findViewById(R.id.overemp);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                selection="2";  /*It's for Jobseeker*/
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
        emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                selection="1";  /*It's for Employer*/
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
        dialog.show();
    }
}
