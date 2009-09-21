package javax.management.timer;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

public class Timer extends NotificationBroadcasterSupport implements MBeanRegistration {
	/**
     * Number of milliseconds in one second.
     * Useful constant for the <CODE>addNotification</CODE> method.
     */
    public static final long ONE_SECOND = 1000;

    /**
     * Number of milliseconds in one minute.
     * Useful constant for the <CODE>addNotification</CODE> method.
     */
    public static final long ONE_MINUTE = 60*ONE_SECOND;

    /**
     * Number of milliseconds in one hour.
     * Useful constant for the <CODE>addNotification</CODE> method.
     */
    public static final long ONE_HOUR   = 60*ONE_MINUTE;

    /**
     * Number of milliseconds in one day.
     * Useful constant for the <CODE>addNotification</CODE> method.
     */
    public static final long ONE_DAY    = 24*ONE_HOUR;

    /**
     * Number of milliseconds in one week.
     * Useful constant for the <CODE>addNotification</CODE> method.
     */
    public static final long ONE_WEEK   = 7*ONE_DAY;
    
	public void postDeregister() {
		// TODO Auto-generated method stub
		
	}

	public void postRegister(Boolean successful) {
		// TODO Auto-generated method stub
		
	}

	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub
		
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}