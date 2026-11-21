package com.distribuidora.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FormatoUtils {

    private FormatoUtils() {}

    /** Formatea un importe para mostrar en pantalla sin notación científica (ej: evita "1.09E7"). */
    public static String formatoImporte(double numero) {
        return new BigDecimal(numero).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    /** Igual que formatoImporte pero sin decimales, para el monto total del comprobante. */
    public static String formatoImporteSinDecimales(double numero) {
        return new BigDecimal(numero).setScale(0, RoundingMode.HALF_UP).toPlainString();
    }

    /** Sin decimales y con punto como separador de miles (ej: 61404 -> "61.404"), para el comprobante de venta. */
    public static String formatoImporteConMiles(double numero) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        return new DecimalFormat("#,##0", simbolos).format(new BigDecimal(numero).setScale(0, RoundingMode.HALF_UP));
    }
}
