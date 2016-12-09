package com.escom.tt2016.lalontudmaps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText et_latitud,et_longitud;
    TextView tv_resultado;
    Button btn_enviar_datos;
    ObtenerWebService hiloconexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_latitud=(EditText) findViewById(R.id.et_latitud);
        et_longitud=(EditText) findViewById(R.id.et_longitud);
        tv_resultado=(TextView) findViewById(R.id.tv_resultado);
        btn_enviar_datos=(Button) findViewById(R.id.btn_enviar_datos);

        btn_enviar_datos.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_enviar_datos:
                hiloconexion=new ObtenerWebService();
                hiloconexion.execute(et_latitud.getText().toString(),et_longitud.getText().toString());// Parámetros que recibe doInBackground

                break;

            default:
                break;
        }

    }


    public class  ObtenerWebService extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... params) {

            //http://maps.googleapis.com/maps/api/geocode/json?latlng=38.404593,-0.529534&sensor=false
            String cadena="http://maps.googleapis.com/maps/api/geocode/json?latlng=";
            cadena= cadena+ params[0];
            cadena = cadena + ",";
            cadena= cadena +params[1];
            cadena=cadena+"&sensor=false";
            String devuelve = "";
            URL url = null; // Url de donde queremos obtener información

            try {
                url=new URL(cadena);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();//Abrir la conexion
                connection.setRequestProperty("User-Agent", "Mozilla/5.0" + " (Linux; Android 1.5; es-ES) Ejemplo HTTP");
                int respuesta = connection.getResponseCode();
                StringBuilder result =new StringBuilder();

                if (respuesta== 200){
                    InputStream in = new BufferedInputStream(connection.getInputStream());  // preparo la cadena de entrada
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));  // la introduzco en un BufferedReader
                    /*El siguiente proceso lo hago por que  el JSONOBject necesita un String  y tengo que transformar
                    * el BufferedReader  a String.Esto a travez de un StringBuilder*/

                    String line ;
                    while((line=reader.readLine()) != null){
                        result.append(line);//Paso toda la entrada al StringBuilder
                    }
                    //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                    JSONObject respuestaJSON = new JSONObject(result.toString());   //Creo un JSONObject a partir del StringBuilder
                    //Accedemos al vector de resultados
                    JSONArray resultJSON = respuestaJSON.getJSONArray("results");   // results es el nombre del campo en el JSON/Vamos obteniendo todos los campos que nos interesen.
                    //En este caso obtenemos la primera dirección de los resultados.
                    String direccion="SIN DATOS PARA ESA LONGITUD Y LATITUD";
                    if (resultJSON.length()>0){
                        direccion = resultJSON.getJSONObject(0).getString("formatted_address");    // dentro del results pasamos a Objeto la seccion formated_address
                    }
                    devuelve = "Dirección: " + direccion;   // variable de salida que mandaré al onPostExecute para que actualice la UI




                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return devuelve;

        }

        @Override
        protected void onCancelled(String aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            tv_resultado.setText(aVoid);

        }

        @Override
        protected void onPreExecute() {
            tv_resultado.setText("");
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
        }
    }

}
