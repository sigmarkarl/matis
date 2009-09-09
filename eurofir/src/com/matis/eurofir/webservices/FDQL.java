package com.matis.eurofir.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

public class FDQL extends DefaultHandler2 {
	String 	sql = "";
	
	boolean	inSelect = false;
	boolean inWhere = false;
	boolean inOrderBy = false;
	int		inFieldName = 0;
	int		inCondition = 0;
	boolean	inConditionField = false;
	String	conditionOperator = null;
	int		inConditionValue = 0;
	String	logOp = null;
	String	ordDir = null;
	
	String	selStr = "";
	String	condStr = "";
	String	joinStr = "";
	String	fromStr = "";
	
	String	joinFood = "fd.OriginalFoodCode = cv.OriginalFoodCode";
	String	joinComponent = "cv.OriginalComponentCode = co.OriginalComponentCode";
	String	joinAll = joinFood + " AND " + joinComponent;
	
	Set<String>	fields = new HashSet<String>();
	
	Map<String,String>	tableNameMap = new HashMap<String,String>();
	Map<String,String>	columnTableMap = new HashMap<String,String>();
	
	public FDQL() throws IOException {
		InputStream 	inputStream = FDQL.class.getResourceAsStream("/tables.txt");
		BufferedReader	reader = new BufferedReader( new InputStreamReader(inputStream) );
		String line = reader.readLine();
		while( line != null ) {
			String[] split = line.split("[\t]+");
			if( split.length > 1 ) {
				readTableColumns( split[0] );
				tableNameMap.put( split[0], split[1] );
			}
			
			line = reader.readLine();
		}
	}
	
	private void readTableColumns( String tableName ) throws IOException {
		InputStream 	inputStream = FDQL.class.getResourceAsStream("/"+tableName);
		BufferedReader	reader = new BufferedReader( new InputStreamReader(inputStream) );
		String line = reader.readLine();
		while( line != null ) {
			columnTableMap.put( line, tableName );
			
			line = reader.readLine();
		}
	}
	
	public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {		
		if( qName.equals("SelectClause") ) {
			inSelect = true;
			
			selStr = "select ";
		} else if( qName.equals("WhereClause") ) {
			inWhere = true;
			
			sql += " where ";
		} else if( qName.equals("OrderByClause") ) {
			inOrderBy = true;
			
			sql += " orderby ";
		} else if( qName.equals("FieldName") ) {
			inFieldName = -inFieldName+1;
		} else if( qName.equals("Condition") ) {
			inCondition = -inCondition+1;
			
			logOp = attributes.getValue("logicalOperator");
			//System.err.println(logOp);
		} else if( qName.contains("ConditionField") ) {
			inConditionField = true;
		} else if( qName.equals("ConditionOperator") ) {
			conditionOperator = "";
		} else if( qName.equals("ConditionValue") ) {
			inConditionValue = -inConditionValue+1;
		} else if( qName.equals("OrderByField") ) {
			ordDir = attributes.getValue("orderingDirection");
		}
	}
	 
	 public void endElement (String uri, String localName, String qName) throws SAXException {
		 if( qName.equals("SelectClause") ) {
			inFieldName = 0;
			inSelect = false;
		} else if( qName.equals("WhereClause") ) {
			inCondition = 0;
			inWhere = false;
			
			sql += condStr + ")";
		} else if( qName.equals("OrderByClause") ) {
			inOrderBy = false;
		} else if( qName.equals("FieldName") ) {
			inFieldName = -inFieldName;
		} else if( qName.equals("Condition") ) {
			inCondition = -inCondition;
			if( conditionOperator.equals("IN") ) {
				condStr += ")";
			}
			conditionOperator = null;
			//if( inCondition < -1 ) sql += " "+logOp+" ";
			logOp = null;
			inConditionValue = 0;
		} else if( qName.contains("ConditionField") ) {
			inConditionField = false;
			inFieldName = 0;
		} else if( qName.equals("ConditionOperator") ) {
			//conditionOperator = null;
		} else if( qName.equals("ConditionValue") ) {
			inConditionValue = -inConditionValue;
		} else if( qName.equals("OrderByField") ) {
			ordDir = null;
		}
	 }
	 
