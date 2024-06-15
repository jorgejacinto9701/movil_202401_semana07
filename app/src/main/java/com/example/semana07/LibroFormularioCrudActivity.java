package com.example.semana07;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

import com.example.semana07.util.FunctionUtil;

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

    RadioButton rbtActivo, rbtInactivo;
    TextView txtEstado;


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

        rbtActivo = findViewById(R.id.rbtActivo);
        rbtInactivo = findViewById(R.id.rbtInactivo);
        txtEstado = findViewById(R.id.txtEstado);

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
            rbtActivo.setVisibility(View.GONE);
            rbtInactivo.setVisibility(View.GONE);
            txtEstado.setVisibility(View.GONE);
        }else if (metodo.equals("ACTUALIZAR")){
            idTitlePage.setText("Actualiza Libro");
            btnEnviar.setText("Actualizar");

            objActual = (Libro) getIntent().getExtras().get("var_objeto");
            txtTitulo.setText(objActual.getTitulo());
            txtAnio.setText(String.valueOf(objActual.getAnio()));
            txtSerie.setText(objActual.getSerie());

            if (objActual.getEstado() == 1){
                rbtActivo.setChecked(true);
            }else{
                rbtInactivo.setChecked(true);
            }
        }



        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(LibroFormularioCrudActivity.this, MainActivity.class);
                    startActivity(intent);
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = txtTitulo.getText().toString();
                String anio = txtAnio.getText().toString();
                String serie= txtSerie.getText().toString();
                String idPais = spnPais.getSelectedItem().toString().split(":")[0];
                String idCategoria = spnCategoria.getSelectedItem().toString().split(":")[0];

                Pais objPais = new Pais();
                objPais.setIdPais(Integer.parseInt(idPais.trim()));

                Categoria objCategoria = new Categoria();
                objCategoria.setIdCategoria(Integer.parseInt(idCategoria.trim()));

                Libro objLibro = new Libro();
                objLibro.setTitulo(titulo);
                objLibro.setAnio(Integer.parseInt(anio));
                objLibro.setSerie(serie);
                objLibro.setPais(objPais);
                objLibro.setCategoria(objCategoria);
                objLibro.setFechaRegistro(FunctionUtil.getFechaActualStringDateTime());
                if (metodo.equals("REGISTRAR")){
                    objLibro.setEstado(1);
                    registra(objLibro);
                }else{
                    objLibro.setEstado(rbtActivo.isChecked() ? 1 : 0);
                    objLibro.setIdLibro(objActual.getIdLibro());
                    actualiza(objLibro);
                }

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
                    if (metodo.equals("ACTUALIZAR")){
                        String id = String.valueOf(objActual.getPais().getIdPais());
                        String nombre = String.valueOf(objActual.getPais().getNombre());
                        String row = id + " : " + nombre;
                        for(int i = 0; i <= paises.size(); i++ ){
                            if (paises.get(i).equals(row)){
                                spnPais.setSelection(i);
                                break;
                            }
                        }
                    }
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
                if (metodo.equals("ACTUALIZAR")){
                     String id = String.valueOf(objActual.getCategoria().getIdCategoria());
                     String nombre = String.valueOf(objActual.getCategoria().getDescripcion());
                     String row = id + " : " + nombre;
                     for(int i = 0; i <= categorias.size(); i++ ){
                         if (categorias.get(i).equals(row)){
                             spnCategoria.setSelection(i);
                             break;
                         }
                     }
                }
            }
            @Override
            public void onFailure(Call<List<Categoria>> call, Throwable t) {

            }
        });
    }

    void registra(Libro obj){
        Call<Libro> call = serviceLibro.registraLibro(obj);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (response.isSuccessful()){
                    Libro objSalida = response.body();
                    mensajeAlert(" Registro de Libro exitoso:  "
                            + " \n >>>> ID >> " + objSalida.getIdLibro()
                            + " \n >>> Título >>> " +  objSalida.getTitulo());
                }
            }
            @Override
            public void onFailure(Call<Libro> call, Throwable t) {

            }
        });
    }

    void actualiza(Libro obj){
        Call<Libro> call = serviceLibro.actualizaLibro(obj);
        call.enqueue(new Callback<Libro>() {
            @Override
            public void onResponse(Call<Libro> call, Response<Libro> response) {
                if (response.isSuccessful()){
                    Libro objSalida = response.body();
                    mensajeAlert(" Actualización de de Libro exitosa:  "
                            + " \n >>>> ID >> " + objSalida.getIdLibro()
                            + " \n >>> Título >>> " +  objSalida.getTitulo());
                }
            }
            @Override
            public void onFailure(Call<Libro> call, Throwable t) {

            }
        });
    }


    void mensajeToast(String mensaje){
        Toast toast1 =  Toast.makeText(getApplicationContext(),mensaje, Toast.LENGTH_LONG);
        toast1.show();
    }
    public void mensajeAlert(String msg){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(true);
        alertDialog.show();
    }

}