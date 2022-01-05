import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//Program by Christopher Hoang
//Main program that acts as a hub or menu, allowing users to register/log in and check their appointments
// for primarily vaccines
public class VaccinePortal 
{
	public static void main (String[] args) throws ParseException
	{
		System.out.println("Welcome to your appointment portal!\n");
		
		User user = null;

		//Check if serialization file exists 
		File userInfo = new File("userinfo.ser");
		boolean exists = userInfo.exists();

		//if it does exist, skip registration, otherwise register the user
		if (exists == true)
		{
			//deserialization the file if it exists
			try
			{
				FileInputStream file = new FileInputStream("userinfo.ser");
				ObjectInputStream fileIn = new ObjectInputStream(file);

				user = (User)fileIn.readObject();
				file.close();
				fileIn.close();

			}

			catch (IOException e)
			{
				System.out.println("An IOException has been caught");
			}

			catch(ClassNotFoundException ex)
			{
				System.out.println("A ClassNotFoundException has been found - user info not found");
			}
		}
		else
		{
			user = register();
		}

		//print menu to show and navigate options user can do
		printMenu();
		Scanner scanner = new Scanner(System.in);
		String command = scanner.nextLine();

		while (!command.equalsIgnoreCase("q"))
		{
			if(command.equalsIgnoreCase("a"))
			{
				addAppoint(user);
			}
			else if (command.equalsIgnoreCase("d"))
			{
				display(user);
			}
			else if (command.equalsIgnoreCase("del"))
			{
				delAppoint(user);
			}
			else 
			{
				System.out.println("\n Please try entering a command again out of the options in the menu");
			}

			printMenu();
			command = scanner.nextLine();
			
		}

		//If there is a userInfo serialized file that already exists, delete and put an updated file
		//based on any menu actions of adding/deleting appointments
		if (userInfo.delete())
		{
			writeObjectToFile(user);
		}
	}

	/**
	 * For first time users, this method registers the user, and saves their information into a 
	 * config.properties file when it is done.
	 */
	public static User register()
	{
		System.out.println("Please provide a username:");
		System.out.println("Username: ");
		Scanner scanner = new Scanner(System.in);

		String username = scanner.nextLine();

		System.out.println("\nPlease set up a password as well");
		System.out.println("Password: ");
		String password = scanner.nextLine();

		//Use regular expression to check if password is strong
		//Checks if at least one uppercase, lowercase letter, and number
		String regex = ("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");

		Pattern pattern = Pattern.compile(regex);

		Matcher match = pattern.matcher(password);

		//Checks string against regex, asking for new password if it does not meet all requirements (or null)
		while (match.matches() == false || password == null)
		{
			if (password == null)
			{
				System.out.println("\nYou did not enter anything; please enter a password");
			}
			else
			{
				System.out.println("\nWe recommend the password have at least one lowercase," 
				+ "uppercase letter, as well as a number");
			}

			System.out.println("\nPlease try setting up a password again");
			System.out.println("Password: ");
			password = scanner.nextLine();
			match = pattern.matcher(password);

		}
		

		User newUser = new User(username, password);
		System.out.println("\nAlright, you're all set up");

		writeObjectToFile(newUser);

		return newUser;

	}

