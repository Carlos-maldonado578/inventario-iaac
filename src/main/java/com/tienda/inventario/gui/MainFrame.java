package com.tienda.inventario.gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Ventana principal del sistema de inventario de la tienda de electrónicos.
 * Implementa la interfaz gráfica con Swing y se comunica con la API REST.
 *
 * Aplica pilares de POO:
 * - Herencia: MainFrame extends JFrame
 * - Encapsulamiento: métodos privados para cada panel
 * - Abstracción: los métodos HTTP ocultan detalles de comunicación
 * - Polimorfismo: ActionListeners con comportamientos distintos por botón
 *
 * IMPORTANTE: Para ejecutar esta clase, el servidor Spring Boot debe estar
 * corriendo en http://localhost:8080 (ejecutar ElectronicaApplication primero).
 */
public class MainFrame extends JFrame {

    /** URL base de la API REST */
    private static final String BASE_URL = "http://localhost:8080/api";

    /**
     * Constructor: configura la ventana principal y agrega los 5 módulos
     * de gestión mediante un JTabbedPane.
     */
    public MainFrame() {
        super("Sistema de Inventario — Tienda Electrónica");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opcion = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "¿Desea salir del sistema de inventario?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (opcion == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📦 Productos",   crearPanelProductos());
        tabbedPane.addTab("👥 Clientes",    crearPanelClientes());
        tabbedPane.addTab("🏭 Proveedores", crearPanelProveedores());
        tabbedPane.addTab("💰 Ventas",      crearPanelVentas());
        tabbedPane.addTab("📊 Inventario",  crearPanelInventario());

