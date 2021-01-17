
package com.crystalaboujneid.bloodbank.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crystalaboujneid.bloodbank.R;
import com.crystalaboujneid.bloodbank.Utils.Endpoints;
import com.crystalaboujneid.bloodbank.Utils.VolleySingleton;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner,spinner1;
    private EditText nameEt, cityEt, bloodGroupEt, donorEt, passwordEt, mobileEt;
    private Button submitButton;
    String blood = "no";
    String group;
    TextView eligibilityEt;
    CheckBox yesCb, noCb;
    ImageView bgimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameEt = findViewById(R.id.name);
        cityEt = findViewById(R.id.city);
        passwordEt = findViewById(R.id.password);
        mobileEt = findViewById(R.id.number);

        eligibilityEt = findViewById(R.id.check_eligibility);
        eligibilityEt.setMovementMethod(LinkMovementMethod.getInstance());

        final Animation zoomin = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        final Animation zoomout = AnimationUtils.loadAnimation(this, R.anim.zoomout);

        bgimage = findViewById(R.id.back2);
        bgimage.setAnimation(zoomin);
        bgimage.setAnimation(zoomout);

        zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(zoomin);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(zoomout);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        yesCb = findViewById(R.id.checkbox_yes);
        //noCb = findViewById(R.id.checkbox_no);

        yesCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    blood = "yes";
                } else{
                    blood = "no";
                }
            }
        });

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner1 = (Spinner)findViewById(R.id.spinner1);

        String[] items1 = new String[] { "A", "A+", "A-", "B", "B+", "B-", "AB", "AB+", "AB-", "O","O+", "O-"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item,items1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        group = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("");
                builder.setMessage("By pressing Continue, you confirm that the information you entered is correct.");

                builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name, city, blood_group, blood_donor, password, mobile;
                        name = nameEt.getText().toString();
                        city = cityEt.getText().toString();
                        //blood_group = bloodGroupEt.getText().toString();
                        //blood_donor = donorEt.getText().toString();
                        blood_group = group;
                        blood_donor = blood;
                        password = passwordEt.getText().toString();
                        mobile = mobileEt.getText().toString();
                        //showMessage(name+"\n"+city+"\n"+blood_group+"\n"+password+"\n"+mobile);

                        if(isValid(name, city, blood_group, blood_donor, password, mobile)){
                            register(name, city, blood_group, blood_donor, password, mobile);
                        }
                    }
                });

                builder.create().show();
            }
        });
    }





    private void register(final String name, final String city, final String blood_group, final String blood_donor, final String password, final String mobile){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.register_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("Success")){
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("city",city).apply();
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    RegisterActivity.this.finish();
                }else{
                    Toast.makeText(RegisterActivity.this, response, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("VOLLEY", error.getMessage());
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("city", city);
                params.put("blood_group", blood_group);
                params.put("blood_donor",blood_donor);
                params.put("password", password);
                params.put("number", mobile);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean isValid(String name, String city, String blood_group, String blood_donor, String password, String mobile){
        List<String> valid_blood_groups = new ArrayList<>();
        valid_blood_groups.add("A+");
        valid_blood_groups.add("A");
        valid_blood_groups.add("A-");
        valid_blood_groups.add("B+");
        valid_blood_groups.add("B");
        valid_blood_groups.add("B-");
        valid_blood_groups.add("AB");
        valid_blood_groups.add("AB+");
        valid_blood_groups.add("AB-");
        valid_blood_groups.add("O");
        valid_blood_groups.add("O+");
        valid_blood_groups.add("O-");

        if(name.isEmpty()){
            showMessage("Name is Empty");
            return false;
        }else if(city.isEmpty()){
            showMessage("City Name is Required");
            return false;
        } else if(!valid_blood_groups.contains(blood_group)){
            showMessage("Blood Group is invalid, choose from " + valid_blood_groups);
            return false;
        } else if(blood_donor.isEmpty()){
            showMessage("State whether you want to be a donor or not");
            return false;
        }else if(mobile.isEmpty() || mobile.length() != 8){
            showMessage("Mobile Number should be 8 digits");
            return false;
        }
        return true;
    }

    private void showMessage(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}