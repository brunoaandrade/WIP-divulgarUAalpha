package pt.mydomain.rabbittester;

/**
 * Created by tjrs0_000 on 16/06/2015.
 */
public class Constants {

    public interface ACTION {
        public static String MAIN_ACTION = "pt.mydomain.services.action.main";
        public static String ACCEPT = "pt.mydomain.services.action.accept";
        public static String DENNY = "pt.mydomain.services.action.denny";
        public static String NEXT_ACTION = "pt.mydomain.services.action.next";
        public static String STARTFOREGROUND_ACTION = "pt.mydomain.services.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "pt.mydomain.services.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

}
