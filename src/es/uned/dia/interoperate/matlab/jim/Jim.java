package es.uned.dia.interoperate.matlab.jim;

import javax.swing.UIManager;
import java.awt.*;
import java.net.*;
import java.io.*;

/**
 * JIM Server
 * Server to support the remote MATLAB interaction
 * @author <a href="mailto:gfarias@bec.uned.es">Gonzalo Farias</a>
 * @version 1.1
 */
public class Jim {
  boolean packFrame = false;
  static JimTCP jimTCPServer=null;
  static String[]  jimArgs=new String[4];
  static jimMonitor jM=new jimMonitor();

  //Create the application
  public Jim() {
    Marco frame = new Marco();
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  /**
   * Main method
   */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new Jim();
  }

  /**
   * Starts the server
   */
  public static synchronized void upServer(String[] args){

    if (!jM.stateServer()){
      jimArgs = args;
      jimTCPServer = new JimTCP(jimArgs);
      if (jimTCPServer.serverStarted()){
        jM.upServer();
        jimTCPServer.start();
      }
    }
  }

  /**
   * Stops the server
   */
  public static synchronized void downServer(){
    if (jM.stateServer()){
      jM.downServer();
      try {
        Socket socket = new java.net.Socket("localhost",new Integer(jimArgs[0]).intValue());
        jimTCPServer.join();
        jimTCPServer = null;
      }
      catch (IOException ioe) {
        Marco.messageArea("Error " + ioe);
      }
      catch (Exception e2) {
        Marco.messageArea("Error " + e2);
      }
    }
  }
}
