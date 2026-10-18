package view.estilos;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CampoMoneda extends JTextField {

    private boolean programatico = false;   // true cuando el texto lo pone el código, no el usuario

    public CampoMoneda() {
        setHorizontalAlignment(SwingConstants.RIGHT);
        ((AbstractDocument) getDocument()).setDocumentFilter(new FiltroMoneda());

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                BigDecimal v = getValor();
                ponerTexto(v == null ? "" : v.toPlainString().replace('.', ','));
                SwingUtilities.invokeLater(CampoMoneda.this::selectAll);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setValor(getValor());   // vuelve al formato con miles y decimales
            }
        });
    }

    // Devuelve el monto como BigDecimal (null si está vacío)
    public BigDecimal getValor() {
        String t = getText().replaceAll("[^0-9,]", "").replace(',', '.');
        if (t.isEmpty() || t.equals(".")) {
            return null;
        }
        try {
            return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void setValor(BigDecimal valor) {
        ponerTexto(valor == null ? "" : "$ " + formatear(valor));
    }

    // Versiones con double / Double (usados por los modelos Cliente, Producto, etc.)
    public BigDecimal getMonto() {
        return getValor();
    }

    public void setMonto(BigDecimal monto) {
        setValor(monto);
    }

    // Método de conveniencia para controladores: devuelve valor primitivo double (0.0 si está vacío)
    public BigDecimal getValorNumerico() {
        BigDecimal v = getValor();
        return v == null ? BigDecimal.ZERO : v;
    }

    // 1234567.5 -> "1.234.567,50"
    public static String formatear(BigDecimal valor) {
        if (valor == null) return "0,00";
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", simbolos).format(valor);
    }

    private void ponerTexto(String texto) {
        programatico = true;
        setText(texto);
        programatico = false;
    }

    // Solo deja escribir dígitos y una coma con hasta 2 decimales
    private class FiltroMoneda extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attrs)
                throws BadLocationException {
            replace(fb, offset, 0, texto, attrs);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String texto, AttributeSet attrs)
                throws BadLocationException {
            if (programatico || texto == null) {
                super.replace(fb, offset, length, texto, attrs);
                return;
            }
            String limpio = ".".equals(texto) ? "," : texto.replaceAll("[^0-9,]", "");
            String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
            String nuevo = actual.substring(0, offset) + limpio + actual.substring(offset + length);
            if (nuevo.matches("\\d{0,12}(,\\d{0,2})?")) {
                super.replace(fb, offset, length, limpio, attrs);
            }
        }
    }
}