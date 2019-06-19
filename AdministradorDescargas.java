/*
    Author: Ian Pedraza

    Más contenido en:
    https://www.ianpedraza.com/
*/

package com.ianpedraza.descargas.Objetos;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;
import com.ianpedraza.descargas.Broadcasts.DonwloadCompleteReceiver;

public class AdministradorDescargas {

    private static final String TAG = "MyDownloadManager.class";
    private String url;
    private static final int CODIGO_SOLICITUD = 6545;
    private String nombre_archivo;
    private Context contexto;
    private Activity actividad;
    private String folder;

    public AdministradorDescargas(Context contexto, Activity actividad) {
        this.contexto = contexto;
        this.actividad = actividad;
        this.folder = Environment.DIRECTORY_DOWNLOADS;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre_archivo() {
        return nombre_archivo;
    }

    public void setNombre_archivo(String nombre_archivo) {
        this.nombre_archivo = nombre_archivo;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public void descargar(String nombre_archivo, String url){
        this.nombre_archivo = nombre_archivo;
        this.url = url;
        download();
    }


    private void download() {
        if (isDownloadManagerAvailable()) {
            checkSelfPermission();
        } else {
            Toast.makeText(contexto, "Download manager is not available", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isDownloadManagerAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    private void checkSelfPermission() {

        if (ContextCompat.checkSelfPermission(contexto,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(actividad,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODIGO_SOLICITUD);

        } else {
            executeDownload();
        }
    }

    private void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CODIGO_SOLICITUD: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    executeDownload();
                } else {
                    Toast.makeText(contexto, "Please give permissions ", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void executeDownload() {

        contexto.registerReceiver(new DonwloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Descargando archvio " + nombre_archivo);
        request.setTitle("Descargando");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        request.setDestinationInExternalPublicDir( folder, nombre_archivo);

        DownloadManager manager = (DownloadManager) contexto.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
