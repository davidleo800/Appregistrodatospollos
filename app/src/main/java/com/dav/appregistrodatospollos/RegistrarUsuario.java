package com.dav.appregistrodatospollos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuario extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{

    private MaterialToolbar materialToolbar;
    private TextInputLayout tiDocumento, tiNombre, tiApellido, tiGranja;
    private TextInputEditText tietDocumento, tietNombre, tietApellido, tietGranja;
    private Button btnRegistrar;
    private RadioButton rbVeterinario, rbGranjero;
    private RadioGroup rgItems;
    private CoordinatorLayout coorLayout;
    ProgressDialog progreso;
    private int typeUser;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        tiDocumento = findViewById(R.id.tiDocu);
        tiNombre = findViewById(R.id.tiNom);
        tiApellido = findViewById(R.id.tiApell);
        tiGranja = findViewById(R.id.tiGranja);

        tietDocumento = findViewById(R.id.tietDocu);
        tietNombre = findViewById(R.id.tietNom);
        tietApellido = findViewById(R.id.tietApell);
        tietGranja = findViewById(R.id.tietGranja);

        rbVeterinario = findViewById(R.id.rbVet);
        rbGranjero = findViewById(R.id.rbGranj);

        rgItems = findViewById(R.id.rgItems);

        btnRegistrar = findViewById(R.id.btnReg);

        materialToolbar = findViewById(R.id.topAppBar);

        coorLayout = findViewById(R.id.coordinatorLayoutreg);

        progreso = new ProgressDialog(this);

        request = Volley.newRequestQueue(getApplicationContext());



        tiGranja.setVisibility(View.GONE);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), Login.class);
                startActivityForResult(intent, 0);
            }
        });

        textwatcherValidacion();

        rgItems.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rbVet){
                    tiGranja.setVisibility(View.GONE);
                } else {
                    tiGranja.setVisibility(View.VISIBLE);
                }
            }
        });


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                if(tiDocumento.getEditText().getText().toString().equals("") ||
                        tiNombre.getEditText().getText().toString().equals("") ||
                            tiApellido.getEditText().getText().toString().equals("") ||
                        ((rbVeterinario.isChecked() == false && rbGranjero.isChecked() == false))){

                    if(tiDocumento.getEditText().getText().toString().equals(""))
                        tiDocumento.setError("Complete este campo");
                    else  tiDocumento.setError(null);
                    if(tiNombre.getEditText().getText().toString().equals(""))
                        tiNombre.setError("Complete este campo");
                    else tiNombre.setError(null);
                    if(tiApellido.getEditText().getText().toString().equals(""))
                        tiApellido.setError("Complete este campo");
                    else tiApellido.setError(null);
                    if(rbVeterinario.isChecked() == false && rbGranjero.isChecked() == false){
                        rgItems.setBackgroundColor(R.color.colorRed);
                        Toast.makeText(RegistrarUsuario.this, "Seleccione tipo de usuario", Toast.LENGTH_LONG).show();
                    }



                    Toast.makeText(RegistrarUsuario.this, "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    if(tiGranja.getEditText().getText().toString().equals("") && rbGranjero.isChecked()){
                        if(tiGranja.getEditText().getText().toString().equals(""))
                            tiGranja.setError("Complete este campo");
                        else tiGranja.setError(null);
                    }else{
                        validateUser();
                        //cargarWebService();
                    }
                }
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(btnRegistrar.getWindowToken(), 0);
            }
        });


    }

    @Override
    public void onErrorResponse(VolleyError error) {

        progreso.dismiss();
        Toast.makeText(RegistrarUsuario.this, "Error al registrar"+error.toString(), Toast.LENGTH_SHORT).show();
        Log.i("Error", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {

        progreso.dismiss();
        Snackbar.make(coorLayout, "Registro exitoso", Snackbar.LENGTH_LONG).show();
        tietDocumento.setText("");
        tietNombre.setText("");
        tietApellido.setText("");
        tietGranja.setText("");
        rgItems.clearCheck();
        tiGranja.setVisibility(View.GONE);

    }

    private void cargarWebService() {

        int usuario;
        String url;
        if (rbVeterinario.isChecked()) {

            usuario = 1;
             url = "https://weakened-bet.000webhostapp.com/webservice_app/RegistroUsuarios.php?" +
                     "ID_CC="+tiDocumento.getEditText().getText().toString()+
                     "&Nombre="+tiNombre.getEditText().getText().toString()+
                     "&Apellido="+tiApellido.getEditText().getText().toString()+
                     "&usuario="+usuario;
            url = url.replace(" ", "%20");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            request.add(jsonObjectRequest);
        }
        else {

            usuario = 2;
            url = "https://weakened-bet.000webhostapp.com/webservice_app/RegistroUsuarios.php?" +
                    "ID_CC="+tiDocumento.getEditText().getText().toString()+
                    "&Nombre="+tiNombre.getEditText().getText().toString()+
                    "&Apellido="+tiApellido.getEditText().getText().toString()+
                    "&usuario="+usuario+"&granja="+tiGranja.getEditText().getText().toString();
            url = url.replace(" ", "%20");

            jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            request.add(jsonObjectRequest);
        }


    }

    public void textwatcherValidacion() {

        tietDocumento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiDocumento.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiNombre.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietApellido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiApellido.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietGranja.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiGranja.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void validateUser(){
        progreso.setMessage("Cargando datos");
        progreso.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.URL_LOGIN), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    progreso.dismiss();
                    tiDocumento.setError("Usuario ya existe");
                    Toast.makeText(RegistrarUsuario.this, "Usuario existente, por favor ingrese uno nuevo",
                            Toast.LENGTH_SHORT).show();
                }else{
                    // no existe usuario
                    cargarWebService();
                }

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progreso.dismiss();
                Toast.makeText(RegistrarUsuario.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("usuario", tiDocumento.getEditText().getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
