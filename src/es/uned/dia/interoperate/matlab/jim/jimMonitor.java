package es.uned.dia.interoperate.matlab.jim;

/**
 * JIM Server
 * @author <a href="mailto:gfarias@bec.uned.es">Gonzalo Farias</a>
 * @version 1.1
 */
public class jimMonitor {
  static boolean jimTCPUp;
  public jimMonitor() {
    jimTCPUp=false;
  }

  public static synchronized void upServer(){
    jimTCPUp=true;
  }

  public static synchronized void downServer(){
    jimTCPUp=false;
  }

  public static synchronized boolean stateServer(){
    return jimTCPUp;
  }
}
