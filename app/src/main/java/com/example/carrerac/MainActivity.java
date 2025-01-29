package com.example.carrerac;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNumeroCorredores, editTextDistancia;
    private TextView textViewResultado;
    private Button buttonIniciarCarrera;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextNumeroCorredores = findViewById(R.id.editTextNumeroCorredores);
        editTextDistancia = findViewById(R.id.editTextDistancia);
        textViewResultado = findViewById(R.id.textViewResultado);
        buttonIniciarCarrera = findViewById(R.id.buttonIniciarCarrera);

        // Inicializar el RequestQueue de Volley
        requestQueue = Volley.newRequestQueue(this);

        buttonIniciarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarCarrera();
            }
        });
    }

    private void iniciarCarrera() {
        // Obtener los valores de los EditText
        String numeroCorredores = editTextNumeroCorredores.getText().toString().trim();
        String distancia = editTextDistancia.getText().toString().trim();

        // Validar los campos
        if (numeroCorredores.isEmpty() || distancia.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese número de corredores y distancia", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Crear el JSON con los datos
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("numeroCorredores", Integer.parseInt(numeroCorredores));
            jsonBody.put("distancia", Integer.parseInt(distancia));

            // URL de la API de Lambda
            String url = "https://btz1p3kdcf.execute-api.us-east-2.amazonaws.com/Cliente";

            // Crear la solicitud POST con Volley
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Parseamos el campo 'body' que es un string JSON
                                JSONObject body = new JSONObject(response.getString("body"));

                                // Extraer del cuerpo el mensaje y los corredores
                                String mensaje = body.getString("mensaje");
                                textViewResultado.setText(mensaje);

                                // Procesamos los corredores
                                if (body.has("corredores")) {
                                    JSONArray corredores = body.getJSONArray("corredores");
                                    StringBuilder resultados = new StringBuilder();
                                    //Apariencia del texto
                                    for (int i = 0; i < corredores.length(); i++) {
                                        JSONObject corredor = corredores.getJSONObject(i);
                                        String nombre = corredor.getString("nombre");
                                        int velocidad = corredor.getInt("velocidad");
                                        int posicion = corredor.getInt("posicion");
                                        int distancia = corredor.getInt("distancia");

                                        resultados.append("Corredor: ").append(nombre).append("\n")
                                                .append("Velocidad: ").append(velocidad).append(" km/h\n")
                                                .append("Posición: ").append(posicion).append("\n")
                                                .append("Distancia: ").append(distancia).append(" metros\n\n");
                                    }

                                    textViewResultado.append("\n\n" + resultados.toString());
                                }
                            } catch (Exception e) {
                                textViewResultado.setText("Error al procesar la respuesta");
                            }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Manejar errores de la solicitud
                    Toast.makeText(MainActivity.this, "Error al iniciar la carrera", Toast.LENGTH_SHORT).show();
                }
            });

            // Agregar la solicitud a la cola de peticiones
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(this, "Error en la solicitud", Toast.LENGTH_SHORT).show();
        }
    }
}
