package com.adcc.tool.amqssimulator.view;

import com.adcc.tool.amqssimulator.ActiveMQFactory;
import com.adcc.tool.amqssimulator.AppConfiguration;
import com.adcc.tool.amqssimulator.IBMMQFactory;
import com.adcc.tool.amqssimulator.util.LogUtil;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.MQConfigurationFactory;
import com.adcc.utility.mq.entity.MQState;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.transfer.*;
import com.adcc.utility.mq.transfer.active.ActiveMQConnectionPool;
import com.adcc.utility.mq.transfer.active.ActiveMQTransfer;
import com.adcc.utility.mq.transfer.ibm.IBMMQConnectionPool;
import com.adcc.utility.mq.transfer.ibm.IBMMQTransfer;
import com.google.common.base.Strings;

import com.google.common.collect.Lists;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.web.swt.SWTResourceManager;

import java.util.List;
import java.util.Map;

/**
 * MQ主界面
 */
public class AMQSMainView {

    private static final String LOG_MSG_RECV = "RECV";

    private static final String LOG_MSG_SEND = "SEND";

    // IBMMQ
    private IBMMQFactory ibmMQFactory = AppConfiguration.getInstance().getIbmmqFactory();
    private MQTransfer ibmMQTransfer;
    private MQConfiguration ibmMQConfiguration;
    private MQConnectionPool ibmMQPool;

    // 连续发送
    private SendMsgThread sendMsgThread;

    // 自动发送配置
    private Map<String, Object> autoSendConfig = null;

    // ActiveMQ
    private ActiveMQFactory activeMQFactory = AppConfiguration.getInstance().getActiveMQFactory();
    private MQTransfer activeMQTransfer;
    private MQConfiguration activeMQConfiguration;
    private MQConnectionPool activeMQPool;

    // File
    private SendMsgListThread sendFileThread;

    // DB
    private SendMsgListThread sendDbThread;

    // *---------------------------Create contents of the window.----------------------------------
    /**
     * Open the window.
     */
    public void open() {
        createContents();
        shell.open();

        Display display = Display.getDefault();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     * @wbp.parser.entryPoint
     */
    private void createContents() {
        shell = new Shell(SWT.DIALOG_TRIM | SWT.CLOSE | SWT.RESIZE | SWT.MIN);
        shell.layout();
        shell.setSize(814, 684);
        shell.setText("AMQS模拟器");
        int x = (Display.getDefault().getBounds().width - shell.getBounds().width) / 2;
        int y = (Display.getDefault().getBounds().height - shell.getBounds().height) / 2;
        shell.setLocation(x, y);
        shell.addShellListener(new ShellAdapter() {
            @Override
            public void shellClosed(ShellEvent e) {
                close(e);
            }
        });

        createMenu();
        createGroupConfig();
        createGroupState();
        createGroupSend();
        createGroupRecv();

        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        btnSend.setEnabled(false);
        btnSendOnce.setEnabled(false);
        btnSendCancel.setEnabled(false);
        btnAutoSend.setEnabled(false);
        btnConfirmAutoSend.setEnabled(false);
        btnPauseAutoSend.setEnabled(false);
        btnCancelAutoSend.setEnabled(false);
    }

    private void createMenu() {
        menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);

        MenuItem menuItemFile = new MenuItem(menu, SWT.CASCADE);
        menuItemFile.setText("文件");
    }

