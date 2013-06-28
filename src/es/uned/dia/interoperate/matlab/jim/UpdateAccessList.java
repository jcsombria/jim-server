package es.uned.dia.interoperate.matlab.jim;

import java.sql.*;

/**
 * Class to update database for user authorization
 * @author <a href="mailto:hvargas@bec.uned.es">Hector Vargas</a>
 */
public class UpdateAccessList extends Thread{

    private int retardo;
    private boolean isRunning = true;
    private String autBD, autUser, autPwd;

    public UpdateAccessList(String _autBD, String _autUser, String _autPwd,int retardoms){
        this.retardo = retardoms;
        autBD=_autBD;
        autUser=_autUser;
        autPwd=_autPwd;
    }

    public void run(){

        while(isRunning){
          try {
              String driver = "com.mysql.jdbc.Driver";
              String direccion = "jdbc:mysql://localhost:3306/"+autBD;
              String user = autUser;
              String password = autPwd;
              String query = "SELECT * FROM accesslist WHERE valid='true' ORDER BY starttime;";
              Class.forName(driver).newInstance();
              Connection conexion = DriverManager.getConnection(direccion, user, password);
              Statement s = conexion.createStatement();
              ResultSet rs = s.executeQuery(query);

              while (rs.next()) {
                int id = rs.getInt("id");
                java.sql.Date endfecha = rs.getDate("endtime");
                java.sql.Time endhora = rs.getTime("endtime");

                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(sdf.parse(endfecha+" "+endhora));

                long localTime = System.currentTimeMillis();
                long endTime = (cal.getTime()).getTime();
                //             System.out.println("end time :"+endTime);
                //             System.out.println("localTime:"+localTime);

                if (rs.getBoolean("valid")){
                  if (localTime >= endTime){
                    String query1 = "UPDATE accesslist SET valid='false' WHERE id="+id+";";
                    s.executeUpdate(query1);
                  }else{
                    }
                  }
                }
              rs.close();
              s.close();
              conexion.close();

          }catch(Exception ex){
            System.out.println(ex.toString());
          }
          delay(retardo);
        }
    }

    public void stopUpdateAccessList(){
        isRunning = false;
    }

    private void delay(int mseconds) {
        try{
            Thread.sleep(mseconds);
        }catch (InterruptedException e) {
            System.out.println("Delay interrupted!");
        }
    }
}