	 public void endDocument() {
		 Set<String>	allTables = new HashSet<String>();
		 for( String field : fields ) {
			 String table = columnTableMap.get(field);
			 if( table != null ) allTables.add( field );
		 }
		 
		 if( allTables.size() == 0 ) {
			 if( fields.contains("OriginalFoodCode") ) {
				 fromStr = "Food fd";
			 } else if( fields.contains("OriginalComponentCode") ) {
				 fromStr = "Component co";
			 }
		 } else {
			 
		 }
		 
		 sql = selStr + fromStr + joinStr + sql;
	 }
	 
	 public void characters( char[] ch, int start, int length ) {
		 if( inSelect && inFieldName > 0 ) {
			 String field = new String(ch,start,length).trim();
			 fields.add( field );
			 String table = columnTableMap.get(field);
			 String tname = tableNameMap.get( table );
			 
			 field = tname+"."+field;
			 if( inFieldName == 1 ) {
				 selStr += field;
			 } else {
				 selStr += ","+field;
			 }
		 } else if( inWhere && inCondition > 0 ) {
			 if( inFieldName > 0 ) {
				 String field = new String(ch,start,length).trim();
				 fields.add( field );
				 String table = columnTableMap.get(field);
				 String tname = tableNameMap.get( table );
				 
				 field = tname+"."+field;
				 
				 if( inCondition > 1 ) condStr += ") "+logOp+" ";
				 condStr = "("+condStr+field;
			 } else if( conditionOperator != null ) {
				 if( conditionOperator.length() == 0 ) {
					 //System.err.println(inConditionValue);
					 //if( inCondition == 1 ) {
						 //conditionOperator = new String(ch,start,length).trim(); 
						 //sql += " " + conditionOperator + " ";
					 //} else {
					 conditionOperator = new String(ch,start,length).trim(); 
					 condStr += " " + conditionOperator + " ";
					 //}
				 } else if( inConditionValue > 0 ) {
					 if( conditionOperator.equals("IN") ) {
						 if( inConditionValue == 1 ) {
							 condStr += "('"+new String(ch,start,length)+"'";
						 } else {
							 condStr += ",'"+new String(ch,start,length)+"'";
						 }
					 } else if( conditionOperator.equals("BETWEEN") ) {
						 if( inConditionValue == 1 ) {
							 condStr += new String(ch,start,length)+" AND ";
						 } else {
							 condStr += new String(ch,start,length);
						 }
					 } else if( conditionOperator.equals("LIKE") ) {
						 if( inConditionValue == 1 ) {
							 condStr += "'"+new String(ch,start,length)+"'";
						 }
					 } else {
						 if( inConditionValue == 1 ) {
							 condStr += new String(ch,start,length);
						 }
					 }
				 }
			 } else {
				 //sql += 
			 }
		 } else if( inOrderBy ) {
			 if( inFieldName > 0 ) {
				 String field = new String(ch,start,length).trim();
				 fields.add( field );
				 String table = columnTableMap.get(field);
				 String tname = tableNameMap.get( table );
				 
				 field = tname+"."+field;
				 
				 if( inFieldName == 1 ) {
					 sql += field;
				 } else {
					 sql += "," + field;
				 }
				 
				 if( ordDir != null ) sql += " "+ordDir;
			 }
		 }
	 }
	
	public static String fdqlToSql( InputStream is ) throws ParserConfigurationException, SAXException, IOException {
		SAXParserFactory 	factory = SAXParserFactory.newInstance();
		SAXParser			parser = factory.newSAXParser();
		
		FDQL fdql = new FDQL();
		parser.parse( is, fdql );
		
		return fdql.sql;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream is = FDQL.class.getResourceAsStream("/example_fdql.xml");
		try {
			System.err.println( FDQL.fdqlToSql( is ) );
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
