package model;
import java.util.*;
import customExceptions.*;
import java.time.*;
import java.io.*;

public class ClientService {

	/**
	 * Descripción de los atributos.
	 */
    private char letter;
	private int num;
    private ArrayList<User> users;
	private ArrayList<Turn> actualTurns;
	private ArrayList<Turn> attendedTurns;
	private ArrayList<TurnType> turnTypes;
	private model.Date systemDate;
	private model.Time systemTime;
	private LocalTime attentionTime;
	private boolean onWait;

    public ClientService() {
        letter = 'A';
		num = -1;
		users = new ArrayList<User>();
		actualTurns = new ArrayList<Turn>();
		attendedTurns = new ArrayList<Turn>();
		turnTypes = new ArrayList<TurnType>();
		systemDate = new model.Date();
		systemTime = new model.Time();
		attentionTime = null;
		onWait = false;
    }

    /**
	 * @return the letter
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * @param letter the letter to set
	 */
	public void setLetter(char letter) {
		this.letter = letter;
	}

	/**
	 * @return the number
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num the number to set
	 */
	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * @return the users
	 */
	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}

	/**
	 * @return the actualTurns
	 */
	public ArrayList<Turn> getActualTurns() {
		return actualTurns;
	}

	/**
	 * @param actualTurns the actualTurns to set
	 */
	public void setActualTurns(ArrayList<Turn> actualTurns) {
		this.actualTurns = actualTurns;
	}

	/**
	 * @return the attendedTurns
	 */
	public ArrayList<Turn> getAttendedTurns() {
		return attendedTurns;
	}

	/**
	 * @param attendedTurns the attendedTurns to set
	 */
	public void setAttendedTurns(ArrayList<Turn> attendedTurns) {
		this.attendedTurns = attendedTurns;
	}

	/**
	 * @return the turnTypes
	 */
	public ArrayList<TurnType> getTurnTypes() {
		return turnTypes;
	}

	/**
	 * @param turnTypes the turnTypes to set
	 */
	public void setTurnTypes(ArrayList<TurnType> turnTypes) {
		this.turnTypes = turnTypes;
	}

	/**
	 * @return the systemDate
	 */
	public model.Date getSystemDate() {
		return systemDate;
	}

	/**
	 * @param systemDate the systemDate to set
	 */
	public void setSystemDate(model.Date systemDate) {
		this.systemDate = systemDate;
	}

	/**
	 * @return the systemTime
	 */
	public model.Time getSystemTime() {
		return systemTime;
	}

	/**
	 * @param systemTime the systemTime to set
	 */
	public void setSystemTime(model.Time systemTime) {
		this.systemTime = systemTime;
	}

	/**
	 * This method receives a document number to verify if its exists in the system.
	 * <b>pre:</b> An ArrayList that stores the users in the system has already been created.
	 * <b>post:</b> It is known if the document number is repeated or not.
	 * @param documentNumber is a String that corresponds to the document number to verify.
	 * @return String that indicates if the document number is accepted or not.
	 * @throws RepeatedDocumentException
	 */
	public String verifyID(String documentNumber) throws RepeatedDocumentException{
        for (int i = 0; i<users.size(); i++) {
			if (users.get(i).getDocumentNumber().equals(documentNumber)) {
				throw new RepeatedDocumentException(documentNumber);
			}
		}
		return "ok";
    }

	/**
	 * This method receives the attributes of an user in order to register it in the system.
	 * <b>pre:</b> An ArrayList that stores the users in the system has already been created.
	 * <b>post:</b> A new user has been created and added to the system.
	 * @param documentType is a String that corresponds to the document type of the user to add.
	 * @param documentNumber is a String that corresponds to the document number to verify.
	 * @param name is a String that corresponds to the name of the user to add.
	 * @param lastNames is a String that corresponds to the last names of the user to add.
	 * @param phone is a String that corresponds to the phone of the user to add.
	 * @param address is a String that corresponds to the address of the user to add.
	 */
    public void registerUser(String documentType, String documentNumber, String name, String lastNames, String phone, String address) {
        if (phone.equals(""))
            phone = "Not given";
        if (address.equals(""))
            address = "Not given";
        users.add(new User(documentType, documentNumber, name, lastNames, phone, address));
    }

    /**
     * This method receives the id of an user in order to search it in the system and return its info.
     * <b>pre:</b> An ArrayList that stores the users in the system has already been created.
	 * <b>post:</b> The information of the desired user is returned in a String.
     * @param id is a String that correspond to the user id to search.
     * @return String with the information of the found user.
     * @throws UserNotFoundException
     */
	public String findUser(String id) throws UserNotFoundException{
		String userFound = "";
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getDocumentNumber().equals(id)) {
				userFound = "\n-------USER-------\n"+users.get(i).toString();
			}
		}
		if (userFound.equals("")) {
			throw new UserNotFoundException(id);
		}
		else {
			return userFound;
		}
	}

	/**
	 * This method receives the id of an user in order to register him a turn.
     * <b>pre:</b> An ArrayList that stores the users in the system has already been created.
	 * <b>post:</b> Next turn available is correctly assigned to the desired user.
	 * @param id is a String that correspond to the user id to register a turn.
	 * @return String indicating that the turn has been correctly assigned and its number.
	 * @throws UserAlreadyHasTurnException
	 */
    public String assignTurn(String id, int turnType) throws UserAlreadyHasTurnException{
		String turn = "";
		String msj = "";
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getDocumentNumber().equals(id)) {
				if (users.get(i).hasTurn()) {
					throw new UserAlreadyHasTurnException(id, users.get(i).getAssignedTurn());
				}
				else if (users.get(i).getBannedUntil()!=null) {
					LocalDateTime now = LocalDateTime.of(systemDate.getSystemDate(), systemTime.getSystemTime());
					if (users.get(i).getBannedUntil().isAfter(now)) {
						msj = "<<User is banned until "+users.get(i).getBannedUntil().toString()+">>";
					}
					else {
						turn = nextTurn();
						users.get(i).assignTurn(turn);
						users.get(i).setHasTurn(true);
						Turn pturn = new Turn(turn, users.get(i), turnTypes.get(turnType), LocalTime.now());
						attentionTime = (attentionTime==null)?LocalTime.now():attentionTime;
						actualTurns.add(pturn);
						users.get(i).addRequestedTurn(pturn);
						users.get(i).setBannedUntil(null);
						msj = "<<Turn assigned correctly ["+turn+"] type: "+turnTypes.get(turnType).getName()+" - duration: "+turnTypes.get(turnType).getDuration()+" minutes>>";
					}
				}
				else {
					turn = nextTurn();
					users.get(i).assignTurn(turn);
					users.get(i).setHasTurn(true);
					Turn pturn = new Turn(turn, users.get(i), turnTypes.get(turnType), LocalTime.now());
					attentionTime = (attentionTime==null)?LocalTime.now():attentionTime;
					actualTurns.add(pturn);
					users.get(i).addRequestedTurn(pturn);
					msj = "<<Turn assigned correctly ["+turn+"] type: "+turnTypes.get(turnType).getName()+" - duration: "+turnTypes.get(turnType).getDuration()+" minutes>>";
				}
			}
		}
		return msj;
	}

    /**
     * This method generates the number of the next turn available and returns it.
     * <b>pre:</b> Global variables num (turn's number) and letter (turn's letter) have already been created.
	 * <b>post:</b> Next turn available is correctly generated and returned.
     * @return Next turn available.
     */
	public String nextTurn() {
		String turn = "";
		num ++;
		if (num==100 && letter!='Z') {
			letter += 1;
			num = 0;
			turn = letter+"0"+String.valueOf(num);
		}
		else if (num==100 && letter=='Z') {
			letter = 'A';
			num = 0;
			turn = letter+"0"+String.valueOf(num);
		}
		else if (num>=10) {
			turn = letter+String.valueOf(num);
		}
		else {
			turn = letter+"0"+String.valueOf(num);
		}
		return turn;
	}

	/**
	 * This method finds the actual turn to attend and returns it.
	 * <b>pre:</b> An ArrayList that stores the actual turns has already been created.
	 * <b>post:</b> It is know which is the actual turn to attend.
	 * @return actual turn to attend.
	 */
	public String getActualTurn() {
		String turnID = "";
		if (actualTurns.size()==0) {
			turnID = "<<There are no turns to attend>>";
		}
		else {
			turnID = actualTurns.get(0).getTurnID();
			attendedTurns.add(actualTurns.get(0));
			actualTurns.remove(0);
		}
		return turnID;
	}

	/**
	 * This method ends a turn and places it where it corresponds.
	 * <b>pre:</b> ArrayLists that stores the attended turns and not attended turns have already been created.
	 * <b>post:</b> A given turn is ended and placed where it corresponds.
	 * @param op is an int that corresponds to the option chose by the user.
	 * @param turnID is a String that corresponds to the turn id to end.
	 */
	public void endTurn(int op, String turnID) {
		Turn pturn = null;
		for (int i=0; i<attendedTurns.size(); i++) {
			if (attendedTurns.get(i).getTurnID().equals(turnID)) {
				attendedTurns.get(i).setInUse(false);
				attendedTurns.get(i).getUser().setHasTurn(false);
				attendedTurns.get(i).getUser().assignTurn("");
				pturn = attendedTurns.get(i);
				break;
			}
		}
		if (op==1) {
			pturn.setStatusWhenCalled("User present when attended");
		}
		else {
			pturn.setStatusWhenCalled("User not present when attended");
		}
	}

	public String addNewTypeOfTurn(String name, String dur) {
		String msj = "";
		float duration = Float.parseFloat(dur);
		for (int i=0; i<turnTypes.size(); i++) {
			if (turnTypes.get(i).getName().equals(name)) {
				msj = "<<A turn type with this name already exists>>";
			}
		}
		if (msj=="") {
			turnTypes.add(new TurnType(name, duration));
			msj = "<<New type of turn correctly added>>";
		}
		return msj;
	}
	
	public String typesOfTurns() {
		String types = "";
		for (int i=0; i<turnTypes.size(); i++) {
			types = (i==0)?"\nTurn types:":types;
			types += "\n["+(i+1)+"] "+turnTypes.get(i).getName() + " -> " + turnTypes.get(i).getDuration() + " minutes.";
		}
		return (types!="")?types:"<<There are no turn types added yet>>";
	}
	
	public String configurateCalendar(String strTime) {
		String msj = "<<New time correctly configured>>";
		if (strTime.split(":")[0].length()==1) {
			strTime = "0"+strTime;
		}
		LocalTime time = LocalTime.parse(strTime);
		LocalTime difference;
		if (time.isBefore(LocalTime.now())) {
			difference = LocalTime.now().minusHours(time.getHour()).minusMinutes(time.getMinute());
			systemTime.setMinusTime(difference);
			systemTime.setPlusTime(LocalTime.of(0, 0));
		}
		else if (time.isAfter(LocalTime.now())) {
			difference = time.minusHours(LocalTime.now().getHour()).minusMinutes(LocalTime.now().getMinute());
			systemTime.setPlusTime(difference);
			systemTime.setMinusTime(LocalTime.of(0, 0));
		}
		for (int i=0; i<actualTurns.size(); i++) {
			endTurn((int)(Math.random()*2), getActualTurn());
		}
		return msj;
	}
	
	public String configurateCalendar(int month, int day, int year) {
		String msj = "<<New date correctly configured>>";
		if (year>LocalDate.now().getYear()) {
			systemDate.setYearsToSum(year-LocalDate.now().getYear());
			systemDate.setYearsToSub(0);
		} else {
			systemDate.setYearsToSub(LocalDate.now().getYear()-year);
			systemDate.setYearsToSum(0);
		}
		if (month>LocalDate.now().getMonthValue()) {
			systemDate.setMonthsToSum(month-LocalDate.now().getMonthValue());
			systemDate.setMonthsToSub(0);
		} else {
			systemDate.setMonthsToSub(LocalDate.now().getMonthValue()-month);
			systemDate.setMonthsToSum(0);
		}
		if (day>LocalDate.now().getDayOfMonth()) {
			systemDate.setDaysToSum(day-LocalDate.now().getDayOfMonth());
			systemDate.setDaysToSub(0);
		} else {
			systemDate.setDaysToSub(LocalDate.now().getDayOfMonth()-day);
			systemDate.setDaysToSum(0);
		}
		for (int i=0; i<actualTurns.size(); i++) {
			endTurn((int)(Math.random()*2), getActualTurn());
		}
		return msj;
	}
	
	public String requestedTurnsReport(String id, int op) throws IOException, FileNotFoundException{
		File userTurns = null;
		BufferedWriter bw = null;
		if (op==2) {
			userTurns = new File("data/"+id+".report");
			bw = new BufferedWriter(new FileWriter(userTurns));
		}
		String report = "";
		User user = null;
		for (int i=0; i<users.size(); i++) {
			if (users.get(i).getDocumentNumber().equals(id)) {
				user = users.get(i);
			}
		}
		if (user.getRequestedTurns().size()==0) {
			report = "<<This user hasn't requested any turn yet>>";
		}
		else {
			report = "\n------REQUESTED TURNS------";
			if (op==2) {
				bw.write("USER'S DOCUMENT NUMBER: "+id+"\n");
				bw.write("\n------REQUESTED TURNS------");
			}
			for (int i=0; i<user.getRequestedTurns().size(); i++) {
				report += "\n"+user.getRequestedTurns().get(i).getTurnID();
				if (op==2) {
					bw.write("\n"+user.getRequestedTurns().get(i).getTurnID());
				}
				if (user.getRequestedTurns().get(i).isInUse()) {
					report += " - not yet attended";
					if (op==2) {
						bw.write(" - not yet attended");
					}
				}
				else {
					report += " - attended ("+user.getRequestedTurns().get(i).getStatusWhenCalled()+")";
					if (op==2) {
						bw.write(" - attended ("+user.getRequestedTurns().get(i).getStatusWhenCalled()+")");
					}
				}
			}
		}
		bw.close();
		return report;
	}
	
	public String banUser(String id) {
		String msj = "";
		User user = null;
		for (int i=0; i<users.size(); i++) {
			if (users.get(i).getDocumentNumber().contentEquals(id)) {
				user = users.get(i);
			}
		}
		if (user.getRequestedTurns().size()>=2) {
			if (user.getRequestedTurns().get(user.getRequestedTurns().size()-1).getStatusWhenCalled().equals("User not present when attended") && user.getRequestedTurns().get(user.getRequestedTurns().size()-2).getStatusWhenCalled().equals("User not present when attended")) {
				LocalDateTime banTime = LocalDateTime.now().plusDays(2);
				msj = "<<User banned until "+banTime.toString()+">>";
				user.setBannedUntil(banTime);
			}
			else {
				msj = "<<User doesn't meet the requirements to be banned>>";
			}
		}
		else {
			msj = "<<User doesn't meet the requirements to be banned>>";
		}
		return msj;
	}
	
	public String AllTurnsReport(int op, int op2) throws IOException {
		String msj = "";
		BufferedWriter bw = null;
		if (op2==2) {
			File allTurns = new File("data/AllRequestedTurns.report");
			bw = new BufferedWriter(new FileWriter(allTurns));
		}
		ArrayList<Turn> totalTurns = new ArrayList<Turn>();
		totalTurns.addAll(actualTurns);
		totalTurns.addAll(attendedTurns);
		switch (op) {
			case 1: // Bubble Sort by turn ID using comparable interface
				for (int i=totalTurns.size(); i>0; i--) {
					for (int j=0; j<i-1; j++) {
						if (totalTurns.get(j).compareTo(totalTurns.get(j+1))>0) {
							Turn temp = totalTurns.get(j);
							totalTurns.set(j, totalTurns.get(j+1));
							totalTurns.set(j+1, temp);
						}
					}
				}
				break;
			case 2: // Sort by turn duration with Comparator as an external class
				totalTurns.sort(new TurnComparatorByTime());
				break;
			case 3: // Reverse order sort by turn duration with Comparator as an external class
				totalTurns.sort(Collections.reverseOrder(new TurnComparatorByTime()));
				break;
			case 4: // Sort by turn type with Comparator as an anonymous class
				totalTurns.sort(new Comparator<Turn>() {
					public int compare (Turn turn1, Turn turn2) {
						if (turn1.getType().getName().compareTo(turn2.getType().getName())<0) {
							return -1;
						}
						else if (turn1.getType().getName().compareTo(turn2.getType().getName())>0) {
							return 1;
						}
						return 0;
					}
				});
				break;
			case 5: // Selection sort by user document number with String compareTo Comparator
				for (int i=0; i<totalTurns.size()-1; i++) {
					int minIdx = i;
					for (int j=i+1; j<totalTurns.size(); j++) {
						if (totalTurns.get(j).getUser().getDocumentNumber().compareTo(totalTurns.get(minIdx).getUser().getDocumentNumber())<0) {
							minIdx = j;
						}
					}
					Turn temp = totalTurns.get(i);
					totalTurns.set(i, totalTurns.get(minIdx));
					totalTurns.set(minIdx, temp);
				}
				break;
		}
		if (op2==2) {
			bw.write("ALL REQUESTED TURNS\n\n");
		}
		else {
			msj = "\n-------ALL REQUESTED TURNS-------\n";
		}
		for (int i=0; i<totalTurns.size(); i++) {
			if (op2==2) {
				bw.write("Turn: "+totalTurns.get(i).getTurnID()+" - Type: "+totalTurns.get(i).getType().getName()+" - Duration: "+totalTurns.get(i).getType().getDuration()+" - User document: "+totalTurns.get(i).getUser().getDocumentNumber()+"\n");
			}
			else {
				msj += "Turn: "+totalTurns.get(i).getTurnID()+" - Type: "+totalTurns.get(i).getType().getName()+" - Duration: "+totalTurns.get(i).getType().getDuration()+" - User document: "+totalTurns.get(i).getUser().getDocumentNumber()+"\n";
			}
		}
		if (op2==2) {
			bw.close();
		}
		msj = (totalTurns.size()==0)?"<<There aren't any turns registered yet>>":(msj.equals(""))?"<<You can find the report on the following path: /Laboratorio2_AP2/data/AllRequestedTurns.report>>":msj;
		return msj;
	}
	
	public String generateRandomUsers(int num) throws IOException, FileNotFoundException{
		String msj = "<<"+num+" random users correctly registered>>";
		String[] documentTypes = {"Citizenship card", "Identity card", "Civil registration", "Passport", "Foreign identity card"};
		for (int i=0; i<num; i++) {
			System.out.println("entra");
			BufferedReader nameReader = new BufferedReader(new FileReader("data/NAMES.txt"));
			BufferedReader lastNameReader = new BufferedReader(new FileReader("data/LAST_NAMES.txt"));
			int num1 = (int)(Math.random()*1000)+1;
			int num2 = (int)(Math.random()*1000)+1;
			for (int j=0; j<num1; j++) {
				nameReader.readLine();
			}
			String name = nameReader.readLine();
			for (int j=0; j<num2; j++) {
				lastNameReader.readLine();
			}
			String lastName = lastNameReader.readLine();
			int num3 = (int)(Math.random()*5);
			String documentNumber = String.valueOf((int)(Math.random()*10e8));
			users.add(new User(documentTypes[num3], documentNumber, name, lastName, "Not given", "Not given"));
			nameReader.close();
			lastNameReader.close();
		}
		return msj; 
	}
	
	public String randomlyAssociateTurns(int num) throws UserAlreadyHasTurnException{
		String msj = "<<"+num+" turns correctly associated>>";
		if (turnTypes.size()==0) {
			msj = ("<<There are no turn types added yet>>");
		}
		else {
			if (num>users.size()) {
				msj = "<<The number of turns exceeds the amount of users registered in the system>>";
			}
			else {
				for (int i=0; i<num; i++) {
					int type = (int)(Math.random()*turnTypes.size());
					assignTurn(users.get(i).getDocumentNumber(), type);
					//assignTurn(users.get((int)(Math.random()*users.size())).getDocumentNumber(), type);
					//endTurn((int)(Math.random()*2), getActualTurn());
				}
			}
		}
		return msj;
	}
	
	public String attendTurnsUntilCurrentTime() {
		String msj = "";
		LocalTime time = systemTime.getSystemTime();
		if (actualTurns.size()==0) {
			msj = "<<There are no turns to attend>>";
		}
		for (int i=0; i<actualTurns.size();) {
			if (onWait) {
				if(time.isAfter(attentionTime.plusSeconds(15))) {
					attentionTime.plusSeconds(15);
					onWait = false;
					msj += "+ 15 seconds of wait\n";
				}
				else {
					attentionTime = actualTurns.get(i).getRequestedAt();
					break;
				}
			}
			LocalTime turnTime = actualTurns.get(i).getType().getDurationInTime();
			if (time.isAfter(attentionTime.plusHours(turnTime.getHour()).plusMinutes(turnTime.getMinute()).plusSeconds(turnTime.getSecond()))) {
				attentionTime.plusHours(turnTime.getHour()).plusMinutes(turnTime.getMinute()).plusSeconds(turnTime.getSecond());
				String turnID = actualTurns.get(i).getTurnID();
				attendedTurns.add(actualTurns.get(i));
				actualTurns.remove(i);
				endTurn((int)(Math.random()*2), turnID);
				onWait = true;
				msj += "Turn "+turnID+" attended\n";
			}
			else {
				attentionTime = actualTurns.get(i).getRequestedAt();
				break;
			}
			if (onWait && time.isAfter(attentionTime.plusSeconds(15))) {
				attentionTime.plusSeconds(15);
				onWait = false;
				msj += "+ 15 seconds of wait\n";
				if (actualTurns.size()==0) {
					attentionTime = null;
				}
			}
			else {
				attentionTime = (actualTurns.size()==0)?null:actualTurns.get(i).getRequestedAt();
				break;
			}
		}
		msj = (msj=="")?"<<No turns could be attended yet>>":msj;
		return msj;
	}
}