        add(tabbedPane);
        setVisible(true);
    }

    // =========================================================
    // MÓDULO 1: PANEL PRODUCTOS
    // =========================================================

    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registrar Producto"));

        JTextField txtCodigo      = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtCantidad    = new JTextField();
        JTextField txtPrecio      = new JTextField();
        JTextField txtUbicacion   = new JTextField();

        formPanel.add(new JLabel("Código:"));        formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Descripción:"));   formPanel.add(txtDescripcion);
        formPanel.add(new JLabel("Cantidad:"));      formPanel.add(txtCantidad);
        formPanel.add(new JLabel("Precio ($):"));    formPanel.add(txtPrecio);
        formPanel.add(new JLabel("Ubicación:"));     formPanel.add(txtUbicacion);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar Producto");
        JButton btnConsultar = new JButton("Consultar Todos");
        JButton btnLimpiar   = new JButton("Limpiar");
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnConsultar);
        botonesPanel.add(btnLimpiar);
        formPanel.add(new JLabel());
        formPanel.add(botonesPanel);

        String[] columnas = {"ID", "Código", "Descripción", "Cantidad", "Precio ($)", "Ubicación"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Inventario de Productos"));

        btnRegistrar.addActionListener(e -> {
            if (txtCodigo.getText().trim().isEmpty() || txtDescripcion.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Los campos Código y Descripción son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String json = String.format(
                "{\"codigo\":\"%s\",\"descripcion\":\"%s\",\"cantidad\":%s," +
                "\"precio\":%s,\"ubicacion\":\"%s\"}",
                txtCodigo.getText().trim(),
                txtDescripcion.getText().trim(),
                txtCantidad.getText().trim().isEmpty() ? "0" : txtCantidad.getText().trim(),
                txtPrecio.getText().trim().isEmpty() ? "0.0" : txtPrecio.getText().trim(),
                txtUbicacion.getText().trim()
            );
            String respuesta = realizarPost(BASE_URL + "/productos", json);
            if (respuesta != null) {
                JOptionPane.showMessageDialog(this, "✅ Producto registrado correctamente.");
                limpiarCampos(txtCodigo, txtDescripcion, txtCantidad, txtPrecio, txtUbicacion);
            }
        });

        btnConsultar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            String respuesta = realizarGet(BASE_URL + "/productos");
            if (respuesta != null && !respuesta.equals("[]")) {
                String[] registros = respuesta.replace("[{", "").replace("}]", "").split("\\},\\{");
                for (String reg : registros) {
                    if (!reg.trim().isEmpty()) {
                        modeloTabla.addRow(new Object[]{
                            extraerValor(reg, "id"),
                            extraerValor(reg, "codigo"),
                            extraerValor(reg, "descripcion"),
                            extraerValor(reg, "cantidad"),
                            "$" + extraerValor(reg, "precio"),
                            extraerValor(reg, "ubicacion")
                        });
                    }
                }
            }
        });

        btnLimpiar.addActionListener(e ->
            limpiarCampos(txtCodigo, txtDescripcion, txtCantidad, txtPrecio, txtUbicacion));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // MÓDULO 2: PANEL CLIENTES
    // =========================================================

    private JPanel crearPanelClientes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registrar Cliente"));

        JTextField txtNombre    = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtTelefono  = new JTextField();

        formPanel.add(new JLabel("Nombre:"));    formPanel.add(txtNombre);
        formPanel.add(new JLabel("Dirección:")); formPanel.add(txtDireccion);
        formPanel.add(new JLabel("Teléfono:"));  formPanel.add(txtTelefono);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar Cliente");
        JButton btnConsultar = new JButton("Consultar Todos");
        JButton btnLimpiar   = new JButton("Limpiar");
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnConsultar);
        botonesPanel.add(btnLimpiar);
        formPanel.add(new JLabel());
        formPanel.add(botonesPanel);

        String[] columnas = {"ID", "Nombre", "Dirección", "Teléfono"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Listado de Clientes"));

        btnRegistrar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String json = String.format(
                "{\"nombre\":\"%s\",\"direccion\":\"%s\",\"telefono\":\"%s\"}",
                txtNombre.getText().trim(),
                txtDireccion.getText().trim(),
                txtTelefono.getText().trim()
            );
            String respuesta = realizarPost(BASE_URL + "/clientes", json);
            if (respuesta != null) {
                JOptionPane.showMessageDialog(this, "✅ Cliente registrado correctamente.");
                limpiarCampos(txtNombre, txtDireccion, txtTelefono);
            }
        });

        btnConsultar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            String respuesta = realizarGet(BASE_URL + "/clientes");
            if (respuesta != null && !respuesta.equals("[]")) {
                String[] registros = respuesta.replace("[{", "").replace("}]", "").split("\\},\\{");
                for (String reg : registros) {
                    if (!reg.trim().isEmpty()) {
                        modeloTabla.addRow(new Object[]{
                            extraerValor(reg, "id"),
                            extraerValor(reg, "nombre"),
                            extraerValor(reg, "direccion"),
                            extraerValor(reg, "telefono")
                        });
                    }
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarCampos(txtNombre, txtDireccion, txtTelefono));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // MÓDULO 3: PANEL PROVEEDORES
    // =========================================================

    private JPanel crearPanelProveedores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registrar Proveedor"));

        JTextField txtCodigo    = new JTextField();
        JTextField txtNombre    = new JTextField();
        JTextField txtDireccion = new JTextField();
        JTextField txtCorreo    = new JTextField();
        JTextField txtTelefono  = new JTextField();

        formPanel.add(new JLabel("Código:"));    formPanel.add(txtCodigo);
        formPanel.add(new JLabel("Nombre:"));    formPanel.add(txtNombre);
        formPanel.add(new JLabel("Dirección:")); formPanel.add(txtDireccion);
        formPanel.add(new JLabel("Correo:"));    formPanel.add(txtCorreo);
        formPanel.add(new JLabel("Teléfono:"));  formPanel.add(txtTelefono);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar Proveedor");
        JButton btnConsultar = new JButton("Consultar Todos");
        JButton btnLimpiar   = new JButton("Limpiar");
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnConsultar);
        botonesPanel.add(btnLimpiar);
        formPanel.add(new JLabel());
        formPanel.add(botonesPanel);

        String[] columnas = {"ID", "Código", "Nombre", "Dirección", "Correo", "Teléfono"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Listado de Proveedores"));

        btnRegistrar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El campo Nombre es obligatorio.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String json = String.format(
                "{\"codigo\":\"%s\",\"nombre\":\"%s\",\"direccion\":\"%s\"," +
                "\"correo\":\"%s\",\"telefono\":\"%s\"}",
                txtCodigo.getText().trim(), txtNombre.getText().trim(),
                txtDireccion.getText().trim(), txtCorreo.getText().trim(),
                txtTelefono.getText().trim()
            );
            String respuesta = realizarPost(BASE_URL + "/proveedores", json);
            if (respuesta != null) {
                JOptionPane.showMessageDialog(this, "✅ Proveedor registrado correctamente.");
                limpiarCampos(txtCodigo, txtNombre, txtDireccion, txtCorreo, txtTelefono);
            }
        });

        btnConsultar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            String respuesta = realizarGet(BASE_URL + "/proveedores");
            if (respuesta != null && !respuesta.equals("[]")) {
                String[] registros = respuesta.replace("[{", "").replace("}]", "").split("\\},\\{");
                for (String reg : registros) {
                    if (!reg.trim().isEmpty()) {
                        modeloTabla.addRow(new Object[]{
                            extraerValor(reg, "id"),    extraerValor(reg, "codigo"),
                            extraerValor(reg, "nombre"), extraerValor(reg, "direccion"),
                            extraerValor(reg, "correo"), extraerValor(reg, "telefono")
                        });
                    }
                }
            }
        });

        btnLimpiar.addActionListener(e ->
            limpiarCampos(txtCodigo, txtNombre, txtDireccion, txtCorreo, txtTelefono));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // MÓDULO 4: PANEL VENTAS
    // =========================================================

    private JPanel crearPanelVentas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder("Registrar Venta"));

        JTextField txtCliente        = new JTextField();
        JTextField txtCodigoProducto = new JTextField();
        JTextField txtCantidad       = new JTextField();
        JTextField txtPrecioUnitario = new JTextField();
        JLabel     lblDescuentoInfo  = new JLabel("Sin descuento aplicado");
        JLabel     lblTotalFinal     = new JLabel("$0.00");

        lblDescuentoInfo.setForeground(Color.BLUE);
        lblTotalFinal.setFont(lblTotalFinal.getFont().deriveFont(Font.BOLD, 14f));

        String[] tiposCompra = {"DIRECTO", "EN_LINEA"};
        JComboBox<String> cbTipoCompra = new JComboBox<>(tiposCompra);

        formPanel.add(new JLabel("Nombre Cliente:"));      formPanel.add(txtCliente);
        formPanel.add(new JLabel("Código Producto:"));     formPanel.add(txtCodigoProducto);
        formPanel.add(new JLabel("Cantidad:"));            formPanel.add(txtCantidad);
        formPanel.add(new JLabel("Precio Unitario ($):")); formPanel.add(txtPrecioUnitario);
        formPanel.add(new JLabel("Tipo de Compra:"));      formPanel.add(cbTipoCompra);
        formPanel.add(new JLabel("Descuento:"));           formPanel.add(lblDescuentoInfo);
        formPanel.add(new JLabel("Total a Pagar:"));       formPanel.add(lblTotalFinal);

        ActionListener calcularPreview = e -> {
            try {
                int    cantidad = Integer.parseInt(txtCantidad.getText().trim());
                double precio   = Double.parseDouble(txtPrecioUnitario.getText().trim());
                String tipo     = (String) cbTipoCompra.getSelectedItem();
                double subtotal = cantidad * precio;
                double pctDesc  = 0.0;

                if ("EN_LINEA".equals(tipo) && cantidad > 3) {
                    pctDesc = 0.15;
                    lblDescuentoInfo.setText("✅ 15% descuento EN LÍNEA (-$" +
                        String.format("%.0f", subtotal * pctDesc) + ")");
                    lblDescuentoInfo.setForeground(new Color(0, 128, 0));
                } else if ("EN_LINEA".equals(tipo)) {
                    lblDescuentoInfo.setText("⚠ En línea pero cantidad ≤ 3: sin descuento");
                    lblDescuentoInfo.setForeground(Color.ORANGE);
                } else {
                    lblDescuentoInfo.setText("Compra directa en tienda: sin descuento");
                    lblDescuentoInfo.setForeground(Color.BLUE);
                }
                lblTotalFinal.setText("$" + String.format("%.0f", subtotal * (1 - pctDesc)));
            } catch (NumberFormatException ex) {
                lblTotalFinal.setText("$0.00");
                lblDescuentoInfo.setText("Ingrese cantidad y precio para calcular");
            }
        };

        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { calcularPreview.actionPerformed(null); }
            public void removeUpdate(DocumentEvent e)  { calcularPreview.actionPerformed(null); }
            public void changedUpdate(DocumentEvent e) { calcularPreview.actionPerformed(null); }
        };
        txtCantidad.getDocument().addDocumentListener(docListener);
        txtPrecioUnitario.getDocument().addDocumentListener(docListener);
        cbTipoCompra.addActionListener(calcularPreview);

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar Venta");
        JButton btnConsultar = new JButton("Ver Historial");
        JButton btnLimpiar   = new JButton("Limpiar");
        botonesPanel.add(btnRegistrar);
        botonesPanel.add(btnConsultar);
        botonesPanel.add(btnLimpiar);
        formPanel.add(new JLabel());
        formPanel.add(botonesPanel);

        String[] columnas = {"ID", "Cliente", "Tipo Compra", "Producto", "Cantidad",
                             "Descuento %", "Total ($)"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Historial de Ventas"));

        btnRegistrar.addActionListener(e -> {
            if (txtCliente.getText().trim().isEmpty() ||
                txtCodigoProducto.getText().trim().isEmpty() ||
                txtCantidad.getText().trim().isEmpty() ||
                txtPrecioUnitario.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Todos los campos son obligatorios para registrar la venta.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String tipo = (String) cbTipoCompra.getSelectedItem();
            String json = String.format(
                "{\"nombreCliente\":\"%s\",\"tipoCompra\":\"%s\"," +
                "\"codigoProducto\":\"%s\",\"cantidadProductos\":%s," +
                "\"precioUnitario\":%s}",
                txtCliente.getText().trim(),
                tipo,
                txtCodigoProducto.getText().trim(),
                txtCantidad.getText().trim(),
                txtPrecioUnitario.getText().trim()
            );
            String respuesta = realizarPost(BASE_URL + "/ventas", json);
            if (respuesta != null) {
                JOptionPane.showMessageDialog(this,
                    "✅ Venta registrada exitosamente.\nTotal cobrado: " + lblTotalFinal.getText());
                limpiarCampos(txtCliente, txtCodigoProducto, txtCantidad, txtPrecioUnitario);
                lblDescuentoInfo.setText("Sin descuento aplicado");
                lblTotalFinal.setText("$0.00");
            }
        });

        btnConsultar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            String respuesta = realizarGet(BASE_URL + "/ventas");
            if (respuesta != null && !respuesta.equals("[]")) {
                String[] registros = respuesta.replace("[{", "").replace("}]", "").split("\\},\\{");
                for (String reg : registros) {
                    if (!reg.trim().isEmpty()) {
                        modeloTabla.addRow(new Object[]{
                            extraerValor(reg, "id"),
                            extraerValor(reg, "nombreCliente"),
                            extraerValor(reg, "tipoCompra"),
                            extraerValor(reg, "codigoProducto"),
                            extraerValor(reg, "cantidadProductos"),
                            extraerValor(reg, "descuentoPorcentaje") + "%",
                            "$" + extraerValor(reg, "total")
                        });
                    }
                }
            }
        });

        btnLimpiar.addActionListener(e -> {
            limpiarCampos(txtCliente, txtCodigoProducto, txtCantidad, txtPrecioUnitario);
            lblDescuentoInfo.setText("Sin descuento aplicado");
            lblTotalFinal.setText("$0.00");
        });

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // MÓDULO 5: PANEL INVENTARIO (REPORTE)
    // =========================================================

    private JPanel crearPanelInventario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Control de Inventario"));

        JButton btnActualizar  = new JButton("Actualizar Reporte");
        JLabel  lblTotalUnidad = new JLabel("Haga clic en 'Actualizar Reporte' para ver el estado actual del inventario.");
        lblTotalUnidad.setForeground(Color.DARK_GRAY);

        controlPanel.add(btnActualizar);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(lblTotalUnidad);

        String[] columnas = {"ID", "Código", "Descripción", "Stock Actual", "Precio ($)", "Ubicación"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Estado Actual del Inventario"));

        btnActualizar.addActionListener(e -> {
            modeloTabla.setRowCount(0);
            String respuesta = realizarGet(BASE_URL + "/productos");
            if (respuesta != null && !respuesta.equals("[]")) {
                String[] registros = respuesta.replace("[{", "").replace("}]", "").split("\\},\\{");
                int totalUnidades = 0;
                for (String reg : registros) {
                    if (!reg.trim().isEmpty()) {
                        String cantidad = extraerValor(reg, "cantidad");
                        modeloTabla.addRow(new Object[]{
                            extraerValor(reg, "id"),
                            extraerValor(reg, "codigo"),
                            extraerValor(reg, "descripcion"),
                            cantidad,
                            "$" + extraerValor(reg, "precio"),
                            extraerValor(reg, "ubicacion")
                        });
                        try {
                            totalUnidades += Integer.parseInt(cantidad);
                        } catch (NumberFormatException ex) { /* ignorar */ }
                    }
                }
                lblTotalUnidad.setText("📦 Total de unidades en inventario: " + totalUnidades +
                    "  |  Productos distintos: " + modeloTabla.getRowCount());
                lblTotalUnidad.setForeground(new Color(0, 100, 0));
            } else {
                lblTotalUnidad.setText("⚠ No hay productos registrados en el inventario.");
                lblTotalUnidad.setForeground(Color.RED);
            }
        });

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================
    // MÉTODOS AUXILIARES DE COMUNICACIÓN HTTP
    // =========================================================

    /**
     * Realiza una solicitud GET a la URL indicada y retorna la respuesta en String.
     * Aplica abstracción: oculta los detalles del protocolo HTTP al resto de la GUI.
     */
    private String realizarGet(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) sb.append(linea);
                reader.close();
                return sb.toString();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error de conexión con la API REST.\n\n" +
                "Asegúrese de que el servidor Spring Boot esté ejecutándose:\n" +
                "  → Comando: ./mvnw spring-boot:run\n" +
                "  → Puerto: http://localhost:8080\n\n" +
                "Detalle técnico: " + e.getMessage(),
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Realiza una solicitud POST enviando datos en formato JSON.
     * Utilizado para crear nuevos registros en todos los módulos.
     */
    private String realizarPost(String urlStr, String jsonBody) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes("UTF-8"));
            }

            int code = conn.getResponseCode();
            if (code == 200 || code == 201) {
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = reader.readLine()) != null) sb.append(linea);
                reader.close();
                return sb.toString();
            } else {
                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(errorStream, "UTF-8"));
                    String errorMsg = errorReader.readLine();
                    errorReader.close();
                    JOptionPane.showMessageDialog(this,
                        "Error del servidor (HTTP " + code + "):\n" + errorMsg,
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al enviar datos a la API:\n" + e.getMessage(),
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Extrae el valor de un campo especifico de un fragmento JSON simple.
     * Soporta valores de texto (con comillas) y numéricos.
     */
    private String extraerValor(String json, String campo) {
        String clave = "\"" + campo + "\":";
        int inicio = json.indexOf(clave);
        if (inicio == -1) return "";
        inicio += clave.length();
        if (inicio >= json.length()) return "";

        char primerChar = json.charAt(inicio);
        if (primerChar == '"') {
            int fin = json.indexOf('"', inicio + 1);
            return fin == -1 ? "" : json.substring(inicio + 1, fin);
        } else {
            int finComa  = json.indexOf(',', inicio);
            int finLlave = json.indexOf('}', inicio);
            int fin = (finComa == -1) ? finLlave
                     : (finLlave == -1) ? finComa
                     : Math.min(finComa, finLlave);
            return fin == -1 ? json.substring(inicio).trim()
                             : json.substring(inicio, fin).trim();
        }
    }

    /** Limpia el contenido de uno o varios JTextField. */
    private void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }

    // =========================================================
    // MÉTODO MAIN
    // =========================================================

    /**
     * Punto de entrada de la interfaz grafica de escritorio.
     *
     * PASOS PARA EJECUTAR:
     * 1. Primero ejecutar el servidor: ./mvnw spring-boot:run
     * 2. Luego ejecutar esta clase como aplicacion Java normal
     *    (click derecho en MainFrame.java → Run Java)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
