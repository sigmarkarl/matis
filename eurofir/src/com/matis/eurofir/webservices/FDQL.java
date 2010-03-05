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
	boolean	inNameConditionField = false;
	String	conditionOperator = null;
	int		inConditionValue = 0;
	String	logOp = null;
	String	ordDir = null;
	
	String	selStr = "";
	String	condStr = "";
	String	joinStr = "";
	String	fromStr = "";
	
	String	firstLog = "";
	String	nameCondition = "";
	
	String	joinFood = "fd.OriginalFoodCode = cv.OriginalFoodCode";
	String	joinComponent = "cv.OriginalComponentCode = co.OriginalComponentCode";
	String	joinReference = "cv.ValueReferenceFK = rf.ReferenceID";
	String	joinAll = joinFood + " AND " + joinComponent + " AND " + joinReference;
	
	Set<String>	fields = new HashSet<String>();
	
	Map<String,String>	tableNameMap = new HashMap<String,String>();
	Map<String,String>	columnTableMap = new HashMap<String,String>();
	
	static Map<String,String[]>	fieldMap = new HashMap<String,String[]>();
	
	static {
		String[]	foodAllMinimum = {"OriginalFoodCode", "FoodGroupIS1", "FoodGroupIS2", "OriginalFoodName", "EnglishFoodName", "LangualCodes", "AcquisitionType"};
		fieldMap.put( "FoodAllMinimum", foodAllMinimum );
		String[]	componentAllMinimum = {"EuroFIRComponentIdentifier", "OriginalComponentCode", "OriginalComponentName", "EnglishComponentName", "Unit", "'W' as MatrixUnit"};
		fieldMap.put( "ComponentAllMinimum", componentAllMinimum );
		String[]	componentValueAllMinimum = {"DateOfGeneration", "MethodType", "MethodIndicator", "MethodParameter", "SelectedValue", "ValueType", "N", "Minimum", "Maximum", "StandardDeviation", "QI_Eurofir", "Remarks", "Citation", "ReferenceType", "rAcquisitionType", "WWW"};
		fieldMap.put( "ComponentValueAllMinimum", componentValueAllMinimum );
		String[]	ecompid = {"EuroFIRComponentIdentifier"};
		fieldMap.put( "ecompid", ecompid );
		String[]	foodName = {"OriginalFoodName"};
		fieldMap.put( "FoodName", foodName );
		String[]	enFoodName = {"EnglishFoodName"};
		fieldMap.put( "enFoodName", enFoodName );
		String[]	isFoodName = {"OriginalFoodName"};
		fieldMap.put( "isFoodName", isFoodName );
	}
	
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
			selStr = "SELECT ";
		} else if( qName.equals("WhereClause") ) {
			inWhere = true;
			
			//sql += " where ";
		} else if( qName.equals("OrderByClause") ) {
			inOrderBy = true;
			sql += " ORDER BY ";
		} else if( qName.equals("NameConditionField") ) {
			//inNameConditionField = true;
			nameCondition = attributes.getValue("language");
		} else if( qName.equals("FieldName") ) {
			inFieldName = -inFieldName+1;
		} else if( qName.equals("Condition") ) {
			inCondition = -inCondition+1;
			logOp = attributes.getValue("logicalOperator");
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
		} else if( qName.equals("NameConditionField") ) {
			nameCondition = "";
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
			 if( table != null ) allTables.add( table );
		 }
		 
		 joinStr = " where ";
		 fromStr += " from ";
		 if( allTables.size() == 0 ) {
			 boolean ofc = fields.contains("OriginalFoodCode");
			 boolean occ = fields.contains("OriginalComponentCode");
			 if( ofc && occ ) {
				 fromStr += "ComponentValue cv";
			 } else if( ofc ) {
				 fromStr += "Food fd";
			 } else if( occ ) {
				 fromStr += "Component co";
			 }
		 } else {
			 boolean fd = allTables.contains("Food");
			 boolean co = allTables.contains("Component");
			 boolean cv = allTables.contains("ComponentValue");
			 if( fd && co ) {
				 fromStr += "Food fd, ComponentValue cv, Component co, Reference rf";
				 joinStr += "fd.OriginalFoodCode = cv.OriginalFoodCode AND co.OriginalComponentCode = cv.OriginalComponentCode AND cv.ValueReferenceFK = rf.ReferenceID ";
			 } else if( fd ) {
				 if( cv ) {
					 fromStr += "Food fd, ComponentValue cv";
					 joinStr += "fd.OriginalFoodCode = cv.OriginalFoodCode ";
				 } else fromStr += "Food fd";
			 } else if( co ) {
				 if( cv ) {
					 fromStr += "ComponentValue cv, Component co";
					 joinStr += "co.OriginalComponentCode = cv.OriginalComponentCode ";
				 } else fromStr += "Component co";
			 }
		 }
		 
		 if( allTables.size() > 1 ) {
			 sql = selStr + fromStr + joinStr + firstLog + sql;
		 } else {
			 sql = selStr + fromStr + joinStr + sql;
		 }
		 
		 if( allTables.contains("ComponentValue") ) {
			 sql = sql.replace("null.OriginalFoodCode", "cv.OriginalFoodCode");
			 sql = sql.replace("null.OriginalComponentCode", "cv.OriginalComponentCode");
			 //sql = sql.replace("null.OriginalReferenceCode", "rf.OriginalReferenceCode");
			 sql = sql.replace("null.Citation", "rf.Citation");
			 sql = sql.replace("null.ReferenceType", "rf.ReferenceType");
			 sql = sql.replace("null.rAcquisitionType", "rf.AcquisitionType as rAcquisitionType");
			 sql = sql.replace("null.WWW", "rf.WWW");
		 } else {
			 sql = sql.replace("null.OriginalFoodCode", "fd.OriginalFoodCode");
			 sql = sql.replace("null.OriginalComponentCode", "co.OriginalComponentCode");
		 }
		 
		 sql = sql.replaceAll("null.", "");
	 }
	 
	 public String fieldAdd( String field ) {
		 fields.add( field );
		 String table = columnTableMap.get(field);
		 String tname = tableNameMap.get( table );
		 
		 //if( tname != null && !tname.equals("null") ) 
		 field = tname+"."+field;
		 
		 return field;
	 }	 
	
	 public void subAdd( String field ) {
		 if( inFieldName == 1 ) {
			 selStr += field;
		 } else {
			 selStr += ","+field;
		 }
	 }
	 
	 public void characters( char[] ch, int start, int length ) {
		 if( inSelect && inFieldName > 0 ) {
			 String field = new String(ch,start,length).trim();
			 if( fieldMap.containsKey( field ) ) {
				 String[]	allFields = fieldMap.get( field );
				 for( String subfield : allFields ) {
					 subfield = fieldAdd( subfield );
					 subAdd( subfield );
					 inFieldName++;
				 }
			 } else {
				 field = fieldAdd( field );
				 subAdd( field );
			 }
		 } else if( inWhere && inCondition > 0 ) {
			 if( inFieldName > 0 ) {
				 String field = new String(ch,start,length).trim();
				 if( nameCondition.length() > 0 ) field = nameCondition + field;
				 if( fieldMap.containsKey(field) && fieldMap.get(field).length == 1 ) field = fieldMap.get(field)[0];
				 
				 field = fieldAdd( field );
				 
				 if( inCondition == 1 ) firstLog = logOp+" ";
				 else if( inCondition > 1 ) condStr += ") "+logOp+" ";
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
							 String val = new String(ch,start,length);							 
							 if( isNumeric( val ) ) condStr += val;
							 else condStr += "'"+val+"'";
						 }
					 }
				 }
			 } else {
				 //sql += 
			 }
		 } else if( inOrderBy ) {
			 if( inFieldName > 0 ) {
				 String field = new String(ch,start,length).trim();
				 
				 field = fieldAdd( field );
				 subAdd( field );
				 
				 if( ordDir != null ) sql += " "+ordDir;
			 }
		 }
	 }
	 
	 public boolean isNumeric( String val ) {
		 try {
			 Double.parseDouble( val );
			 return true;
		 } catch( Exception e ) {
			 return false;
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
		//InputStream is = FDQL.class.getResourceAsStream("/example_fdql.xml");
		//InputStream is = FDQL.class.getResourceAsStream("/simple_fdql.xml");
		InputStream is = System.in;
		try {
			System.out.println( FDQL.fdqlToSql( is ) );
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
