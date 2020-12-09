import java.util.*;
import java.io.*;
import java.sql.*;

class p1 {

  private static String driver;
  private static String url;
  private static String username;
  private static String password;
  private static int state;
  private static int custId;

  public static void main(String argv[]) {
   if (argv.length != 1) {
      System.out.println("Need database properties filename");
    } else {
      init(argv[0]); 
      try { 
        Class.forName(driver);                                                                  //load the driver
        Connection con = DriverManager.getConnection(url, username, password);                  //Create the connection
        Statement stmt = con.createStatement();  						//Create a statement
		state = 0;
		custId = -1;
		Scanner scan = new Scanner(System.in);
		while(state > -1){
			if(state == 0){
				mainMenu(scan);
			}
			else if(state == 1){
				newCust(stmt, scan);			//add customer menu
			}
			else if(state == 2) {
				custLogin(stmt, scan);			//customer login menu
			}
			else if(state == 3) {
				custMain(stmt, scan);			//customer main menu
			}
			else if(state == 31) {
				custAcctOpen(stmt, scan);		//open account
			}
			else if(state == 32) {
				custAcctClose(stmt, scan);		//close account
			}
			else if(state == 33) {
				custDep(stmt, scan);			//deposit into account
			}
			else if(state == 34) {
				custWith(stmt, scan);			//withdraw from account
			}
			else if(state == 35) {
				custTrns(stmt, scan);			//transfer between accounts
			}
			else if(state == 36) {
				custAcctSum(stmt);				//summary of customer's accounts
			}
			else if(state == 4) {
				adminMain(stmt, scan);			//admin main menu
			}
			else if(state == 41) {
				adminAcctSum(stmt, scan);		//see account summary of specified customer
			}
			else if(state == 42) {
				adminReportA(stmt);				//Report A/all customer info and total baalnce
			}
			else if(state == 43) {
				adminReportB(stmt, scan);		//Report B/average total balance of age range
			}
		}
		
		System.out.println("Exiting");
		scan.close();
        stmt.close();                                                                           //Close the statement after we are done with the statement
        con.close();                                                                            //Close the connection after we are done with everything
      } catch (Exception e) {
        System.out.println("Exception in main()");
        e.printStackTrace();
      }
    }
  }//main

  static void init(String filename) {
    try {
      Properties props = new Properties();                                                      //Create a new Properties object
      FileInputStream input = new FileInputStream(filename);                                    //Create a new FileInputStream object using our filename parameter
      props.load(input);                                                                        //Load the file contents into the Properties object
      driver = props.getProperty("jdbc.driver");                                                //Load the driver
      url = props.getProperty("jdbc.url");                                                      //Load the url
      username = props.getProperty("jdbc.username");                                            //Load the username
      password = props.getProperty("jdbc.password");                                            //Load the password
    } catch (Exception e) {
      System.out.println("Exception in init()");
      e.printStackTrace();
    }
  }//init
  
  static void mainMenu(Scanner scan){
	  System.out.println("Welcome to the Self Services Banking System! - Main Menu");  //print menu
	  System.out.println("1.  New Customer");
	  System.out.println("2.  Customer Login");
	  System.out.println("3.  Exit");
	  String in = "";
	  while(!in.equals("1") && !in.equals("2") && !in.equals("3")){          //only allow the numbers to be inputted
		in = scan.next();
		if(!in.equals("1") && !in.equals("2") && !in.equals("3")){
			System.out.println("Please enter 1, 2, or 3");
		}
	  }
	  if(in.equals("1")){   												//change state depending on option chosen 
		  state = 1;
	  }
	  else if(in.equals("2")){
		  state = 2;
	  }
	  else{
		  state = -1;
	  }
  }
 
