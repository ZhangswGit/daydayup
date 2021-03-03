package com.swapClient.window;

import bean.LoginUser;
import bean.Message;
import com.swapCommon.header.MessageHead;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Data :  2021/3/3 15:41
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
@Data
public class LoginInterFace extends JFrame {
    private ChannelFuture channelFuture;

    private JTextArea jTextArea;

    private JTextField jTextField;//输入账号

    private JPasswordField jPasswordField;//密码框

    private JButton jButton;//登录按钮

    public LoginInterFace(String title) {
        this.setTitle(title);
        this.setSize(400, 250);//窗口大小
        this.setLocation(200, 200);//窗口位置
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);//关闭按钮关闭程序
        this.setResizable(false);
        this.add(loginJPanel(), BorderLayout.CENTER);
        this.setVisible(true);
    }

    public JPanel loginJPanel() {
        JPanel loginJPanel = new JPanel();

        JLabel label1 = new JLabel("用户名:");
        JLabel label2 = new JLabel("密码:");
        jTextField = new JTextField(10);
        jPasswordField = new JPasswordField(10);
        loginJPanel.add(label1);
        loginJPanel.add(jTextField);
        loginJPanel.add(label2);
        loginJPanel.add(jPasswordField);

        jTextArea = new JTextArea(5, 10);
        loginJPanel.add(jTextArea);
        jButton = new JButton("登录");
        loginJPanel.add(jButton, BorderLayout.CENTER);

        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String userName = jTextField.getText().trim();
                String passWord = new String(jPasswordField.getPassword());
                if (StringUtils.isAnyBlank(userName, passWord)) {
                    jTextArea.setText("请输入账号密码");
                    return;
                }
                log.info("userName:{},passWord:{}", userName, passWord);

                channelFuture.channel().writeAndFlush(Message.builder()
                        .messageHead(MessageHead.AUTH)
                        .body(LoginUser.builder().userName(userName).passWord(passWord).build())
                        .build()).addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        //重启
                    }
                });
            }
        });
        return loginJPanel;
    }
}
