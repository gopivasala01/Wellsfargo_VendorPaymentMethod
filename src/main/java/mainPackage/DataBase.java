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
		            RunnerClass.pendingLeases = new String[rows][4];
		           int  i=0;
		            while(rs.next())
		            {
		            	String 	ID =  rs.getObject(1).toString();
		            	String 	company =  (String) rs.getObject(2);
		                String  paymentEntityID = rs.getObject(3).toString();
		                String checkNumber = rs.getObject(4).toString();;
		               
		               
		               
		    			//ID
		                try 
		                {
		    				RunnerClass.pendingLeases[i][0] = ID;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][0] = "";
		                }
		              //Company
		                try 
		                {
		    				RunnerClass.pendingLeases[i][1] = company;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][1] = "";
		                }
		              //leaseEntityID
		                try 
		                {
		    				RunnerClass.pendingLeases[i][2] = paymentEntityID;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][2] = "";
		                }
		              //DataDifference between moveindate and today
		                try 
		                {
		    				RunnerClass.pendingLeases[i][3] = checkNumber;
		                }
		                catch(Exception e)
		                {
		                	RunnerClass.pendingLeases[i][3] = "";
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
	
	


}
