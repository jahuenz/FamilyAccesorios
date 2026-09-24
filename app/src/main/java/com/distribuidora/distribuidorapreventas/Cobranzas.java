package com.distribuidora.distribuidorapreventas;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.distribuidora.dao.ClienteDAO;
import com.distribuidora.dao.CobranzaDAO;
import com.distribuidora.dao.MovimientoDAO;
import com.distribuidora.dao.RutaDAO;
import com.distribuidora.dao.TransaccionDAO;
import com.distribuidora.model.Cliente;
import com.distribuidora.model.Cobranza;
import com.distribuidora.utils.ComprobanteStorage;
import com.distribuidora.utils.Preferencias;
import com.distribuidora.utils.VentanaDialogo;

import static com.distribuidora.utils.FormatoUtils.formatoImporte;

public class Cobranzas extends Activity {

    // id_valor: 1=Efectivo, 2=Cheque físico, 3=Transferencia Family,
    //           4=Transferencia Tercero, 5=eCheq Family, 6=eCheq Terceros
    private static final int EFECTIVO             = 1;
    private static final int CHEQUE_FISICO        = 2;
    private static final int TRANSFERENCIA_FAMILY = 3;
    private static final int TRANSFERENCIA_TERCERO = 4;
    private static final int ECHEQ_FAMILY         = 5;
    private static final int ECHEQ_TERCERO        = 6;

    private ClienteDAO clienteDAO;
    private CobranzaDAO cobranzaDAO;
    private MovimientoDAO movimientoDAO;
    private Cliente cliente;

    private TextView txtNombreCliente;
    private TextView txtSaldoAdeudado;
    private Spinner spnFormaPago;
    private Button btnEditar;
    private Button btnGuardar;
    private Button btnCancelar;

    private EditText edtImporte;
    private EditText edtNota;

    // Campos dinámicos
    private LinearLayout llNumero;
    private LinearLayout llFechaVencimiento;

    private EditText edtNumero;
    private EditText edtFechaVencimiento;

    private double importe;
    private int idCliente;
    private int idValorSeleccionado = EFECTIVO;
    private Long idCobranza;

