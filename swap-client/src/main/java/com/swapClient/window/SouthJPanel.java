package com.swapClient.window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Data :  2021/2/23 10:53
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class SouthJPanel extends JPanel {

    private JButton jButton;//发送按钮
    private JComboBox jComboBox;//下拉选项

    public SouthJPanel(){

        JTextField input_field = new JTextField(20);
        jButton = new JButton("发送");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                input_field.setText("");
            }
        });
        this.add(input_field, BorderLayout.WEST);
        this.add(jButton, BorderLayout.SOUTH);
    }

}
