package com.dav.appregistrodatospollos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.ProgressDialog;
import android.content.ContentValues;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

import android.animation.Animator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    RecyclerView rvServidor;
    private ProgressDialog progressDialog;
    List<Tb_Detalles_Class> listaServidor = new ArrayList<>();
    AdapterServer adapterServidor;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingButton;
    JSONArray datos;
    TextView tvtitulo;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();
        //setUpToolBar();
        floatingButton = findViewById(R.id.fab_nav_vet);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        rvServidor = findViewById(R.id.rvServidor);
        rvServidor.setLayoutManager(new GridLayoutManager(this, 1));
        progressDialog= new ProgressDialog(this);

        tvtitulo = findViewById(R.id.tvTitulo);
        obtenerServidor();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        rvServidor.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy<0){
                    floatingButtonStart();
                    if (flag == 0)
                        floatingbuttonFinal();
                    else floatingButtonStart();
                }else{
                    floatingbuttonFinal();

                }




            }
        });



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                obtenerServidor();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        /*
        Bundle extras = getIntent().getExtras();
        String nombre;
        SharedPreferences preferences1 = getSharedPreferences("preferencesLogin2", Context.MODE_PRIVATE);
        nombre = preferences1.getString("Nombre","");
        if (nombre == ""){
            nombre = extras.getString("Nombre");
            tvNombre.setText(nombre);
            SharedPreferences preferences = getSharedPreferences("preferencesLogin2", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Nombre", nombre);
            editor.commit();
        }else{
            tvNombre.setText(nombre);
        }
        */
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
                //SharedPreferences preferences = getSharedPreferences("preferencesLogin2", Context.MODE_PRIVATE);
                //preferences.edit().clear().commit();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



    private void setUpToolBar(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void obtenerServidor() {
        listaServidor.clear();
        progressDialog.setMessage("Cargando datos");
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.URL_OBTENER_tb_detalles),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("tb_detalles"); //Ventas
                            datos = jsonArray;
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
                                                jsonObject1.getString("peso")
                                        )
                                );
                            }

                            adapterServidor = new AdapterServer(MainActivity.this, listaServidor);
                            rvServidor.setAdapter(adapterServidor);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                progressDialog.dismiss();
            }
        });

        requestQueue.add(stringRequest);
    }

    public void floatingButtonStart() {
        floatingButton.show();
        floatingButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_white_18dp);
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
                    .setStartDelay(200)
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
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rvServidor.smoothScrollToPosition(0);
                flag = 0;
            }
        });
    }

    public void floatingbuttonFinal() {
                floatingButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_white_18dp);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    final Interpolator interpolador = AnimationUtils.loadInterpolator(getBaseContext(),
                            android.R.interpolator.fast_out_slow_in);

                    floatingButton.animate().scaleY(1)
                            .scaleX(1).rotation(1)
                            .setInterpolator(interpolador)
                            .setDuration(600)
                            .setStartDelay(200)
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
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rvServidor.smoothScrollToPosition(datos.length()-1);
                flag = 1;
                floatingButton.hide();
            }
        });

    }

}