    private void createGroupConfig() {
        grpIBMConfig = new Group(shell, SWT.NONE);
        grpIBMConfig.setText("IBMMQ 配置");
        grpIBMConfig.setBounds(10, 20, 235, 408);

        final Label lblHost = new Label(grpIBMConfig, SWT.NONE);
        lblHost.setBounds(10, 25, 40, 25);
        lblHost.setText("HOST:");
        txtIBMHost = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMHost.setBounds(69, 22, 153, 23);
        txtIBMHost.setText(ibmMQFactory.getHost());

        final Label lblPort = new Label(grpIBMConfig, SWT.NONE);
        lblPort.setBounds(10, 62, 40, 25);
        lblPort.setText("PORT:");
        txtIBMPort = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMPort.setBounds(69, 62, 153, 23);
        txtIBMPort.setText(String.valueOf(ibmMQFactory.getPort()));

        final Label lblQM = new Label(grpIBMConfig, SWT.NONE);
        lblQM.setBounds(10, 99, 25, 25);
        lblQM.setText("QM:");
        txtIBMQM = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMQM.setBounds(69, 99, 153, 23);
        txtIBMQM.setText(ibmMQFactory.getQueueManager());

        final Label lblCH = new Label(grpIBMConfig, SWT.NONE);
        lblCH.setBounds(10, 136, 53, 25);
        lblCH.setText("CHL:");
        txtIBMCHL = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMCHL.setBounds(69, 136, 153, 23);
        txtIBMCHL.setText(ibmMQFactory.getChannel());

        final Label lblQSend = new Label(grpIBMConfig, SWT.NONE);
        lblQSend.setBounds(10, 173, 51, 17);
        lblQSend.setText("发送队列:");
        txtIBMSendQueue = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMSendQueue.setBounds(69, 173, 153, 23);
        txtIBMSendQueue.setText(ibmMQFactory.getSendQueue());

        final Label lblQRecv = new Label(grpIBMConfig, SWT.NONE);
        lblQRecv.setBounds(10, 210, 51, 17);
        lblQRecv.setText("接收队列:");
        txtIBMRecvQueue = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMRecvQueue.setBounds(69, 210, 153, 23);
        txtIBMRecvQueue.setText(ibmMQFactory.getRecvQueue());

        final Label lblInterval = new Label(grpIBMConfig, SWT.NONE);
        lblInterval.setBounds(12, 247, 51, 17);
        lblInterval.setText("发送间隔:");
        txtIBMInterval = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMInterval.setBounds(69, 247, 45, 23);
        txtIBMInterval.setText("50");

        final Label lblExpired = new Label(grpIBMConfig, SWT.NONE);
        lblExpired.setBounds(120, 247, 51, 17);
        lblExpired.setText("超时时间:");
        txtIBMExpired = new Text(grpIBMConfig, SWT.BORDER);
        txtIBMExpired.setBounds(177, 247, 45, 23);
        txtIBMExpired.setText("-1");

        // 连接
        btnConnect = new Button(grpIBMConfig, SWT.NONE);
        btnConnect.setBounds(26, 285, 60, 27);
        btnConnect.setText("连接");
        btnConnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnConnect(e);
            }
        });

        // 连续发送
        btnSend = new Button(grpIBMConfig, SWT.NONE);
        btnSend.setBounds(92, 285, 60, 27);
        btnSend.setText("连续发送");
        btnSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnSend(e);
            }
        });

        // 发送
        btnSendOnce = new Button(grpIBMConfig, SWT.NONE);
        btnSendOnce.setBounds(162, 285, 60, 27);
        btnSendOnce.setText("发送");
        btnSendOnce.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnSendOnce(e);
            }
        });

        // 断开
        btnDisconnect = new Button(grpIBMConfig, SWT.NONE);
        btnDisconnect.setBounds(26, 317, 60, 27);
        btnDisconnect.setText("断开");
        btnDisconnect.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnDisconnect();
            }
        });

        // 取消发送
        btnSendCancel = new Button(grpIBMConfig, SWT.NONE);
        btnSendCancel.setBounds(92, 317, 60, 27);
        btnSendCancel.setText("取消发送");
        btnSendCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnSendCancel();
            }
        });

        // 自动发送
        btnAutoSend = new Button(grpIBMConfig, SWT.NONE);
        btnAutoSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                autoSend();
            }
        });
        btnAutoSend.setBounds(162, 317, 60, 27);
        btnAutoSend.setText("自动发送");

        // 确认自动发送
        btnConfirmAutoSend = new Button(grpIBMConfig, SWT.NONE);
        btnConfirmAutoSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                confirmAutoSend();
            }
        });
        btnConfirmAutoSend.setBounds(51, 349, 80, 27);
        btnConfirmAutoSend.setText("确认自动发送");

        btnPauseAutoSend = new Button(grpIBMConfig, SWT.NONE);
        btnPauseAutoSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pauseAutoSend();
            }
        });
        btnPauseAutoSend.setBounds(140, 349, 80, 27);
        btnPauseAutoSend.setText("暂停自动发送");

        // 取消自动发送
        btnCancelAutoSend = new Button(grpIBMConfig, SWT.NONE);
        btnCancelAutoSend.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cancelAutoSend();
            }
        });
        btnCancelAutoSend.setText("取消自动发送");
        btnCancelAutoSend.setBounds(140, 381, 80, 27);
    }

    private void createGroupState() {
        grpIBMState = new Group(shell, SWT.NONE);
        grpIBMState.setText("IBMMQ 状态");
        grpIBMState.setBounds(10, 434, 235, 170);

        final Label lblState = new Label(grpIBMState, SWT.NONE);
        lblState.setBounds(12, 26, 51, 17);
        lblState.setText("连接状态:");
        cmpIBMState = new Composite(grpIBMState, SWT.NONE);
        cmpIBMState.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
        cmpIBMState.setBounds(69, 24, 153, 23);

        final Label lblSendConut = new Label(grpIBMState, SWT.NONE);
        lblSendConut.setBounds(12, 66, 51, 17);
        lblSendConut.setText("发送计数:");
        txtIBMSendCount = new Text(grpIBMState, SWT.BORDER | SWT.READ_ONLY);
        txtIBMSendCount.setBounds(69, 62, 153, 23);

        final Label lblRecvCount = new Label(grpIBMState, SWT.NONE);
        lblRecvCount.setBounds(12, 102, 51, 17);
        lblRecvCount.setText("接收计数:");
        txtIBMRecvCount = new Text(grpIBMState, SWT.BORDER | SWT.READ_ONLY);
        txtIBMRecvCount.setBounds(69, 98, 153, 23);

        final Button btnClearSendCount = new Button(grpIBMState, SWT.NONE);
        btnClearSendCount.setBounds(52, 133, 80, 27);
        btnClearSendCount.setText("清空发送计数");
        btnClearSendCount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                LogUtil.info(getClass().getName(), "清空发送计数:" + String.valueOf(IBMSendNum));
                IBMSendNum = 0;
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        txtIBMSendCount.setText(String.valueOf(IBMSendNum));
                    }
                });
            }
        });

        final Button btnClearReceiveCount = new Button(grpIBMState, SWT.NONE);
        btnClearReceiveCount.setBounds(142, 133, 80, 27);
        btnClearReceiveCount.setText("清空接收计数");
        btnClearReceiveCount.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                LogUtil.info(getClass().getName(), "清空接收计数:" + String.valueOf(IBMRecvNum));
                IBMRecvNum = 0;
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        txtIBMRecvCount.setText(String.valueOf(IBMRecvNum));
                    }
                });
            }
        });
    }

    private void createGroupSend() {
        grpIBMSend = new Group(shell, SWT.NONE);
        grpIBMSend.setText("IBMMQ 发送");
        grpIBMSend.setBounds(250, 20, 533, 265);

        txtIBMSendContent = new Text(grpIBMSend, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        txtIBMSendContent.setBounds(10, 22, 513, 230);
    }

    private void createGroupRecv() {
        grpIBMRecv = new Group(shell, SWT.NONE);
        grpIBMRecv.setText("IBMMQ 接收");
        grpIBMRecv.setBounds(251, 300, 533, 305);

        txtIBMRecvContent = new Text(grpIBMRecv, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
        txtIBMRecvContent.setBounds(10, 22, 513, 270);
    }

    // *---------------------------Operator the button.----------------------------------
    // 关闭
    private void close(ShellEvent e) {
        MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
        mb.setText("系统提示");
        mb.setMessage("确定要关闭吗?");
        if (e.doit = mb.open() == SWT.OK) {
            btnDisconnect();
            shell.dispose();
            System.exit(0);
        }
    }

    // 连接
    private void btnConnect(SelectionEvent e) {
        try {
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendOnce.setEnabled(true);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(true);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);

            // 设置MQ连接参数
            ibmMQConfiguration = MQConfigurationFactory.getInstance().createIBMMQConfiguration(txtIBMHost.getText().trim(),
                    Integer.valueOf(txtIBMPort.getText().trim()), txtIBMQM.getText().trim(), txtIBMCHL.getText().trim());
            // 设置MQ连接池参数
            ibmMQPool = MQConnectionPoolFactory.getInstance().createIBMMQConnectionPool();
            ((IBMMQConnectionPool) ibmMQPool).setActiveMode(ibmMQFactory.getActiveMode());
            ((IBMMQConnectionPool) ibmMQPool).setTimeout(ibmMQFactory.getTimeout());
            ((IBMMQConnectionPool) ibmMQPool).setMaxConnections(ibmMQFactory.getMaxConnections());
            ((IBMMQConnectionPool) ibmMQPool).setMaxIdelConnections(ibmMQFactory.getMaxIdelConnections());
            ibmMQPool.init(ibmMQConfiguration);
            // 实例化IBMMQTransfer
            ibmMQTransfer = new IBMMQTransfer(ibmMQConfiguration, ibmMQPool);
            // 设置MQ监听器
            ibmMQTransfer.setMQStateListener(new MQStateListener() {
                @Override
                public void onState(MQState mqState, Map<String, String> mqInfo) {
                    processIBMMQState(mqState, mqInfo);
                }
            });
            ibmMQTransfer.setQueueListener(new QueueMsgListener() {
                @Override
                public void onQueueMsg(String qName, Message message, Map<String, String> mqInfo) {
                    processIBMMQQueueMsg(qName, message, mqInfo);
                }
            }, txtIBMRecvQueue.getText().trim());
            ibmMQTransfer.startAsync();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "btnConnect() error", ex);
        }
    }
    private void processIBMMQState(MQState mqState, Map<String, String> mqInfo) {
        try {
            LogUtil.info(getClass().getName(), "mqInfo:" + mqInfo + " state:" + mqState);
            if (mqState == MQState.CONNECTED) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        cmpIBMState.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
                    }
                });
            } else if (mqState == MQState.CONNECTING) {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        cmpIBMState.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
                    }
                });
            } else {
                Display.getDefault().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        cmpIBMState.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
                    }
                });
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "processIBMMQState() error", ex);
        }
    }
    private void processIBMMQQueueMsg(String qName, final Message message, Map<String, String> mqInfo) {
        try {
            if (message != null && message.getLength() > 0) {
                LogUtil.info(LOG_MSG_RECV, "receive message from queue:" + qName + " MESSAGE:\r\n" + message);

                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        txtIBMRecvCount.setText(String.valueOf(++IBMRecvNum));
                        if (IBMRecvNum >= 90000000) {
                            LogUtil.info(getClass().getName(), "接收计数超9千万，清空接收计数：" + String.valueOf(IBMRecvNum));
                            IBMRecvNum = 0;
                        }
                        if (IBMRecvNum % 100 == 0) {
                            txtIBMRecvContent.setText("\r\n" + message);
                        } else {
                            txtIBMRecvContent.append("\r\n" + message);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "processIBMMQQueueMsg() error", ex);
        }
    }

    // 连续发送
    private void btnSend(SelectionEvent e) {
        try {
            // 报文内容
            String msgContent = txtIBMSendContent.getText();
            if (msgContent == null || msgContent.isEmpty()) {
                MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                mb.setText("系统提示");
                mb.setMessage("报文内容为空！");
                mb.open();
                return;
            }

            // 设置其他功能是否可用
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(false);
            btnSendOnce.setEnabled(false);
            btnSendCancel.setEnabled(true);
            btnAutoSend.setEnabled(false);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);

            // 获取有效时间，默认-1
            int expiredIBM = -1;
            try {
                if (!Strings.isNullOrEmpty(txtIBMExpired.getText().trim())) {
                    if (Integer.parseInt(txtIBMExpired.getText().trim()) > 0) {
                        expiredIBM = Integer.parseInt(txtIBMExpired.getText().trim());
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse expired time IBM falied", ex);
            }
            // 获取间隔时间，默认50
            int intervalIBM = 50;
            try {
                if (!Strings.isNullOrEmpty(txtIBMInterval.getText().trim())) {
                    if (Integer.parseInt(txtIBMInterval.getText().trim()) > 0) {
                        intervalIBM = Integer.parseInt(txtIBMInterval.getText().trim());
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse interval time IBM falied", ex);
            }

            // 连续发送
            sendMsgThread = new SendMsgThread(txtIBMSendQueue.getText().trim(), msgContent, expiredIBM, intervalIBM);
            sendMsgThread.start();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "btnSend() error", ex);
        }
    }

    // 发送
    private void btnSendOnce(SelectionEvent e) {
        try {
            // 报文内容
            String msgContent = txtIBMSendContent.getText();
            if (msgContent == null || msgContent.isEmpty()) {
                MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                mb.setText("系统提示");
                mb.setMessage("报文内容为空！");
                mb.open();
                return;
            }
            // 获取有效时间，默认-1
            int expiredIBM = -1;
            try {
                if (!Strings.isNullOrEmpty(txtIBMExpired.getText().trim())) {
                    if (Integer.parseInt(txtIBMExpired.getText().trim()) > 0) {
                        expiredIBM = Integer.parseInt(txtIBMExpired.getText().trim());
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse expired time IBM falied", ex);
            }

            // 发送报文
            sendMsg(txtIBMSendQueue.getText().trim(), new Message(msgContent.getBytes()), expiredIBM, false);
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "btnSendOnce() error", ex);
        } finally {
            // 设置其他功能是否可用
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendOnce.setEnabled(true);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(true);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);
        }
    }

    // 取消发送
    private void btnSendCancel() {
        try {
            if (sendMsgThread != null) {
                sendMsgThread.close();
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "btnSendCancel() error", ex);
        } finally {
            // 设置其他功能是否可用
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendOnce.setEnabled(true);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(true);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);
        }
    }

    // 断开
    private void btnDisconnect() {
        try {
            // 取消发送
            if (sendMsgThread != null) {
                sendMsgThread.close();
            }
            // 取消ActiveMQ
            if (activeMQTransfer != null) {
                activeMQTransfer.stopAsync();
            }
            if (activeMQPool != null) {
                activeMQPool.dispose();
            }
            // 取消File
            if (sendFileThread != null) {
                sendFileThread.close();
                Thread.sleep(1000);
                sendFileThread =  null;
            }
            // 取消DB
            if (sendDbThread != null) {
                sendDbThread.close();
                Thread.sleep(1000);
                sendDbThread =  null;
            }

            // IBMMQ 断开连接
            if (ibmMQTransfer != null) {
                ibmMQTransfer.stopAsync();
            }
            if (ibmMQPool != null) {
                ibmMQPool.dispose();
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "btnDisconnect() error", ex);
        } finally {
            // 设置其他功能是否可用
            btnConnect.setEnabled(true);
            btnDisconnect.setEnabled(false);
            btnSend.setEnabled(false);
            btnSendOnce.setEnabled(false);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(false);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);
            // 解锁发送框
            txtIBMSendContent.setEditable(true);
            // 去除dialog数据
            if (autoSendConfig!= null) {
                autoSendConfig.clear();
            }
        }
    }

    // 自动发送
    private void autoSend() {
        try {
            AutoSendDialog autoSendDialog = new AutoSendDialog(shell);
            autoSendConfig = autoSendDialog.open();
            if (autoSendConfig != null && !autoSendConfig.isEmpty()) {
                // 按钮联动
                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
                btnSend.setEnabled(false);
                btnSendOnce.setEnabled(false);
                btnSendCancel.setEnabled(false);
                btnAutoSend.setEnabled(false);
                btnConfirmAutoSend.setEnabled(true);
                btnPauseAutoSend.setEnabled(false);
                btnCancelAutoSend.setEnabled(false);
                // 锁死发送框
                txtIBMSendContent.setEditable(false);
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "autoSend() error", ex);
        }
    }

    // 确认自动发送
    private void confirmAutoSend() {
        try {
            if (autoSendConfig != null && autoSendConfig.containsKey("flag")) {
                // 按钮联动
                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
                btnSend.setEnabled(false);
                btnSendOnce.setEnabled(false);
                btnSendCancel.setEnabled(false);
                btnAutoSend.setEnabled(false);
                btnConfirmAutoSend.setEnabled(false);
                btnPauseAutoSend.setEnabled(true);
                btnCancelAutoSend.setEnabled(true);
                if (AutoSendDialog.FLAG_ACTIVEMQ.equals(autoSendConfig.get("flag"))) {
                    activeMQAutoSend();
                } else if (AutoSendDialog.FLAG_FILE.equals(autoSendConfig.get("flag"))) {
                    fileAutoSend();
                } else if (AutoSendDialog.FLAG_DB.equals(autoSendConfig.get("flag"))) {
                    dbAutoSend();
                }
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "confirmAutoSend() error", ex);
        }
    }
    private void activeMQAutoSend() {
        try {
            // 获取有效时间，默认-1
            int expiredIBM = -1;
            try {
                if (!Strings.isNullOrEmpty(txtIBMExpired.getText().trim())) {
                    if (Integer.parseInt(txtIBMExpired.getText().trim()) > 0) {
                        expiredIBM = Integer.parseInt(txtIBMExpired.getText().trim());
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse expired time IBM falied", ex);
            }
            final int expired = expiredIBM;
            // 发送队列
            final String queue = txtIBMSendQueue.getText().trim();

            // ActiveMQ配置
            String activeMQUrl = (String)autoSendConfig.get("url");
            String activeMQRecvQueue = (String)autoSendConfig.get("recvQueue");
            activeMQConfiguration = MQConfigurationFactory.getInstance().createActiveMQConfiguration(activeMQFactory.getUser(), activeMQFactory.getPassword(), activeMQUrl);
            // ActiveMQ连接池
            activeMQPool = new ActiveMQConnectionPool();
            ((ActiveMQConnectionPool) activeMQPool).setMaxConnections(activeMQFactory.getMaxConnections());
            ((ActiveMQConnectionPool) activeMQPool).setMaxActive(activeMQFactory.getMaxActive());
            ((ActiveMQConnectionPool) activeMQPool).setIdleTimeout(activeMQFactory.getIdelTimeout());
            ((ActiveMQConnectionPool) activeMQPool).setMaxConnections(activeMQFactory.getExpiryTimeout());
            activeMQPool.init(activeMQConfiguration);
            // 实例化ActiveMQTransfer
            activeMQTransfer = new ActiveMQTransfer(activeMQConfiguration, activeMQPool);
            // 设置监听
            activeMQTransfer.setMQStateListener(new MQStateListener() {
                @Override
                public void onState(MQState mqState, Map<String, String> mqInfo) {
                    try {
                        LogUtil.info(LOG_MSG_RECV, "mqInfo:" + mqInfo + " state:" + mqState);
                    } catch (Exception ex) {
                        LogUtil.error(getClass().getName(), "onState() error", ex);
                    }
                }
            });
            activeMQTransfer.setQueueListener(new QueueMsgListener() {
                @Override
                public void onQueueMsg(String qName, final Message message, Map<String, String> mqInfo) {
                    try {
                        if (message != null && message.getLength() > 0) {
                            LogUtil.info(LOG_MSG_RECV, "receive msg from ActiveMQ queue:" + qName + " MESSAGE:\r\n" + message);
                            // 发送报文
                            sendMsg(queue, message, expired, true);
                        }
                    } catch (Exception ex) {
                        LogUtil.error(getClass().getName(), "onQueueMsg() error", ex);
                    }
                }
            }, activeMQRecvQueue);
            activeMQTransfer.startAsync();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "activeMQAutoSend() error", ex);
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mb.setText("系统提示");
            mb.setMessage("ActiveMQ自动发送报错，请查看日志！\r\n" + ex.getMessage());
            mb.open();
        }
    }
    private void fileAutoSend() {
        try {
            boolean sendOnce = (boolean) autoSendConfig.get("sendOnce");
            int intervalTime = (int) autoSendConfig.get("intervalTime");
            List<String> msgList = (List<String>) autoSendConfig.get("msgList");
            // 获取有效时间，默认-1
            int expired = -1;
            try {
                if (!Strings.isNullOrEmpty(txtIBMExpired.getText().trim())) {
                    if (Integer.parseInt(txtIBMExpired.getText().trim()) > 0) {
                        expired = Integer.parseInt(txtIBMExpired.getText().trim());
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse expired time IBM falied", ex);
            }
            // 发送队列
            String queue = txtIBMSendQueue.getText().trim();

            // 处理线程
            if (sendFileThread != null) {
                sendFileThread.close();
                Thread.sleep(1000);
                sendFileThread =  null;
            }
            // 启动线程
            sendFileThread = new SendMsgListThread(queue, msgList, expired, intervalTime, sendOnce);
            sendFileThread.start();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "fileAutoSend() error", ex);
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mb.setText("系统提示");
            mb.setMessage("File自动发送报错，请查看日志！\r\n" + ex.getMessage());
            mb.open();
        }
    }
    private void dbAutoSend() {
        try {
            boolean sendOnce = (boolean) autoSendConfig.get("sendOnce");
            int intervalTime = (int) autoSendConfig.get("intervalTime");
            List<String> msgList = (List<String>) autoSendConfig.get("msgList");
            // 获取有效时间，默认-1
            int expired = -1;
            try {
                if (!Strings.isNullOrEmpty(txtIBMExpired.getText().trim())) {
                    if (Integer.parseInt(txtIBMExpired.getText().trim()) > 0) {
                        expired = Integer.parseInt(txtIBMExpired.getText().trim());
                    }
                }

            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "parse expired time IBM falied", ex);
            }
            // 发送队列
            String queue = txtIBMSendQueue.getText().trim();

            // 处理线程
            if (sendDbThread != null) {
                sendDbThread.close();
                Thread.sleep(1000);
                sendDbThread = null;
            }
            // 启动线程
            sendDbThread = new SendMsgListThread(queue, msgList, expired, intervalTime, sendOnce);
            sendDbThread.start();
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "dbAutoSend() error", ex);
            MessageBox mb = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
            mb.setText("系统提示");
            mb.setMessage("DB自动发送报错，请查看日志！\r\n" + ex.getMessage());
            mb.open();
        }
    }

    // 暂停自动发送
    private void pauseAutoSend() {
        try {
            // 取消ActiveMQ
            if (activeMQTransfer != null) {
                activeMQTransfer.stopAsync();
            }
            if (activeMQPool != null) {
                activeMQPool.dispose();
            }
            // 取消File
            if (sendFileThread != null) {
                sendFileThread.close();
                Thread.sleep(1000);
                sendFileThread =  null;
            }
            // 取消DB
            if (sendDbThread != null) {
                sendDbThread.close();
                Thread.sleep(1000);
                sendDbThread =  null;
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "pauseAutoSend() error", ex);
        } finally {
            // 按钮联动
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(false);
            btnSendOnce.setEnabled(false);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(false);
            btnConfirmAutoSend.setEnabled(true);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(true);
        }
    }

    // 取消自动发送
    private void cancelAutoSend() {
        try {
            // 取消ActiveMQ
            if (activeMQTransfer != null) {
                activeMQTransfer.stopAsync();
            }
            if (activeMQPool != null) {
                activeMQPool.dispose();
            }
            // 取消File
            if (sendFileThread != null) {
                sendFileThread.close();
                Thread.sleep(1000);
                sendFileThread =  null;
            }
            // 取消DB
            if (sendDbThread != null) {
                sendDbThread.close();
                Thread.sleep(1000);
                sendDbThread =  null;
            }
        } catch (Exception ex) {
            LogUtil.error(getClass().getName(), "cancelAutoSend() error", ex);
        } finally {
            // 按钮联动
            btnConnect.setEnabled(false);
            btnDisconnect.setEnabled(true);
            btnSend.setEnabled(true);
            btnSendOnce.setEnabled(true);
            btnSendCancel.setEnabled(false);
            btnAutoSend.setEnabled(true);
            btnConfirmAutoSend.setEnabled(false);
            btnPauseAutoSend.setEnabled(false);
            btnCancelAutoSend.setEnabled(false);
            // 解锁发送框
            txtIBMSendContent.setEditable(true);
            // 去除dialog数据
            if (autoSendConfig != null) {
                autoSendConfig.clear();
            }
        }
    }

    /**
     * 发送报文
     * */
    private void sendMsg(String queue, final Message message, int expired, boolean showMsg) {
        try {
            // 发送报文
            if (expired == -1) {
                ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message);
            } else {
                ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message, expired);
            }

            // 记录日志
            LogUtil.info(LOG_MSG_SEND, "send message to queue:" + queue + " MESSAGE:\r\n" + message);
            // 计数
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    if (IBMSendNum >= 90000000) {
                        LogUtil.info(getClass().getName(), "超过九千万条，清空发送计数:" + String.valueOf(IBMSendNum));
                        IBMSendNum = 0;
                    }
                    txtIBMSendCount.setText(String.valueOf(++IBMSendNum));
                }
            });
            // 显示发送内容
            if (showMsg) {
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        if (IBMSendNum % 100 == 0) {
                            txtIBMSendContent.setText(message.toString()+"\r\n");
                        } else {
                            txtIBMSendContent.append(message.toString()+"\r\n");
                        }
                    }
                });
            }
        } catch (Exception ex) {
            // 记录日志
            LogUtil.info(getClass().getName(), "send message to queue:" + queue + " failed:\r\n" + message);
        }
    }

    /**
     * 发送报文线程
     * */
    private class SendMsgThread extends Thread {

        private String queue;

        private Message message;

        private int expired;

        private int interval;

        private boolean isStared;

        public SendMsgThread(String queue, String msg, int expired, int interval) {
            setDaemon(true);
            this.queue = queue;
            this.message = new Message(msg.getBytes());
            this.expired = expired;
            this.interval = interval;
        }

        public void close() {
            isStared = false;
        }

        @Override
        public void run() {
            try {
                isStared = true;
                while (isStared) {
                    try {
                        if (expired == -1) {
                            ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message);
                        } else {
                            ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message, expired);
                        }

                        // 记录日志
                        LogUtil.info(LOG_MSG_SEND, "send message to queue:" + queue + " MESSAGE:\r\n" + message);
                        // 计数
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                if (IBMSendNum >= 90000000) {
                                    LogUtil.info(getClass().getName(), "超过九千万条，清空发送计数:" + String.valueOf(IBMSendNum));
                                    IBMSendNum = 0;
                                }
                                txtIBMSendCount.setText(String.valueOf(++IBMSendNum));
                            }
                        });

                        // 休眠interval
                        Thread.sleep(interval);
                    } catch (Exception ex) {
                        LogUtil.info(getClass().getName(), "send message to queue:" + queue + " failed:\r\n" + message);
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "run() error", ex);
            }
        }
    }

    /**
     * 发送报文List线程
     * */
    private class SendMsgListThread extends Thread {

        private String queue;

        private List<Message> msgList = Lists.newArrayList();

        private int expired;

        private int interval;

        private boolean sendOneFlag = true;

        private boolean isStarted = false;

        public SendMsgListThread(String queue, List<String> msgList, int expired, int interval, boolean flag) {
            setDaemon(true);
            this.queue = queue;
            for (String strMessage : msgList) {
                Message message = new Message(strMessage.getBytes());
                this.msgList.add(message);
            }
            this.expired = expired;
            this.interval = interval;
            this.sendOneFlag = flag;
        }

        public void close() {
            isStarted = false;
        }

        @Override
        public void run() {
            try {
                isStarted = true;
                // 单发
                if (sendOneFlag) {
                    sendMsgList();
                } else {
                    // 连发
                    while(isStarted) {
                        sendMsgList();
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "run() error", ex);
            }
        }

        /**
         * 消息发送
         */
        private void sendMsgList() {
            try {
                for (final Message message : msgList) {
                    try {
                        if (!isStarted) {
                            break;
                        }
                        // 发送
                        if (expired != -1) {
                            ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message);
                        } else {
                            ((IBMMQTransfer) ibmMQTransfer).sendQueue(queue, message, expired);
                        }

                        // 记录日志
                        LogUtil.info(LOG_MSG_SEND, "send message to queue:" + queue + " MESSAGE:\r\n" + message);
                        // 计数
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                if (IBMSendNum >= 90000000) {
                                    LogUtil.info(getClass().getName(), "超过九千万条，清空发送计数:" + String.valueOf(IBMSendNum));
                                    IBMSendNum = 0;
                                }
                                txtIBMSendCount.setText(String.valueOf(++IBMSendNum));
                            }
                        });
                        //内容显示到界面
                        Display.getDefault().syncExec(new Runnable() {
                            public void run() {
                                if (IBMSendNum % 100 == 0) {
                                    txtIBMSendContent.setText(message.toString()+"\r\n");
                                } else {
                                    txtIBMSendContent.append(message.toString()+"\r\n");
                                }
                            }
                        });

                        // 休眠interval
                        Thread.sleep(interval);
                    } catch (Exception ex) {
                        // 记录日志
                        LogUtil.info(getClass().getName(), "send message to queue:" + queue + " failed:\r\n" + message);
                    }
                }
            } catch (Exception ex) {
                LogUtil.error(getClass().getName(), "sendMsg() error", ex);
            }
        }

    }

    // *---------------------------界面元素定义----------------------------------
    private Shell shell;
    private Menu menu;

    // IBMMQ配置
    private Group grpIBMConfig;
    private Text txtIBMHost;
    private Text txtIBMPort;
    private Text txtIBMQM;
    private Text txtIBMCHL;
    private Text txtIBMSendQueue;
    private Text txtIBMRecvQueue;
    private Text txtIBMInterval;
    private Text txtIBMExpired;
    // 按钮
    private Button btnConnect;
    private Button btnSend;
    private Button btnSendOnce;
    private Button btnSendCancel;
    private Button btnDisconnect;
    private Button btnAutoSend;
    private Button btnConfirmAutoSend;
    private Button btnPauseAutoSend;
    private Button btnCancelAutoSend;

    // IBMMQ状态
    private Group grpIBMState;
    private Composite cmpIBMState;
    private Text txtIBMSendCount;
    private Text txtIBMRecvCount;
    private static long IBMSendNum;
    private static long IBMRecvNum;

    // IBMMQ发送
    private Group grpIBMSend;
    private Text txtIBMSendContent;

    // IBMMQ接收
    private Group grpIBMRecv;
    private Text txtIBMRecvContent;
}
