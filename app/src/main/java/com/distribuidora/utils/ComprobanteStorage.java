package com.distribuidora.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

/**
 * Guarda los comprobantes (tickets de cobro/venta/devolución) en la carpeta pública
 * "Descargas/comprobantes" que el explorador de archivos y la app de Descargas del
 * teléfono muestran por default.
 *
 * Desde Android 10 (API 29) el almacenamiento con alcance ("scoped storage") impide
 * escribir directamente en esa carpeta pública con un File — hay que pasar por
 * MediaStore. En versiones anteriores se sigue usando la carpeta privada de la app,
 * ya que escribir en el directorio público ahí requiere un permiso en tiempo de
 * ejecución que la app nunca implementó (y ese escenario es cada vez menos relevante).
 */
public class ComprobanteStorage {

    private static final String SUBCARPETA = "comprobantes";

    private ComprobanteStorage() {}

    /** Guarda el bitmap como JPEG y devuelve un Uri listo para abrir con ACTION_VIEW. */
    public static Uri guardarComprobante(Context context, Bitmap bitmap, String nombreArchivo) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return guardarEnDescargasPublicas(context, bitmap, nombreArchivo);
        }
        return guardarEnCarpetaPrivada(context, bitmap, nombreArchivo);
    }

    private static Uri guardarEnDescargasPublicas(Context context, Bitmap bitmap, String nombreArchivo) throws IOException {
        ContentResolver resolver = context.getContentResolver();

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + SUBCARPETA);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IOException("No se pudo crear el comprobante en Descargas");
        }

        try (OutputStream os = resolver.openOutputStream(uri)) {
            if (os == null) {
                throw new IOException("No se pudo abrir el comprobante para escribir");
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
        } catch (IOException e) {
            resolver.delete(uri, null, null);
            throw e;
        }

        values.clear();
        values.put(MediaStore.MediaColumns.IS_PENDING, 0);
        resolver.update(uri, values, null, null);

        return uri;
    }

    private static Uri guardarEnCarpetaPrivada(Context context, Bitmap bitmap, String nombreArchivo) throws IOException {
        String directorio = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + SUBCARPETA + "/";
        File folder = new File(directorio);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta de comprobantes: " + directorio);
        }

        File file = new File(directorio + nombreArchivo);
        file.createNewFile();
        try (FileOutputStream fOut = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        }

        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/jpeg"}, null);

        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }
}
