package com.swapClient.window;

import javax.swing.*;

/**
 * @Data :  2021/2/23 10:53
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
public class NorthJPanel extends JPanel {

    public NorthJPanel(){
        JTextArea area=new JTextArea("asdasd");
        area.setSize(500, 400);
        area.setText("");
        this.add(area);
    }
}
