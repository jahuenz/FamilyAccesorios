/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.distribuidora.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Archivo {

    private String ruta;
    private File archivoFisico;

    public Archivo(String ruta) {
        this.ruta = ruta;
        this.archivoFisico = new File(ruta);
    }

    public String getRuta() {
        return ruta;
    }
    
    public String getRutaAbsoluta(){
        return archivoFisico.getAbsolutePath();
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public void crearArchivo() {
        if (!existeArchivo()) {
            try {
                archivoFisico.createNewFile();

            } catch (IOException ex) {
                Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            archivoFisico.delete();
            try {
                archivoFisico.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public boolean existeArchivo(){
        if (archivoFisico.exists()) {
            return true;
        }else{
            return false;
        }
    }
    
    public List<String> leerArchivo(){        
        List<String> lineas = new ArrayList<String>();
        String temp = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivoFisico));

            temp = br.readLine();
            while (temp != null) {
                //if (!temp.startsWith("#")) {
                    lineas.add(temp);
                //}
                    
                temp = br.readLine();
            }

            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lineas;
    }
    
    public List<String> leerArchivoUsuarios(String idUsuario){        
        List<String> lineas = new ArrayList<String>();
        String temp = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivoFisico));
            boolean primeraLinea = true;
            temp = br.readLine();
            while (temp != null) {
                String[] campos = new String[3];
                campos = temp.split(",");
                
                if(primeraLinea){
                    lineas.add(temp);
                    primeraLinea = false;
                }else{
                    if (idUsuario.equals(campos[0])) {
                        lineas.add(temp);
                    }
                }  
                temp = br.readLine();
            }

            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lineas;
    }
    
    public List<String> leerArchivoRutas(String idUsuario){        
        List<String> lineas = new ArrayList<String>();
        boolean primeraLinea = true;
        String temp = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(archivoFisico));

            temp = br.readLine();
            while (temp != null) {
                String[] campos = new String[6];
                campos = temp.split(",");
                
                if(primeraLinea){
                    lineas.add(temp);
                    primeraLinea = false;
                }else{
                    if (idUsuario.equals(campos[2])) {
                        lineas.add(temp);
                    }
                }                    
                temp = br.readLine();
            }

            br.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lineas;
    }
    
    public void escribirArchivo(String contenido, boolean append) {
        try {
            FileWriter fw = new FileWriter(archivoFisico, append);
            fw.append(contenido + "\n");
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void vaciarArchivo(){
        try {
            FileWriter fw = new FileWriter(archivoFisico, false);
            fw.append("");
            fw.close();
            
        } catch (IOException ex) {
            Logger.getLogger(Archivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
