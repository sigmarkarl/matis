package com.matis.eurofir.webservices;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class EuroFIRWebService {
	public static PrintStream	ostream = System.out;
	static String				cdataStr = "<![CDATA[";
	static String				cstopStr = "]]>";
	static String				passi = "jP4dj4";
	//static String				passi = "drsmorc.311";
	
	public static void parse( String val ) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchAlgorithmException {
		//String envbody = "<env:Body>";
		//int body = val.indexOf(envbody) + envbody.length();
		
		//val = val.replaceAll("[\\r\\n\\f\\t]", "");
		
		String 	startStr = "<eur:";
		int 	start = val.indexOf(startStr)+startStr.length();
		int		stop = val.indexOf(">", start);
		String	methodname = val.substring(start, stop);
		
		String	stopStr = "</eur:"+methodname+">";
		int		stopField = val.indexOf( stopStr );
		String	allParams = val.substring( stop+1, stopField );
		
		List<Class>		paramTypes = new ArrayList<Class>();
		List<String>	paramNames = new ArrayList<String>();
		List<String>	arguments = new ArrayList<String>();
		
		start = allParams.indexOf( startStr, 0 );
		while( start >= 0 ) {
			start += startStr.length();
			
			stop = allParams.indexOf(">", start);
			String	paramname = allParams.substring(start, stop);
			
			stopStr = "</eur:"+paramname+">";
			stopField = allParams.indexOf(stopStr, stop+1);
			//stop = allParams.indexOf( stopStr, start );
			
			String	param = allParams.substring( stop+1, stopField ).trim();
			int ind = param.indexOf( cdataStr );
			if( ind != -1 ) {
				ind += cdataStr.length();
				int stopInd = param.indexOf(cstopStr, ind);
				param = param.substring(ind, stopInd);
			}
			
			paramTypes.add( String.class );
			paramNames.add( paramname );
			arguments.add( param );
			
			start = allParams.indexOf( startStr, stop );
		}
		
		Class[]	parameterTypes = paramTypes.toArray( new Class[0] );
		Method	m = EuroFIRWebService.class.getMethod( methodname, parameterTypes );
		
		String 		api_signature = "";
		SortedMap<String,String>	smap = new TreeMap<String,String>();
		for( int i = 0; i < paramNames.size(); i++ ) {
			String paramName = paramNames.get(i);
			if( !paramName.equals("api_signature") ) smap.put( paramNames.get(i), arguments.get(i) );
			else {
				api_signature = arguments.get(i);
			}
		}		
		
		//String sign = Authentication.getEuroFIRSignature( smap );
		String sign = Authentication.getEuroFIRSignatureJakarta( smap );
		ostream.print( "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eur=\"http://eurofir.webservice.namespace\">\n" );
		ostream.print( "<soapenv:Header/>\n" );
        ostream.print( "<soapenv:Body>\n" );
		if( sign.equals(api_signature) ) {
			Object[]	args = { smap.get("api_userid"), smap.get("api_permission"), smap.get("fdql_sentence"), smap.get("version"), api_signature };
			//ostream.print( "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" );
			//ostream.print( "<env:Body>\n" );
			m.invoke(null, args);
			//ostream.print( "</env:Body>\n" );
			//ostream.print( "</env:Envelope>\n" );
		} else {
			ostream.println( "<error>" );
			ostream.print( cdataStr );
			ostream.println( sign + " vs " + api_signature );
			for( String key : smap.keySet() ) {
				String value = smap.get(key);
				ostream.println( key + " " + value );
			}
			ostream.print( cstopStr );
			ostream.println( "</error>" );
		}
		ostream.print( "</soapenv:Body>\n" );
		ostream.print( "</soapenv:Envelope>\n" );
	}
	
	public static void GetContentInformation( String api_userid, String api_permission, String fdql_sentence, String version, String api_signature ) {
		//try {
			//ByteArrayInputStream	bais = new ByteArrayInputStream( fdql_sentence.getBytes() );
			//String 					fdql = FDQL.fdqlToSql( bais );
			//ostream.write( fdql.getBytes() );
		ostream.print( cdataStr );
		ostream.print( "<EuroFIRServiceFDTPResponse xmlns=\"http://eurofir.webservice.namespace\">\n" );
		Ws.header( ostream );
		Ws.footer( ostream );
		ostream.print( "</EuroFIRServiceFDTPResponse>" );
		ostream.print( cstopStr );
		/*} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	
	public static void GetFoodInformation( String api_userid, String api_permission, String fdql_sentence, String version, String api_signature ) {
		try {
			ByteArrayInputStream	bais = new ByteArrayInputStream( fdql_sentence.getBytes() );
			String 					sql = FDQL.fdqlToSql( bais );
			ostream.print( "<![CDATA[" );
			ostream.print( "<EuroFIRServiceFDTPResponse xmlns=\"http://eurofir.webservice.namespace\">\n" );
			getFoodFromSql(sql);
			ostream.print( "</EuroFIRServiceFDTPResponse>" );
			ostream.print( "]]>\n" );
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void getFoodFromSql(String sql) throws ClassNotFoundException, SQLException {
		Ws.header( ostream );
		Class.forName("com.mysql.jdbc.Driver");
		String connectionUrl = "jdbc:mysql://localhost:3306/isgem"; //?useUnicode=yes&characterEncoding=UTF-8";
		Connection conn = DriverManager.getConnection(connectionUrl, "root", passi );
		Ws.body( conn, sql, ostream );
		conn.close();
		Ws.footer( ostream );
	}
	
	public static void getFoodFDTP( String[] ids ) throws ClassNotFoundException, SQLException {
		/*String lastCon = "(fd.OriginalFoodCode = '"+ids[0]+"'";
		for( int i = 1; i < ids.length; i++ ) {
			lastCon += " or fd.OriginalFoodCode = '"+ids[i]+"'";
		}
		lastCon += ")";*/
		
		Ws.header( ostream );
		Class.forName("com.mysql.jdbc.Driver");
		String connectionUrl = "jdbc:mysql://localhost:3306/isgem"; //?useUnicode=yes&characterEncoding=UTF-8";
		Connection conn = DriverManager.getConnection(connectionUrl, "root", passi );
		ostream.println("<Foods>");
		for( String id : ids ) {
			String lastCon = "fd.OriginalFoodCode = '"+id+"'";
			String sql = "SELECT cv.OriginalFoodCode,fd.LangualCodes,fd.FoodGroupIS1,fd.FoodGroupIS2,fd.OriginalFoodName,fd.EnglishFoodName,co.EuroFIRComponentIdentifier,cv.OriginalComponentCode,co.OriginalComponentName,co.EnglishComponentName,cv.Unit,'W' as MatrixUnit,cv.AcquisitionType,cv.DateOfGeneration,cv.MethodType,cv.MethodIndicator,cv.MethodParameter,cv.SelectedValue,cv.ValueType,cv.N,cv.Minimum,cv.Maximum,cv.StandardDeviation,cv.QI_Eurofir,cv.Remarks,rf.Citation,rf.ReferenceType,rf.AcquisitionType as rAcquisitionType,rf.WWW from Food fd, ComponentValue cv, Component co, Reference rf where fd.OriginalFoodCode = cv.OriginalFoodCode AND co.OriginalComponentCode = cv.OriginalComponentCode AND rf.ReferenceID = cv.ValueReferenceFK AND "+lastCon;
			Ws.food( conn, sql, ostream );
		}
		ostream.println("</Foods>");
		conn.close();
		Ws.footer( ostream );
	}
	
	static byte[]	bb = new byte[8096];
	public static void parseStream( InputStream stream ) throws IOException, SecurityException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String str = "";
		int r = stream.read( bb );
		while( r > 0 ) {
			str += new String( bb, 0, r, "UTF8" );
			r = stream.read( bb );
		}
		parse( str );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if( args.length > 0 ) {
			try {
				getFoodFDTP( args[0].split(",") );
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {
				InputStream stream = System.in;
				parseStream( stream );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
