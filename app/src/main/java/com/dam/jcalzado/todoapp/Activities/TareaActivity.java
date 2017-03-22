package com.dam.jcalzado.todoapp.Activities;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.dam.jcalzado.todoapp.Persistencia.TareaSQLiteHelper;
import com.dam.jcalzado.todoapp.R;

import java.util.Calendar;
import java.util.Date;

public class TareaActivity extends AppCompatActivity {

    private int tareaID;
    private int idActualización;
    // Atributos: Base de datos.
    private TareaSQLiteHelper tareasHelper;
    private SQLiteDatabase db;
    // Atributos: UI.
    private EditText etFecha, etHora, etTitulo;
    private RadioButton rbEstadoHecha, rbEstadoPendiente, rbPrioridadBaja, rbPrioridadMedia, rbPrioridadAlta;
    private Button btnCancelar, btnGuardar, btnReset;
    // Atributo: Constante 7 días.
    private static final int SIETE_DIAS = 604800000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea);

        // Creamos/Abrimos la base de datos.
        tareasHelper = new TareaSQLiteHelper(getApplicationContext());
        db = tareasHelper.getWritableDatabase();

        Bundle bundle = getIntent().getExtras();
        tareaID = bundle.getInt("tareaID");
        Toast.makeText(this, "ID: "+tareaID, Toast.LENGTH_SHORT).show();

        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etFecha = (EditText) findViewById(R.id.etFecha);
        etHora = (EditText) findViewById(R.id.etHora);
        rbEstadoHecha = (RadioButton) findViewById(R.id.rbEstadoHecha);
        rbEstadoPendiente = (RadioButton) findViewById(R.id.rbEstadoPendiente);
        rbPrioridadBaja = (RadioButton) findViewById(R.id.rbPrioridadBaja);
        rbPrioridadMedia = (RadioButton) findViewById(R.id.rbPrioridadMedia);
        rbPrioridadAlta = (RadioButton) findViewById(R.id.rbPrioridadAlta);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnReset = (Button) findViewById(R.id.btnReset);


        if (tareaID < 0) { // Se hace una llamada para crear una tarea nueva.
            fijarFechaPorDefecto();
        } else if (tareaID >= 0) { // Se hace una llamada para actualizar una tarea.
            if(db != null) {
                Cursor cursor = db.rawQuery("SELECT * FROM Tareas WHERE id="+tareaID, null);
                if(cursor.moveToFirst()) {
                    do {
                        idActualización = cursor.getInt(0);

                        etTitulo.setText(cursor.getString(1));
                        etTitulo.setInputType(InputType.TYPE_NULL);

                        if (cursor.getString(2).equals("Baja")) {
                            rbPrioridadBaja.setChecked(true);
                        } else if (cursor.getString(2).equals("Media")) {
                            rbPrioridadMedia.setChecked(true);
                        }else {
                            rbPrioridadAlta.setChecked(true);
                        }

                        if (cursor.getString(3).equals("Hecha")) {
                            rbEstadoHecha.setChecked(true);
                        } else {
                            rbEstadoPendiente.setChecked(true);
                        }

                        etFecha.setText(cursor.getString(4).substring(0,10));
                        etHora.setText(cursor.getString(4).substring(11,16));
                    } while (cursor.moveToNext());
                }
            }
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tareaID < 0) {
                    crearTarea();
                } else if (tareaID >= 0) {
                    actualizarTarea(idActualización);
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        btnReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etTitulo.setText("");
                fijarFechaPorDefecto();
                rbEstadoPendiente.toggle();
                rbPrioridadMedia.toggle();
            }
        });


    }

    private void crearTarea() {
        if(db != null) {
            // Creamos el registro para insertar como un objeto ContentValues.
            ContentValues nuevaTarea = new ContentValues();
            // Introducimos los datos como Clave/Valor.
            if(etTitulo.length() != 0) {
                nuevaTarea.put("titulo", etTitulo.getText().toString());
            } else {
                Toast.makeText(this, "Introduzca un nombre para la Tarea.", Toast.LENGTH_SHORT).show();
            }
            if(rbPrioridadBaja.isChecked()) {
                nuevaTarea.put("prioridad", "Baja");
            } else if (rbPrioridadMedia.isChecked()) {
                nuevaTarea.put("prioridad", "Media");
            } else if (rbPrioridadAlta.isChecked()) {
                nuevaTarea.put("prioridad", "Alta");
            } else {
                Toast.makeText(this, "Seleccione una prioridad para la Tarea.", Toast.LENGTH_SHORT).show();
            }
            if(rbEstadoPendiente.isChecked()) {
                nuevaTarea.put("estado", "Pendiente");
            } else if (rbEstadoHecha.isChecked()) {
                nuevaTarea.put("estado", "Hecha");
            } else {
                Toast.makeText(this, "Seleccione un estado para la Tarea.", Toast.LENGTH_SHORT).show();
            }
            if(etFecha.length()==10 && etHora.length()==5) {
                nuevaTarea.put("deadline", etFecha.getText().toString() + "-" + etHora.getText().toString());
            } else {
                Toast.makeText(this, "Inserte una fecha y hora correctas para la Tarea.", Toast.LENGTH_SHORT).show();
            }

            // Insertamos el registro en la BD.
            db.insert("Tareas", null, nuevaTarea);
            Toast.makeText(this, "Tarea insertada", Toast.LENGTH_SHORT).show();
            db.close();
            finish();
        } else {
            Toast.makeText(this, "Base de Datos no abierta", Toast.LENGTH_SHORT).show();
            db.close();
        }
    }

    private void actualizarTarea(int id) {
        ContentValues valoresActualizar = new ContentValues();

        //valoresActualizar.put("titulo", etTitulo.getText().toString());

        if(rbPrioridadBaja.isChecked()) {
            valoresActualizar.put("prioridad", "Baja");
        } else if (rbPrioridadMedia.isChecked()) {
            valoresActualizar.put("prioridad", "Media");
        } else if (rbPrioridadAlta.isChecked()) {
            valoresActualizar.put("prioridad", "Alta");
        }

        if(rbEstadoPendiente.isChecked()) {
            valoresActualizar.put("estado", "Pendiente");
        } else if (rbEstadoHecha.isChecked()) {
            valoresActualizar.put("estado", "Hecha");
        }

        if(etFecha.length()==10 && etHora.length()==5) {
            valoresActualizar.put("deadline", etFecha.getText().toString() + "-" + etHora.getText().toString());
        } else {
            Toast.makeText(this, "Inserte una fecha y hora correctas para la Tarea.", Toast.LENGTH_SHORT).show();
        }

        db.update("Tareas", valoresActualizar, "id="+id, null);
        Toast.makeText(this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
        db.close();
        finish();
    }

    public void fijarFechaPorDefecto() {
        Date momento = new Date();
        momento = new Date(momento.getTime() + SIETE_DIAS);
        Calendar cal = Calendar.getInstance();
        cal.setTime(momento);

        etFecha.setText(crearStringFecha(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)));
        etHora.setText(crearStringHora(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
    }

    private static String crearStringFecha(int año, int mes, int dia) {
        mes++;
        String sMes = String.valueOf(mes);
        String sDia = String.valueOf(dia);
        if(mes < 10) { sMes = "0" + mes; }
        if (dia < 10) { sDia = "0" + dia; }
        return sDia + "/" + sMes + "/" + año;
    }

    public static String crearStringHora(int horas, int minutos) {
        String sHoras = String.valueOf(horas);
        String sMinutos = String.valueOf(minutos);
        if(horas < 10) { sHoras = "0" + horas; }
        if(minutos < 10) { sMinutos = "0" + minutos; }
        return sHoras + ":" + sMinutos;
    }
}