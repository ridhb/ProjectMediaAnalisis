package mediaOnline;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class ScheduledTask extends TimerTask {
	
	private static int SQLparam;
	
	//public static String StringLink;
	public ScheduledTask (int SQLparam){
		//this.SQLparam  = SQLparam;
	}
	
	public static String[] Mytask (String Site){
		Document doc;
		String [] getStringLink = new String [6];
		try {
			// need http protocol
				doc = Jsoup.connect(Site).get();
				Elements links = doc.select("a[href]");
				String [] Linking = new String [links.size()]; 
				String [] Texting = new String [links.size()]; 
				int x=0;
				for (Element link : links) {
					Linking [x] =link.attr("href");
					Texting [x] = link.text();
					x++;
				}
				if (Site.equals("http://kompas.com")){
					getStringLink [0] = Linking [40];
					getStringLink [1] = Texting [40];
					getStringLink [2] = Linking [42];
					getStringLink [3] = Texting [42];
					getStringLink [4] = Linking [44];
					getStringLink [5] = Texting [44];
				}
				else if (Site.equals("http://detik.com")){
					getStringLink [0] = Linking [9];
					getStringLink [1] = Texting [9];
					getStringLink [2] = Linking [10];
					getStringLink [3] = Texting [10];
					getStringLink [4] = Linking [11];
					getStringLink [5] = Texting [11];
				}

		} catch (IOException e) {
			System.out.println("parsing gagal ");
			e.printStackTrace();
			 //System.exit(0);
		}
		return getStringLink;
	}
	public static Connection konek(){
		try {
			   Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException ex) {
			   System.out.println("MySQL JDBC Driver tidak ditemukan");
			   ex.printStackTrace();
		}
			  
			 // System.out.println("Appliying MySQL JDBC Driver...");
			  Connection connection = null;
			  
			  try {
			   connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/infoMedia","Username", "Password");
			  } catch (SQLException ex) {
			   System.out.println("Gagal Membuat koneksi.");
			   ex.printStackTrace();
			   System.exit(1);
			  }
			  return connection;
	
	}
	
	public static String istTblSQL (){
		String insertTableSQL = "INSERT INTO getText3"
					+ "(no_id, SiteName, texting, linking, datetime ) VALUES"
					+ "(?,?,?,?,?)";
		
	return insertTableSQL;
	}
	
	private String GetStringfromQuery (Connection connection, String getString){
		ResultSet hasilQuery3 = null;
		String getStringLinkQuery3 =null;
		//System.out.println(getString);
		
		String sql3 = "SELECT no_id, linking FROM getText3 WHERE linking = " + "'" + getString+ "'" ;	
		
		//System.out.println(getStringLinkQuery3);
		try{
			Statement stm3 = connection.createStatement();
			hasilQuery3 = stm3.executeQuery(sql3);
			while(hasilQuery3.next()){
		        getStringLinkQuery3 = hasilQuery3.getString("linking");
		      }
			hasilQuery3.close();

		}
		catch (Exception ex8){
			System.err.println(ex8);
		}
		
		return getStringLinkQuery3;
		
	}
	
	public static void InserttoSQL (Connection connection, 
			String getString, String getLink, String insertTableSQL, 
			String SiteS){
		//Date now = new Date(); // to display current time
		//System.out.println("Checking.... " + "Time is :" + now);
		try{
			
		PreparedStatement preparedStatement = null;
		  preparedStatement = connection.prepareStatement(insertTableSQL);
		  //preparedStatement.setInt(1, indeks++);
		  preparedStatement.setInt(1, SQLparam);
		  preparedStatement.setString(2, SiteS.substring(7, 13));
		  preparedStatement.setString (3, getString);
		  preparedStatement.setString (4, getLink);
		  java.util.Date dt = new java.util.Date();
		  java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  String currentTime = sdf.format(dt);
		  preparedStatement.setString(5, currentTime);
		  
		  
		  System.out.println(SQLparam + " " + currentTime + " " + getString + " " + getLink );
		  SQLparam++;
		  // execute insert SQL stetement
		 preparedStatement.executeUpdate();
	  }
	  catch (SQLException ex){
		  //System.out.println("not found table or id");
		  ex.printStackTrace();
	  }
		
	}
	public void IFInserttoSQl (Connection connection,
			String getLink, 
			String getString,
			String getStringLinkQuery3,
			String insertTableSQL,
			String SiteS){
		
		//System.out.println(getStringLinkQuery3);
		if (getLink == null){
			//System.out.println("Not Saving...");
		}
		else if (getStringLinkQuery3 != null){
			//System.out.println("Not Saved 2");
			//System.out.println("Not Saving...");
		}
		else{
			InserttoSQL (connection,getString, getLink, insertTableSQL, SiteS);
			//System.out.println("Saved.... in no_id" );
		}
	}
	public void running_program (){
		String [] Site = new String [2];
		Site [0] = "http://kompas.com";
		Site [1] = "http://detik.com";

		
		Connection connection = konek();
		String insertTableSQL = istTblSQL ();
		

		//for (int loop = Starting ; loop <Ending; loop++){
			//now = new Date(); // initialize date
		Date now = new Date(); // initialize date
		System.out.println("Checking.... " + "Time is :" + now);
			//Kompas
			String [] getStringLink = Mytask(Site [0]);
			String getStringLinkQuery3 = GetStringfromQuery (connection, getStringLink [0]);		
			IFInserttoSQl (connection,getStringLink[0], getStringLink[1],getStringLinkQuery3,insertTableSQL,Site[0]);
			
			String getStringLinkQuery32 = GetStringfromQuery (connection, getStringLink [2]);		
			IFInserttoSQl (connection,getStringLink[2], getStringLink[3],getStringLinkQuery32,insertTableSQL,Site[0]);
			
			String getStringLinkQuery33 = GetStringfromQuery (connection, getStringLink [4]);		
			IFInserttoSQl (connection,getStringLink[4], getStringLink[5],getStringLinkQuery33,insertTableSQL,Site[0]);
			
			
			//detik	
			String [] getStringLinkd = Mytask(Site [1]);
			String getStringLinkQuery3d = GetStringfromQuery (connection, getStringLinkd [0]);		
			IFInserttoSQl (connection,getStringLinkd[0], getStringLinkd[1],getStringLinkQuery3d,insertTableSQL,Site[1]);	
		
			String getStringLinkQuery3d2 = GetStringfromQuery (connection, getStringLinkd [2]);		
			IFInserttoSQl (connection,getStringLinkd[2], getStringLinkd[3],getStringLinkQuery3d2,insertTableSQL,Site[1]);	
		
			String getStringLinkQuery3d3 = GetStringfromQuery (connection, getStringLinkd [4]);		
			IFInserttoSQl (connection,getStringLinkd[4], getStringLinkd[5],getStringLinkQuery3d3,insertTableSQL,Site[1]);
		}		  
	//}
	
	
	public void run() {
		running_program ();
	}
}


public class MediaAnalis extends ScheduledTask{
  public MediaAnalis(int SQLParam) {
		super( SQLParam );
		// TODO Auto-generated constructor stub
	}

public static void main(String[] args)  {
	Timer time = new Timer(); // Instantiate Timer Object
	ScheduledTask st = new ScheduledTask(1); // Instantiate SheduledTask class
	time.schedule(st, 0, 500); // Create Repetitively task for every 1 secs
	
}
		
}
