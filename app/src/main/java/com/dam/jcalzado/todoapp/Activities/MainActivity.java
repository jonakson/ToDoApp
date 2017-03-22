package com.dam.jcalzado.todoapp.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.dam.jcalzado.todoapp.Adaptadores.AdaptadorLV;
import com.dam.jcalzado.todoapp.Modelos.Tarea;
import com.dam.jcalzado.todoapp.Persistencia.TareaSQLiteHelper;
import com.dam.jcalzado.todoapp.R;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    // Atributos: UI.
    private FloatingActionButton fabTarea;
    // Atributos: ListView.
    private ListView listViewTareas;
    private AdaptadorLV adaptador;
    // Atributos: Lista de Tareas.
    private List<Tarea> tareas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewTareas = (ListView) findViewById(R.id.listViewTareas);
        tareas = new ArrayList<>();

        // Creamos/Abrimos la base de datos.
        TareaSQLiteHelper tareasHelper = new TareaSQLiteHelper(getApplicationContext());
        db = tareasHelper.getWritableDatabase();

        // Llenamos el ListView haciendo uso de nuestro Adapter.
        adaptador = new AdaptadorLV(this, tareas);
        listViewTareas.setAdapter(adaptador);
        listViewTareas.setClickable(true);
        listViewTareas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lanzarActualizarTarea(view, position);
            }
        });

        actualizar();

        // Lanzamos la Activity para crear nuevas tareas con el Floating Action Button.
        fabTarea = (FloatingActionButton) findViewById(R.id.fabTarea);
        fabTarea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                lanzarCrearTarea();
            }
        });


    }

    private void actualizar() {
        // Borramos todos los elementos actuales de la lista.
        tareas.clear();
        // Cargamos todos los elementos actuales de la BD en la lista.
        tareas.addAll(obtenerTareas());
        // Refrescamos el Adaptador.
        adaptador.notifyDataSetChanged();

    }

    private List<Tarea> obtenerTareas() {
        // Obtenemos todos los registros de la tabla Tareas.
        Cursor cursor = db.rawQuery("SELECT * FROM Tareas", null);
        List<Tarea> lista = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String titulo = cursor.getString(cursor.getColumnIndex("titulo"));
                String prioridad = cursor.getString(cursor.getColumnIndex("prioridad"));
                String estado = cursor.getString(cursor.getColumnIndex("estado"));
                String deadline = cursor.getString(cursor.getColumnIndex("deadline"));

                lista.add(new Tarea(id,titulo,prioridad,estado,deadline));
                cursor.moveToNext();
            }
        }
        return lista;
    }

    private void lanzarCrearTarea() {
        Intent i = new Intent(this, TareaActivity.class);
        i.putExtra("tareaID", -1);
        startActivity(i);
    }
    private void lanzarActualizarTarea(View view, int position) {
        Intent i = new Intent(view.getContext(), TareaActivity.class);
        Tarea t = (Tarea)adaptador.getItem(position);
        i.putExtra("tareaID", t.getId());
        startActivity(i);

        //Intent i = new Intent(this, TareaActivity.class);
        //startActivity(i);
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        actualizar();
        super.onResume();
    }
}