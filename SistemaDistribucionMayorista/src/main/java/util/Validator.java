package util;

import java.util.regex.Pattern;

public final class Validator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private Validator() {}

    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean esEmailValido(String email) {
        return email != null && EMAIL.matcher(email.trim()).matches();
    }

    // El CUIT tiene 11 dígitos (se acepta con o sin guiones)
    public static boolean esCuitValido(String cuit) {
        return cuit != null && cuit.replaceAll("\\D", "").length() == 11;
    }
}