package es.uned.dia.interoperate.matlab.jim;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Class Marco to make the frame
 * @author Gonzalo
 *
 */
public class Marco extends JFrame {
  private static final long serialVersionUID = 1L;
  JPanel contentPane;
  JMenuBar jMenuBar = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  static JLabel statusBar = new JLabel();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JButton jButton1 = new JButton();
  JPanel jPanelOptions = new JPanel();
  JScrollPane jScrollPaneMessageArea = new JScrollPane();
  static JTextArea jTextMessageArea = new JTextArea();
  TitledBorder titledBorder1;
  JToolBar jToolBar1 = new JToolBar();
  JButton jButtonStop = new JButton();
  JButton jButtonPlay = new JButton();
  JLabel jLabelPort = new JLabel();
  JLabel jLabelBufferIn = new JLabel();
  JLabel jLabelBufferOut = new JLabel();
  JLabel jLabelMaxMatlab = new JLabel();

  JTextField jTextFieldServicePort = new JTextField();
  JTextField jTextFieldBufferOut = new JTextField();
  JComboBox jComboBoxMaxMatlab = new JComboBox();
  JLabel jLabelWorkDirectory = new JLabel();
  JTextField jTextFieldWorkDirectory = new JTextField();
  JCheckBox jCheckBoxAllowExternal = new JCheckBox();
  JCheckBox jCheckBoxAllowDOS = new JCheckBox();

  //Authorization
  JCheckBox jCheckBoxAuthenticate = new JCheckBox();
  JTextField jTextFieldAuthenticateDB = new JTextField();
  JLabel jLabelAuthenticateUser = new JLabel();
  JTextField jTextFieldAuthenticateUser = new JTextField();
  JLabel jLabelAuthenticatePwd = new JLabel();
  JPasswordField jPasswordFieldAuthenticatePwd = new JPasswordField();

  JTextField jTextFieldBufferIn = new JTextField();
  JCheckBox jCheckBoxAllowLog = new JCheckBox();
  JComboBox jComboBoxLog = new JComboBox();
  JMenuItem jMenuItemStart = new JMenuItem();
  JMenuItem jMenuItemStop = new JMenuItem();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenuItem jMenuItem4 = new JMenuItem();
  JMenuItem jMenuItemClearLog = new JMenuItem();
  JMenuItem jMenuItemDefault = new JMenuItem();
  JMenuItem jMenuItemEdit = new JMenuItem();

  //Make frame
  public Marco() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit(); jButtonPlay_actionPerformed(null);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private Object makeObj(final String item)  {
    return new Object() { public String toString() { return item; } };
  }

  public static void messageArea(String str){
    jTextMessageArea.append(str+"\n");
  }
  public static void userCounter(int uc){
    statusBar.setText(" Connected users:"+uc);
  }