	/**
	 * Adds an appointment to the user's list. Has checks to ensure a proper date (no 32 days in Jan, 
	 * leap year days in Feb) as well as proper hours and minutes for time. Most of it is asking for
	 * a value again if it is invalid (61 minutes for a time as an example). Asks the user to confirm,
	 * if so add the appointment, otherwise reenter the information or quit.
	 * @param username
	 * @throws ParseException
	 */
	public static void addAppoint(User username) throws ParseException
	{
		System.out.println("Please enter the date for your new appointment");
		Scanner scanner = new Scanner (System.in);
		
		//Strings of AM or PM to be compared when asking for user wants AM or PM
		String am = "am";
		String pm = "pm";

		//string representations of date to be made as a Date object 
		String strDate ="";
		String strYear ="";
		String strMonth ="";
		String strDay ="";

		String confirmChanges = ""; //preview of appointment as a string
		boolean saved = false; //boolean expression for outer loop
		boolean isEqual; //boolean expression for checking am or pm loop

		while (!saved)
		{
			System.out.println("\nEnter the year of your appointment");
			int year = scanner.nextInt();

			//get year month and day
			while(year < 0)
			{
				System.out.println("\nEnter a valid year");
				year = scanner.nextInt();
			}

			System.out.println("\nEnter the month of your appointment as a number");
			int month = scanner.nextInt();

			while(month > 12 || month < 1)
			{
				System.out.println("\nEnter a valid month");
				month = scanner.nextInt();
			}

			System.out.println("\nEnter the day of your appointment");
			int day;

			boolean numDays = false;
			boolean isLeap = checkLeapYr(year);

			day = scanner.nextInt();

			//switch case for number of days based on the month and/or leap year
			while(numDays == false)
			{
				
				switch(month)
				{
					case 1:
					case 3:
					case 5:
					case 7: 
					case 8:
					case 10:
					case 12:
						if (day > 0 && day < 32)
						{
							numDays = true;
						}
						break;
					case 4:
					case 6:
					case 9:
					case 11:
						if (day > 0 && day < 31)
						{
							numDays = true;
						}
						break;
					case 2:
						if (isLeap == true)
						{
							if (day > 0 && day < 30)
							{
								numDays = true;
							}
						}
						else 
						{
							if (day > 0 && day < 29)
							{
								numDays = true;
							}
						}
						break;

					default:
						break;
				}
				if (numDays == true)
				{
					break;
				}

				System.out.println("\nEnter a valid day of the month");
				day = scanner.nextInt();
			}

			//Format date as a string, then format it into Date object
			strYear = Integer.toString(year);
			strMonth = Integer.toString(month);
			strDay = Integer.toString(day);

			strDate = strMonth + "-" + strDay + "-" + strYear;
			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date date = formatter.parse(strDate);

			//ask for hour and minutes of the time
			System.out.println("\nNow enter the time of the appointment");
			System.out.println("\nEnter the hour");
			int hour = scanner.nextInt();
			while(hour < 1 && hour > 12)
			{
				System.out.println("\nEnter a valid hour, from 1-12");
				hour = scanner.nextInt();
			}

			System.out.println("\nEnter the minute section of the time of the appointment");
			int min = scanner.nextInt();

			while(min < 0 && min > 59)
			{
				System.out.println("\nEnter a valid minute time, from 0-59 minutes");
				min = scanner.nextInt();
			}

			//Ask for AM or PM
			System.out.println("\nEnter whether the time is in AM or PM, case does not matter");
			scanner.nextLine();
			String AMorPM = scanner.nextLine();
			isEqual = false;
			
			while(!isEqual)
			{
				if(AMorPM.equalsIgnoreCase(am) || AMorPM.equalsIgnoreCase(pm))
				{
					isEqual = true;
				}
				else
				{
					System.out.println("\nEnter AM or PM properly, case does not matter");
					AMorPM = scanner.nextLine();
				}
			}
				
			

			System.out.println("\nAlright does everything look good? press y to confirm, press n to"
								+ " deny");
			System.out.println(date + " " + hour + ":" + min + AMorPM);
			confirmChanges = scanner.nextLine();
			if (confirmChanges.equalsIgnoreCase("y"))
			{
				saved = true;
				Appointment newAppointment = new Appointment(date, hour, min, AMorPM);
				username.addToAppointments(newAppointment);

				System.out.println("\nAppointment added!");
			}
			else if (confirmChanges.equalsIgnoreCase("n"))
			{
				System.out.println("\nWould you like to reenter your information or quit out?");
				System.out.println("Press r to reenter, q to quit out");
				String checkQuit = scanner.nextLine();
				if (checkQuit.equalsIgnoreCase("r"))
				{
					System.out.println("\nOK please enter your appointment information again");
				}
				else if(checkQuit.equalsIgnoreCase("q"))
				{
					System.out.println("\nQuitted out of adding an appointment");
					return;
				}
			
			}
			
			
		}
		
	}

	/**
	 * Deletes an appointment from a user's list of appointments. Checks if there's any appointments
	 * to delete, and if there is, delete the specified one
	 * @param user The user with its respective list of appointments
	 */
	public static void delAppoint(User user)
	{
		if (user.getNumAppointments() != 0)
		{
			List<Appointment> appointments = user.getAppointments();
			display(user);
			System.out.println("\nWhich appointment would you like to delete?");
			System.out.println("Select the number next to the appointment you would like to delete:");

			Scanner scanner = new Scanner(System.in);
			int which = scanner.nextInt();
			while (which < 1 || which > appointments.size())
			{
				System.out.println("\nEnter a valid appointment value");
				which = scanner.nextInt();
			}

			Appointment appointToDel = appointments.get(which - 1);
			user.removeFromAppointments(appointToDel);

			System.out.println("\n Appointment " + which + " deleted");
		}
		else
		{
			System.out.println("\nThere are no appointments to delete");
		}

	}

	/**
	 * Display all the appointments a user has 
	 * @param user User with list of appointments
	 */
	public static void display(User user)
	{
		if (user.getNumAppointments() != 0)
		{
			List<Appointment> appointments = user.getAppointments();
			System.out.println("\n Your appointments:");

			int counter;
			for(int i = 0; i < appointments.size(); i++)
			{
				counter = i + 1;
				System.out.println(counter + " - " + appointments.get(i).toString());
			}
			System.out.println();
			}
		else
		{
			System.out.println("\nhere are no appointments to display");
		}

		
	}


	/**
	 * Method to print the menu of options for the portal
	 */
	public static void printMenu()
	{
		System.out.println("\n****************************************");
		System.out.println("Enter a letter to navigate the appointment portal");
		System.out.println("a/A - Add appointment");
		System.out.println("del/DEL - Delete appointment");
		System.out.println("d/D - Display all appointments including date and time");
		System.out.println("q/Q - quit");
		System.out.println("****************************************\n");
	}

	/**
	 * Helper method to check leap year. To verify a valid amount of days in a month depending on the 
	 * year
	 * @param Year year to check if it is a leap year
	 * @return True if leap year, false otherwise
	 */
	public static boolean checkLeapYr(int year)
	{
		boolean isLeap = false;
		if(year % 4 == 0)
		{
			if (year % 100 == 0)
			{
				if(year % 400 == 0)
				{
					isLeap = true;
				}
				else
				{
					isLeap = false;
				}
			}
			
			else
			{
				isLeap = true;
			}
		}
		else 
		{
			isLeap = false;
		}

		return isLeap;
	}

	/**
	 * A non secure attempt at saving a user's information, including their username, password,
	 * and their appointments. Written by serialization
	 * @param user The user object being serialized
	 */
	public static void writeObjectToFile(User user)
	{
		try
		{
			FileOutputStream fileOutput = new FileOutputStream("userinfo.ser");
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOutput);
			objectOut.writeObject(user);
			objectOut.close();
			fileOutput.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
