package com.swapClient.window;

import bean.Message;
import bean.SwapUser;
import com.swapCommon.header.MessageHead;
import io.netty.channel.ChannelFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
@Data
public class MainInterface extends JFrame {

    private DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                    .withLocale( Locale.CHINESE )
                    .withZone( ZoneId.systemDefault() );

    private ChannelFuture channelFuture;

    private JTextArea jTextArea;//展示框

    private JTextField jTextField;//输入框

    private SwapUser localSwapUser;

    private List<SwapUser> users;

    private JButton jButton;//发送按钮

    private JComboBox jComboBox;//下拉选项

    private SwapUser goalSwapUser;//被选中用户

    private String title;

    public MainInterface(String title) {
        this.jTextField = new JTextField(20);
        this.jTextArea = new JTextArea(20,30);
        this.title = title;

        this.setTitle(title);
        this.setSize(600, 500);//窗口大小
        this.setLocation(200, 200);//窗口位置
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);

        this.add(northJPanel(), BorderLayout.NORTH);
    }

    public void visible(List<SwapUser> users){
        this.users = users;
        this.add(southJPanel(), BorderLayout.SOUTH);
        this.setVisible(true);
    }

    private JPanel northJPanel(){
        JPanel northJPanel = new JPanel();
        jTextArea.setLineWrap(true);//激活换行
        jTextArea.setWrapStyleWord(true);//激活断行不断字功能
        northJPanel.add(new JScrollPane(jTextArea));//激活滚动条
        return northJPanel;
    }

    private JPanel southJPanel(){
        JPanel southJPanel = new JPanel();

        jComboBox = new JComboBox();
        users.stream().forEach(user -> jComboBox.addItem(user));
        //默认选取第一个用户为被选中用户
        goalSwapUser = users.get(0);

        jComboBox.addPopupMenuListener(new PopupMenuListener(){
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                goalSwapUser = (SwapUser) jComboBox.getSelectedItem();
                log.info("selected goalUser {}", goalSwapUser.getUserName());
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        jButton = new JButton("发送");
        jButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String text = jTextField.getText();
                if (StringUtils.isBlank(text.trim())) {
                    return;
                }
                jTextField.setText("");
                jTextArea.append(String.format("%s   %s", localSwapUser.getUserName(), formatter.format(Instant.now()) + "\r\n"));
                jTextArea.append(text + "\r\n");

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
}
