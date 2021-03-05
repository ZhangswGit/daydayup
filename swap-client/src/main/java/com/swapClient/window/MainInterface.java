package com.swapClient.window;

import bean.Message;
import bean.SwapUser;
import com.swapCommon.define.Define;
import com.swapCommon.header.MessageHead;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

/**
 * @Data : 2021/02/22
 * @Author : zhangsw
 * @Descripe : TODO
 * @Version : 0.1
 */
@Slf4j
public class MainInterface extends JFrame {

    private DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.CHINESE)
                    .withZone(ZoneId.systemDefault());

    private ChannelFuture channelFuture;

    private JTextArea jTextArea;//展示框

    private JTextField jTextField;//输入框

    private SwapUser localSwapUser;

    private List<SwapUser> users;

    private JButton jButton;//发送按钮

    private JComboBox jComboBox;//下拉选项

    private SwapUser goalSwapUser;//被选中用户

    private String title;//

    private JPanel northJPanel;

    private JPanel southJPanel;

    private static MainInterface mainInterface;//主窗口 单例

    public static MainInterface getMainInterface() {
        if (mainInterface == null) {
            synchronized (MainInterface.class) {
                if (mainInterface == null) {
                    return new MainInterface("聊天窗口");
                }
            }
        }
        return mainInterface;
    }

    private MainInterface(String title) {
        this.northJPanel = new JPanel();
        this.jTextArea = new JTextArea(20, 30);
        this.title = title;

        this.southJPanel = new JPanel();
        this.jTextField = new JTextField(20);
        this.jComboBox = new JComboBox();
        this.jButton = new JButton("发送");

        this.setTitle(title);
        this.setSize(600, 500);//窗口大小
        this.setLocation(200, 200);//窗口位置
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        this.add(northJPanel(), BorderLayout.NORTH);
//        this.addWindowListener(new WindowListener() {
//
//            @Override
//            public void windowOpened(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowClosing(WindowEvent e) {
//            }
//
//            @Override
//            public void windowClosed(WindowEvent e) {
//            }
//
//            @Override
//            public void windowIconified(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowDeiconified(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowActivated(WindowEvent e) {
//
//            }
//
//            @Override
//            public void windowDeactivated(WindowEvent e) {
//
//            }
//        });

        this.add(southJPanel(), BorderLayout.SOUTH);
    }

    /**
     * 激活聊天窗口
     *
     * @param localSwapUser
     * @param users
     */
    public void setMainInterfaceVisible(SwapUser localSwapUser, List<SwapUser> users, boolean visible) {
        this.localSwapUser = localSwapUser;
        this.users = users;
        activating();
        this.setVisible(visible);
    }

    //展示取域(消息展示)
    private JPanel northJPanel() {
        jTextArea.setLineWrap(true);//激活换行
        jTextArea.setWrapStyleWord(true);//激活断行不断字功能
        northJPanel.add(new JScrollPane(jTextArea));//激活滚动条
        return northJPanel;
    }

    //消息发送取域(用户选择，输入消息框，发送)
    private JPanel southJPanel() {
        //下拉框监听被选项
        jComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                goalSwapUser = (SwapUser) jComboBox.getSelectedItem();
                log.info("selected goalUser :{}", goalSwapUser);
            }
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        //按钮监听
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (goalSwapUser == null) {
                    addMessage(Define.noUserSelected.getDetail() + "\r\n");
                    return;
                }

                String text = jTextField.getText();
                if (StringUtils.isBlank(text.trim())) {
                    return;
                }
                jTextField.setText("");
                addMessage(String.format("%s   %s", localSwapUser.getUserName(), formatter.format(Instant.now()) + "\r\n"));
                addMessage(text + "\r\n");

                Message message = Message.builder()
                        .body(text)
                        .localSwapUser(localSwapUser)
                        .goalSwapUser(goalSwapUser)
                        .messageHead(MessageHead.MUTUAL)
                        .build();

                channelFuture.channel().writeAndFlush(message);
                log.info("receive text :{}", text);
            }
        });

        southJPanel.add(jComboBox, BorderLayout.WEST);
        southJPanel.add(jTextField, BorderLayout.CENTER);
        southJPanel.add(jButton, BorderLayout.SOUTH);

        return southJPanel;
    }

    private void activating() {
        if (!CollectionUtils.isEmpty(users)) {
            users.stream().forEach(user -> jComboBox.addItem(user));
            //默认选取第一个用户为被选中用户
            goalSwapUser = users.get(0);
        }
    }

    /**
     * 消息展示框添加信息
     *
     * @param message
     */
    public void addMessage(String message) {
        jTextArea.append(message);
    }

    /**
     * 登录设置通道
     *
     * @param channelFuture
     */
    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }
}
