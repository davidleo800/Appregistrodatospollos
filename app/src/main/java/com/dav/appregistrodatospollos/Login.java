package com.dav.appregistrodatospollos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    TextInputLayout etDocumento;
    TextInputEditText tiDococumento;
    Button btIngresar;
    private ProgressDialog progressDialog;
    String documento, granja, nombre;
    private TextView tvSignup;
    int typeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etDocumento = findViewById(R.id.etDoc);
        tiDococumento = findViewById(R.id.tiDoc);

        tvSignup = findViewById(R.id.link_signup);

        btIngresar = findViewById(R.id.btIngresar);

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), RegistrarUsuario.class);
                startActivityForResult(intent, 0);
            }
        });

        recoverPreferences();
        btIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documento = etDocumento.getEditText().getText().toString();
                if(!documento.isEmpty()) {
                    validateUser();
                }else {
                    Toast.makeText(Login.this, "Ingrese un dato",Toast.LENGTH_LONG).show();
                }
            }
        });

        progressDialog= new ProgressDialog(this);

    }

    private void validateUser(){

        progressDialog.setMessage("Iniciado sesión");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.URL_LOGIN), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){

                try {
                    JSONObject jsonRespuesta = new JSONObject(response);
                        typeUser = jsonRespuesta.getInt("usuario");
                        granja = jsonRespuesta.getString("granja");
                        int doc = jsonRespuesta.getInt("ID_CC");
                        nombre = jsonRespuesta.getString("Nombre");
                        savePreferences();
                        if(typeUser == 1) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("Nombre", nombre);
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(getApplicationContext(), MainGalponero.class);
                            intent.putExtra("Granja", granja);
                            intent.putExtra("Doc", doc);
                            intent.putExtra("Nombre", nombre);
                            progressDialog.dismiss();
                            startActivity(intent);
                            finish();
                        }

                }catch (JSONException e){
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Usuario incorrecto", Toast.LENGTH_SHORT).show();
                }

                }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", documento);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("preferencesLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Documento", documento);
        editor.putInt("typeUser", typeUser);
        editor.putString("Granja", granja);
        editor.putString("Nombre", nombre);
        editor.putBoolean("Session", true);
        editor.commit();
    }

    private void recoverPreferences(){
        SharedPreferences preferences = getSharedPreferences("preferencesLogin", Context.MODE_PRIVATE);
        tiDococumento.setText(preferences.getString("Documento",""));
    }


}