  static void newCust(Statement stmt, Scanner scan){
	  Boolean cont = true;
	  String name = "";
	  String gender = "";
	  int age = -1;
	  int pin = -1;
	  System.out.println("New Customer Creation");
	  System.out.println("What is the name of the new customer?");					//take in inputs
	  name=scan.next();
	  System.out.println("What is the gender of the new customer? (M or F)");
	  gender=scan.next();
	  System.out.println("What is the age of the new customer?");
	  while(cont){
		try{
			age = scan.nextInt();
			cont = false;
		}
		catch(InputMismatchException e){
			System.out.println("Invalid age. Please enter a valid integer.");
			scan.nextLine();
		}
	  }
	  System.out.println("What is the pin of the new customer?");
	  cont = true;
	  while(cont){
		  try{
			pin = scan.nextInt();
			cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Invalid pin. Please enter a valid integer.");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  cont = true;
	  if(gender.length() > 1 || (!gender.equalsIgnoreCase("M") && !gender.equalsIgnoreCase("F"))){ //checking for invalid inputs
		  System.out.println("Failure. Invalid gender.");
		  cont = false;
	  }
	  if(age <= 0){
		  System.out.println("Failure. Invalid age.");
		  cont = false;
	  }
	  if(pin < 0){
		  System.out.println("Failure. Invalid pin.");
		  cont = false;
	  }
	  if(cont){																					//perform insert and find id based on table size
		String query = "insert into p1.customer(Name, Gender, Age, Pin) values('" +
						name + "', '" + gender.toUpperCase() + "', " + age + ", " + pin + ")";
		String countStmt = "select count(*) from p1.customer";
		try{					
			stmt.executeUpdate(query); 
			ResultSet rs = stmt.executeQuery(countStmt);
			rs.next();
			int c = rs.getInt(1) + 99;
			System.out.println("Success! Customer ID is: " + c);
			rs.close();
		}
		catch (Exception e) {
			System.out.println("Exception in newCust()");
			e.printStackTrace();
		}
      }
	  System.out.println("******************************");
	  state = 0;
  }
  
  static void custLogin(Statement stmt, Scanner scan){
	  boolean cont = true;
	  int id = -1;
	  int pin = -1;
	  System.out.println("Customer Login");
	  System.out.println("Input Customer ID");			// take in inputs
	  while(cont){
		try{
			id = scan.nextInt();
			cont = false;
		}
		catch(InputMismatchException e){
			System.out.println("Invalid ID. Please enter a valid integer.");
			scan.nextLine();
		}
	  }
	  System.out.println("Input pin");
	  cont = true;
	  while(cont){
		try{
			pin = scan.nextInt();
			cont = false;
		}
		catch(InputMismatchException e){
			System.out.println("Invalid pin. Please enter a valid integer.");
			scan.nextLine();
		}
	  }
	  System.out.println("******************************");
	  cont = true;										//check for invalid inputs
	  if(id < 0){
		  System.out.println("Failure. Invalid ID.");
		  cont = false;
		  state = 0;
	  }
	  if(pin < 0){
		  System.out.println("Failure. Invalid pin.");
		  cont = false;
		  state = 0;
	  }
	  if(cont) {										//continue if valid inputs
		  if(pin == 0 && id == 0) {					    //Special case for admin menu where you set state and make view to be used by admin menus
			  state = 4;
		  }
		  else {										//check for valid login
			  String check = "select count(*) from p1.customer where ID=" + id + " and Pin=" + pin;
			  try{					
				  ResultSet rs = stmt.executeQuery(check);
				  rs.next();
				  int c = rs.getInt(1);
				  if(c == 0) {							//if no matching pin and id combo, go back to main menu
					  System.out.println("Failure. Invalid ID and/or pin.");
					  state = 0;
				  }
				  else {								//if combo found go to customer main menu
					  System.out.println("Successful Login");
					  custId = id;
					  state = 3;
				  }
				  rs.close();
			  }
			  catch (Exception e) {
				  System.out.println("Exception in custLogin()");
				  e.printStackTrace();
			  }
		  }
	  }
	  System.out.println("******************************");
  }
  
  static void custMain(Statement stmt, Scanner scan) {
	  System.out.println("Customer Main Menu");  //print menu
	  System.out.println("1.  Open Account");
	  System.out.println("2.  Close Account");
	  System.out.println("3.  Deposit");
	  System.out.println("4.  Withdraw");
	  System.out.println("5.  Transfer");
	  System.out.println("6.  Account Summary");
	  System.out.println("7.  Exit");
	  int in = -1;
	  while(in < 1 || in > 7) {					//get valid input of one of the menu options
		  try {
			  in = scan.nextInt();
			  if(in < 1 || in > 7) {
				  System.out.println("Please enter 1, 2, 3, 4, 5, 6, or 7");
			  }
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter 1, 2, 3, 4, 5, 6, or 7");
			  scan.nextLine();
		  }
	  }
	  if(in == 7) {								//option 7 is return to main menu
		  state = 0;
	  }
	  else {									//other options means going to respective menus
		  state = 30 + in;
	  }
  }
  
  static void custAcctOpen(Statement stmt, Scanner scan) {
	  int id = -1;
	  String type = "";
	  int balance = -1;
	  boolean cont = true;
	  System.out.println("Open Account");			//take in inputs
	  System.out.println("Enter customer ID");
	  while(cont) {
		  try {
			  id = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("Enter account type ('S' for savings or 'C' for checking)");
	  type = scan.next();
	  System.out.println("Enter initial balance");
	  cont = true;
	  while(cont) {
		  try {
			  balance = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  cont = true;			//error checking
	  if(id < 100) {
		  System.out.println("Failure. Invalid customer ID.");
		  cont = false;
	  }
	  if(!type.equalsIgnoreCase("S") && !type.equalsIgnoreCase("C")) {
		  System.out.println("Failure. Invalid account type.");
		  cont = false;
	  }
	  if(balance < 0) {
		  System.out.println("Failure. Invalid balance.");
		  cont = false;
	  }
	  if(cont) {			//passed error checking
		  String check = "select count(*) from p1.customer where ID=" + id;		//checking for existence of id
		  String count = "select count(*) from p1.account";						//get count in account for acct number
		  String insert = "insert into p1.account(ID, Balance, Type, Status) values(" +		//insert statement
				  			id + ", " + balance + ", '" + type.toUpperCase() + "', 'A')";
		  try {
			  ResultSet rs  = stmt.executeQuery(check);
			  rs.next();
			  int c = rs.getInt(1);
			  if(c == 0) {													//no such given id in customer table
				  System.out.println("Failure. Invalid customer ID.");
			  }
			  else {
				  stmt.executeUpdate(insert);
				  ResultSet rs2 = stmt.executeQuery(count);
				  rs2.next();
				  int num = rs2.getInt(1) + 999;
				  System.out.println("Success! Account number = " + num);
				  rs2.close();
			  }
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in custAcctOpen()");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void custAcctClose(Statement stmt, Scanner scan) {
	  int num = -1;
	  boolean cont = true;
	  System.out.println("Close Account");			//take in inputs
	  System.out.println("Enter account number");
	  while(cont) {
		  try {
			  num = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  if(num < 1000) {			//check to see if within range of account numbers
		  System.out.println("Failure. Invalid account number.");
	  }
	  else {
		  String check = "select count(*) from p1.account where Number=" + num;				//check if account number valid
		  String update = "update p1.account set Status='I', Balance=0 where Number=" + num;//update statement to close account
		  try {
			  ResultSet rs = stmt.executeQuery(check);					//check if there is an account with given number
			  rs.next();
			  int c = rs.getInt(1);
			  if(c == 0) {
				  System.out.println("Failure. Invalid account number.");
			  }
			  else {
				  stmt.executeUpdate(update);							//close account
				  System.out.println("Account closed");
			  }
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in custAcctClose()");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void custDep(Statement stmt, Scanner scan) {
	  int num = -1;
	  int ammt = -1;
	  boolean cont = true;
	  System.out.println("Deposit into Account");			//take in inputs
	  System.out.println("Enter account number");
	  while(cont) {
		  try {
			  num = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("Enter deposit ammount");
	  cont = true;
	  while(cont) {
		  try {
			  ammt = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  cont = true;
	  if(num < 1000) {			//check to see if within range of account numbers
		  System.out.println("Failure. Invalid account number.");
		  cont = false;
	  }
	  if(ammt < 0) {			//check to see if valid amount
		  System.out.println("Failure. Invalid deposit amount.");
		  cont = false;
	  }
	  if(cont) {
		  String check = "select count(*) from p1.account where Status='A' and Number=" + num;	  //check if account number valid
		  String update = "update p1.account set Balance=Balance+" + ammt + " where Number=" + num;//update statement to deposit
		  try {
			  ResultSet rs = stmt.executeQuery(check);					//check if there is an account with given number
			  rs.next();
			  int c = rs.getInt(1);
			  if(c == 0) {
				  System.out.println("Failure. Invalid account number or account may be closed.");
			  }
			  else {
				  stmt.executeUpdate(update);							//deposit to account
				  System.out.println("Deposit Successful");
			  }
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in custDep()");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void custWith(Statement stmt, Scanner scan) {
	  int num = -1;
	  int ammt = -1;
	  boolean cont = true;
	  System.out.println("Withdraw from Account");			//take in inputs
	  System.out.println("Enter account number. Can only withdraw from your own account.");
	  while(cont) {
		  try {
			  num = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("Enter withdraw ammount");
	  cont = true;
	  while(cont) {
		  try {
			  ammt = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  cont = true;
	  if(num < 1000) {			//check to see if within range of account numbers
		  System.out.println("Failure. Invalid account number.");
		  cont = false;
	  }
	  if(ammt < 0) {			//check to see if valid amount
		  System.out.println("Failure. Invalid withdraw amount.");
		  cont = false;
	  }
	  if(cont) {
		  String check = "select ID, Balance from p1.account where Status='A' and Number=" + num;	//check if account number valid
		  String update = "update p1.account set Balance=Balance-" + ammt + " where Number=" + num;              //update statement to withdraw
		  try {
			  ResultSet rs = stmt.executeQuery(check);					//check if there is an account with given number
			  int i = -1;
			  int b = -1;
			  while(rs.next()) { 		//get id and balance if there is one
				  i = rs.getInt(1);
				  b = rs.getInt(2);
			  }
			  if(i < 100 || b < 0) { //failure state of no account selected
				  System.out.println("Failure. Invalid account number. Account may be closed/does not exist.");
				  cont = false;
			  }
			  else if(i != custId) { //if not customer they can't withdraw
				  System.out.println("Failure. Invalid account number. Can only withdraw from own account.");
				  cont = false;
			  }
			  else if(b < ammt) {   //if balance is too low can't withdraw
				  System.out.println("Failure. Not enough funds.");
				  cont = false;
			  }
			  if(cont) {
				  stmt.executeUpdate(update);							//withdraw from account
				  System.out.println("Withdraw Successful");
			  }
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in custWith()");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void custTrns(Statement stmt, Scanner scan) {
	  int srcNum = -1;
	  int destNum = -1;
	  int ammt = -1;
	  boolean cont = true;
	  System.out.println("Transfer Between Accounts");			//take in inputs
	  System.out.println("Enter source account number. Can only withdraw from your own account.");
	  while(cont) {
		  try {
			  srcNum = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("Enter destination account number");
	  cont = true;
	  while(cont) {
		  try {
			  destNum = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("Enter transfer ammount");
	  cont = true;
	  while(cont) {
		  try {
			  ammt = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  System.out.println("******************************");
	  cont = true;
	  if(srcNum < 1000) {			//check to see if within range of account numbers
		  System.out.println("Failure. Invalid source account number.");
		  cont = false;
	  }
	  if(destNum < 1000) {			//check to see if within range of account numbers
		  System.out.println("Failure. Invalid destination account number.");
		  cont = false;
	  }
	  if(ammt < 0) {			//check to see if valid amount
		  System.out.println("Failure. Invalid withdraw amount.");
		  cont = false;
	  }
	  if(cont) {
		  String checkSrc = "select ID, Balance from p1.account where Status='A' and Number=" + srcNum;	//check if account number valid
		  String checkDest = "select count(*) from p1.account where Status='A' and Number=" + destNum;	//check if account number valid
		  String withdraw = "update p1.account set Balance=Balance-" + ammt + " where Number=" + srcNum; //update statement to withdraw
		  String deposit = "update p1.account set Balance=Balance+" + ammt + " where Number=" + destNum; //update statement to deposit
		  try {
			  ResultSet rs = stmt.executeQuery(checkSrc);					//check if there is an account with given number
			  int i = -1;
			  int b = -1;
			  while(rs.next()) {		//get id and balance if accoutn exists
				  i = rs.getInt(1);
				  b = rs.getInt(2);
			  }
			  if(i < 100 || b < 0) {	//failed check no acccount
				  System.out.println("Failure. Invalid source account number. Account may be closed/does not exist.");
				  cont = false;
			  }
			  else if(i != custId) {	//not transfering from customer
				  System.out.println("Failure. Invalid source account number. Can only transfer from own account.");
				  cont = false;
			  }
			  else if(b < ammt) {	//not enough funds
				  System.out.println("Failure. Not enough funds.");
				  cont = false;
			  }
			  ResultSet rs2 = stmt.executeQuery(checkDest);	//check to see if destination account exists
			  rs2.next();
			  i = rs2.getInt(1);
			  if(i == 0) {	//account doesn't exist
				  System.out.println("Failure. Invalid destination account number. Account may be closed/does not exist.");
				  cont = false;
			  }
			  if(cont) {
				  stmt.executeUpdate(withdraw);							//withdraw from source account
				  stmt.executeUpdate(deposit);							//deposit to destination account
				  System.out.println("Transfer Successful");
			  }
			  rs2.close();
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in custTrns()");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void custAcctSum(Statement stmt) {
	  int sum = 0;
	  String select = "select Number, Balance from p1.account where Status='A' and ID=" + custId;//select all account under customer's id
	  System.out.println("******************************");
	  try {
	  	System.out.println("Account Summary");
	  	System.out.println("--------------------");
	  	ResultSet rs = stmt.executeQuery(select);
		System.out.printf("%-11s %-11s%n", "NUMBER", "BALANCE");
		System.out.println("----------- -----------");
	  	while(rs.next()) {							//go through each row and print their values and add balance to sum
	  		int n = rs.getInt(1);
	  		int b = rs.getInt(2);
	  		System.out.printf("%11s %11s%n", n+"", b+"");
	  		sum += b;
	  	}
	  	System.out.println("-----------------------");
		System.out.printf("%-11s %11s%n", "TOTAL", sum+"");//print out total balance
	  	rs.close();
	  }
	  catch (Exception e) {
		  System.out.println("Exception in custAcctSum()");
		  e.printStackTrace();
	  }
	  System.out.println("******************************");
	  state = 3;
  }
  
  static void adminMain(Statement stmt, Scanner scan) {
	  System.out.println("Administrator Main Menu");  //print menu
	  System.out.println("1.  Account Summary for a Customer");
	  System.out.println("2.  Report A::Customer Information with Total Balance in Decreasing Order");
	  System.out.println("3.  Report B::Find the Average Total Balance Between Age Groups");
	  System.out.println("4.  Exit");
	  int in = -1;
	  while(in < 1 || in > 4) {					//get valid input of one of the menu options
		  try {
			  in = scan.nextInt();
			  if(in < 1 || in > 4) {
				  System.out.println("Please enter 1, 2, 3, or 4");
			  }
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter 1, 2, 3, or 4");
			  scan.nextLine();
		  }
	  }
	  if(in == 4) {								//option 4 is return to main menu and delete view
		  state = 0;
	  }
	  else {									//other options means going to respective menus
		  state = 40 + in;
	  }
  }
  
  static void adminAcctSum(Statement stmt, Scanner scan) {
	  int sum = 0;
	  int id = -1;
	  boolean cont = true;
	  System.out.println("Enter customer ID");	//take in input
	  while(cont) {
		  try {
			  id = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  String select = "select Number, Balance from p1.account where Status='A' and ID=" + id;//select all account under customer's id that are active
	  System.out.println("******************************");
	  if(id < 100) {
		  System.out.println("Failure. Invalid customer ID.");
	  }
	  else {
		  try {
			  System.out.println("Account Summary");
			  System.out.println("--------------------");
			  ResultSet rs = stmt.executeQuery(select);
			  System.out.printf("%-11s %-11s%n", "NUMBER", "BALANCE");
				System.out.println("----------- -----------");
			  int n = -1;
			  int b = -1;
			  while(rs.next()) {							//go through each row and print their values and add balance to sum
				  n = rs.getInt(1);
				  b = rs.getInt(2);
				  System.out.printf("%11s %11s%n", n+"", b+"");
				  sum += b;
			  }
			  if(n < 0 || b < 0) {							//failure to get results
				  System.out.println("Failure. Invalid customer ID or no open accounts under ID.");
			  }
			  else {
				  System.out.println("-----------------------");
				  System.out.printf("%-11s %11s%n", "TOTAL", sum+"");//print out total balance
			  }
			  rs.close();
		  }
		  catch (Exception e) {
			  System.out.println("Exception in adminAcctSum");
			  e.printStackTrace();
		  }
	  }
	  System.out.println("******************************");
	  state = 4;
  }
  
  static void adminReportA(Statement stmt) {
	  String join = "(select c.ID, c.name, c.age, c.gender, a.balance from p1.customer c inner join p1.account a on a.ID=c.ID)"; //join statement
	  String select = "select max(ID) id, max(name) name, max(age), max(gender) gender, sum(balance) total from " + join + 
						" group by id order by total desc"; //select the attributes needed for the table
	  try {
		  System.out.println("******************************");
		  System.out.println("Report A");
		  System.out.println("--------------------");
		  ResultSet rs = stmt.executeQuery(select);
		  System.out.printf("%-11s %-15s %-6s %-6s %-11s%n", "ID", "NAME", "GENDER", "AGE", "TOTAL");
		  System.out.println("----------- --------------- ------ ------ -----------");
		  while(rs.next()) {						//print out every customer's info
			  int id = rs.getInt(1);
			  String name = rs.getString(2);
			  int age = rs.getInt(3);
			  String gender = rs.getString(4);
			  int balance = rs.getInt(5);
			  System.out.printf("%11s %-15s %-6s %6s %11s%n", id+"", name, gender, age+"", balance+"");
		  }
		  System.out.println("******************************");
		  rs.close();
	  }
	  catch (Exception e) {
		  System.out.println("Exception in adminReportA()");
		  e.printStackTrace();
	  }
	  state = 4;
  }
  
  static void adminReportB(Statement stmt, Scanner scan) {
	  int min = -1;
	  int max = -1;
	  boolean cont = true;
	  System.out.println("Enter minimum age of range");
	  while(cont) {
		  try {
			  min = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  cont = true;
	  System.out.println("Enter maximum age of range");
	  while(cont) {
		  try {
			  max = scan.nextInt();
			  cont = false;
		  }
		  catch(InputMismatchException e){
			  System.out.println("Please enter a valid integer");
			  scan.nextLine();
		  }
	  }
	  String join = "(select c.ID, c.age, a.balance from p1.customer c inner join p1.account a on a.ID=c.ID)"; //join statement
	  String totalBalance = "(select sum(balance) balance, ID from " + join + " where age>=" + min +" and age<=" + max +
								" group by ID)"; //get everyone's total balances
	  String select = "select avg(balance) avg from " + totalBalance; //select the attributes needed for the table
	  try {
		  ResultSet rs = stmt.executeQuery(select);				//run query and print report
		  rs.next();
		  int b = rs.getInt(1);
		  System.out.println("******************************");
		  System.out.println("Report B");
		  System.out.println("--------------------");
		  System.out.println("AVERAGE TOTAL BALANCE");
		  System.out.println("---------------------");
		  System.out.println(b);
		  System.out.println("******************************");
		  rs.close();
	  }
	  catch (Exception e) {
		  System.out.println("Exception in adminReportB()");
		  e.printStackTrace();
	  }
	  state = 4;
  }
}
