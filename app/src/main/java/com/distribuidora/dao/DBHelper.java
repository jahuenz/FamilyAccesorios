package com.distribuidora.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

//Defino el ayudante
public class DBHelper extends SQLiteOpenHelper {
	
    public static final String DB_NAME = "distribuidora.db";
    public static final int DB_VERSION = 14;
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.distribuidora/databases/";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    
    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
    }
	
    // Called only once, first time the DB is created. Por defecto.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UsuarioDAO.CREATE);
        db.execSQL(ClienteDAO.CREATE);
        db.execSQL(CabeceraPedidoDAO.CREATE);
        db.execSQL(ConfiguracionDAO.CREATE);
        db.execSQL(DescuentoDAO.CREATE);
        db.execSQL(DetallePedidoDAO.CREATE);
        db.execSQL(MovimientoDAO.CREATE);
        db.execSQL(ProductoDAO.CREATE);
        db.execSQL(RubroDAO.CREATE);
        db.execSQL(RutaDAO.CREATE);
        db.execSQL(StockDAO.CREATE);
        db.execSQL(TipoPedidoDAO.CREATE);
        db.execSQL(CondicionVentaDAO.CREATE);
        db.execSQL(DetallePedidoTemporalDAO.CREATE);
        db.execSQL(CobranzaDAO.CREATE);
    }
	
    // Called whenever newVersion != oldVersion
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Typically do ALTER TABLE statements, but... we're just in development, so:
        db.execSQL("DROP TABLE IF EXISTS " + UsuarioDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + ClienteDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + CabeceraPedidoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + ConfiguracionDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + DescuentoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + DetallePedidoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + MovimientoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + ProductoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + RubroDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + RutaDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + StockDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + TipoPedidoDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + CondicionVentaDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + DetallePedidoTemporalDAO.TABLA);
        db.execSQL("DROP TABLE IF EXISTS " + CobranzaDAO.TABLA);
        onCreate(db); // run onCreate to get the new database
    }
    
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase(); 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase(); 
        	try {
 
    			copyDataBase(); 
    		} catch (IOException e) { 
        		throw new Error("Error copying database"); 
        	}    	
    	} 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){ 
    	SQLiteDatabase checkDB = null; 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY); 
    	}catch(SQLiteException e){ 
    		//database does't exist yet. 
    	} 
    	if(checkDB != null){ 
    		checkDB.close(); 
    	} 
    	return checkDB != null ? true : false;
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }

}
