package es.uned.dia.interoperate.matlab.jim;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.text.*;
import jmatlink.*;

import java.sql.*; //Authentication

public class jimThread extends java.lang.Thread {
  public jimThread() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private boolean sincronismo = true;
  private String systemAS;
  private int stepsAS;
  private Socket sessionSocket;
  static JMatLink eng=null;
  
  //static JMatLink eng = new JMatLink();
  private int BUFFER_IN, BUFFER_OUT;
  private sessionMonitor sM;
  private int EjsId;
  private String RemoteFunction;
  private DataOutputStream bufferOutput;
  private DataInputStream bufferInput;
  private long matlabId;
  boolean theFirstTime = true;
  String[] outvarsStepAS;
  int[][] classvarSim = null;
  int PACKAGE_SIZE = 1;
  String commandUpdate = "";
  static String homeDir = "";
  static boolean allowExt,allowDOS,allowLogFull,allowLogSimple;
  String wdJim="";
  String evalBefore = "";

  //Authorization
  static boolean aut;
  static String autBD, autUser, autPwd;
  java.util.Date tiempoinicio = null; //Authentication
  java.util.Date tiempotermino = null; //Authentication
  long  remainingTime=0;

  //***********************************************************
   //			constructor
   //***********************************************************
  public jimThread(Socket s, int bi, int bo, sessionMonitor sm,String wd,boolean aDOS,boolean aExt,String aLog, boolean _aut, String _autBD,String _autUser, String _autPwd) {
    wdJim=wd;
    if (wdJim.startsWith("\\"))
      wdJim=wdJim.substring(1,wdJim.length());
    sessionSocket = s;
    BUFFER_IN = bi;
    BUFFER_OUT = bo;
    sM = sm;
    allowExt=aExt;
    allowDOS=aDOS;
    allowLogFull=(aLog.compareToIgnoreCase("full")==0);
    allowLogSimple=(aLog.compareToIgnoreCase("simple")==0);
    //Authorization
    aut=_aut;
    autBD=_autBD;
    autUser=_autUser;
    autPwd=_autPwd;
    
    if (eng==null)
    {
      //Get JMatLink
      lookForJMatLink();
    }
  }
  
  private void lookForJMatLink(){
    java.net.URI uri=null;
    //java.net.URI uribase=null;
    //java.net.URI urijar=null;
    
    //get jimserver.jar
    String pathOfJimc=getJimjar();         
    //get jmatlink
    String pathOfJmatlink=getJmatlink(pathOfJimc);  
    
    try{ 
      uri = new java.net.URI("file:/"+pathOfJmatlink+"JMatlink.dll");
      //uribase = new java.net.URI(getBaseDirectory());
      //urijar = new java.net.URI(pathOfJimc);
    }
    catch(java.net.URISyntaxException e){ System.out.println(e);}              
    File efile = new File(uri);   
    //File efilebase= new File(uribase);  
    //File efilejar=new File(urijar);
    eng = new JMatLink(efile.toString());
  }
  
  protected  String getBaseDirectory(){
    String pathOfJimc2= this.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
    pathOfJimc2=pathOfJimc2.substring(0,pathOfJimc2.lastIndexOf("/"));  
    return pathOfJimc2;
  } 

  
  private  String getJimjar(){  
    String temporalDirectory=null;
    try{         
      File checkfile = new File(new java.net.URI(getBaseDirectory()+"/jimserver.jar")); 
      if (checkfile.exists()) return getBaseDirectory();
      temporalDirectory=createTemporalDirectory();
      //lookfor jimc.jar 
      JarFile jarbase = new JarFile(new File (this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));
      JarEntry entry1;        
      for (Enumeration<JarEntry> e = jarbase.entries(); e.hasMoreElements(); ) {
        entry1 = (JarEntry) e.nextElement();
        if (entry1.getName().endsWith("jimserver.jar")) {                          
          //get jimc.jar          
          File file = new File(new java.net.URI(temporalDirectory+"/jimserver.jar"));       
          if (!file.exists()){              
            file.createNewFile();           
            InputStream in = this.getClass().getResourceAsStream("/"+entry1.getName());                      
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[1048];
            while(in.available() > 0) {
              int read = in.read(buffer);
              out.write(buffer, 0, read);
            }
            out.close();
            file.deleteOnExit();          
          }            
          break;            
        }
      }
    }
    catch (java.io.IOException e){System.out.println("Error:"+e);} 
    catch (Exception e) { e.printStackTrace(); }
    return temporalDirectory;   
  }

  private String createTemporalDirectory() throws java.net.URISyntaxException {
    String temp_dir_name=null;
    try{
      File temp_file= File.createTempFile("_jim",null,new File(new java.net.URI(getBaseDirectory())));              
      temp_dir_name=temp_file.getName();
      temp_file.delete();                                                                 
      File temp_dir = new File(new java.net.URI(getBaseDirectory()+"/"+temp_dir_name));           
      temp_dir.mkdir();       
      temp_dir.deleteOnExit();
    }catch (java.io.IOException e){System.out.println("Error:"+e);} 
    catch (Exception e) { e.printStackTrace(); }

    return (getBaseDirectory()+"/"+temp_dir_name);
  }
  
  private String getJmatlink(String _pathOfJimc){
    try{
      //get JMatlink.dll                
      JarFile jar = new JarFile(new File(new java.net.URI(_pathOfJimc+"/jimserver.jar")));             
      ZipEntry entry2 = jar.getEntry("jmatlink/JMatLink.dll");          
      File efile = new File(new java.net.URI("file:/"+getTemporalSystemDirectory().replace('\\','/')+"JMatlink.dll"));          
      
      System.out.println(efile);
      
      InputStream in = new BufferedInputStream(jar.getInputStream(entry2));
      OutputStream out = new BufferedOutputStream(new FileOutputStream(efile));     
      byte[] buffer = new byte[2048];
      for (;;)  {
        int nBytes = in.read(buffer);
        if (nBytes <= 0) break;
        out.write(buffer, 0, nBytes);
      }          
      out.flush();
      out.close();
      in.close(); 
      efile.deleteOnExit();   
    }
    catch (java.io.IOException e){System.out.println("Error:"+e);} 
    catch (Exception e) { e.printStackTrace(); }
    return getTemporalSystemDirectory().replace('\\','/');
  }
  
