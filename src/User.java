import java.util.*;
import java.io.Serializable;

//look up serialization in order to make a file for each instance of user to save each user's info
//user with name password and list of their appointments
public class User implements Serializable
{
	//default serial version id
	private static final long serialVersionUID = 1L;

	private String username;
	private String password; 
	private List<Appointment> appointments;


	public User(String username, String password)
	{
		this.username = username;
		this.password = password; 

		appointments = new ArrayList<Appointment>();
	}

	public String getName()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public List<Appointment> getAppointments()
	{
		return appointments;
	}

	public void addToAppointments(Appointment newAppoint)
	{
		appointments.add(newAppoint);
	}

	public void removeFromAppointments(Appointment newAppoint)
	{
		appointments.remove(newAppoint);
	}

	public int getNumAppointments()
	{
		return appointments.size();
	}
}
