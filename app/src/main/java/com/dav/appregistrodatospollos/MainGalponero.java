package com.dav.appregistrodatospollos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class MainGalponero extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout tiGalpon, tiAlimento, tiMortalidad, tiPeso;
    TextInputEditText tietGalpon, tietAlimento, tietMortalidad, tietPeso;
    Button btnAgregar;
    // Button btnSync;
    EditText etFecha;
    TextView tvNombre, tvGranja, tvregistros;
    private Toolbar toolbar;
    RecyclerView rvLocal;
    List<Tb_Detalles_Class> listaLocal = new ArrayList<>();
    AdapterLocal adapterLocal;
    private ProgressDialog progressDialog;
    FloatingActionButton floatingButton;
    CoordinatorLayout coorLayout;
    int documentoGalponero;
    private int day, month, year;
    boolean click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_galponero);

        // btnSync = findViewById(R.id.btnSync);
        btnAgregar = findViewById(R.id.btnAgregar);

        tiGalpon = findViewById(R.id.tiGal);
        tiAlimento = findViewById(R.id.tiAlim);
        tiMortalidad = findViewById(R.id.tiMort);
        tiPeso = findViewById(R.id.tiPeso);

        tietGalpon = findViewById(R.id.tietGal);
        tietAlimento = findViewById(R.id.tiettAlim);
        tietMortalidad = findViewById(R.id.tietMort);
        tietPeso = findViewById(R.id.tietPeso);

        etFecha = findViewById(R.id.etFecha);


        tvNombre = findViewById(R.id.tvNombre);
        tvGranja = findViewById(R.id.tvGranja);
        tvregistros = findViewById(R.id.textView2);

        coorLayout = findViewById(R.id.coordinatorLayout);

        rvLocal = findViewById(R.id.rvLocal);
        // Definicion de floating action button
        floatingButton = findViewById(R.id.floating_action_button);

        progressDialog= new ProgressDialog(this);

        rvLocal.setLayoutManager(new GridLayoutManager(this, 1));
        etFecha.setInputType(InputType.TYPE_NULL);
        etFecha.setOnClickListener(this);

        floatingButton.show();
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = !click;
                progressDialog.setMessage("Cargando datos");
                progressDialog.show();

                sincronizar();
                // Snackbar.make(v, "Se presionó el FAB", Snackbar.LENGTH_LONG).show();

            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(new Date());
        etFecha.setText(fecha);

        Bundle extras = getIntent().getExtras();
        String granja;
        String nombre;
        /*
        SharedPreferences preferences = getSharedPreferences("preferencesLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Documento", documento);
        */
        SharedPreferences preferences1 = getSharedPreferences("preferencesLogin1", Context.MODE_PRIVATE);
        granja = preferences1.getString("Granja","");
        nombre = preferences1.getString("Nombre","");
        documentoGalponero = preferences1.getInt("Doc",0);

            if (granja == "" || nombre == "" || documentoGalponero == 0) {
                nombre = extras.getString("Nombre");
                granja = extras.getString("Granja");
                documentoGalponero = extras.getInt("Doc");
                tvNombre.setText(nombre);
                tvGranja.setText(granja);
                SharedPreferences preferences = getSharedPreferences("preferencesLogin1", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Granja", granja);
                editor.putString("Nombre", nombre);
                editor.putInt("Doc", documentoGalponero);
                editor.commit();
            } else {
                tvNombre.setText(nombre);
                tvGranja.setText(granja);
            }


        /*btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sincronizar();
            }
        });*/

        textwatcherValidacion();

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(etFecha.getText().toString().equals("") || tiAlimento.getEditText().getText().toString().equals("") ||
                        tiPeso.getEditText().getText().toString().equals("") || tiMortalidad.getEditText().getText().toString().equals("")
                        || tiGalpon.getEditText().getText().toString().equals("")) {

                    if(tiGalpon.getEditText().getText().toString().equals(""))
                        tiGalpon.setError("Complete este campo");
                    else  tiGalpon.setError(null);
                    if(tiAlimento.getEditText().getText().toString().equals(""))
                        tiAlimento.setError("Complete este campo");
                    else tiAlimento.setError(null);
                    if(tiMortalidad.getEditText().getText().toString().equals(""))
                        tiMortalidad.setError("Complete este campo");
                    else tiMortalidad.setError(null);
                    if(tiPeso.getEditText().getText().toString().equals(""))
                        tiPeso.setError("Complete este campo");
                    else tiPeso.setError(null);


                    Toast.makeText(MainGalponero.this, "Se deben completar todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    agregarLocal();
                }
            }
        });
        obtenerLocal();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // User chose the "Settings" item, show the app settings UI...
                SharedPreferences preferences = getSharedPreferences("preferencesLogin", Context.MODE_PRIVATE);
                SharedPreferences preferences1 = getSharedPreferences("preferencesLogin1", Context.MODE_PRIVATE);
                preferences.edit().clear().commit();
                preferences1.edit().clear().commit();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void sincronizar() {
        floatingButtonSync();
        JSONArray jsonArrayProducto = new JSONArray();
        for(int i = 0 ; i < listaLocal.size() ; i++) {
            JSONObject jsonObjectProducto = new JSONObject();
            try {
                jsonObjectProducto.put("fecha", listaLocal.get(i).getFecha());
                jsonObjectProducto.put("granja", listaLocal.get(i).getGranja());
                jsonObjectProducto.put("galpon", listaLocal.get(i).getGalpon());
                jsonObjectProducto.put("galponero", listaLocal.get(i).getGalponero());
                jsonObjectProducto.put("mortalidad", listaLocal.get(i).getMortalidad());
                jsonObjectProducto.put("alimento", listaLocal.get(i).getAlimento());
                jsonObjectProducto.put("peso", listaLocal.get(i).getPeso());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArrayProducto.put(jsonObjectProducto);
        }
        JSONObject json = new JSONObject();
        try {
            json.put("Detalles", jsonArrayProducto);
        } catch (JSONException e) {
            progressDialog.dismiss();
            e.printStackTrace();
        }

        String jsonStr = json.toString();
        registrarServidor(jsonStr);
    }

    public void registrarServidor(final String json) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainGalponero.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_REGISTRAR_tb_detalles), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("OK")) {
                    floatingButtonDone();
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainGalponero.this, "dbSistema", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();
                    admin.borrarRegistros(db);

                    progressDialog.dismiss();
                    Snackbar.make(coorLayout, "Se envió datos exitosamente", Snackbar.LENGTH_LONG).show();
                    obtenerLocal();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("json", json);
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void agregarLocal() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainGalponero.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        //Bundle extras = getIntent().getExtras();
        //int documentoGalponero = extras.getInt("Doc");

        ContentValues registro = new ContentValues();
        registro.put("fecha", etFecha.getText().toString());
        registro.put("granja", tvGranja.getText().toString());
        registro.put("galpon", tiGalpon.getEditText().getText().toString());
        registro.put("galponero", String.valueOf(documentoGalponero));
        registro.put("mortalidad", tiMortalidad.getEditText().getText().toString());
        registro.put("alimento", tiAlimento.getEditText().getText().toString());
        registro.put("peso", tiPeso.getEditText().getText().toString());

        db.insert("tb_detalles", null, registro);

        tietGalpon.setText("");
        tietAlimento.setText("");
        tietMortalidad.setText("");
        tietPeso.setText("");

        db.close();
        obtenerLocal();
    }
    int cont = 0;
    public void obtenerLocal() {
        listaLocal.clear();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainGalponero.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from tb_detalles", null);
        if(fila != null && fila.getCount() != 0) {
            tvregistros.setText("Registros pendientes por cargar");
                cont ++;
                if (cont <= 1) {
                    floatingButtonStart();
                }else{
                    floatingButton.setRotation(0f);
                    floatingButton.show();
                }

            fila.moveToFirst();
            do {
                listaLocal.add(
                        new Tb_Detalles_Class(
                                fila.getString(0),
                                fila.getString(1),
                                fila.getString(2),
                                fila.getString(3),
                                fila.getString(4),
                                fila.getString(5),
                                fila.getString(6),
                                fila.getString(7)
                        )
                );
            } while (fila.moveToNext());

            adapterLocal = new AdapterLocal(MainGalponero.this, listaLocal);
            rvLocal.setAdapter(adapterLocal);
            // btnSync.setEnabled(true);


        } else {
            adapterLocal = new AdapterLocal(MainGalponero.this, listaLocal);
            rvLocal.setAdapter(adapterLocal);
            // btnSync.setEnabled(false);
            floatingButton.hide();
            tvregistros.setText("No hay registros pendientes por cargar");
        }

        db.close();
    }
    @Override
    public void onClick(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                etFecha.setText(i+"-"+(i1+1)+"-"+i2);
            }
        }, year, month,day);
        datePickerDialog.show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void floatingButtonDone() {
        floatingButton.setRotation(0f);
        floatingButton.setImageResource(R.drawable.baseline_cloud_done_white_18dp);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);

            floatingButton.animate().scaleY(3)
                    .scaleX(1).rotation(0)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(600)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                           /* floatingButton.animate()
                                    .scaleY(0)
                                    .scaleX(0)
                                    .setInterpolator(interpolador)
                                    .setDuration(600)
                                    .start();*/
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    public void floatingButtonStart() {
        floatingButton.setImageResource(R.drawable.baseline_cloud_upload_white_18dp);
        floatingButton.show();
        floatingButton.setScaleX(0);
        floatingButton.setScaleY(0);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.fast_out_slow_in);
            floatingButton.animate()
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator(interpolador)
                    .setDuration(600)
                    .setStartDelay(500)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
        }
    }

    public void floatingButtonSync() {
        floatingButton.setImageResource(R.drawable.baseline_sync_white_18dp);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                    android.R.interpolator.linear);

            floatingButton.animate()
                    .rotation( click ? 20000: 20000).setDuration(60000)
                    .setInterpolator(interpolador)
                    .start();
        }
    }

    public void textwatcherValidacion() {

        tietGalpon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiGalpon.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietAlimento.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiAlimento.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietMortalidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiMortalidad.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tietPeso.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiPeso.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

}
