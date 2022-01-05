import java.util.Date;
import java.io.Serializable;

//appointmnet class which lists the date and time including AM or PM

public class Appointment implements Serializable
{
//default serial version id
private static final long serialVersionUID = 1L;

	private Date date;
	private int hour;
	private int minute;
	private String AMPM;

	public Appointment(Date date, int hour, int minute, String AMPM)
	{
		this.date = date;
		this.hour = hour;
		this.minute = minute;
		this.AMPM = AMPM;
	}


	public Date getDate()
	{
		return date;
	}

	public int getHour()
	{
		return hour;
	}

	public int getMinute()
	{
		return minute;
	}

	public String getAMPM()
	{
		return AMPM;
	}

	public String toString()
	{
		String appointment = date + " " + hour + ":" + minute + AMPM;
		return appointment;
	}
}