package com.adcc.tool.amqssimulator.view;

import com.google.common.collect.Maps;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.web.swt.SWTResourceManager;

import com.adcc.tool.amqssimulator.ActiveMQFactory;
import com.adcc.tool.amqssimulator.AppConfiguration;
import com.adcc.tool.amqssimulator.DBFactory;
import com.adcc.tool.amqssimulator.util.LogUtil;
import com.csvreader.CsvReader;
import com.google.common.base.Strings;

import org.eclipse.swt.widgets.Button;

public class AutoSendDialog extends Dialog {

	public static String FLAG_ACTIVEMQ = "ACTIVEMQ";

	public static String FLAG_FILE = "FILE";

	public static String FLAG_DB = "DB";

	private Map<String, Object> result = new HashMap<>();

	// ActiveMQFactory
	private ActiveMQFactory activeMQFactory = AppConfiguration.getInstance().getActiveMQFactory();

	// DBFactory
	private DBFactory dbFactory = AppConfiguration.getInstance().getDbFactory();

	/**
	 * Create the dialog.
	 * @param parent
	 */
	public AutoSendDialog(Shell parent) {
		super(parent);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Map<String, Object> open() {
		getParent().setEnabled(false);
		createContents();
		dialog.open();

		Display display = getParent().getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	private void createContents() {
		dialog = new Shell(getParent(),SWT.DIALOG_TRIM | SWT.CLOSE);
		dialog.layout();
		dialog.setSize(553, 275);
		dialog.setText("自动发送数据来源配置");
		int x = (Display.getDefault().getBounds().width - dialog.getBounds().width) / 2;
		int y = (Display.getDefault().getBounds().height - dialog.getBounds().height) / 2;
		dialog.setLocation(x, y);
		dialog.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				close(e);
			}
		});