    static final int PICK_CONTACT_REQUEST = 1;
    Bitmap src;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_cobranzas);

        Bundle bundle = getIntent().getExtras();
        idCliente = bundle.getInt("idCliente");

        clienteDAO = new ClienteDAO(getApplicationContext());
        cobranzaDAO = new CobranzaDAO(getApplicationContext());
        movimientoDAO = new MovimientoDAO(getApplicationContext());

        cliente = clienteDAO.obtenerCliente(idCliente);

        txtNombreCliente  = (TextView) findViewById(R.id.txtNombreCliente);
        txtSaldoAdeudado  = (TextView) findViewById(R.id.txtTotalAdeudadoValor);
        spnFormaPago      = (Spinner) findViewById(R.id.spnFormaPago);
        btnEditar         = (Button) findViewById(R.id.btnEditar);
        btnGuardar        = (Button) findViewById(R.id.btnGuardar);
        btnCancelar       = (Button) findViewById(R.id.btnCancelar);
        edtImporte        = (EditText) findViewById(R.id.edtImporte);
        edtNota           = (EditText) findViewById(R.id.edtNota);

        llNumero          = (LinearLayout) findViewById(R.id.llNumero);
        llFechaVencimiento = (LinearLayout) findViewById(R.id.llFechaVencimiento);

        edtNumero         = (EditText) findViewById(R.id.edtNumero);
        edtFechaVencimiento = (EditText) findViewById(R.id.edtFechaVencimiento);

        configurarDatePickerVencimiento();

        txtNombreCliente.setText(cliente.getRazonSocial());
        txtSaldoAdeudado.setText(formatoImporte(cliente.getSaldoCtaCte()));

        spnFormaPago.setEnabled(false);

        mostrarModalFormaPago();

        btnEditar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarModalFormaPago();
            }
        });

        btnCancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
                startActivity(i);
            }
        });

        btnGuardar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    if (importe > cliente.getSaldoCtaCte()) {
                        confirmarExcesoSaldo();
                    } else {
                        cobranzaRealizadaDialog();
                    }
                }
            }
        });
    }

    private void confirmarExcesoSaldo() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Atención");
        ad.setMessage("Está cargando un importe mayor al saldo adeudado. ¿Desea continuar?");
        ad.setCancelable(false);
        ad.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cobranzaRealizadaDialog();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    private void mostrarModalFormaPago() {
        final String[] opciones = {
            "1. EFECTIVO",
            "2. CHEQUE FISICO",
            "3. TRANSFERENCIA A FAMILY",
            "4. TRANSFERENCIA A TERCEROS",
            "5. E-CHEQ A FAMILY",
            "6. E-CHEQ A TERCEROS"
        };
        // Pre-seleccionado con el valor actual: Android no dispara el listener de
        // setSingleChoiceItems para el ítem ya tildado al abrir, así que arranca
        // asumiendo que el usuario mantiene esa opción si toca "Aceptar" directo.
        final int[] seleccion = {idValorSeleccionado - 1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione forma de pago");
        builder.setSingleChoiceItems(opciones, idValorSeleccionado - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                seleccion[0] = which;
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (seleccion[0] == -1) {
                    Toast.makeText(Cobranzas.this, "Debe seleccionar una opción", Toast.LENGTH_SHORT).show();
                    return;
                }
                idValorSeleccionado = seleccion[0] + 1;
                spnFormaPago.setSelection(seleccion[0]);
                actualizarCamposDinamicos(idValorSeleccionado);
                limpiarCamposDinamicos();
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void actualizarCamposDinamicos(int idValor) {
        // numero + fecha_vencimiento: solo Cheque físico y eCheq (Family o Terceros)
        boolean mostrarChequeEcheq = (idValor == CHEQUE_FISICO || idValor == ECHEQ_FAMILY || idValor == ECHEQ_TERCERO);
        llNumero.setVisibility(mostrarChequeEcheq ? View.VISIBLE : View.GONE);
        llFechaVencimiento.setVisibility(mostrarChequeEcheq ? View.VISIBLE : View.GONE);
    }

    private void limpiarCamposDinamicos() {
        edtNumero.setText("");
        edtFechaVencimiento.setText("");
    }

    private boolean validarCampos() {
        // Importe siempre obligatorio
        String importeStr = edtImporte.getText().toString();
        try {
            importe = Double.parseDouble(importeStr);
        } catch (Exception e) {
            importe = 0;
        }
        if (importe <= 0) {
            new VentanaDialogo(this, "Error", "El importe debe ser mayor a 0.", false).mostrar();
            return false;
        }

        if (idValorSeleccionado == CHEQUE_FISICO || idValorSeleccionado == ECHEQ_FAMILY || idValorSeleccionado == ECHEQ_TERCERO) {
            if (edtNumero.getText().toString().trim().length() != 4) {
                new VentanaDialogo(this, "Error", "Ingrese los últimos 4 números.", false).mostrar();
                return false;
            }
            if (edtFechaVencimiento.getText().toString().trim().isEmpty()) {
                new VentanaDialogo(this, "Error", "Ingrese la fecha de vencimiento.", false).mostrar();
                return false;
            }
        }

        return true;
    }

    protected void actualizarBaseDeDatos() {
        Preferencias preferencias = new Preferencias(getApplicationContext());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        TransaccionDAO transaccionDAO = new TransaccionDAO(getApplicationContext());

        try {
            transaccionDAO.iniciarTransaccion();
            ContentValues parametros = new ContentValues();

            String idcab = sdf.format(new Date()) + preferencias.getIdVendedor();
            parametros.put("id", idcab);
            parametros.put("fecha", sdf.format(new Date()));
            parametros.put("importe", importe);
            parametros.put("id_valor", idValorSeleccionado);
            parametros.put("item", 1);

            if (idValorSeleccionado == CHEQUE_FISICO || idValorSeleccionado == ECHEQ_FAMILY || idValorSeleccionado == ECHEQ_TERCERO) {
                parametros.put("fecha_emision", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                parametros.put("numero", edtNumero.getText().toString().trim());
                parametros.put("fecha_vencimiento", edtFechaVencimiento.getText().toString().trim());
            }

            parametros.put("nota", edtNota.getText().toString().trim());
            parametros.put("id_cliente", idCliente);
            parametros.put("id_usuario", preferencias.getIdVendedor());
            idCobranza = transaccionDAO.insertar("COBRANZA", parametros);

            parametros = new ContentValues();
            parametros.put("fecha", new Date().getTime());
            parametros.put("tipo", "COBRO");
            parametros.put("descripcion", "Identificador de cobranza: " + idCobranza);
            parametros.put("id_usuario", preferencias.getIdVendedor());
            parametros.put("id_cliente", idCliente);
            transaccionDAO.insertar("MOVIMIENTO", parametros);

            double nvoSaldo = cliente.getSaldoCtaCte() - importe;
            parametros = new ContentValues();
            parametros.put(ClienteDAO.SALDO_CTACTE, nvoSaldo);
            transaccionDAO.actualizar("CLIENTE", parametros, "id=" + idCliente);

            parametros = new ContentValues();
            parametros.put(RutaDAO.ATENDIDO, 1);
            transaccionDAO.actualizar(RutaDAO.TABLA, parametros, "id_cliente=" + idCliente);

            transaccionDAO.transaccionExitosa();

        } catch (Exception e) {
            new VentanaDialogo(Cobranzas.this, "Error", "Error al guardar el cobro", false).mostrar();
        } finally {
            transaccionDAO.cerrarTransaccion();
        }
    }

    private void generarTicket() {
        Cobranza cobro = cobranzaDAO.obtenerCobro(idCobranza);

        src = BitmapFactory.decodeResource(getResources(), R.drawable.comprobante35);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(src.getWidth(), src.getHeight(), conf);

        String numComprobante = "Num. Comprobante: " + cobro.getId();
        String clienteStr     = "Cliente: " + cliente.getRazonSocial();
        String montoVenta     = "Monto total: $" + formatoImporte(cobro.getImporte());
        String fecha          = "Fecha: " + cobro.getFecha("dd/MM/yyyy");
        String formaPagoStr   = "Forma de pago: " + spnFormaPago.getSelectedItem().toString();
        String divisor        = "-----------------------------------------------------------------------------------------------------------";
        String tipoMovimiento = "Tipo de operación: COBRO";

        Canvas cs = new Canvas(bmp);
        Paint tPaint = new Paint();
        tPaint.setTextSize(getSizeInPx(10.0f));
        tPaint.setColor(Color.BLACK);
        tPaint.setStyle(Paint.Style.FILL);
        cs.drawBitmap(src, 0f, 0f, null);
        float height = tPaint.measureText("1");
        float x_coord = 5f;

        cs.drawText("FAMILY ACCESORIOS",               getSizeInPx(110.0f), getSizeInPx(height + 5f), tPaint);
        cs.drawText("San Francisco, Córdoba",           getSizeInPx(105.0f), getSizeInPx(height + 15f), tPaint);
        cs.drawText("Tel.: 3564 15644150/15589544",     getSizeInPx(82.5f),  getSizeInPx(height + 25f), tPaint);
        cs.drawText(numComprobante, x_coord, getSizeInPx(height + 40f), tPaint);
        cs.drawText(clienteStr,     x_coord, getSizeInPx(height + 50f), tPaint);
        cs.drawText(montoVenta,     x_coord, getSizeInPx(height + 60f), tPaint);
        cs.drawText(fecha,          x_coord, getSizeInPx(height + 70f), tPaint);
        cs.drawText(formaPagoStr,   x_coord, getSizeInPx(height + 80f), tPaint);

        float y = 90f;
        if (!cobro.getNumero().isEmpty()) {
            cs.drawText("Número: " + cobro.getNumero(), x_coord, getSizeInPx(height + y), tPaint);
            y += 10f;
        }
        if (!cobro.getFecha_vencimiento().isEmpty()) {
            cs.drawText("Vencimiento: " + cobro.getFecha_vencimiento(), x_coord, getSizeInPx(height + y), tPaint);
            y += 10f;
        }
        if (!cobro.getNota().isEmpty()) {
            cs.drawText("Nota: " + cobro.getNota(), x_coord, getSizeInPx(height + y), tPaint);
            y += 10f;
        }

        cs.drawText(tipoMovimiento, x_coord, getSizeInPx(height + y), tPaint);
        y += 10f;
        cs.drawText(divisor,        x_coord, getSizeInPx(height + y + 5f), tPaint);
        y += 15f;

        cs.drawText("TOTAL", x_coord, getSizeInPx(height + y), tPaint);
        cs.drawText("$" + formatoImporte(cobro.getImporte()), getSizeInPx(275.0f), getSizeInPx(height + y), tPaint);

        String nombreArchivo = cliente.getRazonSocial() + "-" + cobro.getFecha("dd-MM-yyyy_HHmm") + ".jpg";
        Uri photoURI;
        try {
            photoURI = ComprobanteStorage.guardarComprobante(getApplicationContext(), bmp, nombreArchivo);
        } catch (IOException e) {
            Log.e("Comprobante", "Error al guardar el comprobante", e);
            FirebaseCrashlytics.getInstance().recordException(e);
            new VentanaDialogo(this, "Error", "No se pudo guardar el comprobante. Revise el almacenamiento del teléfono.", false).mostrar();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(photoURI, "image/jpeg");
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    private int getSizeInPx(float v) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (v * scale + 0.5f);
    }

    private void cobranzaRealizadaDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Cerrando cobranza");
        ad.setMessage("¿Confirma que desea guardar la cobranza?");
        ad.setCancelable(false);
        ad.setPositiveButton("Aceptar y generar ticket", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                actualizarBaseDeDatos();
                generarTicket();
            }
        });
        ad.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK || resultCode == 3200 || resultCode == RESULT_CANCELED) {
                Intent i = new Intent(getApplicationContext(), ListadoClientes.class);
                startActivity(i);
            }
        }
    }

    private void configurarDatePickerVencimiento() {
        edtFechaVencimiento.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(Cobranzas.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String fecha = String.format("%02d/%02d/%04d", day, month + 1, year);
                            edtFechaVencimiento.setText(fecha);
                        }
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });
    }

}
