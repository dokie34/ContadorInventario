/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contadorinventario;

import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowFilter.Entry;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import au.com.bytecode.opencsv.*;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.EOF;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.TableColumn;

/**
 *
 * @author maestro
 */


public class Contador extends javax.swing.JFrame {
    
    //public Scanner entrada = new Scanner(System.in);
    Hashtable tabla = new Hashtable();
    
    String codigo,cont;
    int valor;

    /**
     * Creates new form Contador
     */
    public Contador() {
        initComponents();
        
        BotonImportarExcel.setText("<html>Importar <br> de Excel</html>");
        BotonExportarExcel.setText("<html>Exportar <br> a Excel</html>");
        
        ////////////////////// Tamaño de la tabla
        TableColumn cantCol = jTable1.getColumn("Cantidad");
        cantCol.setMaxWidth(85);
        cantCol.setWidth(85);
        cantCol.setPreferredWidth(75);
        TableColumn codCol = jTable1.getColumn("Código");
        codCol.setMaxWidth(185);
        codCol.setWidth(185);
        codCol.setPreferredWidth(175);
        jTable1.setMaximumSize(jTable1.getSize());
        
        

        
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
 
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                close();
            }
        });
    }
    
 
    private void close(){
        if (JOptionPane.showConfirmDialog(rootPane, "¿Desea realmente salir del sistema?",
                "Salir del sistema", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            System.exit(0);
    }         
    
    private void imprimir(){
            String auxLlaves,auxValores;
            int i=0;
            
            Enumeration<String> llaves =tabla.keys();
            Enumeration valores = tabla.elements();
            
            DefaultTableModel modelo= new DefaultTableModel();
            modelo = (DefaultTableModel) jTable1.getModel();
            
            while(modelo.getRowCount()>0)modelo.removeRow(0);
            
            System.out.println("\n");
            while (llaves.hasMoreElements()) {
                    auxLlaves = llaves.nextElement();
                    String auxExistencia = ((Articulo)tabla.get(auxLlaves)).existencia;
                    String auxDescripcion = ((Articulo)tabla.get(auxLlaves)).descripcion;
                    
                    System.out.println("Llave: "+ auxLlaves + "  Valor: " + auxExistencia);

                    modelo.addRow(new Object[]{"",""});
                    
                    modelo.setValueAt(auxLlaves, i, 0);
                    modelo.setValueAt(auxDescripcion, i, 1);
                    modelo.setValueAt(auxExistencia, i, 2);

                    i++;
            } 
    }
    
    private void AgregarArticulo(){
            String codigo,cont = "0";
            int valor;
            
            codigo = InsertarCodigo.getText();
            
            if (InsertarCodigo.getText().isEmpty()!=true) {
                if (tabla.get(codigo)!=null){
                    Articulo prod = (Articulo)tabla.get(codigo);
                    valor=Integer.parseInt(prod.existencia);
                    valor++;
                    cont=String.valueOf(valor);
                    prod.existencia=cont;
                    tabla.put(codigo, prod);
                    EtiquetaMostrar.setText("");
                    InsertarCodigo.setText("");
                    EtiquetaMostrar.setText("<html>ARTÍCULO AGREGADO <br><br>Código:"+codigo+" <br> Cantidad:"+cont+"</html>");
                } else {
                    EtiquetaMostrar.setText("");
                    InsertarCodigo.setText("");
                    EtiquetaMostrar.setText("<html>ERROR <br>Código no registrado</html>");
                }
                
            imprimir();
            
            }else{
                EtiquetaMostrar.setText("<html>ERROR <br> Código no introducido</html>");
            }
    }
    
    private void EliminarArticulo(){
        String codigo,cont;
                int valor;
                
                codigo = InsertarCodigo.getText();
                
                if (InsertarCodigo.getText().isEmpty()!=true) {
                    if (tabla.get(codigo)!=null ){
                        Articulo prod = (Articulo)tabla.get(codigo);
                        valor=Integer.parseInt(prod.existencia);
                        if (valor>0){
                            valor--;
                            cont=String.valueOf(valor);
                            prod.existencia=cont;
                            tabla.put(codigo, prod);
                            EtiquetaMostrar.setText("");
                            InsertarCodigo.setText("");
                            EtiquetaMostrar.setText("<html>ARTÍCULO ELIMINADO <br><br>Código:"+codigo+" <br> Cantidad:"+cont+"</html>");
                        }else{
                            EtiquetaMostrar.setText("");
                            InsertarCodigo.setText("");
                            EtiquetaMostrar.setText("<html>Hay 0 existencias de <br>este artículo</html>");
                        }
                    } else {
                    EtiquetaMostrar.setText("");
                    InsertarCodigo.setText("");
                    EtiquetaMostrar.setText("El artículo solicitado no existe");
                    }

                    imprimir();
                
                }else{
                    EtiquetaMostrar.setText("");
                    EtiquetaMostrar.setText("<html>ERROR <br> Código no introducido</html>");
                }
    }
    
    private void ImportarExcel(){
        CSVReader reader;
        String auxLlaves,auxValores;
        String[] linea;
        Enumeration<String> llaves =tabla.keys();
        Enumeration valores = tabla.elements();

        JFileChooser file=new JFileChooser();
        file.showSaveDialog(this);
        File archivo =file.getSelectedFile();

        if (archivo!=null){
            try {

                reader = new CSVReader(new FileReader(archivo), '\t');
                linea=reader.readNext();
                while ((linea=reader.readNext())!=null) {
                    Articulo prod= new Articulo(linea[0], linea[1], linea[2], linea[3], linea[4], linea[5], linea[6], linea[7]);
                    tabla.put(linea[0],prod);
                }
                imprimir();

            } catch (IOException ex) {
                Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "No se pudo abrir tu archivo, inténtalo otra vez" , "Advertencia", WIDTH);
            }
        }
    }
    
    
    private void ExportarExcel(){
        CSVWriter writer;
        String auxLlaves;
        Enumeration<String> llaves =tabla.keys();
        Enumeration valores = tabla.elements();
        
        JFileChooser file=new JFileChooser();
        file.showSaveDialog(this);
        File archivo =file.getSelectedFile();
        
        if (archivo!=null){
            try {
                writer = new CSVWriter(new FileWriter(archivo+".xls"), '\t');
                String[] encabezados="Codigo#Descripcion#Precio Costo#Precio Venta#Precio Mayoreo#Existencia#Inv. Minimo#Departamento".split("#");
                writer.writeNext(encabezados);

                while (llaves.hasMoreElements()) {
                    auxLlaves = llaves.nextElement();
                    Articulo prod = (Articulo)tabla.get(auxLlaves);
                    String CadAux=auxLlaves+"#"+prod.descripcion+"#"+prod.costo+"#"+prod.venta+"#"+prod.mayoreo+"#"+prod.existencia+"#"+prod.minimo+"#"+prod.departamento;
                    String[] entries = CadAux.split("#");
                    writer.writeNext(entries);
                }
                writer.close(); 

                JOptionPane.showMessageDialog(rootPane, "Lista exportada", "Hecho" , WIDTH);

            } catch (IOException ex) {
                Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "No se pudo guardar tu archivo, inténtalo otra vez" , "Advertencia", WIDTH);
            }
        }
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        InsertarCodigo = new javax.swing.JTextField();
        EtiquetaMostrar = new javax.swing.JLabel();
        BotonAgregar = new javax.swing.JToggleButton();
        BotonEliminar = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        BotonImportarExcel = new javax.swing.JButton();
        BotonExportarExcel = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuAbrirLista = new javax.swing.JMenuItem();
        MenuaGuardarLista = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MenuImportarExcel = new javax.swing.JMenuItem();
        MenuExportarExcel = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Contador de artículos");
        setUndecorated(true);
        setResizable(false);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Artículos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("SansSerif", 1, 18), java.awt.Color.black)); // NOI18N
        jPanel1.setToolTipText("");
        jPanel1.setName(""); // NOI18N

        InsertarCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InsertarCodigoActionPerformed(evt);
            }
        });
        InsertarCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InsertarCodigoKeyPressed(evt);
            }
        });

        EtiquetaMostrar.setFont(new java.awt.Font("Dialog", 1, 20)); // NOI18N
        EtiquetaMostrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        EtiquetaMostrar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BotonAgregar.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        BotonAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Tick.png"))); // NOI18N
        BotonAgregar.setText("Agregar");
        BotonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAgregarActionPerformed(evt);
            }
        });

        BotonEliminar.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        BotonEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Cross.png"))); // NOI18N
        BotonEliminar.setText("Eliminar");
        BotonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(InsertarCodigo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(BotonAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BotonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(EtiquetaMostrar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(InsertarCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(EtiquetaMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonAgregar)
                    .addComponent(BotonEliminar))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lista de Artículos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 18), java.awt.Color.black)); // NOI18N

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Cantidad"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 584, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 448, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        BotonImportarExcel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        BotonImportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Excel2.png"))); // NOI18N
        BotonImportarExcel.setIconTextGap(20);
        BotonImportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonImportarExcelActionPerformed(evt);
            }
        });

        BotonExportarExcel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        BotonExportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Excel.png"))); // NOI18N
        BotonExportarExcel.setIconTextGap(20);
        BotonExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonExportarExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BotonImportarExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BotonExportarExcel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonImportarExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonExportarExcel, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33))
        );

        jMenu1.setText("Archivo");

        MenuAbrirLista.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        MenuAbrirLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Open.png"))); // NOI18N
        MenuAbrirLista.setText("Abrir lista (txt)");
        MenuAbrirLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuAbrirListaActionPerformed(evt);
            }
        });
        jMenu1.add(MenuAbrirLista);

        MenuaGuardarLista.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        MenuaGuardarLista.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Save.png"))); // NOI18N
        MenuaGuardarLista.setText("Guardar lista (txt)");
        MenuaGuardarLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuaGuardarListaActionPerformed(evt);
            }
        });
        jMenu1.add(MenuaGuardarLista);

        jMenuItem1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Minimize.png"))); // NOI18N
        jMenuItem1.setText("Minimizar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Close.png"))); // NOI18N
        jMenuItem4.setText("Salir");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Excel");

        MenuImportarExcel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        MenuImportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ImportExcel.png"))); // NOI18N
        MenuImportarExcel.setText("Importar de Excel");
        MenuImportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuImportarExcelActionPerformed(evt);
            }
        });
        jMenu2.add(MenuImportarExcel);

        MenuExportarExcel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        MenuExportarExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/ExportExcel.png"))); // NOI18N
        MenuExportarExcel.setText("Exportar a Excel");
        MenuExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MenuExportarExcelActionPerformed(evt);
            }
        });
        jMenu2.add(MenuExportarExcel);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InsertarCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InsertarCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_InsertarCodigoActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void InsertarCodigoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InsertarCodigoKeyPressed
        // TODO add your handling code here:
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){ 
            if (BotonAgregar.isSelected()) {
                if (tabla.isEmpty()!=true){
                    AgregarArticulo();
                }else{
                    JOptionPane.showMessageDialog(rootPane,"No hay una lista importada","", WIDTH);
                    InsertarCodigo.setText("");
                }
            }
            else if(BotonEliminar.isSelected()){
                if (tabla.isEmpty()!=true){
                    EliminarArticulo();
                }else{
                    JOptionPane.showMessageDialog(rootPane,"No hay una lista importada","", WIDTH);
                    InsertarCodigo.setText("");
                }
            }
            else{
                JOptionPane.showMessageDialog(rootPane, "¡¡¡Selecciona una opción!!!", "" , WIDTH);
                InsertarCodigo.setText("");
            }
            
        }
    }//GEN-LAST:event_InsertarCodigoKeyPressed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        close();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void MenuExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuExportarExcelActionPerformed
        // TODO add your handling code here:
        ExportarExcel(); 
    }//GEN-LAST:event_MenuExportarExcelActionPerformed

    private void MenuaGuardarListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuaGuardarListaActionPerformed
        // TODO add your handling code here:    
        CSVWriter writer;
        String auxLlaves,auxValores;
        Enumeration<String> llaves =tabla.keys();
        Enumeration valores = tabla.elements();
        
        JFileChooser file=new JFileChooser();
        file.showSaveDialog(this);
        File archivo =file.getSelectedFile();
        
        if (archivo!=null){
            try {
                writer = new CSVWriter(new FileWriter(archivo+".txt"), '\t');

                while (llaves.hasMoreElements()) {
                    auxLlaves = llaves.nextElement();
                    Articulo prod = (Articulo)tabla.get(auxLlaves);
                    String CadAux=auxLlaves+"#"+prod.descripcion+"#"+prod.costo+"#"+prod.venta+"#"+prod.mayoreo+"#"+prod.existencia+"#"+prod.minimo+"#"+prod.departamento;
                    String[] entries = CadAux.split("#");
                    writer.writeNext(entries);
                }
                writer.close(); 

                JOptionPane.showMessageDialog(rootPane, "Lista guardada", "Hecho" , WIDTH);

            } catch (IOException ex) {
                Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "No se pudo guardar tu archivo, inténtalo otra vez" , "Advertencia", WIDTH);
            }
        }
    }//GEN-LAST:event_MenuaGuardarListaActionPerformed

    private void MenuAbrirListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuAbrirListaActionPerformed
        // TODO add your handling code here:
        CSVReader reader;
        String auxLlaves,auxValores;
        String[] linea;
        Enumeration<String> llaves =tabla.keys();
        Enumeration valores = tabla.elements();

        JFileChooser file=new JFileChooser();
        file.showSaveDialog(this);
        File archivo =file.getSelectedFile();

        if (archivo!=null){
            try {

                reader = new CSVReader(new FileReader(archivo), '\t');
                while ((linea=reader.readNext())!=null) {
                    Articulo prod= new Articulo(linea[0], linea[1], linea[2], linea[3], linea[4], linea[5], linea[6], linea[7]);
                    tabla.put(linea[0],prod);

                }
                imprimir();

            } catch (IOException ex) {
                Logger.getLogger(Contador.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(rootPane, "No se pudo abrir tu archivo, inténtalo otra vez" , "Advertencia", WIDTH);
            }
        }
    }//GEN-LAST:event_MenuAbrirListaActionPerformed

    private void MenuImportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MenuImportarExcelActionPerformed
        // TODO add your handling code here:
        ImportarExcel();
    }//GEN-LAST:event_MenuImportarExcelActionPerformed

    private void BotonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAgregarActionPerformed
        // TODO add your handling code here:
        if (BotonAgregar.isSelected()){
            BotonEliminar.setSelected(false);
            BotonAgregar.setFont(new Font("Dialog", 1, 22));
            BotonEliminar.setFont(new Font("Dialog", 1, 16));
        }else{
            BotonAgregar.setFont(new Font("Dialog", 1, 16));
        }
    }//GEN-LAST:event_BotonAgregarActionPerformed

    private void BotonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarActionPerformed
        // TODO add your handling code here:
        if (BotonEliminar.isSelected()){
            BotonAgregar.setSelected(false);
            BotonEliminar.setFont(new Font("Dialog", 1, 22));
            BotonAgregar.setFont(new Font("Dialog", 1, 16));
        }else{
            BotonEliminar.setFont(new Font("Dialog", 1, 16));
        }
    }//GEN-LAST:event_BotonEliminarActionPerformed

    private void BotonImportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonImportarExcelActionPerformed
        // TODO add your handling code here:
        ImportarExcel();
    }//GEN-LAST:event_BotonImportarExcelActionPerformed

    private void BotonExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonExportarExcelActionPerformed
        // TODO add your handling code here:
        ExportarExcel();
    }//GEN-LAST:event_BotonExportarExcelActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        setExtendedState(JFrame.CROSSHAIR_CURSOR); 
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Contador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Contador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Contador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Contador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Contador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton BotonAgregar;
    private javax.swing.JToggleButton BotonEliminar;
    private javax.swing.JButton BotonExportarExcel;
    private javax.swing.JButton BotonImportarExcel;
    private javax.swing.JLabel EtiquetaMostrar;
    private javax.swing.JTextField InsertarCodigo;
    private javax.swing.JMenuItem MenuAbrirLista;
    private javax.swing.JMenuItem MenuExportarExcel;
    private javax.swing.JMenuItem MenuImportarExcel;
    private javax.swing.JMenuItem MenuaGuardarLista;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}