  //Initiate elments
  private void jbInit() throws Exception  {
    contentPane = (JPanel) this.getContentPane();
    titledBorder1 = new TitledBorder("");
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    this.setResizable(true);
    this.setSize(new Dimension(394, 308));
    this.setTitle("Jim Server 1.1");
    statusBar.setBackground(Color.lightGray);
    statusBar.setAlignmentX((float) 0.0);
    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
    statusBar.setDebugGraphicsOptions(0);
    statusBar.setToolTipText("");
    statusBar.setText(" Connected users:0");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new Marco_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About...");
    jMenuHelpAbout.addActionListener(new Marco_jMenuHelpAbout_ActionAdapter(this));
    jButton1.setText("jButton1");
    jTextMessageArea.setEditable(false);
    jTextMessageArea.setSelectionEnd(10);
    jTextMessageArea.setSelectionStart(0);
    jTextMessageArea.setText("");
    jTextMessageArea.setRows(8);
    jTextMessageArea.setTabSize(8);
    contentPane.setFont(new java.awt.Font("Dialog", 0, 12));
    contentPane.setForeground(Color.black);
    contentPane.setMaximumSize(new Dimension(32767, 32767));
    jPanelOptions.setLayout(new GridLayout(0,2,10,0));
    jTabbedPane1.setTabPlacement(JTabbedPane.BOTTOM);
    jTabbedPane1.setEnabled(true);
    jTabbedPane1.setAlignmentY((float) 0.5);
    jTabbedPane1.setBorder(null);
    jTabbedPane1.setDoubleBuffered(false);
    
    
    titledBorder1.setBorder(BorderFactory.createEtchedBorder());
    jButtonStop.setText("Stop");
    jButtonStop.addActionListener(new Marco_jButtonStop_actionAdapter(this));
    jButtonStop.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/stopoff.png")));
    jButtonPlay.setAlignmentY((float) 0.5);
    jButtonPlay.setDebugGraphicsOptions(0);
    jButtonPlay.setToolTipText("");
    jButtonPlay.setActionCommand("jButton2");
    jButtonPlay.setBorderPainted(true);
    jButtonPlay.setHorizontalTextPosition(SwingConstants.TRAILING);
    jButtonPlay.setText("Start");
    jButtonPlay.addActionListener(new Marco_jButtonPlay_actionAdapter(this));
    jButtonPlay.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/play.png")));

    //Authorization
    jCheckBoxAuthenticate.setEnabled(true);
    jCheckBoxAuthenticate.setToolTipText("");
    jCheckBoxAuthenticate.setSelected(false);
    jCheckBoxAuthenticate.setText("Authenticate");
    jCheckBoxAuthenticate.addActionListener(new Marco_jCheckBoxAuthenticate_actionAdapter(this));
    //    jTextFieldAuthenticateDB.setText("netlab"); 
    jTextFieldAuthenticateDB.setText("");
    jTextFieldAuthenticateDB.setEnabled(false);
    jLabelAuthenticateUser.setRequestFocusEnabled(true);
    jLabelAuthenticateUser.setText(" User");
    //jTextFieldAuthenticateUser.setText("root"); 
    jTextFieldAuthenticateUser.setText("");
    jTextFieldAuthenticateUser.setEnabled(false);
    jLabelAuthenticatePwd.setRequestFocusEnabled(true);
    jLabelAuthenticatePwd.setText(" Pwd");
    jPasswordFieldAuthenticatePwd.setText("");
    jPasswordFieldAuthenticatePwd.setEditable(true);
    jPasswordFieldAuthenticatePwd.setEnabled(true);

    // jPasswordFieldAuthenticatePwd.setEditable(false); 
    // jPasswordFieldAuthenticatePwd.setEnabled(false); 

    jLabelPort.setRequestFocusEnabled(true);
    jLabelPort.setText(" Service Port");
    jLabelBufferIn.setText(" Buffer In Size");
    jLabelBufferIn.setRequestFocusEnabled(true);
    jLabelBufferOut.setText(" Buffer Out Size");
    jLabelBufferOut.setPreferredSize(new Dimension(75, 15));
    jLabelBufferOut.setRequestFocusEnabled(true);
    jLabelBufferOut.setToolTipText("");
    jLabelMaxMatlab.setText(" Max Sessions");
    jLabelMaxMatlab.setRequestFocusEnabled(true);
    jLabelMaxMatlab.setToolTipText("");
    jTextFieldServicePort.setAlignmentX((float) 0.5);
    jTextFieldServicePort.setAlignmentY((float) 0.5);
    jTextFieldServicePort.setDebugGraphicsOptions(0);
    jTextFieldServicePort.setSelectionStart(4);
    jTextFieldServicePort.setText("2005");
    jTextFieldServicePort.setColumns(0);
    jTextFieldServicePort.setHorizontalAlignment(SwingConstants.CENTER);
    jTextFieldBufferOut.setText("1024");
    jTextFieldBufferOut.setHorizontalAlignment(SwingConstants.CENTER);
    jLabelWorkDirectory.setText(" WorkDirectory");
    jLabelWorkDirectory.setRequestFocusEnabled(true);
    jLabelWorkDirectory.setToolTipText("");
    jTextFieldWorkDirectory.setSelectionStart(6);
    jTextFieldWorkDirectory.setText("\\JimWD");
    jCheckBoxAllowExternal.setEnabled(true);
    jCheckBoxAllowExternal.setToolTipText("");
    jCheckBoxAllowExternal.setSelected(true);
    jCheckBoxAllowExternal.setText("External Files");
    jCheckBoxAllowDOS.setText("OS Functions");
    jCheckBoxAllowDOS.setEnabled(true);
    jCheckBoxAllowDOS.setDoubleBuffered(false);
    jCheckBoxAllowDOS.setToolTipText("");
    jCheckBoxAllowDOS.setFocusPainted(true);
    jTextFieldBufferIn.setText("1024");
    jTextFieldBufferIn.setHorizontalAlignment(SwingConstants.CENTER);
    jCheckBoxAllowLog.setText("Log");
    jCheckBoxAllowLog.addActionListener(new Marco_jCheckBoxAllowLog_actionAdapter(this));
    jCheckBoxAllowLog.setSelected(true);
    jCheckBoxAllowLog.setSelectedIcon(null);
    jCheckBoxAllowLog.setRequestFocusEnabled(true);
    jCheckBoxAllowLog.setToolTipText("");
    jComboBoxMaxMatlab.addItem(makeObj("1"));
    jComboBoxMaxMatlab.addItem(makeObj("2"));
    jComboBoxMaxMatlab.addItem(makeObj("3"));
    jComboBoxMaxMatlab.addItem(makeObj("4"));
    jComboBoxMaxMatlab.addItem(makeObj("5"));
    jComboBoxMaxMatlab.addItem(makeObj("6"));
    jComboBoxMaxMatlab.addItem(makeObj("7"));
    jComboBoxMaxMatlab.addItem(makeObj("8"));
    jComboBoxMaxMatlab.addItem(makeObj("9"));
    jComboBoxMaxMatlab.addItem(makeObj("10"));
    jComboBoxMaxMatlab.setSelectedIndex(4);
    jComboBoxLog.setEnabled(true);
    jComboBoxLog.setOpaque(true);
    jComboBoxLog.setMaximumRowCount(3);
    jComboBoxLog.setPopupVisible(false);
    jComboBoxLog.addItem(makeObj("Simple"));
    jComboBoxLog.addItem(makeObj("Full"));
    jComboBoxLog.setSelectedIndex(0);
    jScrollPaneMessageArea.setAlignmentY((float) 0.5);
    jScrollPaneMessageArea.setAutoscrolls(false);
    jMenuItemStart.setText("Start");
    jMenuItemStart.addActionListener(new Marco_jMenuItemStart_actionAdapter(this));
    jMenuItemStop.setText("Stop");
    jMenuItemStop.addActionListener(new Marco_jMenuItemStop_actionAdapter(this));
    jMenu1.setText("Options");
    jMenuItemDefault.setText("Default");
    jMenuItemEdit.setText("Edit");
    jMenuItemClearLog.setText("Clear Log");
    jMenuItemClearLog.addActionListener(new Marco_jMenuItemClearLog_actionAdapter(this));
    jToolBar1.setFloatable(false);
    jMenuItemDefault.setText("Default");
    jMenuItemDefault.addActionListener(new Marco_jMenuItemDefault_actionAdapter(this));
    jMenuItemEdit.setText("Edit");
    jMenuItemEdit.addActionListener(new Marco_jMenuItemEdit_actionAdapter(this));
    jPanelOptions.setEnabled(true);
    jToolBar1.add(jButtonPlay, null);
    jToolBar1.add(jButtonStop, null);

    jToolBar1.setLayout(new FlowLayout(FlowLayout.TRAILING));
    contentPane.add(jToolBar1);
    contentPane.add(jTabbedPane1,BorderLayout.CENTER);
   
    jTabbedPane1.add(jScrollPaneMessageArea,    "Message Area");
    jTabbedPane1.add(jPanelOptions,  "Options");

    jScrollPaneMessageArea.getViewport().add(jTextMessageArea, null);
    jMenuFile.add(jMenuItemStart);
    jMenuFile.add(jMenuItemStop);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuItemClearLog);
    jMenuFile.addSeparator();
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar.add(jMenuFile);
    jMenuBar.add(jMenu1);
    jMenuBar.add(jMenuHelp);

