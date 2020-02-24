package com.dav.appregistrodatospollos;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.ContentValues;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button btnSync, btnAgregar;
    EditText etFecha, etGalponero, etMort, etAlimento, etPeso, etVeterinario;
    Spinner sGranja, sGalpon;
    private Toolbar toolbar;

    RecyclerView rvServidor, rvLocal;



    List<Tb_Detalles_Class> listaServidor = new ArrayList<>();
    List<Tb_Detalles_Class> listaLocal = new ArrayList<>();

    AdapterServer adapterServidor;
    AdapterLocal adapterLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();


        btnSync = findViewById(R.id.btnSync);
        btnAgregar = findViewById(R.id.btnAgregar);
        etFecha = findViewById(R.id.etFecha);
        etGalponero = findViewById(R.id.etGalponero);
        etMort = findViewById(R.id.etMortalidad);
        etAlimento = findViewById(R.id.etAlimento);
        etPeso = findViewById(R.id.etPeso);
        etVeterinario = findViewById(R.id.etVeterinario);

        sGranja = findViewById(R.id.sGranja);
        sGalpon = findViewById(R.id.sGalpon);

        rvServidor = findViewById(R.id.rvServidor);
        rvServidor.setLayoutManager(new GridLayoutManager(this, 1));
        rvLocal = findViewById(R.id.rvLocal);
        rvLocal.setLayoutManager(new GridLayoutManager(this, 1));

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = formato.format(new Date());
        etFecha.setText(fecha);

        obtenerServidor();
        obtenerLocal();

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
                        || etVeterinario.getText().toString().equals("") || etGalponero.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "SE DEBEN LLENAR TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();
                } else {
                    agregarLocal();
                }
            }
        });
    }
    private void setUpToolBar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void sincronizar() {
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
                jsonObjectProducto.put("veterinario", listaLocal.get(i).getVeterinario());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArrayProducto.put(jsonObjectProducto);
        }
        JSONObject json = new JSONObject();
        try {
            json.put("Detalles", jsonArrayProducto);//Productos
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonStr = json.toString();
        registrarServidor(jsonStr);
    }

    public void registrarServidor(final String json) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_REGISTRAR_tb_detalles), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("OK")) {
                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "dbSistema", null, 1);
                    SQLiteDatabase db = admin.getWritableDatabase();
                    admin.borrarRegistros(db);

                    obtenerServidor();
                    obtenerLocal();
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
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();
        registro.put("fecha", etFecha.getText().toString());
        registro.put("granja", sGranja.getSelectedItem().toString());
        registro.put("galpon", sGalpon.getSelectedItem().toString());
        registro.put("galponero",etGalponero.getText().toString());
        registro.put("mortalidad", etMort.getText().toString());
        registro.put("alimento", etAlimento.getText().toString());
        registro.put("peso", etPeso.getText().toString());
        registro.put("veterinario", etVeterinario.getText().toString());

        db.insert("tb_detalles", null, registro);

        etGalponero.setText("");
        etMort.setText("");
        etAlimento.setText("");
        etPeso.setText("");
        etVeterinario.setText("");


        db.close();

        obtenerLocal();

    }

    public void obtenerServidor() {
        listaServidor.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_OBTENER_tb_detalles),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("tb_detalles"); //Ventas

                            for(int i = 0 ; i < jsonArray.length() ; i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                listaServidor.add(
                                        new Tb_Detalles_Class(
                                                jsonObject1.getString("Id_detalle"),
                                                jsonObject1.getString("fecha"),
                                                jsonObject1.getString("granja"),
                                                jsonObject1.getString("galpon"),
                                                jsonObject1.getString("galponero"),
                                                jsonObject1.getString("mortalidad"),
                                                jsonObject1.getString("alimento"),
                                                jsonObject1.getString("peso"),
                                                jsonObject1.getString("veterinario")
                                        )
                                );
                            }

                            adapterServidor = new AdapterServer(MainActivity.this, listaServidor);
                            rvServidor.setAdapter(adapterServidor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(stringRequest);
    }

    public void obtenerLocal() {
        listaLocal.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(MainActivity.this, "dbSistema", null, 1);
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
                                fila.getString(7),
                                fila.getString(8)
                        )
                );
            } while (fila.moveToNext());

            adapterLocal = new AdapterLocal(MainActivity.this, listaLocal);
            rvLocal.setAdapter(adapterLocal);
        } else {
            adapterLocal = new AdapterLocal(MainActivity.this, listaLocal);
            rvLocal.setAdapter(adapterLocal);
            Toast.makeText(this, "No hay registros", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
