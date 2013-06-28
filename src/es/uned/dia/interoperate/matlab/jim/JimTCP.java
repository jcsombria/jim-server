package es.uned.dia.interoperate.matlab.jim;

import java.net.*;
import java.io.*;
import java.util.*;
import java.text.*;

/**
 * JIM Server
 * @author <a href="mailto:gfarias@bec.uned.es">Gonzalo Farias</a>
 * @version 1.1
 */
public class JimTCP extends java.lang.Thread
{
  static int SERVICE_PORT, BUFFER_IN, BUFFER_OUT;
  static int MAX_MATLAB_SESSIONS;
  static jimMonitor jM=new jimMonitor();
  static String WD_JIM;
  static boolean allowOS,allowExt;
  static String allowLog;
  sessionMonitor sM;
  static ServerSocket server = null;
  Socket socket = null;

  //Authorization
  static boolean aut;
  static String autBD, autUser, autPwd;
  static UpdateAccessList updateTable = null;

  //***********************************************************
   //			 main
   //***********************************************************

    public JimTCP(String args[]) {

      Date now = new Date();
      DateFormat df = DateFormat.getDateTimeInstance();
      String sNow = df.format(now);


      //Get Initial Configuration
      switch (args.length) {
        case 1:
          SERVICE_PORT = new Integer(args[0]).intValue();
          break;
        case 2:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          break;
        case 3:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          break;
        case 4:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          break;
        case 5:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          WD_JIM= args[4];
          break;
        case 6:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          WD_JIM= args[4];
          if (args[5].compareToIgnoreCase("yes")==0)
            allowOS=true;
          else
            allowOS=false;
          break;
        case 7:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          WD_JIM= args[4];
          if (args[5].compareToIgnoreCase("yes")==0)
            allowOS=true;
          else
            allowOS=false;
          if (args[6].compareToIgnoreCase("yes")==0)
            allowExt=true;
          else
            allowExt=false;
          break;
        case 8:
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          WD_JIM= args[4];
          if (args[5].compareToIgnoreCase("yes")==0)
            allowOS=true;
          else
            allowOS=false;
          if (args[6].compareToIgnoreCase("yes")==0)
            allowExt=true;
          else
            allowExt=false;
          allowLog=args[7];      // no - simple - full
          break;
        case 12: //Authorization
          SERVICE_PORT = new Integer(args[0]).intValue();
          BUFFER_IN = new Integer(args[1]).intValue();
          BUFFER_OUT = new Integer(args[2]).intValue();
          MAX_MATLAB_SESSIONS = new Integer(args[3]).intValue();
          WD_JIM= args[4];
          if (args[5].compareToIgnoreCase("yes")==0)
            allowOS=true;
          else
            allowOS=false;
          if (args[6].compareToIgnoreCase("yes")==0)
            allowExt=true;
          else
            allowExt=false;
          allowLog=args[7];      // no - simple - full
          if (args[8].compareToIgnoreCase("yes")==0)
            aut=true;
          else
            aut=false;
          autBD=args[9];
          autUser=args[10];
          autPwd=args[11];
          break;
        default:
          SERVICE_PORT = 2005;
          BUFFER_IN = 1024;
          BUFFER_OUT = 1024;
          MAX_MATLAB_SESSIONS = 5;
          WD_JIM="\\JimWD";
          allowOS=true;
          allowExt=true;
          allowLog="simple";

          //Authorization
          aut=true;
          autBD="netlab";
          autUser="root";
          autPwd="";
          break;
      }

      sM = new sessionMonitor(MAX_MATLAB_SESSIONS);
      try {
        server = new ServerSocket(SERVICE_PORT);
        Marco.messageArea("Starting Server on "+sNow);
      }
      catch (IOException ioe) {
        if (allowLog.compareTo("no")!=0)
          Marco.messageArea("error:"+ioe);
        server = null;
        socket=null;
      }
      catch (Exception e) {
        server = null;
          socket=null;
      }

      //Authorization
      if (aut){
        updateTable = new UpdateAccessList(autBD, autUser, autPwd, 20000);
        updateTable.start(); //authentication
      }
    }


      public static void stopUpdate(){
          updateTable.stopUpdateAccessList();
    }


public boolean serverStarted(){
  return(server!=null);
}

public void run() {
  while(jM.stateServer()){
  //    while(Jim.stateServer()){
  try {
    socket = server.accept();
    //if (!Jim.stateServer())
    if (!jM.stateServer())
          break;
        if (sM.addSession()) {
          jimThread jT = new jimThread(socket, BUFFER_IN, BUFFER_OUT, sM,WD_JIM,allowOS,allowExt,allowLog,aut,autBD,autUser,autPwd);

          jT.start();
          Marco.userCounter(sM.getNumberOfSessions());

          Date nowUser = new Date();
          DateFormat dfUser = DateFormat.getDateTimeInstance();
          String sNowUser = dfUser.format(nowUser);

          if (allowLog.compareTo("no")!=0)
            Marco.messageArea("New user: "+socket.getInetAddress().toString().substring(1)+" at "+ sNowUser);
          //sM.getNumberOfSessions();
        }
        else {
          if (allowLog.compareTo("no")!=0)
            Marco.messageArea("Only " + MAX_MATLAB_SESSIONS +
                              " Matlab sessions are allowed");
        }

      }catch (IOException ioe) {
        Marco.messageArea("I/O Error - Could not start server: " + ioe);
        break;
      }
      catch (Exception e) {
        Marco.messageArea("Error:" + e);
        break;
      }
    }// end while

    try{
      socket.close();
      server.close();
    }catch(IOException ioe){
    }
    Marco.messageArea("Server stopped.");
    server = null;
    socket=null;
    //Authorization
    if (aut)  updateTable.stopUpdateAccessList();
  }

} // end class