  private String getTemporalSystemDirectory(){        
    String tempdir = System.getProperty("java.io.tmpdir");
    return tempdir;
  }
  
  //***********************************************************
   //			Authentication Methods
   //***********************************************************

private ResultSet readAccessListTable () {

      ResultSet rs = null;

      try {
        String driver = "com.mysql.jdbc.Driver";
        String direccion = "jdbc:mysql://localhost:3306/"+autBD;
        String user = autUser;
        String password = autPwd;
        String query = "SELECT * FROM accesslist WHERE valid='true' ORDER BY starttime;";
        Class.forName(driver).newInstance();
        Connection conexion = DriverManager.getConnection(direccion, user, password);
        Statement s = conexion.createStatement();
        rs = s.executeQuery(query);
        //rs.close();
        //s.close(); //comentado por gonzalo
        //conexion.close(); //comentado por gonzalo

      }catch(Exception ex){
        System.out.println(ex.toString());
      }

      return rs;
  }

  private boolean checkUserPass(String username, String password){
    boolean result = false;
    ResultSet rs = readAccessListTable();

    if (rs != null){
        try{
          while (rs.next() && !result) {
            int id = rs.getInt("id");
            java.sql.Date startfecha = rs.getDate("starttime");
            java.sql.Time starthora = rs.getTime("starttime");

            java.sql.Date endfecha = rs.getDate("endtime");
            java.sql.Time endhora = rs.getTime("endtime");

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Calendar cal = java.util.Calendar.getInstance();

            try{
              cal.setTime(sdf.parse(startfecha+" "+starthora));
              tiempoinicio = cal.getTime();
              cal.setTime(sdf.parse(endfecha+" "+endhora));
              tiempotermino = cal.getTime();
            }catch (ParseException e) {}

            String usuario = rs.getString("username");
            String clave = rs.getString("password");
            result = usuario.equals(username) & clave.equals(password);
          }
        }catch(java.sql.SQLException sqle){
          System.out.println(sqle.getMessage());
        }
      }
      return result;
    }

  private boolean checkTimeSlot(java.util.Date starttime, java.util.Date endtime){
      System.out.println("inicio:"+starttime);
      System.out.println("fin:"+endtime);
      boolean result = false;
      long localTime = System.currentTimeMillis();
      long start = starttime.getTime();
      long end = endtime.getTime();

      if ((start < localTime) && ((localTime+60*1000) < end)){ // at least 1 minute.
        result = true;
      }

      return result;
    }



  //***********************************************************
   //			     	run
   //***********************************************************

    public synchronized void run() {

      try {
        sessionSocket.setTcpNoDelay(true);
        //sessionSocket.setSoTimeout(5 * 60 * 1000);
        sessionSocket.setSoTimeout(0);

        InputStream inStream = sessionSocket.getInputStream();
        OutputStream outStream = sessionSocket.getOutputStream();

        BufferedInputStream bis = new BufferedInputStream(inStream, BUFFER_IN);
        BufferedOutputStream bos = new BufferedOutputStream(outStream, BUFFER_OUT);

        bufferOutput = new DataOutputStream(bos);
        bufferInput = new DataInputStream(bis);

        //Configure Buffers
        bufferOutput.writeInt(BUFFER_IN);
        bufferOutput.writeInt(BUFFER_OUT);
        bufferOutput.flush();

        //Authentication
        String user,pass;
        int result = 2;//user and pass OK
        long rtime=0;

        //Inform to Client if any authorization is required
        bufferOutput.writeBoolean(aut);
        bufferOutput.flush();

        if (aut) {
          user = (String) bufferInput.readUTF();
          pass = (String) bufferInput.readUTF();
          result=0; //user and pass wrong
          if (checkUserPass(user, pass)){
            if (checkTimeSlot(tiempoinicio, tiempotermino)){
              result = 2;//user ok
              rtime=tiempotermino.getTime()-System.currentTimeMillis();
            }else{
              result = 1;// no match with timeslot
            }
          }
          bufferOutput.writeInt(result);
          bufferOutput.writeLong(rtime);
          bufferOutput.flush();

          if (result !=2){ //authentication error, exit!
            Marco.messageArea("User: "+user+" does not authorized");
            bufferOutput.close();
            bufferInput.close();
            sessionSocket.close();
            if (allowLogSimple || allowLogFull){
              java.util.Date nowUser = new java.util.Date();
              DateFormat dfUser = DateFormat.getDateTimeInstance();
              String sNowUser = dfUser.format(nowUser);
              Marco.messageArea("Out user: "+sessionSocket.getInetAddress().toString().substring(1)+" at " + sNowUser);
            }
            if (!sM.subtractSession()) {
              if (allowLogFull)
                Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" ERROR it couldn't to substract session:");
            }
            Marco.userCounter(sM.getNumberOfSessions());
            return;
          }

          Marco.messageArea("User: "+user+" authorized");
        }
        else{ // no authentication is required
          user = "";;
          pass = "";
          result=2; //user and pass OK
          rtime=1;
        }
        matlabId=-1;        
        matlabId =  eng.engOpenSingleUse();                        
        eng.engEvalString(matlabId,
                          "try,mkdir('"+wdJim+"'),cd "+wdJim+",catch,cd "+wdJim+",end");

        //eng.engSetVisible(matlabId,false);
          
        
        if (homeDir == "") {
            //JMatlink based
            eng.engEvalString(matlabId, "engGetCharArrayD=double(pwd)");
            double[][] arrayD = eng.engGetArray(matlabId,"engGetCharArrayD"); //gonzalo 070410 double[][] arrayD = eng.engGetArray("engGetCharArrayD");
            eng.engEvalString(matlabId, "clear engGetCharArrayD");
            String str = "";
            String[] charArray = double2String(arrayD);

            //Convert String[] to String
            for (int i = 0; i < charArray.length; i++) {
              str = str + charArray[i];
            }

            homeDir = str;
            if (homeDir.endsWith(java.io.File.separator)) {
              ;
            }
            else {
              homeDir = homeDir + java.io.File.separator;
            }
            homeDir = homeDir.replace('\\', '/');
          }

          if (allowDOS){
            File dosFile = new File(homeDir + "dos.m");
            if (dosFile.exists()){
              dosFile.delete();
            }
            dosFile = new File(homeDir + "!.m");
            if (dosFile.exists()){
              dosFile.delete();
            }
            dosFile = new File(homeDir + "system.m");
            if (dosFile.exists()){
              dosFile.delete();
            }
          }
          else{
            String dosString="function out=dos(in) \n";
            dosString=dosString+"%created by Jim to avoid OS function execution \n";
            dosString=dosString+"disp('created by Jim to avoid OS function execution'); \n";
            dosString=dosString+"out=-1; \n";
            File dosFile = new File(homeDir + "dos.m");
            try {
              FileOutputStream dosFileOS = new FileOutputStream(dosFile);
              PrintStream out = new PrintStream(dosFileOS);
              out.println(dosString);
              out.close();
            }
            catch (Exception e) {
              if (allowLogFull)
                Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" Model writing error:" + e);
            }

            dosString="%created by Jim to avoid OS function execution \n";
            dosString=dosString+"disp('created by Jim to avoid OS function execution'); \n";
            dosFile = new File(homeDir + "!.m");
            try {
              FileOutputStream dosFileOS = new FileOutputStream(dosFile);
              PrintStream out = new PrintStream(dosFileOS);
              out.println(dosString);
              out.close();
            }
            catch (Exception e) {
              if (allowLogFull)
                Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" Model writing error:" + e);
            }

