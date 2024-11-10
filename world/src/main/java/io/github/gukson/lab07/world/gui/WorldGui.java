package io.github.gukson.lab07.world.gui;

import pl.edu.pwr.tkubik.jp.farm.api.Role;
import io.github.gukson.lab07.world.context.Field;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WorldGui extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane, machinePanel, panel;
    private JButton[][] buttons;
    private FieldView[][] fieldlabels;
    private Field[][] fields;

    public WorldGui(Field[][] fields) {
        this.fields = fields;
        this.buttons = new JButton[5][5];
        this.fieldlabels = new FieldView[5][5];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 600);
        setResizable(false);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        panel = new JPanel();
        panel.setLayout(new GridLayout(5, 5));
        panel.setBounds(50, 25, 500, 500);

        machinePanel = new JPanel();
        machinePanel.setBounds(0, 0, 700, 600);
        machinePanel.setVisible(true);
        machinePanel.setOpaque(false);
        machinePanel.setLayout(null);
        contentPane.add(machinePanel);

        generateFieldButton();

        contentPane.add(panel);
    }

    private JButton generateFieldButton() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                JPanel tempPanel = new JPanel();
                tempPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                JLabel temp1 = new JLabel();
                temp1.setSize(50,50);
                temp1.setIcon(new ImageIcon("./world/src/main/resources/data/emptyfield.png"));
                JLabel temp2 = new JLabel();
                temp2.setIcon(new ImageIcon("./world/src/main/resources/data/emptyfield.png"));
                temp2.setSize(50,50);
                JLabel temp3 = new JLabel();
                temp3.setSize(50,50);
                temp3.setIcon(new ImageIcon("./world/src/main/resources/data/emptyfield.png"));
                JLabel temp4 = new JLabel();
                temp4.setSize(50,50);
                temp4.setIcon(new ImageIcon("./world/src/main/resources/data/emptyfield.png"));
                tempPanel.add(temp1);
                tempPanel.add(temp2);
                tempPanel.add(temp3);
                tempPanel.add(temp4);
                fieldlabels[y][x] = new FieldView(temp1,temp2,temp3,temp4);

                JButton tempButton = new JButton();
//                tempButton.setSize(100,100);
//                System.out.println(tempButton.getSize());
                tempButton.add(tempPanel);

                tempButton.addActionListener(new ButtonClickListener());
                panel.add(tempButton);
                buttons[y][x] = tempButton;
            }
        }
        return null;
    }

    @Override
    public JPanel getContentPane() {
        return contentPane;
    }

    public JPanel getMachinePanel() {
        return machinePanel;
    }

    public JLabel newMachine(Integer id, Role role) {
        JLabel label = new JLabel("");
        if(role == Role.HARVESTER){
            label.setBounds(71 + id * 100, 25, 57, 100);
            label.setIcon(new ImageIcon("./world/src/main/resources/data/harvester-down.png"));
            machinePanel.add(label);
            label.setVisible(true);
            machinePanel.revalidate();
            machinePanel.repaint();
        }
        else {
            label.setBounds(50, 50 + id * 100, 100, 57);
            label.setIcon(new ImageIcon("./world/src/main/resources/data/seeder-right.png"));
            machinePanel.add(label);
            label.setVisible(true);
            machinePanel.revalidate();
            machinePanel.repaint();
        }
        return label;
    }
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton clickedButton = (JButton) e.getSource();
            int row = -1;
            int col = -1;

            // Szukanie przycisku w siatce
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[i].length; j++) {
                    if (buttons[j][i] == clickedButton) {
                        row = i;
                        col = j;
                        break;
                    }
                }
            }

            if (row != -1 && col != -1) {
                FieldInfoGUI fieldInfoGUI = new FieldInfoGUI(fields[col][row]);
                fieldInfoGUI.setVisible(true);
            }
        }
    }

    public FieldView[][] getFieldlabels() {
        return fieldlabels;
    }


}
