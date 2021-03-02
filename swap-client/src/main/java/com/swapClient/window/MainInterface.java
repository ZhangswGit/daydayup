package com.swapClient.window;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;


/**
 * @Data : 2021/02/22
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MainInterface extends JFrame {

    private JTextArea jTextArea;//

    private JTextField jTextField;//输入框

    private NorthJPanel northJPanel;

    private SouthJPanel southJPanel;

    public MainInterface(String title) {

        this.setTitle(title);
        this.setSize(600, 500);//窗口大小
        this.setLocation(200, 200);//窗口位置
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        northJPanel = new NorthJPanel();
        this.add(northJPanel, BorderLayout.NORTH);

        southJPanel = new SouthJPanel();
        this.add(southJPanel, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}