    JPanel leftPanel= new JPanel();
    leftPanel.setLayout(new GridLayout(0,2,0,10));
    JPanel rigthPanel= new JPanel();
    rigthPanel.setLayout(new GridLayout(0,2,0,10));

    jPanelOptions.add(leftPanel);
    jPanelOptions.add(rigthPanel);

    leftPanel.add(jLabelPort);
    leftPanel.add(jTextFieldServicePort);

    leftPanel.add(jLabelBufferIn);
    leftPanel.add(jTextFieldBufferIn);

    leftPanel.add(jLabelBufferOut);
    leftPanel.add(jTextFieldBufferOut);

    leftPanel.add(jLabelWorkDirectory);
    leftPanel.add(jTextFieldWorkDirectory);

    //Authorization
    leftPanel.add(jCheckBoxAuthenticate);
    leftPanel.add(jTextFieldAuthenticateDB);

    rigthPanel.add(jLabelMaxMatlab);
    rigthPanel.add(jComboBoxMaxMatlab);

    rigthPanel.add(jCheckBoxAllowDOS);    
    rigthPanel.add(new JLabel(" "));


    rigthPanel.add(jCheckBoxAllowExternal);
    rigthPanel.add(new JLabel(" "));

    rigthPanel.add(jCheckBoxAllowLog);
    rigthPanel.add(jComboBoxLog);    

