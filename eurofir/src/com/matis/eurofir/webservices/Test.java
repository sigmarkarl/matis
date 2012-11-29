package com.matis.eurofir.webservices;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.matis.eurofir.webservices.Ws.PseudoResult;

public class Test {

	public Test() {
		InputStream stream = this.getClass().getResourceAsStream("/testrequest3.xml");
		if( stream != null ) {
			try {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				String connectionUrl = "jdbc:sqlserver://navision.rf.is:1433;databaseName=ISGEM2;user=simmi;password=mirodc30;";
				final Connection connection = DriverManager.getConnection(connectionUrl);
				
				PseudoResult rs = new PseudoResult() {
					PreparedStatement	ps;
					ResultSet			rs;
					
					@Override
					public boolean next() {
						try {
							return rs.next();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return false;
					}

					@Override
					public String getString(String col) {
						try {
							return rs.getString( col );
						} catch (SQLException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					public void init(String sql) {
						try {
							ps = connection.prepareStatement(sql);
							rs = ps.executeQuery();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						
						//foodSub( p, rs );
					}
					
					@Override
					public void close() {
						try {
							rs.close();
							ps.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				};
				EuroFIRWebService.parseStream( rs, stream, new PrintWriter( System.out ) );
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else System.out.println("erm");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Test();
	}
}
