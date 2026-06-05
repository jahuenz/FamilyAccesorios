package com.distribuidora.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class RestClient extends AsyncTask<String, Void, String> {
    
	private final ListView lstViewProfesionales;
	List<Map<String,String>> listaProfesionales = new ArrayList<Map<String,String>>();
	private Context context;
	
	//contructor: recive un listView para luego llenar con los datos del json, y un contex necesario para el adapter
	public RestClient(ListView lstView, Context ctx){
		this.lstViewProfesionales = lstView;
		this.context = ctx;
	}
	
	
	@Override
    protected String doInBackground(String... urls) {

        return GET(urls[0]);
    }
    

    @Override
    protected void onPostExecute(String result) {
        initList(result);
        SimpleAdapter simpleAdapter = new SimpleAdapter(context, listaProfesionales, android.R.layout.simple_list_item_1, new String[] {"profesionales"}, new int[] {android.R.id.text1});
        lstViewProfesionales.setAdapter(simpleAdapter);
   }
    
    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
        	HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
 
            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
 
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
    
    private HashMap<String, String> itemProfesional (String nombre, String valor){
		HashMap<String, String> prof = new HashMap<String, String>();
		prof.put(nombre, valor);
		return prof;
	}
    
    //convierte una cadena en json y llena una lista con los datos de los profesionales (nombreApellido y profesion)
    private void initList(String resultado){

    	try{
    		JSONObject jsonResponse = new JSONObject(resultado);
    		JSONArray jsonMainNode = jsonResponse.optJSONArray("profesionales");

    		for(int i = 0; i<jsonMainNode.length(); i++){
    			JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
    			String nombreApellido = jsonChildNode.optString("nombreApellido");
    			String profesion = jsonChildNode.optString("profesion");
    			String outPut = nombreApellido + "-" +profesion;
    			listaProfesionales.add(itemProfesional("profesionales", outPut));
    		}
    	}
    	catch(JSONException e){
    		System.out.println("ERROR al convertir String a Json: "+e.toString());
    	}
    }
    
}