		tabFolder = new TabFolder(dialog, SWT.NONE);
		tabFolder.setBounds(10, 10, 523, 226);
		createGroupActiveMQ();
		createGroupFile();
		createGroupDB();
	}

	// ActiveMQ
	private void createGroupActiveMQ() {
		Group grpActiveMQConfig = new Group(tabFolder, SWT.NONE);

		tbtm_ActiveMQ = new TabItem(tabFolder, SWT.NONE);
		tbtm_ActiveMQ.setText("ActiveMQ");
		tbtm_ActiveMQ.setControl(grpActiveMQConfig);

		// URL
		Label label = new Label(grpActiveMQConfig, SWT.NONE);
		label.setText("URL:");
		label.setBounds(41, 47, 40, 25);
		txtActiveMQUrl = new Text(grpActiveMQConfig, SWT.BORDER);
		txtActiveMQUrl.setText(activeMQFactory.getUrl());
		txtActiveMQUrl.setBounds(87, 44, 153, 23);

		// 接收队列
		Label label_1 = new Label(grpActiveMQConfig, SWT.NONE);
		label_1.setText("接收队列:");
		label_1.setBounds(271, 47, 51, 25);
		txtActiveMQRecvQueue = new Text(grpActiveMQConfig, SWT.BORDER);
		txtActiveMQRecvQueue.setText(activeMQFactory.getRecvQueue());
		txtActiveMQRecvQueue.setBounds(336, 44, 153, 23);

		btnActiveMQSave = new Button(grpActiveMQConfig, SWT.NONE);
		btnActiveMQSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveActiveMQConf(e);
			}
		});
		btnActiveMQSave.setBounds(409, 132, 80, 27);
		btnActiveMQSave.setText("保存");
	}

	// File
	private void createGroupFile() {
		Group grpFile = new Group(tabFolder, SWT.NONE);

		tbtm_File = new TabItem(tabFolder, SWT.NONE);
		tbtm_File.setText("File");
		tbtm_File.setControl(grpFile);

		// 单发
		btnRadioFileOneSend = new Button(grpFile, SWT.RADIO);
		btnRadioFileOneSend.setSelection(true);
		btnRadioFileOneSend.setBounds(60, 54, 52, 17);
		btnRadioFileOneSend.setText("单发");

		// 连发
		Button btnRadioFileContinueSend = new Button(grpFile, SWT.RADIO);
		btnRadioFileContinueSend.setBounds(159, 54, 52, 17);
		btnRadioFileContinueSend.setText("连发");

		// 发送间隔
		Label lblNewLabel_1 = new Label(grpFile, SWT.NONE);
		lblNewLabel_1.setBounds(261, 54, 108, 17);
		lblNewLabel_1.setText("发送间隔（ms）：");
		textFileIntervalTime = new Text(grpFile, SWT.BORDER);
		textFileIntervalTime.setText("50");
		textFileIntervalTime.setBounds(386, 51, 91, 23);

		// 保存
		btnFileSave = new Button(grpFile, SWT.NONE);
		btnFileSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveFileConf(e);
			}
		});
		btnFileSave.setBounds(397, 125, 80, 27);
		btnFileSave.setText("保存");
	}

	// DB
	private void createGroupDB() {
		Group grpDB = new Group(tabFolder, SWT.NONE);

		tbtm_DB = new TabItem(tabFolder, SWT.NONE);
		tbtm_DB.setText("DB");
		tbtm_DB.setControl(grpDB);

		// 单发
		btnRadioDBOneSend = new Button(grpDB, SWT.RADIO);
		btnRadioDBOneSend.setSelection(true);
		btnRadioDBOneSend.setText("单发");
		btnRadioDBOneSend.setBounds(66, 64, 52, 17);

		// 连发
		Button btnRadioDBContinueSend = new Button(grpDB, SWT.RADIO);
		btnRadioDBContinueSend.setText("连发");
		btnRadioDBContinueSend.setBounds(160, 64, 52, 17);

		// 发送间隔
		Label lblms = new Label(grpDB, SWT.NONE);
		lblms.setText("发送间隔（ms）：");
		lblms.setBounds(264, 64, 96, 17);
		textDBIntervalTime = new Text(grpDB, SWT.BORDER);
		textDBIntervalTime.setText("50");
		textDBIntervalTime.setBounds(382, 61, 90, 23);

		// 保存
		btnDBSave = new Button(grpDB, SWT.NONE);
		btnDBSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveDBConf(e);
			}
		});
		btnDBSave.setBounds(392, 128, 80, 27);
		btnDBSave.setText("保存");
	}

	// 关闭
	private void close(TypedEvent e) {
		if (e instanceof ShellEvent) {
			MessageBox mb = new MessageBox(dialog, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
			mb.setText("系统提示");
			mb.setMessage("确定要关闭配置窗口吗?");
			if (((ShellEvent)e).doit = mb.open() == SWT.OK) {
				// 解锁发送框
				getParent().setEnabled(true);
				dialog.dispose();
			}
		} else {
			getParent().setEnabled(true);
			dialog.dispose();
		}
	}

	/**
	 * 保存 activeMQ配置
	 */
	private void saveActiveMQConf(SelectionEvent e) {
		try {
			String activeMQUrl = txtActiveMQUrl.getText().trim();
			if (Strings.isNullOrEmpty(activeMQUrl)) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("URL为空！");
				mb.open();
				return;
			}
			String activeMQRecvQueue = txtActiveMQRecvQueue.getText().trim();
			if (Strings.isNullOrEmpty(activeMQRecvQueue)) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("接收地址为空！");
				mb.open();
				return;
			}
			result = Maps.newHashMap();
			result.put("flag", FLAG_ACTIVEMQ);
			result.put("url", activeMQUrl);
			result.put("recvQueue", activeMQRecvQueue);
			LogUtil.info(getClass().getName(), "ActiveMQ配置：url:" + activeMQUrl
					+ " recvQueue:" + activeMQRecvQueue);

			// 保存成功提示
			MessageBox mb = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存成功！");
			mb.open();
			// 关闭窗口
			close(e);
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "saveActiveMQConf() error", ex);
			result = Maps.newHashMap();
			MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存ActiveMQ配置报错，请查看日志！\r\n" + ex.getMessage());
			mb.open();
		}
	}

	/**
	 * 保存file配置
	 */
	private void saveFileConf(SelectionEvent e) {
		try {
			String fileIntervalTime = textFileIntervalTime.getText().trim();
			if (Strings.isNullOrEmpty(fileIntervalTime)) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("发送间隔为空！");
				mb.open();
				return;
			}

			// 选择文件
			FileDialog dlog = new FileDialog(dialog, SWT.OPEN);
			dlog.setText("选择文件");
			dlog.setFilterPath("C:/");
			dlog.setFilterExtensions(new String[]{"*.txt", "*.csv"});
			dlog.setFilterNames(new String[]{"Text Files (*.txt)", "CSV Files (*.csv)"});
			String fileName = dlog.open();
			if (Strings.isNullOrEmpty(fileName)) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("文件为空！");
				mb.open();
				return;
			}
			// txt格式文件和csv格式文件区分开
			List<String> fileMsgList = null;
			if (fileName.substring(fileName.indexOf(".")+1).equalsIgnoreCase("csv")) {
				fileMsgList = findMsgListFromFileCsv(fileName);
			} else if (fileName.substring(fileName.indexOf(".")+1).equalsIgnoreCase("txt")) {
				fileMsgList = findMsgListFromFileTxt(fileName);
			}
			if (fileMsgList == null || fileMsgList.size() <= 0) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("该文件没有620报文，请选择其他文件！");
				mb.open();
				return;
			}

			result = Maps.newHashMap();
			result.put("flag", FLAG_FILE);
			result.put("sendOnce", btnRadioFileOneSend.getSelection());
			result.put("intervalTime", Integer.parseInt(fileIntervalTime));
			result.put("msgList", fileMsgList);
			LogUtil.info(getClass().getName(), "File配置：sendOnce:" + btnRadioFileOneSend.getSelection()
					+ " intervalTime:" + fileIntervalTime + " msgList:" + fileMsgList.size());

			// 保存成功提示
			MessageBox mb = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存成功！");
			mb.open();
			// 关闭窗口
			close(e);
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "saveFileConf() error", ex);
			result = Maps.newHashMap();
			MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存File配置报错，请查看日志！\r\n" + ex.getMessage());
			mb.open();
		}
	}

	/**
	 * 保存DB配置
	 */
	private void saveDBConf(SelectionEvent e) {
		try {
			String DBIntervalTime = textDBIntervalTime.getText().trim();
			if (Strings.isNullOrEmpty(DBIntervalTime)) {
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("发送间隔为空！");
				mb.open();
				return;
			}

			// 读取DB数据保存到list
			List<String> DBMsgList = findMsgListFromDB();
			if(DBMsgList ==null || DBMsgList.size() ==  0){
				MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("该数据库没有620报文，请修改数据库配置！");
				mb.open();
				return;
			}

			result = Maps.newHashMap();
			result.put("flag", FLAG_DB);
			result.put("sendOnce", btnRadioDBOneSend.getSelection());
			result.put("intervalTime", Integer.parseInt(DBIntervalTime));
			result.put("msgList", DBMsgList);
			LogUtil.info(getClass().getName(), "DB配置：sendOnce:" + btnRadioDBOneSend.getSelection()
					+ " intervalTime:" + DBIntervalTime + " msgList:" + DBMsgList.size());

			// 保存成功提示
			MessageBox mb = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存成功！");
			mb.open();
			// 关闭窗口
			close(e);
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "saveDBConf() error", ex);
			result = Maps.newHashMap();
			MessageBox mb = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
			mb.setText("系统提示");
			mb.setMessage("保存DB配置报错，请查看日志！\r\n" + ex.getMessage());
			mb.open();
		}
	}

	/**
	 * 导入csv格式文件处理
	 * @param file
	 * @return
	 */
	private List<String> findMsgListFromFileCsv(String file) {
		List<String> list = new ArrayList<>();
		CsvReader csvReader = null;
		try {
			// 生成CsvReader对象，以，为分隔符，utf-8编码方式
			csvReader = new CsvReader(file, ',',Charset.forName("utf-8"));
			// 读取表头
			csvReader.readHeaders();
			// 逐条读取记录，直至读完
			while (csvReader.readRecord()) {
				// 处理报文
				String message = csvReader.get("MES_TEXT");
				if (message != null) {
					int  char1Index = message.indexOf("\001");
					int  char3Index = message.indexOf("\003");
					if (char1Index != -1 && char3Index != -1) {
						String newMsg = message.substring(char1Index, char3Index+1);
						list.add(newMsg);
					}
				}
			}
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "findMsgListFromFileCsv() error", ex);
		} finally {
			try {
				if (csvReader != null) {
					csvReader.close();
				}
			} catch (Exception ex) {
				LogUtil.error(getClass().getName(), "close csvReader failed", ex);
			} finally {
				csvReader = null;
			}
		}
		return list;
	}

	/**
	 * 导入txt格式文件处理
	 * @param file
	 * @return
	 */
	private List<String> findMsgListFromFileTxt(String file) {
		List<String> list = new ArrayList<>();
		InputStreamReader reader = null;// 考虑到编码格式
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String lineTxt = null;
			StringBuilder sb = new StringBuilder();
			Boolean flag = false;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				//从文件头开始读
				if (lineTxt.contains("\001")) {
					flag = true;
				}
				//如果是文件尾部则是完整的一条报文，添加到list 中
				if (lineTxt.contains("\003")) {
					sb.append(lineTxt);
					flag = false;
					list.add(sb.toString());
					sb.delete(0, sb.length());
				}
				if (flag) {
					sb.append(lineTxt + '\n');
				}
			}
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "findMsgListFromFile() error", ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				LogUtil.error(getClass().getName(), "close reader failed", ex);
			} finally {
				reader = null;
			}
		}
		return list;
	}

	/**
	 * 读取数据库表数据对消息的处理
	 * @return
	 */
	private List<String> findMsgListFromDB() {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		try {
			dbFactory.getConnection();
			conn = dbFactory.getConn();
			rs = conn.createStatement().executeQuery(dbFactory.getSql());
			while (rs.next()) {
				String tableColumnName = dbFactory.getTableColumnName();
				String msg = rs.getString(tableColumnName);
				list.add(msg);
			}
		} catch (Exception ex) {
			LogUtil.error(getClass().getName(), "readDbIBM error()", ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				LogUtil.error(getClass().getName(), "rs close failed!", ex);
			} finally {
				rs = null;
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				LogUtil.error(getClass().getName(), "conn close failed!", ex);
			} finally {
				conn = null;
			}
		}
		return list;
	}

	// -------------------------------界面元素-------------------------------
	private Shell dialog;
	private TabFolder tabFolder;

	// ActiveMQ
	private TabItem tbtm_ActiveMQ;
	private Text txtActiveMQUrl;
	private Text txtActiveMQRecvQueue;
	private Button btnActiveMQSave;

	// File
	private TabItem tbtm_File;
	private Button btnRadioFileOneSend;
	private Text textFileIntervalTime;
	private Button btnFileSave;

	// DB
	private TabItem tbtm_DB;
	private Button btnRadioDBOneSend;
	private Text textDBIntervalTime;
	private Button btnDBSave;
}
