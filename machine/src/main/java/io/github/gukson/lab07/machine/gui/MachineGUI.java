package io.github.gukson.lab07.machine.gui;

import io.github.gukson.lab07.machine.context.Machine;
import pl.edu.pwr.tkubik.jp.farm.api.Role;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.awt.EventQueue;
import java.util.Objects;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

public class MachineGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JButton registerButton, unregisterButton;
    private ButtonGroup bg = new ButtonGroup();
    private JTextField textField;
    private Machine machine;
    private Role role;
    private MySwingWorker mySwingWorker;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MachineGUI frame = new MachineGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MachineGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 265, 237);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JRadioButton SeederRadio = new JRadioButton("Seeder");
        SeederRadio.setSelected(true);
        SeederRadio.setBounds(6, 37, 90, 23);
        contentPane.add(SeederRadio);
        bg.add(SeederRadio);

        JRadioButton HarvestRadio = new JRadioButton("Harvester");
        HarvestRadio.setBounds(152, 37, 107, 23);
        contentPane.add(HarvestRadio);
        bg.add(HarvestRadio);

        textField = new JTextField();
        textField.setBounds(96, 6, 130, 26);
        contentPane.add(textField);
        textField.setColumns(10);

        JLabel lblNewLabel = new JLabel("Port");
        lblNewLabel.setBounds(23, 11, 61, 16);
        contentPane.add(lblNewLabel);

        registerButton = new JButton("Register");
        registerButton.setBounds(69, 86, 117, 29);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!Objects.equals(textField.getText(), "")){
                    if (SeederRadio.isSelected()) {
                        role = Role.SEEDER;
                    } else {
                        role = Role.HARVESTER;
                    }
                    registerButton.setEnabled(false);
                    unregisterButton.setEnabled(true);
                    try {
                        machine = new Machine(Integer.parseInt(textField.getText()), role);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    } catch (NotBoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    mySwingWorker = new MySwingWorker();
                    mySwingWorker.execute();

                }

            }
        });
        contentPane.add(registerButton);
        unregisterButton = new JButton("Unregister");
        unregisterButton.setBounds(69, 127, 117, 29);
        contentPane.add(unregisterButton);
        unregisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerButton.setEnabled(true);
                unregisterButton.setEnabled(false);
                try {
                    machine.unregister();
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        unregisterButton.setEnabled(false);

    }
    class MySwingWorker extends SwingWorker<Void, Void> {
        @Override
        protected Void doInBackground() throws Exception {
            machine.run();
            return null;
        }

        @Override
        protected void done() {
            // Aktualizacja interfejsu użytkownika po zakończeniu operacji w tle
        }
    }
}