            dosString="function out=system(in) \n";
            dosString=dosString+"%created by Jim to avoid OS function execution \n";
            dosString=dosString+"disp('created by Jim to avoid OS function execution'); \n";
            dosString=dosString+"out=-1; \n";
            dosFile = new File(homeDir + "system.m");
            try {
              FileOutputStream dosFileOS = new FileOutputStream(dosFile);
              PrintStream out = new PrintStream(dosFileOS);
              out.println(dosString);
              out.close();
            }
            catch (Exception e) {
              if (allowLogFull)
                Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" Model writing error:" + e);
            }
          }

          RemoteFunction = "";
          while (jimMonitor.stateServer()) {
          if (sincronismo) {
            EjsId = bufferInput.readInt();
            RemoteFunction = (String) bufferInput.readUTF();

          }
          else {
            if (bufferInput.available() > 0) {
              bufferOutput.flush();
              EjsId = bufferInput.readInt();
              RemoteFunction = (String) bufferInput.readUTF();
              sincronismo = true;
            }
          }

          //Authorization
          if (aut) remainingTime=tiempotermino.getTime()-System.currentTimeMillis();
          else remainingTime=1;
          if (!RemoteFunction.equalsIgnoreCase("exit") && remainingTime>0) {
            matlabConnection();
          }
          else  {
            Marco.messageArea("Disconnecting user..waiting 8 seconds");
            try {
              Thread thread = Thread.currentThread();
              thread.sleep(8000);
            } catch (InterruptedException ie) { }
            bufferOutput.writeInt(1);
            bufferOutput.flush();
            Marco.messageArea("...user disconnected");
            break;
          }
        }
        bufferOutput.close();
        bufferInput.close();
        sessionSocket.close();
      }
      catch (IOException ioe) {
        if (allowLogFull)
            Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" I/O Exception:" + ioe);
      }
      catch (Exception e) {
        if (allowLogFull)
          Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" Exception:" + e);
      }     
      
      if (allowLogSimple || allowLogFull){
        java.util.Date nowUser = new java.util.Date();
        DateFormat dfUser = DateFormat.getDateTimeInstance();
        String sNowUser = dfUser.format(nowUser);
        Marco.messageArea("Out user: "+sessionSocket.getInetAddress().toString().substring(1)+" at " + sNowUser);
      }

      if (!sM.subtractSession()) {
        if (allowLogFull)
          Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" ERROR it couldn't to substract session:");
      }   

      eng.engEvalString(matlabId,
                        "try ,set_param(gcs,'SimulationCommand','stop') ,end");
      eng.engEvalString(matlabId, "try ,bdclose ('all') ,end");
      eng.engClose(matlabId);

      Marco.userCounter(sM.getNumberOfSessions());
    } // end run

  //***********************************************************
  //			matlabConnection
  //***********************************************************

    synchronized void matlabConnection() throws IOException, Exception {


        // --------
        //getRemainingTime
        // --------
        if (RemoteFunction.equalsIgnoreCase("getRemainingTime")) {
          bufferOutput.writeLong(remainingTime);
          bufferOutput.flush();
        }

      // -----------------------------
      // Getting an Image of the Model
      // -----------------------------
      if (RemoteFunction.equalsIgnoreCase("getModelImage")) {
        String modelname = (String)bufferInput.readUTF();
        int size = bufferInput.readInt();
        byte[] ImageData = new byte[size];
        File imageFile = new File(homeDir + modelname+".jpg");

        FileInputStream fileIn = new FileInputStream(imageFile);

        fileIn.read(ImageData);

        bufferOutput.write(ImageData);
        bufferOutput.flush();
        fileIn.close();

      }

      // ------------------
      // Is the Model here?
      // ------------------

      if (RemoteFunction.equalsIgnoreCase("remoteFileExist")) {

        String modelPath = (String) bufferInput.readUTF();

        eng.engEvalString(matlabId, "EjsFID=fopen('" + modelPath + "','r');");
        double EjsFID = eng.engGetScalar(matlabId, "EjsFID");

        if (EjsFID > 0) {
          bufferOutput.writeBoolean(true);
          eng.engEvalString(matlabId, "fclose(EjsFID),clear EjsFID");
        }
        else {
          bufferOutput.writeBoolean(false);
        }
        bufferOutput.flush();
      }

      // ----------------
      // Import the Model
      // ----------------

      if (RemoteFunction.equalsIgnoreCase("importModel") && allowExt) {

        String modelFile = (String) bufferInput.readUTF();
        int longitud = bufferInput.readInt();
        byte myarray[] = new byte[longitud];
        bufferInput.readFully(myarray);

        String IPremote = sessionSocket.getInetAddress().toString().substring(1).
            replace('.', '_');
       // String newModel = "Ejs_" + IPremote + "_" + modelFile;
        String newModel =  modelFile;
        File theFile = new File(homeDir + newModel);

        try {
          FileOutputStream theFileos = new FileOutputStream(theFile);
          DataOutputStream out = new DataOutputStream(theFileos);
          out.write(myarray);
          out.close();
        }
        catch (Exception e) {
          if (allowLogFull)
            Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" Model writing error:" + e);
        }
        ;

        bufferOutput.writeUTF(newModel);
        bufferOutput.flush();
      }

      // ----
      // outputBuffer
      // ----
       if (RemoteFunction.equalsIgnoreCase("outputBuffer")) {
         eng.engOutputBuffer(matlabId,10000);
       }


       // ----
       // getOutputBuffer
       // ----
        if (RemoteFunction.equalsIgnoreCase("getOutputBuffer")) {
          String outputBuffer = eng.engGetOutputBuffer(matlabId);
          bufferOutput.writeUTF(outputBuffer);
          bufferOutput.flush();
        }

        // ----
        // getFigure: Inspired in JMatlink
       // ----
        if (RemoteFunction.equalsIgnoreCase("getFigure")) {
          int figure=bufferInput.readInt();
          int dx=bufferInput.readInt();
          int dy=bufferInput.readInt();

          String tmpDirS    = System.getProperty("java.io.tmpdir");
          String imageFileS = tmpDirS + "jmatlink" + figure + "_" + ".png";

          // image creation
          eng.engEvalString(matlabId, "figure("+ figure +")");
          eng.engEvalString(matlabId, "set(gcf,'PaperUnits','inches')");
          eng.engEvalString(matlabId, "set(gcf,'PaperPosition',[0,0,"+ dx/100 +","+ dy/100 +"])");
          //engEvalString(matlabId,"print("+ figure +",'-djpeg100','-r100','" +imageFileS+ "')");
          eng.engEvalString(matlabId,"print("+ figure +",'-dpng','-r100','" +imageFileS+ "')"); //Gonzalo 070509

          File imageFile = new File(imageFileS);
          int size=(int)imageFile.length();
          byte[] ImageData = new byte[size];
          FileInputStream fileIn = new FileInputStream(imageFile);
          fileIn.read(ImageData);

          bufferOutput.writeInt(size);
          bufferOutput.write(ImageData);
          bufferOutput.flush();
          fileIn.close();
      }


      // ----
      // eval
      // ----
      if (RemoteFunction.equalsIgnoreCase("eval")) {
        String command = (String) bufferInput.readUTF();
        eng.engEvalString(matlabId, command);
        if (allowLogFull) {
          if (!evalBefore.equals(command))
            Marco.messageArea("User:" + sessionSocket.getInetAddress().toString().substring(1) +
                              " Function eval:" + command);
          evalBefore=command;
        }
      }

      // --------------
      // setValueString
      // --------------
      if (RemoteFunction.equalsIgnoreCase("setValueString")) {
        String variable = (String) bufferInput.readUTF();
        String valueString = (String) bufferInput.readUTF();
        eng.engEvalString(matlabId, variable + "='" + valueString + "'");
      }

      // --------------
      // setValueDouble
      // --------------
      if (RemoteFunction.equalsIgnoreCase("setValueDouble")) {

        String variable = (String) bufferInput.readUTF();
        double valueDouble = bufferInput.readDouble();
        eng.engPutArray(matlabId, variable, valueDouble);

      }

      // -----------
      // setValueInt
      // -----------
      if (RemoteFunction.equalsIgnoreCase("setValueInt")) {
        String variable = (String) bufferInput.readUTF();
        int valueInt = bufferInput.readInt();
        eng.engPutArray(matlabId, variable, valueInt);
      }

      // -----------
      // setValueD[]
      // -----------
      if (RemoteFunction.equalsIgnoreCase("setValueD[]")) {
        String variable = (String) bufferInput.readUTF();
        int _dim1 = bufferInput.readInt();
        double[] arrayDouble = new double[_dim1];
        for (int i = 0; i < _dim1; i++) {
          arrayDouble[i] = bufferInput.readDouble();
        }
        eng.engPutArray(matlabId, variable, arrayDouble);

      }

      // -------------
      // setValueD[][]
      // -------------
      if (RemoteFunction.equalsIgnoreCase("setValueD[][]")) {
        String variable = (String) bufferInput.readUTF();
        int _dim1 = bufferInput.readInt();
        int _dim2 = bufferInput.readInt();
        double[][] arrayDouble2 = new double[_dim1][_dim2];
        for (int i = 0; i < _dim1; i++) {
          for (int j = 0; j < _dim2; j++) {
            arrayDouble2[i][j] = bufferInput.readDouble();
          }
        }

        eng.engPutArray(matlabId, variable, arrayDouble2);
      }

      // ---------
      // getString
      // ---------
      if (RemoteFunction.equalsIgnoreCase("getString")) {
        String variable = (String) bufferInput.readUTF();

        //JMatlink based
        eng.engEvalString(matlabId, "EjsengGetCharArrayD=double(" + variable + ")");
        double[][] arrayD = eng.engGetArray(matlabId,"EjsengGetCharArrayD");
        eng.engEvalString(matlabId, "clear EjsengGetCharArrayD");
        String str = "";
        String[] charArray = double2String(arrayD);

        //Convert String[] to String
        for (int i = 0; i < charArray.length; i++) {
          str = str + charArray[i];
        }
        bufferOutput.writeUTF(str);
        bufferOutput.flush();
      }

      // --------
      //getDouble
      // --------
      if (RemoteFunction.equalsIgnoreCase("getDouble")) {
        String variable = (String) bufferInput.readUTF();
        double valueDouble = eng.engGetScalar(matlabId, variable);
        bufferOutput.writeDouble(valueDouble);
        bufferOutput.flush();
      }

      // --------------
      // getDoubleArray
      // --------------
      if (RemoteFunction.equalsIgnoreCase("getDoubleArray")) {
        String variable = (String) bufferInput.readUTF();
        double[][] vectorDouble = eng.engGetArray(matlabId, variable);

        if (vectorDouble[0].length == 1) {
          bufferOutput.writeInt(vectorDouble.length);
          for (int i = 0; i < vectorDouble.length; i++) {
            bufferOutput.writeDouble(vectorDouble[i][0]);
          }
        }
        else {
          bufferOutput.writeInt(vectorDouble[0].length);
          for (int i = 0; i < vectorDouble[0].length; i++) {
            bufferOutput.writeDouble(vectorDouble[0][i]);
          }
        }
        bufferOutput.flush();
      }

      // ----------------
      // getDoubleArray2D
      // ----------------
      if (RemoteFunction.equalsIgnoreCase("getDoubleArray2D")) {
        String variable = (String) bufferInput.readUTF();

        double[][] arrayDouble2 = eng.engGetArray(matlabId, variable);

        if (! (arrayDouble2.length <= 0 || arrayDouble2[0].length <= 0)) {

          bufferOutput.writeInt(arrayDouble2.length);
          bufferOutput.writeInt(arrayDouble2[0].length);
          for (int i = 0; i < arrayDouble2.length; i++) {
            for (int j = 0; j < arrayDouble2[0].length; j++) {
              bufferOutput.writeDouble(arrayDouble2[i][j]);
            }
          }
        }
        else {
          bufferOutput.writeInt(0);
          bufferOutput.writeInt(0);
        }

        bufferOutput.flush();
      }

      // ---------------
      // set packageSize
      // ---------------

      if (RemoteFunction.equalsIgnoreCase("packageSize")) {
        PACKAGE_SIZE = bufferInput.readInt();
      }

      // ---------------
      //   stepMatlabAS
      // ---------------
      if (RemoteFunction.equalsIgnoreCase("stepMatlabAS")) {

        //Def. Variables
        int stepsUpdate;

        String[] outvarsUpdate = null;
        int buffercount = 0;
        double[][] aux;
        int[][] classvarMat = null;
        String outputvarstring = null;

        //Get output vars
        outputvarstring = (String) bufferInput.readUTF();
        outvarsStepAS = outputvarstring.split(",");

        //Get Command
        commandUpdate = (String) bufferInput.readUTF();
        stepsAS = bufferInput.readInt();

        //Get Package Size
        PACKAGE_SIZE = bufferInput.readInt();

        if (allowLogFull)
            Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" stepMatlabAS"+" Command:"+commandUpdate);

        //Execute
        for (int i = 0; i < stepsAS; i++) {
          eng.engEvalString(matlabId, commandUpdate);
          //do {
          //	eng.engEvalString(matlabId,"Ejs_smlkstatus=strcmp(get_param('"+systemAS+"','SimulationStatus'),'paused')");
          //	valueDouble = eng.engGetScalar(matlabId,"Ejs_smlkstatus");
          //}while(valueDouble==0);
        }

        sincronismo = false;
        buffercount = 0;

        //Get Output Types
        classvarMat = new int[outvarsStepAS.length][3];
        for (int i = 0; i < outvarsStepAS.length; i++) {
          eng.engEvalString(matlabId,
                            "Ejs_engClassVarMat=[strcmp(class(" +
                            outvarsStepAS[i] + "),'char')+strcmp(class(" +
                            outvarsStepAS[i] + "),'double')*2,size(" +
                            outvarsStepAS[i] + ",1),size(" + outvarsStepAS[i] +
                            ",2)]");
          aux = eng.engGetArray(matlabId, "Ejs_engClassVarMat");
          classvarMat[i][0] = (int) aux[0][0];
          classvarMat[i][1] = (int) aux[0][1];
          classvarMat[i][2] = (int) aux[0][2];
        }

        do {
          //Output
          for (int i = 0; i < outvarsStepAS.length; i++) {

            switch (classvarMat[i][0]) {
              case 1: //get char Array

                //JMatlink based
                eng.engEvalString(matlabId,
                                  "Ejs_engGetCharArrayD=double(" +
                                  outvarsStepAS[i] + ")");
                double[][] arrayD = eng.engGetArray(matlabId,"Ejs_engGetCharArrayD");
                eng.engEvalString(matlabId, "clear Ejs_engGetCharArrayD");
                String str = "";
                String[] charArray = double2String(arrayD);

                //Convert String[] to String
                for (int k = 0; k < charArray.length; k++) {
                  str = str + charArray[k];
                }
                bufferOutput.writeUTF(str);
                break;

              case 2: //get double Array

                //double

                if ( (classvarMat[i][1] * classvarMat[i][2]) <= 1) {
                  double outdou = eng.engGetScalar(matlabId, outvarsStepAS[i]);
                  bufferOutput.writeDouble(outdou);
                }
                //double Array
                else if ( ( (classvarMat[i][1]) == 1) ||
                         ( (classvarMat[i][2]) == 1)) {
                  double[][] vectorDouble = eng.engGetArray(matlabId,
                      outvarsStepAS[i]);
                  if (vectorDouble[0].length == 1) {
                    bufferOutput.writeInt(vectorDouble.length);
                    for (int k = 0; k < vectorDouble.length; k++) {
                      bufferOutput.writeDouble(vectorDouble[k][0]);
                    }
                  }
                  else {
                    bufferOutput.writeInt(vectorDouble[0].length);
                    for (int k = 0; k < vectorDouble[0].length; k++) {
                      bufferOutput.writeDouble(vectorDouble[0][k]);
                    }
                  }
                }
                //double Matrix
                else {
                  double[][] arrayDouble2 = eng.engGetArray(matlabId,
                      outvarsStepAS[i]);
                  bufferOutput.writeInt(arrayDouble2.length);
                  bufferOutput.writeInt(arrayDouble2[0].length);
                  for (int k = 0; k < arrayDouble2.length; k++) {
                    for (int j = 0; j < arrayDouble2[0].length; j++) {
                      bufferOutput.writeDouble(arrayDouble2[k][j]);
                    }
                  }
                }
                break;

              default:
                bufferOutput.writeDouble( -1.0);
                break;
            } // end switch

          } // end for output

          buffercount++;
          //Send Package?
          if (buffercount >= PACKAGE_SIZE) {
            bufferOutput.flush();
            buffercount = 0;
          }

          //Execute
          for (int i = 0; i < stepsAS; i++) {
            eng.engEvalString(matlabId, commandUpdate);
            //do {
            //	eng.engEvalString(matlabId,"Ejs_smlkstatus=strcmp(get_param('"+systemAS+"','SimulationStatus'),'paused')");
            //	valueDouble = eng.engGetScalar(matlabId,"Ejs_smlkstatus");
            //}while(valueDouble==0);
          }

        }
        while (bufferInput.available() == 0 && jimMonitor.stateServer());

        bufferOutput.flush();
        buffercount = 0;
        sincronismo = true;
      }

      // -------------
      // Update Matlab
      // -------------
      if (RemoteFunction.equalsIgnoreCase("update")) {

        //Def. Variables
        int stepsUpdate;

        String[] outvarsUpdate;
        int buffercount = 0;
        double[][] aux;
        int[][] classvarMat;

        //Get Command
        commandUpdate = (String) bufferInput.readUTF();

        //Execute
        eng.engEvalString(matlabId, commandUpdate);
        sincronismo = false;
        buffercount = 0;

        //Get output vars
        String outputvarstring = (String) bufferInput.readUTF();
        outvarsUpdate = outputvarstring.split(",");

        //Get Steps
        stepsUpdate = bufferInput.readInt();

        //Get Output Types
        classvarMat = new int[outvarsUpdate.length][3];
        for (int i = 0; i < outvarsUpdate.length; i++) {
          eng.engEvalString(matlabId,
                            "Ejs_engClassVarMat=[strcmp(class(" +
                            outvarsUpdate[i] + "),'char')+strcmp(class(" +
                            outvarsUpdate[i] + "),'double')*2,size(" +
                            outvarsUpdate[i] + ",1),size(" + outvarsUpdate[i] +
                            ",2)]");
          aux = eng.engGetArray(matlabId, "Ejs_engClassVarMat");
          classvarMat[i][0] = (int) aux[0][0];
          classvarMat[i][1] = (int) aux[0][1];
          classvarMat[i][2] = (int) aux[0][2];
        }

        do {
          //Output
          for (int i = 0; i < outvarsUpdate.length; i++) {

            switch (classvarMat[i][0]) {
              case 1: //get char Array

                //JMatlink based
                eng.engEvalString(matlabId,
                                  "Ejs_engGetCharArrayD=double(" +
                                  outvarsUpdate[i] + ")");
                double[][] arrayD = eng.engGetArray(matlabId,"Ejs_engGetCharArrayD");
                eng.engEvalString(matlabId, "clear Ejs_engGetCharArrayD");
                String str = "";
                String[] charArray = double2String(arrayD);

                //Convert String[] to String
                for (int k = 0; k < charArray.length; k++) {
                  str = str + charArray[k];
                }
                bufferOutput.writeUTF(str);
                buffercount = buffercount + charArray.length * 4; //aprox.
                break;

              case 2: //get double Array

                //double

                if ( (classvarMat[i][1] * classvarMat[i][2]) <= 1) {
                  double outdou = eng.engGetScalar(matlabId, outvarsUpdate[i]);
                  bufferOutput.writeDouble(outdou);
                  buffercount = buffercount + 8;
                }
                //double Array
                else if ( ( (classvarMat[i][1]) == 1) ||
                         ( (classvarMat[i][2]) == 1)) {
                  double[][] vectorDouble = eng.engGetArray(matlabId,
                      outvarsUpdate[i]);
                  if (vectorDouble[0].length == 1) {
                    bufferOutput.writeInt(vectorDouble.length);
                    buffercount = buffercount + vectorDouble.length * 8;
                    for (int k = 0; k < vectorDouble.length; k++) {
                      bufferOutput.writeDouble(vectorDouble[k][0]);
                    }
                  }
                  else {
                    bufferOutput.writeInt(vectorDouble[0].length);
                    buffercount = buffercount + vectorDouble[0].length * 8;
                    for (int k = 0; k < vectorDouble[0].length; k++) {
                      bufferOutput.writeDouble(vectorDouble[0][k]);
                    }
                  }
                }
                //double Matrix
                else {
                  double[][] arrayDouble2 = eng.engGetArray(matlabId,
                      outvarsUpdate[i]);
                  bufferOutput.writeInt(arrayDouble2.length);
                  bufferOutput.writeInt(arrayDouble2[0].length);
                  for (int k = 0; k < arrayDouble2.length; k++) {
                    for (int j = 0; j < arrayDouble2[0].length; j++) {
                      bufferOutput.writeDouble(arrayDouble2[k][j]);
                    }
                  }
                  buffercount = buffercount +
                      arrayDouble2.length * arrayDouble2[0].length * 8;
                }
                break;

              default:
                bufferOutput.writeDouble( -1.0);
                buffercount = buffercount + 8;
                break;
            } // end switch

          } // end for output

          //Execute or Stop
          if (--stepsUpdate == 0) {
            sincronismo = true;
          }
          else {
            eng.engEvalString(matlabId, commandUpdate);

            //Send Package?
          }
          if (buffercount >= PACKAGE_SIZE) {
            bufferOutput.flush();
            buffercount = 0;
          }

        }
        while ( (!sincronismo) && (bufferInput.available() == 0));

        bufferOutput.flush();
        buffercount = 0;
        sincronismo = true;
      }

      // -----------------
      // haltupdate Matlab
      // -----------------

      if (RemoteFunction.equalsIgnoreCase("haltupdate")) {

        boolean _remove = bufferInput.readBoolean();

        byte[] inFirma = new byte[3];

        if (_remove) {

          inFirma[0] = bufferInput.readByte();
          inFirma[1] = bufferInput.readByte();
          inFirma[2] = bufferInput.readByte();
          bufferOutput.writeByte(inFirma[0]);
          bufferOutput.writeByte(inFirma[1]);
          bufferOutput.writeByte(inFirma[2]);
          bufferOutput.flush();
        }

      }

      // ++++++++++++++++++
      // Simulink Functions
      // ++++++++++++++++++


      // Synchronous Functions
      // ---------------------

      // ----
      // Step
      // ----

      if (RemoteFunction.equalsIgnoreCase("step")) {
        double valueDouble;

        String system = (String) bufferInput.readUTF();

        boolean _resetIC = bufferInput.readBoolean();

        boolean _resetParam = bufferInput.readBoolean();

        int steps = bufferInput.readInt();

        if (_resetIC) {
          eng.engEvalString(matlabId, "Ejs__ResetIC = 1 - Ejs__ResetIC");

        }
        if (_resetParam) {
          eng.engEvalString(matlabId,
                            "set_param('" + system + "','SimulationCommand','update')");

        }
        int waitForStatus=10;
        for (int i = 0; i < steps; i++) {
          eng.engEvalString(matlabId,
                            "set_param('" + system + "','SimulationCommand','continue')");
          do {
            waitForStatus--;
            eng.engEvalString(matlabId,
                              "smlkstatus=strcmpi(get_param('" + system +
                              "','SimulationStatus'),'paused')");
            valueDouble = eng.engGetScalar(matlabId, "smlkstatus");
          }
          while (valueDouble == 0 && waitForStatus>0);
        }

      }

      // Asynchronous Functions
      // ----------------------

      // ------
      // stepAS
      // ------

      if (RemoteFunction.equalsIgnoreCase("stepAS")) {

        //Def. Variables
        double valueDouble;
        String invar1, invar2;
        double indou1, indou2;

        int buffercount = 0;
        double[][] aux;
        String outputvarstring;

        if (theFirstTime) {
          if (allowLogFull)
            Marco.messageArea("User:"+sessionSocket.getInetAddress().toString().substring(1)+" stepAS");

          theFirstTime = false;
          //Get from WS variables to go back to EJS
//          eng.engEvalString(matlabId, "Ejs_var2back=char(variables.name(not(strcmp(variables.fromto,'Delete')))),Ejs_numVarsOut=size(Ejs_var2back,1)");
          eng.engEvalString(matlabId, "Ejs_var2back=char(variables.name((strcmpi(variables.fromto,'out')))),Ejs_numVarsOut=size(Ejs_var2back,1)");
          eng.engEvalString(matlabId, "Ejs_varComma='Ejs_time';");
          eng.engEvalString(matlabId, "for Ejs_i=1:Ejs_numVarsOut,Ejs_varComma=[Ejs_varComma ',' Ejs_var2back(Ejs_i,:)];,end");

          //JMatlink based
          eng.engEvalString(matlabId,
                            "Ejs_engGetCharArrayD=double(Ejs_varComma);");
          double[][] arrayD = eng.engGetArray(matlabId,"Ejs_engGetCharArrayD");
          eng.engEvalString(matlabId, "clear Ejs_engGetCharArrayD");
          String str = "";
          String[] charArray = double2String(arrayD);

          //Convert String[] to String
          for (int k = 0; k < charArray.length; k++) {
            str = str + charArray[k];
          }
          outputvarstring = str.trim();
          outvarsStepAS = outputvarstring.split(",");

          //Get Output Types
          classvarSim = new int[outvarsStepAS.length][3];
          for (int i = 0; i < outvarsStepAS.length; i++) {
            eng.engEvalString(matlabId,
                              "Ejs_engClassVarSim=[strcmp(class(" +
                              outvarsStepAS[i] + "),'char')+strcmp(class(" +
                              outvarsStepAS[i] + "),'double')*2,size(" +
                              outvarsStepAS[i] + ",1),size(" + outvarsStepAS[i] +
                              ",2)]");
            aux = eng.engGetArray(matlabId, "Ejs_engClassVarSim");
            classvarSim[i][0] = (int) aux[0][0];
            classvarSim[i][1] = (int) aux[0][1];
            classvarSim[i][2] = (int) aux[0][2];
          }
        }

        systemAS = (String) bufferInput.readUTF();

        //Set Start Time
        eng.engEvalString(matlabId,
                          "set_param ('" + systemAS + "', 'StartTime','Ejs_time')");

        boolean _resetIC = bufferInput.readBoolean();

        boolean _resetParam = bufferInput.readBoolean();

        stepsAS = bufferInput.readInt();

        //Get Package Size
        PACKAGE_SIZE = bufferInput.readInt();

        eng.engEvalString(matlabId,
                          "set_param('" + systemAS + "','SimulationCommand','stop')");

        if (_resetIC) {
          eng.engEvalString(matlabId, "Ejs__ResetIC = 1 - Ejs__ResetIC");
        }
        if (_resetParam) {
          eng.engEvalString(matlabId,
                            "set_param('" + systemAS + "','SimulationCommand','update')");

        }
        eng.engEvalString(matlabId,
                          "set_param('" + systemAS + "','SimulationCommand','start')");




        // Avanza un paso despues de resetear el modelo.
        eng.engEvalString(matlabId,"set_param('" + systemAS + "','SimulationCommand','continue')");

                   do {
                     eng.engEvalString(matlabId,
                                       "Ejs_smlkstatus=strcmpi(get_param('" + systemAS +
                                       "','SimulationStatus'),'paused')");
                     valueDouble = eng.engGetScalar(matlabId, "Ejs_smlkstatus");
                   }
                   while (valueDouble == 0);
        // Ahora ejecuta los pasos que le corresponden.

        int waitForStatus=10;
        do {
          for (int i = 0; i < stepsAS; i++) {
            waitForStatus--;
            eng.engEvalString(matlabId,
                              "set_param('" + systemAS + "','SimulationCommand','continue')");
            do {
              eng.engEvalString(matlabId,
                                "Ejs_smlkstatus=strcmpi(get_param('" + systemAS +
                                "','SimulationStatus'),'paused')");
              valueDouble = eng.engGetScalar(matlabId, "Ejs_smlkstatus");
            }
            while (valueDouble == 0 &&  waitForStatus>0);
          }
          //Output
          for (int i = 0; i < outvarsStepAS.length; i++) {
           // Marco.messageArea("outvarsStepAS[i]:"+outvarsStepAS[i]);

            switch (classvarSim[i][0]) {
              case 1: //get char Array

                //JMatlink based
                eng.engEvalString(matlabId,
                                  "Ejs_engGetCharArrayD=double(" +
                                  outvarsStepAS[i] + ")");
                double[][] arrayD = eng.engGetArray(matlabId,"Ejs_engGetCharArrayD");
                eng.engEvalString(matlabId, "clear Ejs_engGetCharArrayD");
                String str = "";
                String[] charArray = double2String(arrayD);

                //Convert String[] to String
                for (int k = 0; k < charArray.length; k++) {
                  str = str + charArray[k];
                }
                bufferOutput.writeUTF(str);
                break;

              case 2: //get double Array

                //double

                if ( (classvarSim[i][1] * classvarSim[i][2]) <= 1) {

                  double outdou = eng.engGetScalar(matlabId,
                      outvarsStepAS[i].trim());
                      // Marco.messageArea("outvarsStepAS[i].trim():"+outvarsStepAS[i].trim()+" outdou:"+outdou);
                  bufferOutput.writeDouble(outdou);

                }
                //double Array
                else if ( ( (classvarSim[i][1]) == 1) ||
                         ( (classvarSim[i][2]) == 1)) {
                  double[][] vectorDouble = eng.engGetArray(matlabId,
                      outvarsStepAS[i]);
                  if (vectorDouble[0].length == 1) {
                    bufferOutput.writeInt(vectorDouble.length);
                    for (int k = 0; k < vectorDouble.length; k++) {
                      bufferOutput.writeDouble(vectorDouble[k][0]);
                    }
                  }
                  else {
                    bufferOutput.writeInt(vectorDouble[0].length);
                    for (int k = 0; k < vectorDouble[0].length; k++) {
                      bufferOutput.writeDouble(vectorDouble[0][k]);
                    }
                  }
                }
                //double Matrix
                else {
                  double[][] arrayDouble2 = eng.engGetArray(matlabId,
                      outvarsStepAS[i]);
                  bufferOutput.writeInt(arrayDouble2.length);
                  bufferOutput.writeInt(arrayDouble2[0].length);
                  for (int k = 0; k < arrayDouble2.length; k++) {
                    for (int j = 0; j < arrayDouble2[0].length; j++) {
                      bufferOutput.writeDouble(arrayDouble2[k][j]);
                    }
                  }
                }
                break;

              default:
                bufferOutput.writeDouble( -1.0);
                break;
            } // end switch

          } // end for output

          buffercount++;

          //Send Package?
          if (buffercount >= PACKAGE_SIZE) {
            bufferOutput.flush();
            buffercount = 0;
          }

        }
        while ( (bufferInput.available() == 0) && jimMonitor.stateServer());
        bufferOutput.flush();
        buffercount = 0;
        sincronismo = true;

      }

      // ------------
      // startModelAS
      // ------------

      if (RemoteFunction.equalsIgnoreCase("startmodelAS")) {

        String invar1, invar2, outvar1, outvar2;
        double indou1, indou2, outdou1, outdou2;
        double timeini;

        String system = (String) bufferInput.readUTF();
        eng.engEvalString(matlabId,
                          "set_param('" + system + "','SimulationCommand','stop')");

        //tiempo final
        timeini = bufferInput.readDouble();

        //Variables de tipo param
        invar1 = (String) bufferInput.readUTF();
        indou1 = bufferInput.readDouble();
        invar2 = (String) bufferInput.readUTF();
        indou2 = bufferInput.readDouble();

        //Variables de estado
        outvar1 = (String) bufferInput.readUTF();
        outdou1 = bufferInput.readDouble();
        outvar2 = (String) bufferInput.readUTF();
        outdou2 = bufferInput.readDouble();

        eng.engPutArray(matlabId, "ini", timeini);
        eng.engPutArray(matlabId, invar1, indou1);
        eng.engPutArray(matlabId, invar2, indou2);
        eng.engPutArray(matlabId, outvar1, outdou1);
        eng.engPutArray(matlabId, outvar2, outdou2);

        eng.engEvalString(matlabId,
                          "set_param('" + system + "','SimulationCommand','start')");
      }

      // ----------
      // haltStepAS
      // ----------

      if (RemoteFunction.equalsIgnoreCase("haltStepAS")) {

        eng.engEvalString(matlabId,
                          "set_param(gcs,'SimulationCommand','stop')");
        //eng.engEvalString(matlabId,
        //                  "set_param('" + systemAS + "','SimulationCommand','stop')");


        boolean _remove = bufferInput.readBoolean();

        byte[] inFirma = new byte[3];

        if (_remove) {

          inFirma[0] = bufferInput.readByte();
          inFirma[1] = bufferInput.readByte();
          inFirma[2] = bufferInput.readByte();
          bufferOutput.writeByte(inFirma[0]);
          bufferOutput.writeByte(inFirma[1]);
          bufferOutput.writeByte(inFirma[2]);
          bufferOutput.flush();
        }

      }

    }

  //***********************************************************
   //						double2String
   //***********************************************************

    // JMatlink Utility
    // Convert an n*n double array to n*1 String vector
    private static String[] double2String(double[][] d) {
      String encodeS[] = new String[d.length]; // String vector

      // for all rows
      for (int n = 0; n < d.length; n++) {
        byte b[] = new byte[d[n].length];
        // convert row from double to byte
        for (int i = 0; i < d[n].length; i++) {
          b[i] = (byte) d[n][i];

          // convert byte to String
        }
        try {
          encodeS[n] = new String(b, "UTF8");
        }
        catch (UnsupportedEncodingException e) {}
      }
      return encodeS;
    } // end double2String

  private void jbInit() throws Exception {
  }
} // end class
