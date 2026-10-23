package view.estilos;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class Estilos {

  private Estilos() {}

  public static final Color FONDO = new Color(232, 238, 245);
  public static final Color FONDO_LATERAL = new Color(208, 217, 229);
  public static final Color TARJETA = new Color(241, 245, 250);
  public static final Color AZUL = new Color(29, 78, 216);
  public static final Color TEXTO = new Color(30, 41, 59);
  public static final Color TEXTO_SUAVE = new Color(100, 116, 139);
  public static final Color BORDE = new Color(186, 198, 213);
  public static final Color DESHABILITADO = new Color(226, 232, 240);
  public static final String FUENTE = "Segoe UI";

  public static Font fuente(int estilo, int tamanio) {
    return new Font(FUENTE, estilo, tamanio);
  }

  // Panel tipo "tarjeta": fondo claro, borde fino y margen interno
  public static JPanel crearTarjeta(LayoutManager layout) {
    JPanel p = new JPanel(layout);
    p.setBackground(TARJETA);
    p.setBorder(new CompoundBorder(new LineBorder(BORDE), new EmptyBorder(12, 14, 12, 14)));
    return p;
  }

  public static JButton botonPrimario(String texto) {
    BotonRedondeado b = new BotonRedondeado(texto, AZUL, AZUL.darker());
    b.setForeground(Color.WHITE);
    b.setFont(fuente(Font.BOLD, 11));
    b.setBorder(new EmptyBorder(7, 14, 7, 14));
    return b;
  }

  public static JButton botonSecundario(String texto) {
    BotonRedondeado b = new BotonRedondeado(texto, new Color(248, 250, 252), new Color(226, 232, 240));
    b.setBorderColor(BORDE);
    b.setForeground(TEXTO);
    b.setFont(fuente(Font.BOLD, 11));
    b.setBorder(new EmptyBorder(7, 14, 7, 14));
    return b;
  }

  public static void estilizarCampo(JTextField campo) {
    campo.setFont(fuente(Font.PLAIN, 12));
    campo.setForeground(TEXTO);
    campo.setDisabledTextColor(TEXTO_SUAVE);
    campo.setBackground(Color.WHITE);
    campo.setBorder(new CompoundBorder(new LineBorder(BORDE), new EmptyBorder(5, 8, 5, 8)));
  }

  // Pinta el borde del campo en rojo (error) o vuelve al borde normal
  public static void marcarError(JTextField campo, boolean error) {
    Color color = error ? new Color(185, 28, 28) : BORDE;
    campo.setBorder(new CompoundBorder(new LineBorder(color), new EmptyBorder(5, 8, 5, 8)));
  }

  // Habilita o bloquea un componente y lo pinta en gris cuando está bloqueado
  public static void habilitar(JComponent c, boolean habilitado) {
    c.setEnabled(habilitado);
    if (c instanceof JTextField || c instanceof JComboBox) {
      c.setBackground(habilitado ? Color.WHITE : DESHABILITADO);
    }
  }

  // Fila "etiqueta + campo" para formularios con GridBagLayout
  public static void agregarCampo(JPanel panel, int fila, String etiqueta, JComponent campo) {
    if (campo instanceof JTextField) {
      estilizarCampo((JTextField) campo);
    }
    JLabel lbl = new JLabel(etiqueta);
    lbl.setFont(fuente(Font.PLAIN, 12));
    lbl.setForeground(TEXTO);

    GridBagConstraints c = new GridBagConstraints();
    c.gridy = fila;
    c.insets = new Insets(5, 0, 5, 10);
    c.anchor = GridBagConstraints.WEST;
    c.gridx = 0;
    c.weightx = 0;
    panel.add(lbl, c);

    c.gridx = 1;
    c.weightx = 1;
    c.insets = new Insets(5, 0, 5, 0);
    c.fill = GridBagConstraints.HORIZONTAL;
    panel.add(campo, c);
  }

  // Aplica el estilo a la tabla y la devuelve dentro de un JScrollPane
  public static JScrollPane estilizarTabla(JTable tabla) {
    tabla.setFont(fuente(Font.PLAIN, 11));
    tabla.setRowHeight(28);
    tabla.setShowVerticalLines(false);
    tabla.setGridColor(new Color(220, 227, 236));
    tabla.setIntercellSpacing(new Dimension(0, 1));

    JTableHeader header = tabla.getTableHeader();
    header.setFont(fuente(Font.BOLD, 11));
    header.setBackground(new Color(203, 213, 225));
    header.setForeground(TEXTO);
    header.setReorderingAllowed(false);

    JScrollPane scroll = new JScrollPane(tabla);
    scroll.setBorder(new LineBorder(BORDE));
    scroll.getViewport().setBackground(Color.WHITE);
    return scroll;
  }

  // Botón con esquinas redondeadas dibujado a mano (no depende del look and feel)
  public static class BotonRedondeado extends JButton {
    private final Color colorFondo;   // null = transparente
    private final Color colorHover;
    private Color colorBorde = null;
    private boolean encima = false;

    public BotonRedondeado(String texto, Color fondo, Color hover) {
      super(texto);
      this.colorFondo = fondo;
      this.colorHover = hover;
      setContentAreaFilled(false);
      setFocusPainted(false);
      setOpaque(false);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      addMouseListener(new MouseAdapter() {
        @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
        @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
      });
    }

    public void setBorderColor(Color c) {
      this.colorBorde = c;
    }

    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      Color relleno = !isEnabled() ? DESHABILITADO : (encima ? colorHover : colorFondo);
      if (relleno != null) {
        g2.setColor(relleno);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
      }
      if (colorBorde != null) {
        g2.setColor(colorBorde);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
      }
      g2.dispose();
      super.paintComponent(g);   // dibuja el texto encima
    }
  }
}