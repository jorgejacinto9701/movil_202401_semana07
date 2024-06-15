package com.example.semana07;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.semana07.entity.Categoria;
import com.example.semana07.entity.Libro;
import com.example.semana07.entity.Pais;
import com.example.semana07.service.ServiceCategoria;
import com.example.semana07.service.ServiceLibro;
import com.example.semana07.service.ServicePais;
import com.example.semana07.util.ConnectionRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibroFormularioCrudActivity extends AppCompatActivity {

    //Pais
    Spinner spnPais;
    ArrayAdapter<String> adaptadorPais;
    ArrayList<String> paises = new ArrayList<>();

    //Categoria
    Spinner spnCategoria;
    ArrayAdapter<String> adaptadorCategoria;
    ArrayList<String> categorias = new ArrayList<>();

    //Servicio
    ServiceLibro serviceLibro;
    ServicePais servicePais;
    ServiceCategoria   serviceCategoria;

    EditText txtTitulo, txtAnio, txtSerie;

    Button btnEnviar, btnRegresar;

    TextView idTitlePage;

    String metodo;

    Libro objActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_libro_formulario_crud);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        servicePais = ConnectionRest.getConnection().create(ServicePais.class);
        serviceCategoria = ConnectionRest.getConnection().create(ServiceCategoria.class);
        serviceLibro = ConnectionRest.getConnection().create(ServiceLibro.class);

        adaptadorPais = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, paises);
        spnPais = findViewById(R.id.spnRegLibPais);
        spnPais.setAdapter(adaptadorPais);

        adaptadorCategoria = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categorias);
        spnCategoria = findViewById(R.id.spnRegLibCategoria);
        spnCategoria.setAdapter(adaptadorCategoria);

        txtTitulo = findViewById(R.id.txtRegLibTitulo);
        txtAnio = findViewById(R.id.txtRegLibAnio);
        txtSerie = findViewById(R.id.txtRegLibSerie);

        metodo = (String)getIntent().getExtras().get("var_metodo");

        idTitlePage = findViewById(R.id.idTitlePage);
        btnEnviar = findViewById(R.id.btnRegLibEnviar);
        btnRegresar = findViewById(R.id.btnRegLibRegresar);

        if (metodo.equals("REGISTRAR")){
            idTitlePage.setText("Registra Libro");
            btnEnviar.setText("Registrar");
        }else if (metodo.equals("ACTUALIZAR")){
            idTitlePage.setText("Actualiza Libro");
            btnEnviar.setText("Actualizar");

            objActual = (Libro) getIntent().getExtras().get("var_objeto");
            txtTitulo.setText(objActual.getTitulo());
            txtAnio.setText(String.valueOf(objActual.getAnio()));
            txtSerie.setText(objActual.getSerie());

        }



        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(LibroFormularioCrudActivity.this, MainActivity.class);
                    startActivity(intent);
            }
        });

        cargaPais();
        cargaCategoria();
    }

    void cargaPais(){
        Call<List<Pais>>  call = servicePais.listaTodos();
        call.enqueue(new Callback<List<Pais>>() {
            @Override
            public void onResponse(Call<List<Pais>> call, Response<List<Pais>> response) {
                    List<Pais> lstAux =  response.body();
                    paises.add(" [ Seleccione ] ");
                    for(Pais aux:lstAux){
                        paises.add(aux.getIdPais() + " : "  + aux.getNombre());
                    }
                    adaptadorPais.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<Pais>> call, Throwable t) {

            }
        });
    }
    void cargaCategoria(){
        Call<List<Categoria>>  call = serviceCategoria.listaTodos();
        call.enqueue(new Callback<List<Categoria>>() {
            @Override
            public void onResponse(Call<List<Categoria>> call, Response<List<Categoria>> response) {
                List<Categoria> lstAux =  response.body();
                categorias.add(" [ Seleccione ] ");
                for(Categoria aux:lstAux){
                    categorias.add(aux.getIdCategoria() + " : "  + aux.getDescripcion());
                }
                adaptadorCategoria.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {

            }
        });
    }



}