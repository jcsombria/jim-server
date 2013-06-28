package es.uned.dia.interoperate.matlab.jim;

public class sessionMonitor
{
        static int numberOfSessions;
        static int MaxSessions;

        public sessionMonitor(int MMS) {
                MaxSessions=MMS;
                numberOfSessions=0;
        }

        public static synchronized boolean addSession(){
                if (numberOfSessions<MaxSessions)
                {	numberOfSessions++;
                        return(true);
                }else
                        return(false);
        }

        public static synchronized boolean subtractSession(){
                if (numberOfSessions>0)
                {	numberOfSessions--;
                        return(true);
                }else
                        return(false);
        }

        public static int getNumberOfSessions(){
                return(numberOfSessions);
        }
}
