package mainPackage;

import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase 
{
	public static boolean getLeasesList(String pendingLeasesQuery) throws SocketException
	{
		try
		{
		        Connection con = null;
		        Statement stmt = null;
		        ResultSet rs = null;
		            //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		            con = DriverManager.getConnection(AppConfig.connectionUrl);
		            String SQL = pendingLeasesQuery;
		            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		           // stmt = con.createStatement();
		            rs = stmt.executeQuery(SQL);
		            int rows =0;
		            if (rs.last()) 
		            {
		            	rows = rs.getRow();
		            	// Move to beginning
		            	rs.beforeFirst();
		            }
		            System.out.println("No of Rows = "+rows);
		            RunnerClass.pendingLeases = new String[rows][3];
		           int  i=0;
		            while(rs.next())
		            {
		            	
		            	String 	company =  (String) rs.getObject(1);
		                String  vendorEntityID = rs.getObject(2).toString();
		                Object object = rs.getObject(3);
		                String vendorPaymentMethod = null; // Default value in case rs.getObject(3) is null

		                if (object != null) {
		                    vendorPaymentMethod = object.toString();
		                }
		               
		               
		               
		    			
		              //Company
		                try 
		                {
		    				RunnerClass.pendingLeases[i][0] = company;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][0] = "";
		                }
		              //leaseEntityID
		                try 
		                {
		    				RunnerClass.pendingLeases[i][1] = vendorEntityID;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][1] = "";
		                }
		              //DataDifference between moveindate and today
		                try 
		                {
		    				RunnerClass.pendingLeases[i][2] = vendorPaymentMethod;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][2] = "";
		                }
		    			i++;
		            }	
		            System.out.println("Total Pending Leases  = " +RunnerClass.pendingLeases.length);
		            rs.close();
		            stmt.close();
		            con.close();
		 return true;
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		 return false;
		}
	}
	public static void updateTable(String query) throws SocketException
	 {
		    try (Connection conn = DriverManager.getConnection(AppConfig.connectionUrl);
		        Statement stmt = conn.createStatement();) 
		    {
		      stmt.executeUpdate(query);
		      System.out.println("Record Updated");
		      stmt.close();
	          conn.close();
		    } 
		    catch (Exception e) 
		    {
		    	System.out.println("Error while closing connection");
		      //e.printStackTrace();
		    }
	 }
	
	public static boolean getCompletedBuildingsList()
	{
		try
		{
		        Connection con = null;
		        Statement stmt = null;
		        ResultSet rs = null;
		            //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		            con = DriverManager.getConnection(AppConfig.connectionUrl);
		            String SQL = AppConfig.getBuildingsWithStatusforCurrentDay;
		            stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		           // stmt = con.createStatement();
		            rs = stmt.executeQuery(SQL);
		            int rows =0;
		            if (rs.last()) {
		            	rows = rs.getRow();
		            	// Move to beginning
		            	rs.beforeFirst();
		            }
		            System.out.println("No of buildings with status = "+rows);
		            RunnerClass.completedBuildingList = new String[rows][6];
		           int  i=0;
		            while(rs.next())
		            {
		            	
		            	String 	company =  (String) rs.getObject(1);
		                String  PaymentEntityID = (String) rs.getObject(2);
		                String  CheckNumber = (String) rs.getObject(3);
		                String  AutomationStatus = (String) rs.getObject(4);
		                String  Automation_Notes = (String) rs.getObject(5);
		                String  Automation_CompletionDate = (String) rs.getObject(6);
		                System.out.println(company +" | "+PaymentEntityID+" | "+CheckNumber+" | "+AutomationStatus+" | "+Automation_Notes+" | "+Automation_CompletionDate);
		    				//Company
		    				RunnerClass.completedBuildingList[i][0] = company;
		    				//Port folio
		    				RunnerClass.completedBuildingList[i][1] = PaymentEntityID;
		    				//Third Party Unit ID
		    				RunnerClass.completedBuildingList[i][2] = CheckNumber;
		    				//Lease Name
		    				RunnerClass.completedBuildingList[i][3] = AutomationStatus;
		    				//Target Deposit
		    				RunnerClass.completedBuildingList[i][4] = Automation_Notes;
		    				//Listing Agent
		    				RunnerClass.completedBuildingList[i][5] = Automation_CompletionDate;
		    		
		    				i++;
		            }	
		          
		            rs.close();
		            stmt.close();
		            con.close();
		 return true;
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		 return false;
		}
	}
	
	


}
