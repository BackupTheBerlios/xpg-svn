/**
 * Disponible en http://www.kazak.ws
 * 
 * Desarrollado por Soluciones KAZAK Grupo de Investigacion y Desarrollo de
 * Software Libre Santiago de Cali/Republica de Colombia 2001
 * 
 * CLASS UpdateRecord v 0.1 Descripcion: Esta clase se encarga de manejar el
 * dialogo mediante el cual es posible actualizar uno o varios registros en una
 * tabla.
 * 
 * Preguntas, Comentarios y Sugerencias: xpg@kazak.ws
 * 
 * Fecha: 2001/10/01
 * 
 * Autores: Beatriz Flori�n - bettyflor@kazak.ws Gustavo Gonzalez -
 * xtingray@kazak.ws
 */
package ws.kazak.xpg.records;

import ws.kazak.xpg.idiom.*;
import ws.kazak.xpg.db.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

public class UpdateRecord extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	JTextField area;
    Table table;
    Hashtable hashText = new Hashtable();
    Hashtable dataText = new Hashtable();
    String[] fieldName;
    boolean[] active;
    int itemsA = 0;
    int fieldsTotal;
    String SQLupdate = "";
    String Where = "";
    String update = "";
    boolean wellDone = false;
    JFrame frame;
    String tableName;
    private Vector oldData;
    
    public UpdateRecord(String realTableName, Table table,Vector oldData,JFrame frame) {

        super(frame, true);

        tableName = realTableName;
        setTitle(Language.getWord("UPDT"));
        this.table = table;
        this.frame = frame;
        JPanel global = new JPanel();
        global.setLayout(new BoxLayout(global, BoxLayout.Y_AXIS));
        this.oldData=oldData;
        JPanel base = new JPanel();
        base.setLayout(new GridLayout(0, 1));
        JPanel data = new JPanel();
        data.setLayout(new GridLayout(0, 1));

        fieldsTotal = table.getTableHeader().getNumFields();
        active = new boolean[fieldsTotal];
        //Hashtable hashFields = myTable.getTableHeader().getHashtable();
        Vector fields = table.getTableHeader().getNameFields();
        //Vector data = myTable.getTableHeader().getTableFieldRecord();
        fieldName = new String[fieldsTotal];
        int i = 0;
        for (; i < fieldsTotal; i++) {

            active[i] = false;
            String nfield = (String) fields.elementAt(i);
            String typeField = table.getTableHeader().getType(nfield);

            JCheckBox check = new JCheckBox(nfield + " [" + typeField + "] = ");
            String label = "check-" + i;
            check.setActionCommand(label);
            check.addActionListener(this);
            fieldName[i] = nfield;
            base.add(check);

            if (typeField.equals("bool")) {

                String boolArray[] = { "true", "false" };
                JComboBox booleanCombo = new JComboBox(boolArray);
                booleanCombo.setEnabled(false);
                hashText.put(label, booleanCombo);
                data.add(booleanCombo);
            } else {
                if (typeField.equals("text")) {

                    JButton text = new JButton(Language.getWord("ADDTXT"));
                    text.setEnabled(false);
                    text.setActionCommand("button-" + i);
                    text.addActionListener(this);

                    hashText.put(label, text);
                    data.add(text);
                } else {
                    area = new JTextField(10);
                    try {
                        area.setText(oldData.get(i).toString());
                    }
                    catch(NullPointerException NPEe) {}
                    area.setEditable(false);
                    area.setEnabled(false);

                    hashText.put(label, area);
                    data.add(area);
                }
            }
        }

        JLabel title = new JLabel("UPDATE " + tableName, JLabel.CENTER);
        JPanel first = new JPanel();
        first.setLayout(new FlowLayout(FlowLayout.CENTER));
        first.add(title);

        Border etched1 = BorderFactory.createEtchedBorder();
        TitledBorder title1 = BorderFactory.createTitledBorder(etched1);
        first.setBorder(title1);

        JPanel center = new JPanel();
        center.setLayout(new BorderLayout());
        center.add(base, BorderLayout.WEST);
        center.add(data, BorderLayout.CENTER);

        JPanel up = new JPanel();
        up.setLayout(new FlowLayout(FlowLayout.CENTER));
        up.add(center);

        title1 = BorderFactory.createTitledBorder(etched1, "SET");
        up.setBorder(title1);

        JButton ok = new JButton(Language.getWord("UPDT"));
        ok.setActionCommand("OK");
        ok.addActionListener(this);
        JButton clear = new JButton(Language.getWord("CLR"));
        clear.setActionCommand("CLEAR");
        clear.addActionListener(this);
        JButton cancel = new JButton(Language.getWord("CANCEL"));
        cancel.setActionCommand("CANCEL");
        cancel.addActionListener(this);

        JPanel botons = new JPanel();
        botons.setLayout(new FlowLayout(FlowLayout.CENTER));
        botons.add(ok);
        botons.add(clear);
        botons.add(cancel);

        global.add(first);

        if (fieldsTotal > 15) {
            JScrollPane scroll = new JScrollPane(up);
            scroll.setPreferredSize(new Dimension(400, 400));
            global.add(scroll);
        } else
            global.add(up);

        global.add(botons);

        getContentPane().add(global);
        pack();

        this.addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {

            }
        });
        setLocationRelativeTo(frame);
        setVisible(true);

    }

    public void actionPerformed(java.awt.event.ActionEvent e) {

        if (e.getActionCommand().equals("OK")) {

            update = "UPDATE " + tableName + " SET ";
            String values = "";
            int t = 0;

            if (itemsA == 0) {
                JOptionPane.showMessageDialog(UpdateRecord.this, Language
                        .getWord("NFSU"), Language.getWord("ERROR!"),
                        JOptionPane.ERROR_MESSAGE);
            } else {

                for (int i = 0; i < fieldsTotal; i++) {

                    if (active[i]) {

                        t++;
                        String label = "check-" + i;
                        String data = "";

                        JTextField tmp = new JTextField();
                        int typeComponent = -1;

                        Object obj = (Object) hashText.get(label);

                        if (obj instanceof JTextField) {

                            tmp = (JTextField) obj;
                            data = tmp.getText();
                            typeComponent = 0;
                        } else {
                            if (obj instanceof JComboBox) {
                                JComboBox bool = (JComboBox) obj;
                                data = (String) bool.getSelectedItem();
                                typeComponent = 1;
                            } else {
                                if (obj instanceof JButton) {
                                    data = (String) dataText.get("button-" + i);
                                    typeComponent = 2;
                                    if (data == null)
                                        data = "";
                                }
                            }
                        }

                        data = data.trim();

                        if (data.length() != 0 || typeComponent == 2) {

                            String type = table.getTableHeader().getType(
                                    fieldName[i]);

                            if (!validNumberFormat(type, data)) {

                                JOptionPane.showMessageDialog(
                                        UpdateRecord.this, Language
                                                .getWord("TNE1")
                                                + fieldName[i]
                                                + Language.getWord("TNE2"),
                                        Language.getWord("ERROR!"),
                                        JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            if ((type.startsWith("varchar")
                                    || type.startsWith("char")
                                    || type.startsWith("name")
                                    || type.startsWith("time")
                                    || type.startsWith("text") || type
                                    .startsWith("date"))
                                    && !data.startsWith("'"))

                                data = "'" + data + "'";

                            values += "\"" + fieldName[i] + "\" = " + data;
                            values += ", ";

                        } // fin if
                        else {

                            t = -1;
                            JOptionPane.showMessageDialog(UpdateRecord.this,
                                    Language.getWord("EFIN") + fieldName[i]
                                            + "'.", Language.getWord("ERROR!"),
                                    JOptionPane.ERROR_MESSAGE);
                            if (typeComponent == 0)
                                tmp.requestFocus();

                            break;
                        } // fin else

                    } // fin for

                } // fin else

                if (t > 0) {

                    boolean ignore = false;
                    values = values.substring(0, values.length() - 2);
                    update += values;

                    wellDone = true;
                    setVisible(false);
                }

            }

            System.out.println("Realizando un Update: " + update);  
            
            return;
        }

        if (e.getActionCommand().equals("CANCEL")) {
            setVisible(false);
        }

        if (e.getActionCommand().equals("CLEAR")) {

            for (Enumeration t = hashText.elements(); t.hasMoreElements();) {
                Object obj = (Object) t.nextElement();

                if (obj instanceof JTextField) {
                    JTextField tmp = (JTextField) obj;
                    tmp.setText("");
                } else {
                    if (obj instanceof JComboBox) {
                        JComboBox tmp = (JComboBox) obj;
                        tmp.setSelectedIndex(0);
                    } else {
                        dataText.clear();
                    }
                }
            }
        }

        if (e.getActionCommand().startsWith("check-")) {

            String cad = e.getActionCommand();
            int num = Integer.parseInt(cad.substring(cad.indexOf("-") + 1, cad
                    .length()));
            JCheckBox checktmp = (JCheckBox) e.getSource();

            int typeComponent = -1;
            JTextField tmp = new JTextField();
            JComboBox bool = new JComboBox();
            JButton button = new JButton();

            Object obj = (Object) hashText.get(cad);

            if (obj instanceof JTextField) {
                tmp = (JTextField) obj;
                typeComponent = 0;
            } else {
                if (obj instanceof JComboBox) {
                    bool = (JComboBox) obj;
                    typeComponent = 1;
                } else {
                    if (obj instanceof JButton) {
                        typeComponent = 2;
                        button = (JButton) obj;
                    }
                }
            }

            if (checktmp.isSelected()) {
                active[num] = true;
                itemsA++;

                switch (typeComponent) {

                    case 0:
                        tmp.setEnabled(true);
                        tmp.setEditable(true);
                        tmp.requestFocus();
                        break;

                    case 1:
                        bool.setEnabled(true);
                        bool.requestFocus();
                        break;

                    case 2:
                        button.setEnabled(true);
                }
            } else {
                switch (typeComponent) {

                    case 0:
                        tmp.setEditable(false);
                        tmp.setEnabled(false);
                        break;
                    case 1:
                        bool.setEnabled(false);
                        break;
                    case 2:
                        button.setEnabled(false);
                }

                active[num] = false;
                itemsA--;
            }
        }

        if (e.getActionCommand().startsWith("button-")) {

            String strEvent = e.getActionCommand();
            int num = Integer.parseInt(strEvent.substring(
                    strEvent.indexOf("-") + 1, strEvent.length()));

//            String preStr = (String) dataText.get(strEvent);

            System.out.println("Numero: " + num);            
            
            String preStr = (String)oldData.get(num - 1);
            if (preStr == null)
                preStr = "";

            TextDataInput textWindow = new TextDataInput(UpdateRecord.this,
                    fieldName[num], preStr);
            if (textWindow.isWellDone()) {
                String text = textWindow.getValue();
                dataText.put(strEvent, text);
            }

            return;
        }

    } // Fin del metodo

    public boolean getResult() {
        return wellDone;
    }

    public String getUpdate() {
        return update;
    }

    public boolean validNumberFormat(String TypeField, String value) {

        boolean valid = true;

        if (TypeField.equals("decimal") || TypeField.startsWith("float")
                || TypeField.startsWith("int") || TypeField.equals("numeric")
                || TypeField.equals("serial")) {

            for (int i = 0; i < value.length(); i++) {
                char a = value.charAt(i);
                if (!Character.isDigit(a) && a != '.') {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

} // Fin de la Clase
