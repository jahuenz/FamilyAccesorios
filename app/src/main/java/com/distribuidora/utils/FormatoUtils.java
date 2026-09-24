package com.distribuidora.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormatoUtils {

    private FormatoUtils() {}

    /** Formatea un importe para mostrar en pantalla sin notación científica (ej: evita "1.09E7"). */
    public static String formatoImporte(double numero) {
        return new BigDecimal(numero).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
