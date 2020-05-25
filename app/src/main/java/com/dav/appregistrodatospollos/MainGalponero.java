package com.dav.appregistrodatospollos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

public class MainGalponero extends AppCompatActivity implements View.OnClickListener {

    Button btnSync, btnAgregar;
    EditText etFecha, etGalpon, etMort, etAlimento, etPeso;
    TextView tvNombre, tvGranja;
    private Toolbar toolbar;
    RecyclerView rvLocal;
    List<Tb_Detalles_Class> listaLocal = new ArrayList<>();
    AdapterLocal adapterLocal;
    private ProgressDialog progressDialog;

    int documentoGalponero;
    private int day, month, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_galponero);

        btnSync = findViewById(R.id.btnSync);
        btnAgregar = findViewById(R.id.btnAgregar);
        etFecha = findViewById(R.id.etFecha);
        etGalpon = findViewById(R.id.etGalpon);
        etMort = findViewById(R.id.etMortalidad);
        etAlimento = findViewById(R.id.etAlimento);
        etPeso = findViewById(R.id.etPeso);

        tvNombre = findViewById(R.id.tvNombre);
        tvGranja = findViewById(R.id.tvGranja);

        progressDialog= new ProgressDialog(this);

        rvLocal = findViewById(R.id.rvLocal);
        rvLocal.setLayoutManager(new GridLayoutManager(this, 1));

        etFecha.setOnClickListener(this);

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
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


        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sincronizar();
            }
        });
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etFecha.getText().toString().equals("") || etAlimento.getText().toString().equals("") ||
                        etPeso.getText().toString().equals("") || etMort.getText().toString().equals("")
                        || etGalpon.getText().toString().equals("")) {
                    Toast.makeText(MainGalponero.this, "SE DEBEN LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
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
            case R.id.action_favorite:
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
        progressDialog.setMessage("Cargando datos");
        progressDialog.show();
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
            json.put("Detalles", jsonArrayProducto);//Productos
            progressDialog.dismiss();
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
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainGalponero.this, "dbSistema", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();
                    admin.borrarRegistros(db);

                    obtenerLocal();
                    Toast.makeText(MainGalponero.this,"Se envio datos exitosamente", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
        registro.put("galpon", etGalpon.getText().toString());
        registro.put("galponero", String.valueOf(documentoGalponero));
        registro.put("mortalidad", etMort.getText().toString());
        registro.put("alimento", etAlimento.getText().toString());
        registro.put("peso", etPeso.getText().toString());

        db.insert("tb_detalles", null, registro);

        etGalpon.setText("");
        etMort.setText("");
        etAlimento.setText("");
        etPeso.setText("");

        db.close();
        obtenerLocal();
    }

    public void obtenerLocal() {
        listaLocal.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainGalponero.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from tb_detalles", null);
        if(fila != null && fila.getCount() != 0) {
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
        } else {
            adapterLocal = new AdapterLocal(MainGalponero.this, listaLocal);
            rvLocal.setAdapter(adapterLocal);
            Toast.makeText(this, "No hay registros", Toast.LENGTH_SHORT).show();
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
}