    JPanel rigthPanel21= new JPanel();
    JPanel rigthPanel22= new JPanel();

    rigthPanel21.setLayout(new GridLayout(1,2));
    rigthPanel22.setLayout(new GridLayout(1,2));

    rigthPanel21.add(jLabelAuthenticateUser);
    rigthPanel21.add(jTextFieldAuthenticateUser);
    rigthPanel.add(rigthPanel21);

    rigthPanel22.add(jLabelAuthenticatePwd);
    rigthPanel22.add(jPasswordFieldAuthenticatePwd);
    rigthPanel.add(rigthPanel22);

    JPanel statusBarPanel = new JPanel();
    statusBarPanel.setLayout(new BorderLayout());
    statusBarPanel.add(statusBar,BorderLayout.PAGE_END);
    contentPane.add(statusBarPanel);

    jMenu1.add(jMenuItemDefault);
    jMenu1.add(jMenuItemEdit);
    jMenu1.add(jMenuItemDefault);
    jMenu1.add(jMenuItemEdit);
    this.setJMenuBar(jMenuBar);

    //pack();
  }

  //File | Start
  public void jMenuItemStart_actionPerformed(ActionEvent e) {
    jButtonPlay_actionPerformed(e);
  }

  //File | Stop
  public void jMenuItemStop_actionPerformed(ActionEvent e) {
    jButtonStop_actionPerformed(e);
  }

  //File | Clear Log
  public void jMenuItemClearLog_actionPerformed(ActionEvent e) {
    jTextMessageArea.setText("");
  }

  //File | Exit
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }

  //Help | About 
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    Marco_AcercaDe dlg = new Marco_AcercaDe(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.setVisible(true);
  }

  //Exit window
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  void jButtonPlay_actionPerformed(ActionEvent e) {
    String SERVICE_PORT="2005";
    String BUFFER_IN="1024";
    String BUFFER_OUT="1024";
    String MAX_MATLAB_SESSIONS="5";
    String WD_JIM="\\JimWD";
    String ALLOWDOS="yes";
    String ALLOWEXT="yes";
    String ALLOWLOG="simple";
    //Authorization
    String AUT="yes";
    String AUTBD="netlab";
    String AUTUSER="root";
    String AUTPWD="";
    String[] args=new String[12];
    try {
      int user_SERVICE_PORT=new Integer(jTextFieldServicePort.getText()).intValue();
      int user_BUFFER_IN=new Integer(jTextFieldBufferIn.getText()).intValue();
      int user_BUFFER_OUT=new Integer(jTextFieldBufferOut.getText()).intValue();
      int user_MAX_MATLAB_SESSIONS= jComboBoxMaxMatlab.getSelectedIndex()+1;
      String user_WD_JIM=jTextFieldWorkDirectory.getText();
      if (!jCheckBoxAllowDOS.isSelected())
        ALLOWDOS="no";
      if (!jCheckBoxAllowExternal.isSelected())
        ALLOWEXT="no";
      if (jCheckBoxAllowLog.isSelected()){
        if (jComboBoxLog.getSelectedIndex() == 0)
          ALLOWLOG = "simple";
        else
          ALLOWLOG = "full";
      }
      else
        ALLOWLOG="no";
      if (user_SERVICE_PORT>0 && user_SERVICE_PORT<65535)
        SERVICE_PORT=String.valueOf(user_SERVICE_PORT);
      if (user_BUFFER_IN>0)
        BUFFER_IN=String.valueOf(user_BUFFER_IN);
      if (user_BUFFER_OUT>0)
        BUFFER_OUT=String.valueOf(user_BUFFER_OUT);
      if (user_MAX_MATLAB_SESSIONS>0)
        MAX_MATLAB_SESSIONS=String.valueOf(user_MAX_MATLAB_SESSIONS);
      if (user_WD_JIM!=null)
        WD_JIM=user_WD_JIM;

      //Authorization
      String user_AUTBD=jTextFieldAuthenticateDB.getText();
      String user_AUTUSER=jTextFieldAuthenticateUser.getText();
      String user_AUTPWD=new String(jPasswordFieldAuthenticatePwd.getPassword());
      if (!jCheckBoxAuthenticate.isSelected())  AUT="no";
      if (user_AUTBD!=null)
        AUTBD=user_AUTBD;
      if (user_AUTUSER!=null)
        AUTUSER=user_AUTUSER;
      if (user_AUTPWD!=null)
        AUTPWD=user_AUTPWD;

      args[0]=SERVICE_PORT;
      args[1]=BUFFER_IN;
      args[2]=BUFFER_OUT;
      args[3]=MAX_MATLAB_SESSIONS;
      args[4]=WD_JIM;
      args[5]=ALLOWDOS;
      args[6]=ALLOWEXT;
      args[7]=ALLOWLOG;
      //Authorization
      args[8]=AUT;
      args[9]=AUTBD;
      args[10]=AUTUSER;
      args[11]=AUTPWD;
    }catch(NumberFormatException nfe){
      args[0]="2005";
      args[1]="1024";
      args[2]="1024";
      args[3]="5";
      args[4]="\\JimWD";
      args[5]="yes";
      args[6]="yes";
      args[7]="simple";
      args[8]="yes";
      args[9]="netlab";
      args[10]="root";
      args[11]="";
    }
    Jim.upServer(args);
    jButtonStop.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/stop.png")));
    jButtonStop.setEnabled(true);
    jButtonPlay.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/playoff.png")));

    jButtonPlay.setEnabled(false);
    jMenuItemStart.setEnabled(false);
    jMenuItemStop.setEnabled(true);

    jTextFieldServicePort.setEnabled(false);
    jTextFieldBufferIn.setEnabled(false);
    jTextFieldBufferOut.setEnabled(false);
    jComboBoxMaxMatlab.setEnabled(false);
    jTextFieldWorkDirectory.setEnabled(false);
    jCheckBoxAllowDOS.setEnabled(false);
    jCheckBoxAllowExternal.setEnabled(false);
    jCheckBoxAllowLog.setEnabled(false);
    jComboBoxLog.setEnabled(false);
    //Authorization
    jCheckBoxAuthenticate.setEnabled(false);
    jTextFieldAuthenticateDB.setEnabled(false);
    jTextFieldAuthenticateUser.setEnabled(false);
    jPasswordFieldAuthenticatePwd.setEnabled(false);
    jPasswordFieldAuthenticatePwd.setEditable(false);
  }

  void jButtonStop_actionPerformed(ActionEvent e) {
    Jim.downServer();
    jButtonPlay.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/play.png")));
    jButtonPlay.setEnabled(false);
    jButtonStop.setIcon(new ImageIcon(this.getClass().getResource("/es/uned/dia/interoperate/matlab/jim/stopoff.png")));
    jButtonStop.setEnabled(false);
    jButtonPlay.setEnabled(true);

    jMenuItemStart.setEnabled(true);
    jMenuItemStop.setEnabled(false);

    jTextFieldServicePort.setEnabled(true);
    jTextFieldBufferIn.setEnabled(true);
    jTextFieldBufferOut.setEnabled(true);
    jComboBoxMaxMatlab.setEnabled(true);
    jTextFieldWorkDirectory.setEnabled(true);
    jCheckBoxAllowDOS.setEnabled(true);
    jCheckBoxAllowExternal.setEnabled(true);
    jCheckBoxAllowLog.setEnabled(true);
    jComboBoxLog.setEnabled(true);
    jCheckBoxAuthenticate.setEnabled(true);

    if (jCheckBoxAuthenticate.isSelected()){
      jTextFieldAuthenticateDB.setEnabled(true);
      jTextFieldAuthenticateUser.setEnabled(true);
      jPasswordFieldAuthenticatePwd.setEnabled(true);
      jPasswordFieldAuthenticatePwd.setEditable(true);
    }else{
      jTextFieldAuthenticateDB.setEnabled(false);
      jTextFieldAuthenticateUser.setEnabled(false);
      jPasswordFieldAuthenticatePwd.setEnabled(false);
      jPasswordFieldAuthenticatePwd.setEditable(false);
    }
  }

  void jCheckBoxAllowLog_actionPerformed(ActionEvent e) {
    if (jCheckBoxAllowLog.isSelected())
      jComboBoxLog.setEnabled(true);
    else
      jComboBoxLog.setEnabled(false);
  }

  void jCheckBoxAuthenticate_actionPerformed(ActionEvent e) {
    if (jCheckBoxAuthenticate.isSelected()){
      jTextFieldAuthenticateDB.setEnabled(true);
      jTextFieldAuthenticateUser.setEnabled(true);
      jPasswordFieldAuthenticatePwd.setEnabled(true);
      jPasswordFieldAuthenticatePwd.setEditable(true);
    }else{
      jTextFieldAuthenticateDB.setEnabled(false);
      jTextFieldAuthenticateUser.setEnabled(false);
      jPasswordFieldAuthenticatePwd.setEnabled(false);
      jPasswordFieldAuthenticatePwd.setEditable(false);
    }
  }

  void jMenuItemDefault_actionPerformed(ActionEvent e) {

    jButtonStop_actionPerformed(e);
    jTabbedPane1.setSelectedIndex(1);

    jTextFieldServicePort.setText("2005");
    jTextFieldBufferOut.setText("1024");
    jTextFieldWorkDirectory.setText("\\JimWD");
    jCheckBoxAllowExternal.setSelected(true);
    jTextFieldBufferIn.setText("1024");
    jCheckBoxAllowLog.setSelected(true);
    jComboBoxMaxMatlab.setEnabled(true);
    jComboBoxMaxMatlab.setSelectedIndex(4);
    jComboBoxLog.setSelectedIndex(0);
    jCheckBoxAllowDOS.setSelected(false);

    //Authorization
    jCheckBoxAuthenticate.setSelected(true);
    jTextFieldAuthenticateDB.setText("netlab");
    jTextFieldAuthenticateUser.setText("root");
    jPasswordFieldAuthenticatePwd.setText("");
    jTextFieldAuthenticateDB.setEnabled(true);
    jTextFieldAuthenticateUser.setEnabled(true);
    jPasswordFieldAuthenticatePwd.setEnabled(true);
    jPasswordFieldAuthenticatePwd.setEditable(true);
  }

  void jMenuItemEdit_actionPerformed(ActionEvent e) {
    jButtonStop_actionPerformed(e);
    jTabbedPane1.setSelectedIndex(1);
  }
}

