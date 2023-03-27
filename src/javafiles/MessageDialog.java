package javafiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MessageDialog extends JDialog implements ActionListener {
    protected MultiLineLabel message;
    protected JPanel extendedPanel = new JPanel(), buttonsPanel = new JPanel();
    protected ActionListener actionListener = null;
    public static final String[] labeles = {"Yes", "No", "Ok", "Cancel"},
            commands = {"yes", "no", "ok", "cancel"};
    public static final int YES = 1, NO = 2, OK = 4, CANCEL = 8;

    public MessageDialog(Frame parent, MultiLineLabel message) {
        this(parent, "", message);
    }

    public MessageDialog(Frame parent, MultiLineLabel message, int actions) {
        this(parent, "", message, actions);
    }

    public MessageDialog(Frame parent, String title, MultiLineLabel message) {
        this(parent, title, message, OK);
    }

    public MessageDialog(Frame parent, String title, MultiLineLabel message, int actions) {
        super(parent, title, true);
        this.message = message;
        setLayout(new GridBagLayout());
        setBackground(SystemColor.text);
        extendedPanel.setLayout(new GridBagLayout());
        buttonsPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.gridwidth = GridBagConstraints.REMAINDER;
        add(this.message, c);
        add(extendedPanel, c);
        c.insets.bottom += 2;
        add(buttonsPanel, c);
        c.insets = new Insets(0, 2, 0, 2);
        c.gridwidth = GridBagConstraints.RELATIVE;
        Button button;
        for (int i = 0, action = 1; i < 4; i++, action <<= 1) {
            if ((actions & action) != 0) {
                button = new Button(labeles[i]);
                button.setActionCommand(commands[i]);
                button.addActionListener(this);
                buttonsPanel.add(button, c);
            }
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, commands[3]));
            }
        });
        setResizable(false);
    }

    public void addActionListener(ActionListener listener) {
        actionListener = AWTEventMulticaster.add(actionListener, listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListener = AWTEventMulticaster.remove(actionListener, listener);
    }

    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        if (actionListener != null) actionListener.actionPerformed(e);
    }

    public Component addExt(Component component) {
        return extendedPanel.add(component);
    }

    public Component addExt(Component component, int index) {
        return extendedPanel.add(component, index);
    }

    public void addExt(Component component, Object constraints) {
        extendedPanel.add(component, constraints);
    }

    public void addExt(Component component, Object constraints, int index) {
        extendedPanel.add(component, constraints, index);
    }

    public void removeExt(Component component) {
        extendedPanel.remove(component);
    }

    public void removeExt(int index) {
        extendedPanel.remove(index);
    }

    public void removeAllExt() {
        extendedPanel.removeAll();
    }

    public void showCentred() {
        pack();
        Dimension ds = getToolkit().getScreenSize(), dd = getSize();
        setLocation((ds.width - dd.width) / 2, (ds.height - dd.height) / 2);
        show();
    }
}