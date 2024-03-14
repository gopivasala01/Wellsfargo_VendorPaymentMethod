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
		            	 Object object = rs.getObject(1);
			                String vendorPaymentMethod = null; // Default value in case rs.getObject(3) is null

			                if (object != null) {
			                    vendorPaymentMethod = object.toString();
			                }
		                String  vendorEntityID = rs.getObject(2).toString();
		                String  email = rs.getObject(3).toString();
		               
		               
		               
		               
		    			
		      
		              //leaseEntityID
		                try 
		                {
		    				RunnerClass.pendingLeases[i][0] = vendorPaymentMethod;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][0] = "";
		                }
		              //DataDifference between moveindate and today
		                try 
		                {
		    				RunnerClass.pendingLeases[i][1] = vendorEntityID;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][1] = "";
		                }
		                try 
		                {
		    				RunnerClass.pendingLeases[i][2] = email;
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
		            	
		            	String 	status =  (String) rs.getObject(1);
		                String  Supplier_ID = (String) rs.getObject(2);
		                String  Supplier_contact_email = (String) rs.getObject(3);
		                String  AutomationStatus = (String) rs.getObject(4);
		                String  Automation_Notes = (String) rs.getObject(5);
		                java.util.Date date = (java.util.Date) rs.getObject(6);
		                String Automation_CompletionDate = date != null ? date.toString() : null;
		                System.out.println(status +" | "+Supplier_ID+" | "+Supplier_contact_email+" | "+AutomationStatus+" | "+Automation_Notes+" | "+Automation_CompletionDate);
		    				//Company
		    				RunnerClass.completedBuildingList[i][0] = status;
		    				//Port folio
		    				RunnerClass.completedBuildingList[i][1] = Supplier_ID;
		    				//Third Party Unit ID
		    				RunnerClass.completedBuildingList[i][2] = Supplier_contact_email;
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
	
	
	public static String getCompany(String vendorEntityID){
		try
		{
		        Connection con = null;
		        Statement stmt = null;
		        ResultSet rs = null;
		            //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		            con = DriverManager.getConnection(AppConfig.connectionUrl);
		            String SQL ="Select top 1 Company from vendor_Dashboard where VendorEntityID ='"+ vendorEntityID + "'";
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
		            if(rows>1 || rows == 0) {
		            	return "";
		            	
		            }
		            System.out.println("No of Rows = "+rows);
		            String[][] companyFromVendorPayment = new String[rows][1];
		           int  i=0;
		            while(rs.next())
		            {
		            	
		            	String 	company =  (String) rs.getObject(1);
		             
		              //Company
		                try 
		                {
		                	companyFromVendorPayment[i][0] = company;
		                }
		                catch(Exception e)
		                {
		                	companyFromVendorPayment[i][0] = "";
		                }
		              
		    			i++;
		            }	
		            System.out.println("Company  = " +companyFromVendorPayment[0][0]);
		            rs.close();
		            stmt.close();
		            con.close();
		 return companyFromVendorPayment[0][0];
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		 return "";
		}
		
		
	}
	
	


}