class Marco_jMenuFileExit_ActionAdapter implements ActionListener {
  Marco adaptee;
  Marco_jMenuFileExit_ActionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuFileExit_actionPerformed(e);
  }
}

class Marco_jMenuHelpAbout_ActionAdapter implements ActionListener {
  Marco adaptee;
  Marco_jMenuHelpAbout_ActionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuHelpAbout_actionPerformed(e);
  }
}

class Marco_jButtonPlay_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jButtonPlay_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonPlay_actionPerformed(e);
  }
}

class Marco_jButtonStop_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jButtonStop_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jButtonStop_actionPerformed(e);
  }
}

class Marco_jMenuItemStart_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jMenuItemStart_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemStart_actionPerformed(e);
  }
}

class Marco_jMenuItemStop_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jMenuItemStop_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemStop_actionPerformed(e);
  }
}

class Marco_jMenuItemClearLog_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jMenuItemClearLog_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemClearLog_actionPerformed(e);
  }
}

class Marco_jCheckBoxAllowLog_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jCheckBoxAllowLog_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jCheckBoxAllowLog_actionPerformed(e);
  }
}

//authorization
class Marco_jCheckBoxAuthenticate_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jCheckBoxAuthenticate_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jCheckBoxAuthenticate_actionPerformed(e);
  }
}

class Marco_jMenuItemDefault_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jMenuItemDefault_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemDefault_actionPerformed(e);
  }
}

class Marco_jMenuItemEdit_actionAdapter implements java.awt.event.ActionListener {
  Marco adaptee;
  Marco_jMenuItemEdit_actionAdapter(Marco adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuItemEdit_actionPerformed(e);
  }
}
