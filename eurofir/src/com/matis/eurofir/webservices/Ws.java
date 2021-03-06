package com.matis.eurofir.webservices;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Ws {
	public static void main( String[] args ) {
		new Ws().FoodXML( args[0] );
	}
	
	public static boolean nullStr( String val ) {
		return val == null || val.equals("null");
	}
	
	public static String xtoday;
	public static void header( PrintWriter p ) {
		Calendar toDay = Calendar.getInstance();
		String xar = Integer.toString( toDay.get( Calendar.YEAR ) );
		String xman = Integer.toString( toDay.get( Calendar.MONTH )+1 );
		String xdag = Integer.toString( toDay.get( Calendar.DAY_OF_MONTH ) );
		xtoday = xar + "-" + (xman.length() == 1 ? "0"+xman : xman) + "-" + xdag;
		
		p.println("<EuroFIRFoodDataTransportPackage version=\"1.4\" sentdate=\""+xtoday+"\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../EuroFIR_Food_Data_Transport_Package_version_1_4.xsd\">");
		p.println("<StandardVocabularies>");
		
		p.println("<StandardVocabulary system=\"LanguaL\" position=\"http://www.langual.org/xml/langual2009.xml\" />");
		p.println("<StandardVocabulary system=\"componentidentifier\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Component_Thesaurus_version_1.2.xml\" />");
		p.println("<StandardVocabulary system=\"unit\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Unit_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"matrixunit\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Matrix_Unit_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"methodtype\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Method_Type_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"methodindicator\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Method_Indicator_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"valuetype\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Value_Type_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"acquisitiontype\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Acquisition_Type_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"referencetype\" position=\"http://techweb.eurofir.org/xml/EuroFIR_Reference_Type_Thesaurus_version_1.1.xml\" />");
		p.println("<StandardVocabulary system=\"language\" position=\"http://www.loc.gov/standards/iso639-2/ISO-639-2_8859-1.txt\" />");
		p.println("<StandardVocabulary system=\"country\" position=\"http://www.iso.org/iso/iso_3166-1_list_en.zip\" />");
		
		/*
		p.println("<StandardVocabulary system=\"LanguaL\" position=\"http://www.langual.org/xml/langual.xml\" />"); 
		p.println("<StandardVocabulary system=\"component\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Component_Thesaurus_version_1.0.xml\" />"); 
		p.println("<StandardVocabulary system=\"unit\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml\" />");
		p.println("<StandardVocabulary system=\"matrixunit\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml\" />"); 
		p.println("<StandardVocabulary system=\"methodtype\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Method_Type_Thesaurus_version_1.0.xml\" />");
		p.println("<StandardVocabulary system=\"methodidendicator\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Method_Indicator_Thesaurus_version_1.0.xml\" />"); 
		p.println("<StandardVocabulary system=\"valuetype\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Value_Type_Thesaurus_version_1.0.xml\" />");
		p.println("<StandardVocabulary system=\"acquisitiontype\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Acquisition_Type_Thesaurus_version_1.0.xml\" />"); 
		p.println("<StandardVocabulary system=\"publicationtype\" position=\"http://www.eurofir.org/xml/scheme/EuroFIR_Reference_Type_Thesaurus_version_1.0.xml\" />");
		p.println("<StandardVocabulary system=\"language\" position=\"http://www.loc.gov/standards/iso639-2/ISO-639-2_8859-1.txt\" />");
		p.println("<StandardVocabulary system=\"country\" position=\"http://www.iso.org/iso/iso_3166-1_list_en.zip\" />");
		*/
		
		p.println("</StandardVocabularies>");
		
		p.println("<SenderInformation>");
        p.println("<Sender>Ólafur Reykdal</Sender>");
        p.println("<OrganisationName>Matís</OrganisationName>");
        p.println("<SuperOrganisationName></SuperOrganisationName>");
        p.println("<PostalAddress>Vínlandsleið 12, 113 Reykjavík</PostalAddress>");
        p.println("<Country>IS</Country>");
        p.println("<Telephone>+354 422 5000</Telephone>");
        p.println("<Fax>+354 422 5001</Fax>");
        p.println("<Email>olafur.reykdal@matis.is</Email>");
        p.println("<WWWs>");
        p.println("<WWW>www.matis.is</WWW>");
        p.println("</WWWs>");
        p.println("<Remarks></Remarks>");
        p.println("</SenderInformation>");

        p.println("<Content datasetcreated=\"2012-01-01\" language=\"is\" acquisitiontype=\"F\">");
        p.println("<ContentName>The Icelandic Food Composition Database</ContentName>");
        p.println("<ShortContentName>IS ISGEM 2012</ShortContentName>");
        p.println("<ResponsibleBody>Matí­s</ResponsibleBody>");
        p.println("<LegalRestrictions>Copyright Matis ehf. Data in this dataset may not be published in any form in parts or as a whole without written permission from Matis.</LegalRestrictions>");
        p.println("<SummaryOfContent>The dataset contains a subset of the data in The Icelandic Food Composition Database.</SummaryOfContent>");
        p.println("<BibliographicReference referencetype=\"B\" acquisitiontype=\"F\">ISGEM 2012</BibliographicReference>");
        p.println("<Remarks></Remarks>");
        p.println("<ReasonForCreation>Database Query: XML generated by XMLWebService</ReasonForCreation>");
        p.println("</Content>");
	}
	
	public static void footer( PrintWriter p ) {
		p.println("</EuroFIRFoodDataTransportPackage>");
	}
	
	public static void body( PseudoResult rs, PrintWriter p ) {
		p.println("<Foods>");
		foodSub( p, rs );
		p.println("</Foods>");
	}
	
	public interface PseudoResult {
		public boolean next();
		public String getString( String col );
		public void init( String sql );
		public void close();
	}
	
	/*public static void food( Connection conn, String sql, PrintWriter p ) {
		try {	
			PreparedStatement 	ps = conn.prepareStatement(sql);
			final ResultSet 	rsl = ps.executeQuery();
			
			PseudoResult rs = new PseudoResult() {
				@Override
				public boolean next() {
					try {
						return rsl.next();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return false;
				}

				@Override
				public String getString(String col) {
					try {
						return rsl.getString( col );
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return null;
				}	
			};
			
			foodSub( p, rs );
			
			rsl.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/
	
	public static void foodSub( PrintWriter p, PseudoResult rs ) {
		String				oldname = "";
		boolean				hascomponents = true;
		boolean				hasreferences = false;
		
		Map<String,Float>	energyMap = new HashMap<String,Float>();
		Set<String>			energySet = new HashSet<String>();
		
		energySet.add( "PROT" );
		energySet.add( "FAT" );
		energySet.add( "ALC" );
		energySet.add( "CHOT" );
		energySet.add( "FIBT" );
		
		while( rs.next() ) {
			String name = rs.getString("OriginalFoodCode");
			name = nullStr(name) ? "" : name;
			String langual = rs.getString("LangualCodes");
			boolean sameold = oldname.equals(name);
			
			if( !sameold ) {
				energyMap.clear();
				if( !oldname.equals("") ) {
					p.println("</Components>");
					p.println("</Food>");
				}
				p.println("<Food>");
                p.println("<FoodDescription>");
                p.println("<FoodIdentifiers>");
                p.println("<FoodIdentifier system=\"origfdcd\">");
                p.println("<Identifier>"+name+"</Identifier>");
                p.println("</FoodIdentifier>");
                if( langual != null && langual.length() > 0 ) {
	                p.println("<FoodIdentifier system=\"LanguaL\">");
	                String[] split = langual.split("[ ]+");
	                for( String lang : split ) {
	                	p.println("<Identifier>"+lang+"</Identifier>");
	                }
	                p.println("</FoodIdentifier>");
                }
                p.println("</FoodIdentifiers>");
                
                String ff1 = rs.getString("FoodGroupIS1");
                String ff2 = rs.getString("FoodGroupIS2");
                p.println("<FoodClasses>");
                p.println("<FoodClass system=\"origgpcd\">"+ff1+"."+ff2+"</FoodClass>");
                p.println("</FoodClasses>");

                p.println("<FoodNames>");
                String is = rs.getString("OriginalFoodName");
                if( is != null ) is = is.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
                p.println("<FoodName language=\"is\">"+is+"</FoodName>");
                
                String en = rs.getString("EnglishFoodName");
                if( en != null ) en = en.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
                p.println("<FoodName language=\"en\">"+en+"</FoodName>");
                p.println("</FoodNames>");
                
                p.println("<Remarks></Remarks>");
                p.println("</FoodDescription>");
                
                if( hascomponents ) p.println("<Components>");
			}
            
			if( hascomponents ) {
                //p.println("<Components>");
                String eurocd = rs.getString("EuroFIRComponentIdentifier");
				String origcd = rs.getString("OriginalComponentCode");
				String origcpnm = rs.getString("OriginalComponentName");
				String engcpnm = rs.getString("EnglishComponentName");
				String unit = rs.getString("Unit");
				String matrixUnit = rs.getString("MatrixUnit");
				String acquisitionType = rs.getString("AcquisitionType");
				String dateGenerated = rs.getString("DateOfGeneration");
				String methodType = rs.getString("MethodType");
				String methodIndicator = rs.getString("MethodIndicator");
				String methodParameter = rs.getString("MethodParameter");
				String valueType = rs.getString("ValueType");
				String selectedValue = rs.getString("SelectedValue");
				String numberOfAnalyses = rs.getString("N");
				String minimum = rs.getString("Minimum");
				String maximum = rs.getString("Maximum");
				String standardDeviation = rs.getString("StandardDeviation");
				String qualityIndex = rs.getString("QI_Eurofir");
				String remarks = rs.getString("Remarks");
				
				String selVal = nullStr(selectedValue) ? "" : selectedValue.trim();
				if( selVal != null && selVal.startsWith("<") ) {
					selVal = selVal.substring(1);
					valueType = "LT";
				}
				if( selVal.endsWith(",") ) selVal = selVal.substring(0, selVal.length()-1);
                selVal = selVal.replace(',', '.');
                
                boolean tokst = true;
                try {
                	Double.parseDouble( selVal );
                } catch( Exception e ) {
                	tokst = false;
                }
				
                if( tokst ) {
					p.println("<Component>");
					
					eurocd = nullStr(eurocd) ? "" : eurocd.trim();
					origcd = nullStr(origcd) ? "" : origcd.trim();
					origcpnm = nullStr(origcpnm) ? "" : origcpnm.trim();
					engcpnm = nullStr(engcpnm) ? "" : engcpnm.trim();
					
					unit = nullStr(unit) ? "" : unit;
					matrixUnit = nullStr(matrixUnit) ? "" : matrixUnit;
					dateGenerated = nullStr(dateGenerated) ? "" : dateGenerated.substring(0, 10);
					methodType = nullStr(methodType) ? "" : methodType;
					methodIndicator = nullStr(methodIndicator) ? "" : methodIndicator;
					methodParameter = nullStr(methodParameter) ? "" : methodParameter;
	                
					//if( selectedValue != null ) selectedValue = selectedValue.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
					if( remarks != null ) remarks = remarks.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
					if( minimum != null ) minimum = minimum.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
					if( maximum != null ) maximum = maximum.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
					
					eurocd = eurocd.replace("[", "");
					eurocd = eurocd.replace("]", "");
					
	                p.println("<ComponentIdentifiers>");
	                p.println("<ComponentIdentifier system=\"ecompid\">"+eurocd+"</ComponentIdentifier>");	                
	                p.println("<ComponentIdentifier system=\"origcpcd\">"+origcd+"</ComponentIdentifier>");	                
	                p.println("<ComponentIdentifier system=\"origcpnm\">"+origcpnm+"</ComponentIdentifier>");
	                p.println("<ComponentIdentifier system=\"engcpnam\">"+engcpnm+"</ComponentIdentifier>");
	                p.println("</ComponentIdentifiers>");
	                
	                p.println("<Values>");
	                p.println("<Value unit=\""+unit+"\" matrixunit=\""+matrixUnit+"\" dategenerated=\""+dateGenerated+"\" methodtype=\""+methodType+"\" methodindicator=\""+methodIndicator+"\" methodparameter=\""+methodParameter+"\">");
	                valueType = nullStr(valueType) ? "" : valueType;
	                
	                p.println("<SelectedValue valuetype=\""+valueType+"\" acquisitiontype=\""+(acquisitionType==null?"":acquisitionType)+"\">"+selVal+"</SelectedValue>");
	                
	                float fVal = 0;
	                try {
	                	fVal = Float.parseFloat(selVal);
	                } catch( Exception e ) {
	                	
	                }
	                if( energySet.contains(eurocd) ) energyMap.put(eurocd, fVal);
	                
	                minimum = nullStr(minimum) ? "<Minimum/>" : "<Minimum>"+minimum.trim().replace(',', '.')+"</Minimum>";
	                p.println(minimum);
	                maximum = nullStr(maximum) ? "<Maximum/>" : "<Maximum>"+maximum.trim().replace(',', '.')+"</Maximum>";
	                p.println(maximum);
	                standardDeviation = nullStr(standardDeviation) ? "<StandardDeviation/>" : "<StandardDeviation>"+standardDeviation.trim().replace(',', '.')+"</StandardDeviation>";
	                p.println(standardDeviation);
	                numberOfAnalyses = nullStr(numberOfAnalyses) ? "<NumberOfAnalyticalPortions/>" : "<NumberOfAnalyticalPortions>"+numberOfAnalyses+"</NumberOfAnalyticalPortions>";
	                p.println(numberOfAnalyses);
	                qualityIndex = nullStr(qualityIndex) ? "<QualityIndex/>" : "<QualityIndex>"+qualityIndex+"</QualityIndex>";
	                p.println( qualityIndex );
	                remarks = nullStr(remarks) ? "<Remarks/>" : "<Remarks>"+remarks+"</Remarks>";
	                p.println(remarks);
	                //p.println("<QualityIndex>"+qualityIndex+"</QualityIndex>");
	                //p.println("<Remarks>"+remarks+"</Remarks>");
	
	                String referenceType = rs.getString("ReferenceType");
	                if( referenceType != null && referenceType.length() > 0 ) {
	                	//if( hasreferences ) {
	                	String rAcquisitionType = rs.getString("rAcquisitionType");
	                	String link = rs.getString("WWW");
	                	String citation = rs.getString("Citation");
	                	
	                	citation = nullStr( citation ) ? "" : citation;
	                	citation = citation.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	                	
	                	//citation.replace('&', 'o')
	                	
		                p.println("<References>");
		                p.println("<ValueReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?">":" link=\""+link+"\">")+citation+"</ValueReference>");
		                p.println("<MethodReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?" />":" link=\""+link+"\" />"));
		                p.println("</References>");
	                } else p.println("<References />");
	                
	                p.println("</Value>");
	                p.println("</Values>");
	                p.println("</Component>");
	                
	                if( energyMap.size() == 5 ) {
	                	float kcalVal = energyMap.get("PROT")*4 + energyMap.get("FAT")*9 + energyMap.get("CHOT")*4 + energyMap.get("FIBT")*2 + energyMap.get("ALC")*7;
	                	float kjVal = energyMap.get("PROT")*17 + energyMap.get("FAT")*37 + energyMap.get("CHOT")*17 + energyMap.get("FIBT")*8 + energyMap.get("ALC")*29;
	                	energyCalc( p, Float.toString(kcalVal), "kcal" );
	                	energyCalc( p, Float.toString(kjVal), "kJ" );
	                	energyMap.clear();
	                }
                }
			}
			oldname = name;
		}
		if( !oldname.equals("") ) {
			p.println("</Components>");
			p.println("</Food>");
		}
	}
	
	public static void energyCalc( PrintWriter p, String selVal, String unit ) {
		p.println("<Component>");
		
		String eurocd = "ENRGC";
		String origcd = "";
		if( unit.equals("kJ") ) origcd = "201";
		else origcd = "202";
		String origcpnm = "Orka";
		String engcpnm = "Energy";
		
		String matrixUnit = "W";
		String dateGenerated = xtoday;
		String methodType = "T";
		String methodIndicator = "MI0110";
		String methodParameter = "";
		
        p.println("<ComponentIdentifiers>");
        p.println("<ComponentIdentifier system=\"ecompid\">"+eurocd+"</ComponentIdentifier>");	                
        p.println("<ComponentIdentifier system=\"origcpcd\">"+origcd+"</ComponentIdentifier>");	                
        p.println("<ComponentIdentifier system=\"origcpnm\">"+origcpnm+"</ComponentIdentifier>");
        p.println("<ComponentIdentifier system=\"engcpnam\">"+engcpnm+"</ComponentIdentifier>");
        p.println("</ComponentIdentifiers>");
        
        p.println("<Values>");
        p.println("<Value unit=\""+unit+"\" matrixunit=\""+matrixUnit+"\" dategenerated=\""+dateGenerated+"\" methodtype=\""+methodType+"\" methodindicator=\""+methodIndicator+"\" methodparameter=\""+methodParameter+"\">");
        String valueType = "BE";
        //String acquisitionType = null;
        p.println("<SelectedValue valuetype=\""+valueType+"\" acquisitiontype=\"S\">"+selVal+"</SelectedValue>");
        
        p.println("<Minimum/>");
        p.println("<Maximum/>");
        p.println("<StandardDeviation/>");
        p.println("<NumberOfAnalyticalPortions/>");
        p.println( "<QualityIndex/>" );
        p.println( "<Remarks/>" );
        //p.println("<QualityIndex>"+qualityIndex+"</QualityIndex>");

        /*if( hasreferences ) {
        	String referenceType = rs.getString("ReferenceType");
        	String rAcquisitionType = rs.getString("rAcquisitionType");
        	String link = rs.getString("WWW");
        	String citation = rs.getString("Citation");
        	
            p.println("<References>");
            p.println("<ValueReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?">":" link=\""+link+"\">")+citation.replace('&', 'o')+"</ValueReference>");
            p.println("<MethodReference referencetype=\""+referenceType+"\" acquisitiontype=\""+rAcquisitionType+"\""+((link==null||link.length()==0)?" />":" link=\""+link+"\" />"));
            p.println("</References>");
        }*/
        
        p.println("</Value>");
        p.println("</Values>");
        p.println("</Component>");
	}
	
	public void FoodXML( String fnr ) {  
		if( fnr.length() == 1 ) fnr = "000"+fnr;
		else if( fnr.length() == 2 ) fnr = "00"+fnr;
		else if( fnr.length() == 3 ) fnr = "0"+fnr;
		
		PrintWriter	p = new PrintWriter( System.out );
		header( p );
		
        p.println("<Foods>");
        try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:isgem.db");
			
			String sql = "select * from Food where OriginalFoodCode=\""+fnr+"\"";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while( rs.next() ) {
				String name = rs.getString("OriginalFoodCode");
				
				p.println("<Food>");
                p.println("<FoodDescription>");
                p.println("<FoodIdentifiers>");
                p.println("<FoodIdentifier system=\"origfdcd\">");
                p.println("<Identifier>"+name+"</Identifier>");
                p.println("</FoodIdentifier>");
                p.println("</FoodIdentifiers>");
                
                try {
                	String ff1 = rs.getString("FoodGroupIS1");
					String ff2 = rs.getString("FoodGroupIS2");
                	p.println("<FoodClasses>");
                	p.println("<FoodClass system=\"origgpcd\">"+ff1+"."+ff2+"</FoodClass>");
                	p.println("</FoodClasses>");
                } catch( SQLException sqlex ) {
                	
                }
                
                p.println("<FoodNames>");
                try {
                	String is = rs.getString("OriginalFoodName");
                	p.println("<FoodName language=\"is\">"+is+"</FoodName>");
                } catch( SQLException sqlex ) {
                	
                }
                try {
                	String en = rs.getString("EnglishFoodName");
                	p.println("<FoodName language=\"en\">"+en+"</FoodName>");
                } catch( SQLException sqlex ) {
                	
                }
                p.println("</FoodNames>");

                p.println("<Remarks></Remarks>");
                p.println("</FoodDescription>");
                
                p.println("<Components>");
                //String subsql = "select c.EuroFIRComponentIdentifier, c.OriginalComponentCode, c.OriginalComponentName, c.EnglishComponentName from Component c, ComponentValue cv where cv.OriginalFoodCode="+fnr;
                String subsql = "select c.EuroFIRComponentIdentifier, c.OriginalComponentCode, c.OriginalComponentName, c.EnglishComponentName, c.Unit, 'W' as MatrixUnit, cv.DateOfAnalysis, cv.MethodType, cv.MethodIndicator, cv.MethodParameter, cv.SelectedValue, cv.ValueType, cv.N, cv.Minimum, cv.Maximum, cv.StandardDeviation, cv.QI_Eurofir, cv.Remarks"
                	+" from Component c, ComponentValue cv where c.OriginalComponentCode = cv.OriginalComponentCode and OriginalFoodCode = \""+fnr+"\"";
    			PreparedStatement subps = conn.prepareStatement(subsql);
    			ResultSet subrs = subps.executeQuery();
    			while( subrs.next() ) {
    				String eurocd = subrs.getString("EuroFIRComponentIdentifier");
    				String origcd = subrs.getString("OriginalComponentCode");
    				String origcpnm = subrs.getString("OriginalComponentName");
    				String engcpnm = subrs.getString("EnglishComponentName");
    				String unit = subrs.getString("Unit");
    				String matrixUnit = subrs.getString("MatrixUnit");
    				String dateAnalysed = subrs.getString("DateOfAnalysis");
    				String methodType = subrs.getString("MethodType");
    				String methodIndicator = subrs.getString("MethodIndicator");
    				String methodParameter = subrs.getString("MethodParameter");
    				String valueType = subrs.getString("ValueType");
    				String selectedValue = subrs.getString("SelectedValue");
    				String numberOfAnalyses = subrs.getString("N");
    				String minimum = subrs.getString("Minimum");
    				String maximum = subrs.getString("Maximum");
    				String standardDeviation = subrs.getString("StandardDeviation");
    				String qualityIndex = subrs.getString("QI_Eurofir");
    				String remarks = subrs.getString("Remarks");
    				
    				p.println("<Component>");
    				
    				eurocd = nullStr(eurocd) ? "" : eurocd.trim();
    				origcd = nullStr(origcd) ? "" : origcd.trim();
    				origcpnm = nullStr(origcpnm) ? "" : origcpnm.trim();
    				engcpnm = nullStr(engcpnm) ? "" : engcpnm.trim();
    				
    				unit = nullStr(unit) ? "" : unit;
    				matrixUnit = nullStr(matrixUnit) ? "" : matrixUnit;
    				dateAnalysed = nullStr(dateAnalysed) ? "" : dateAnalysed;
    				methodType = nullStr(methodType) ? "" : methodType;
    				methodIndicator = nullStr(methodIndicator) ? "" : methodIndicator;
    				methodParameter = nullStr(methodParameter) ? "" : methodParameter;
    				
    				if( selectedValue.startsWith("<") ) selectedValue = "less than "+selectedValue.substring(1);
    				
                    p.println("<ComponentIdentifiers>");
                    p.println("<ComponentIdentifier system=\"ecompid\">");
                    p.println(eurocd);
                    p.println("</ComponentIdentifier>");
                    
                    p.println("<ComponentIdentifier system=\"origcpcd\">");
                    p.println(origcd);
                    p.println("</ComponentIdentifier>");
                    
                    p.println("<ComponentIdentifier system=\"origcpnm\">");
                    p.println(origcpnm);
                    p.println("</ComponentIdentifier>");
                    
                    p.println("<ComponentIdentifier system=\"engcpnam\">");
                    p.println(engcpnm);
                    p.println("</ComponentIdentifier>");
                    p.println("</ComponentIdentifiers>");
                    
                    p.println("<Values>");
                    p.println("<Value unit=\""+unit+"\" matrixunit=\""+matrixUnit+"\" dateanalysed=\""+dateAnalysed+"\" methodtype=\""+methodType+"\" methodidentifier=\""+methodIndicator+"\" methodparameter=\""+methodParameter+"\">");
                    
                    valueType = nullStr(valueType) ? "" : valueType;
                    p.println("<SelectedValue valuetype=\""+valueType+"\">");
                    p.println( selectedValue.trim() );
                    p.println("</SelectedValue>");
                    
                    minimum = nullStr(minimum) ? "<Minimum/>" : "<Minimum>"+minimum+"</Minimum>";
                    p.println(minimum);
                    maximum = nullStr(maximum) ? "<Maximum/>" : "<Maximum>"+maximum+"</Maximum>";
                    p.println(maximum);
                    standardDeviation = nullStr(standardDeviation) ? "<StandardDeviation/>" : "<StandardDeviation>"+standardDeviation+"</StandardDeviation>";
                    p.println(standardDeviation);
                    numberOfAnalyses = nullStr(numberOfAnalyses) ? "<NoOfAnalyses/>" : "<NoOfAnalyses>"+numberOfAnalyses+"</NoOfAnalyses>";
                    p.println(numberOfAnalyses);
                    qualityIndex = nullStr(qualityIndex) ? "<QualityIndex/>" : "<QualityIndex>"+qualityIndex+"</QualityIndex>";
                    p.println( qualityIndex );
                    remarks = nullStr(remarks) ? "<Remarks/>" : "<Remarks>"+numberOfAnalyses+"</Remarks>";
                    p.println(remarks);
                    //p.println("<QualityIndex>"+qualityIndex+"</QualityIndex>");
                    //p.println("<Remarks>"+remarks+"</Remarks>");

                    p.println("<References>");
                    p.println("<ValueReference publicationtype=\"\" acquisitiontype=\"\" link=\"\">");
                    p.println("</ValueReference>");
                    p.println("<MethodReference publicationtype=\"\" acquisitiontype=\"\" link=\"\">");
                    p.println("</MethodReference>");
                    p.println("</References>");
                    
                    p.println("</Value>");
                    p.println("</Values>");
                    p.println("</Component>");
    			}
    			subrs.close();
    			subps.close();
    			p.println("</Components>");
    			p.println("</Food>");
			}
			rs.close();
			ps.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p.println("</Foods>");
	}

	    /*<WebMethod()> Public Function FoodXML(ByVal fnr As String) As System.Xml.XmlDocument


	        'Dim isdbxml As String = "d:\isdb.xml"
	        'Dim isdbxml As IO.Stream
	        Dim isdbxml As IO.MemoryStream = New IO.MemoryStream()

	        Dim xar As String = Str(Year(Now()))
	        Dim xman As String = Format(Month(Now()), "00")
	        Dim xdag As String = Format(Day(Now()), "00")
	        Dim xtoday As String = xar + "-" + xman + "-" + xdag

	        Dim settings As New System.Xml.XmlWriterSettings
	        settings.Indent = True
	        settings.IndentChars = "    "
	        settings.Encoding = Encoding.GetEncoding("iso-8859-1") 'ATH

	        Using writer As System.Xml.XmlWriter = System.Xml.XmlWriter.Create(isdbxml, settings)
	            ' Write the root element.
	            'writer.WriteStartElement("EuroFIRFoodDataTransportPackage")
	            writer.WriteStartElement("EuroFIRFoodDataTransportPackage")
	            writer.WriteAttributeString("name", Nothing, "EuroFIR Food Transport Package Markup Language")
	            writer.WriteAttributeString("version", Nothing, "1.3")
	            'writer.WriteAttributeString("versiondate", Nothing, "2008-04-25")  ' Skv. skeyti AM 2008-11-04
	            writer.WriteAttributeString("sentdate", Nothing, xtoday)
	            'writer.WriteAttributeString("service", Nothing, "0")               ' Skv. skeyti AM 2008-11-04


	            writer.WriteStartElement("StandardVocabularies")

	            '<StandardVocabularies>
	            '<StandardVocabulary system="LanguaL" position="http://www.langual.org/xml/langual.xml" /> 
	            '<StandardVocabulary system="component" position="http://www.eurofir.org/xml/scheme/EuroFIR_Component_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="unit" position="http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="matrixunit" position="http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="methodtype" position="http://www.eurofir.org/xml/scheme/EuroFIR_Method_Type_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="methodidendicator" position="http://www.eurofir.org/xml/scheme/EuroFIR_Method_Indicator_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="valuetype" position="http://www.eurofir.org/xml/scheme/EuroFIR_Value_Type_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="acquisitiontype" position="http://www.eurofir.org/xml/scheme/EuroFIR_Acquisition_Type_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="publicationtype" position="http://www.eurofir.org/xml/scheme/EuroFIR_Reference_Type_Thesaurus_version_1.0.xml" /> 
	            '<StandardVocabulary system="language" position="http://www.loc.gov/standards/iso639-2/ISO-639-2_8859-1.txt" /> 
	            '<StandardVocabulary system="country" position="http://www.iso.org/iso/iso_3166-1_list_en.zip" /> 
	            '</StandardVocabularies>

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "LanguaL")
	            writer.WriteAttributeString("position", "http://www.langual.org/xml/langual.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "language")
	            writer.WriteAttributeString("position", "http://www.loc.gov/standards/iso639-2/ISO-639-2_8859-1.txt")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "country")
	            writer.WriteAttributeString("position", "http://www.iso.org/iso/iso_3166-1_list_en.zip")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "component")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Component_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "unit")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "matrixunit")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Matrix_Unit_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "methodtype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Method_Type_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "methodidentifier")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Method_Indicator_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "valuetype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Value_Type_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "quality")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/standardvocabularies/Quality/...")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "acquisitiontype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Acquisition_Type_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "publicationtype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/xml/scheme/EuroFIR_Reference_Type_Thesaurus_version_1.0.xml")
	            writer.WriteEndElement()

	            '--
	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "domaintype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/standardvocabularies/DomainType/...") 'xxxx
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "compilationtype")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/standardvocabularies/CompilationType/...") 'xxxx
	            writer.WriteEndElement()

	            writer.WriteStartElement("StandardVocabulary")
	            writer.WriteAttributeString("system", "EFG")
	            writer.WriteAttributeString("position", "http://www.eurofir.org/standardvocabularies/EFG/...") 'xxxx
	            writer.WriteEndElement()

	            writer.WriteEndElement() 'StandartVocabularies

	            writer.WriteStartElement("SenderInformation")
	            writer.WriteElementString("Sender", "Olafur Reykdal")
	            writer.WriteElementString("OrganisationName", "Matis")
	            writer.WriteElementString("SuperOrganisationName", "")
	            writer.WriteElementString("PostalAddress", "Skulagata 4, 101 Reykjavik")
	            writer.WriteElementString("Country", "IS")
	            writer.WriteElementString("Telephone", "+354 422 5000")
	            writer.WriteElementString("Fax", "+354 422 5001")
	            writer.WriteElementString("Email", "olafur.reykdal@matis.is")
	            writer.WriteStartElement("WWWs")
	            writer.WriteElementString("WWW", "www.matis.is")
	            writer.WriteEndElement()
	            writer.WriteElementString("Remarks", "")
	            writer.WriteEndElement() 'SenderInformation

	            writer.WriteStartElement("Content")
	            writer.WriteAttributeString("datasetcreated", Nothing, "2008-07-03")  ' ATH
	            writer.WriteAttributeString("language", Nothing, "IS")
	            writer.WriteAttributeString("acquisitiontype", Nothing, "F")
	            writer.WriteAttributeString("domaintype", Nothing, "")
	            writer.WriteAttributeString("compilationtype", Nothing, "")
	            writer.WriteElementString("ContentName", "The Icelandic Food Composition Database")
	            writer.WriteElementString("ShortContentName", "IS ISGEM 2008")
	            writer.WriteElementString("ResponsibleBody", "MatÃÂ­s")
	            writer.WriteElementString("LegalRestrictions", "Copyright Matis ehf. Data in this dataset may not be published in any form in parts or as a whole without written permission from Matis.")
	            writer.WriteElementString("SummaryOfContent", "The dataset contains a subset of the data in The Icelandic Food Composition Database.")
	            writer.WriteElementString("BibliographicReference", "ISGEM 2008")
	            writer.WriteElementString("Remarks", "")
	            writer.WriteElementString("ReasonForCreation", "Database Query: XML generated by XMLWebService")
	            writer.WriteEndElement() 'Content

	            '----------------------------------------------------------------------------------- Opna gagnagrunn
	            Dim DBconn As Data.OleDb.OleDbConnection = _
	                New Data.OleDb.OleDbConnection("Provider=Microsoft.Jet.OLEDB.4.0; Data Source=" & Server.MapPath("~\App_Data\isdb.mdb") & ";")
	            DBconn.Open()

	            '----------------------------------------------------------------------------------- Les Faeda skrÃÂ¡
	            Dim comm_fae As New Data.OleDb.OleDbCommand("Select * from faeda where faeda.faeda=""" & fnr & """", DBconn)
	            Dim read_fae As Data.OleDb.OleDbDataReader = comm_fae.ExecuteReader()

	            'writer.WriteElementString("FÃÂ¦rslufjÃÂ¶ldi", read_fae.RecordsAffected.ToString)

	            Dim x As Int32 = 200
	            writer.WriteStartElement("Foods")
	            While read_fae.Read() And x > 0
	                x = x - 1

	                writer.WriteStartElement("Food")
	                writer.WriteStartElement("FoodDescription")
	                writer.WriteStartElement("FoodIdentifiers")
	                writer.WriteStartElement("FoodIdentifier")
	                writer.WriteAttributeString("system", Nothing, "origfdcd")
	                writer.WriteStartElement("Identifier")
	                writer.WriteValue(read_fae.Item("Faeda").ToString())
	                writer.WriteEndElement() 'Identifier
	                writer.WriteEndElement() 'FoodIdentifier
	                writer.WriteEndElement() 'FoodIdentifiers

	                writer.WriteStartElement("FoodClasses")
	                writer.WriteStartElement("FoodClass")
	                writer.WriteAttributeString("system", Nothing, "origgpcd")
	                writer.WriteValue(read_fae.Item("FF1").ToString() & "." & read_fae.Item("FF2").ToString())
	                writer.WriteEndElement() 'FoodClass
	                writer.WriteEndElement() 'FoodClasses

	                writer.WriteStartElement("FoodNames")
	                writer.WriteStartElement("FoodName")
	                writer.WriteAttributeString("language", Nothing, "is")
	                writer.WriteValue(read_fae.Item("Heiti_isl").ToString())
	                writer.WriteEndElement() 'FoodName
	                writer.WriteStartElement("FoodName")
	                writer.WriteAttributeString("language", Nothing, "en")
	                writer.WriteValue(read_fae.Item("Heiti_ens").ToString())
	                writer.WriteEndElement() 'FoodName
	                writer.WriteStartElement("FoodName")
	                writer.WriteAttributeString("language", Nothing, "tx")
	                writer.WriteValue(read_fae.Item("Heiti_latn").ToString())
	                writer.WriteEndElement() 'FoodName
	                writer.WriteEndElement() 'FoodNames

	                writer.WriteElementString("Remarks", "")
	                writer.WriteEndElement() 'Description


	                Dim xfaeda As String = read_fae.Item("Faeda").ToString()

	                '----------------------------------------------------------------------------------- Opna gagnagrunn
	                Dim DBconn2 As Data.OleDb.OleDbConnection = _
	                    New Data.OleDb.OleDbConnection("Provider=Microsoft.Jet.OLEDB.4.0; Data Source=" & Server.MapPath("~\App_Data\isdb.mdb") & ";")
	                DBconn2.Open()
	                'New Data.OleDb.OleDbConnection("Provider=Microsoft.Jet.OLEDB.4.0; Data Source=app_data\ISDB.MDB;User Id=admin;Password=;")

	                '----------------------------------------------------------------------------------- Les Maeling skrÃÂ¡ ++
	                '"SELECT * FROM maeling WHERE (maeling.Faeda=""0001"");"
	                Dim xSelect As String = _
	                "SELECT maeling.FAEDA, maeling.EFNI, maeling.*, efni.Heiti_ens, efni.Eining, efni.EUkodi, efni.*, heimild.* " & _
	                        "FROM (maeling INNER JOIN efni ON maeling.EFNI = efni.EFNI) INNER JOIN heimild ON maeling.HEIMILD = heimild.HEIMILD " & _
	                        "WHERE (([maeling.Faeda]=""" & xfaeda & """))ORDER BY maeling.EFNI;"
	                '"SELECT maeling.*, efni.*, heimild.* " & _
	                '        "FROM (maeling INNER JOIN efni ON maeling.EFNI = efni.EFNI) INNER JOIN heimild ON maeling.HEIMILD = heimild.HEIMILD " & _
	                '        "WHERE (([maeling.Faeda]=""" & xfaeda & """))ORDER BY maeling.EFNI;"

	                'writer.WriteStartElement("xSelect")
	                'writer.WriteString(xSelect)
	                'writer.WriteEndElement()
	                'writer.Flush()
	                'Stop

	                Dim comm_mae As New Data.OleDb.OleDbCommand(xSelect, DBconn2)
	                Dim read_mae As Data.OleDb.OleDbDataReader = comm_mae.ExecuteReader()
	                'writer.WriteElementString("FÃÂ¦rslufjÃÂ¶ldi", read_mae.RecordsAffected.ToString)
	                'writer.WriteStartElement("xSelect")
	                'writer.WriteString(xSelect)
	                'writer.WriteEndElement()
	                If read_mae.HasRows Then
	                    x = 10
	                Else
	                    x = 0
	                    writer.WriteStartElement("ATH - engar fÃÂ¦rslur finnast")
	                    writer.Flush()
	                    Stop
	                End If

	                writer.WriteStartElement("Components")
	                While read_mae.Read()
	                    x = x - 1

	                    Console.WriteLine(read_mae.Item("maeling.efni").ToString())

	                    writer.WriteStartElement("Component")

	                    writer.WriteStartElement("ComponentIdentifiers")

	                    writer.WriteStartElement("ComponentIdentifier")
	                    'writer.WriteAttributeString("system", Nothing, "component")    ' Skv. skeyti AM 2008-11-04
	                    writer.WriteAttributeString("system", Nothing, "ecompid")     ' Skv. skeyti AM 2008-11-04
	                    writer.WriteValue(read_mae.Item("efni.EUkodi").ToString())
	                    writer.WriteEndElement() 'ComponentIdentifier

	                    writer.WriteStartElement("ComponentIdentifier")
	                    writer.WriteAttributeString("system", Nothing, "origcpcd")
	                    writer.WriteValue(read_mae.Item("efni.Efni").ToString())
	                    writer.WriteEndElement() 'ComponentIdentifier

	                    writer.WriteStartElement("ComponentIdentifier")
	                    writer.WriteAttributeString("system", Nothing, "origcpnm")
	                    writer.WriteValue(read_mae.Item("efni.Heiti").ToString())
	                    writer.WriteEndElement() 'ComponentIdentifier

	                    writer.WriteStartElement("ComponentIdentifier")
	                    writer.WriteAttributeString("system", Nothing, "engcpnam")
	                    writer.WriteValue(read_mae.Item("efni.Heiti_ens").ToString())
	                    writer.WriteEndElement() 'ComponentIdentifier

	                    writer.WriteEndElement() 'ComponentIdentifiers

	                    writer.WriteStartElement("Values")

	                    writer.WriteStartElement("Value") 'unit="g" matrixunit="W" dateanalysed="1992" methodtype="T" methodidentifier="ME258">
	                    writer.WriteAttributeString("unit", Nothing, read_mae.Item("efni.Eining").ToString())
	                    writer.WriteAttributeString("matrixunit", Nothing, "W")
	                    writer.WriteAttributeString("dateanalysed", Nothing, "")
	                    writer.WriteAttributeString("methodtype", Nothing, "")
	                    writer.WriteAttributeString("methodidentifier", Nothing, "")
	                    Dim xstring As String
	                    If read_mae.Item("efni.Efni") = "0001" Then ' PrÃÂ³tÃÂ­n
	                        xstring = read_fae.Item("HV_N_STUD").ToString()
	                    ElseIf read_mae.Item("efni.Efni") = "0002" Then ' Fita
	                        xstring = read_fae.Item("FI_F_STUD").ToString()
	                    Else
	                        xstring = ""
	                    End If
	                    writer.WriteAttributeString("methodparameter", Nothing, xstring.Replace(","c, "."c))

	                    writer.WriteStartElement("SelectedValue")
	                    writer.WriteAttributeString("valuetype", Nothing, "MN")
	                    'writer.WriteValue(read_mae.Item("MEDAL").ToString())
	                    writer.WriteValue(iTRIM(Str(read_mae.Item("MEDAL"))))
	                    writer.WriteEndElement() 'BestLocation Maximum

	                    writer.WriteElementString("Minimum", iTRIM(Str(read_mae.Item("MINNSTA"))))
	                    writer.WriteElementString("Maximum", iTRIM(Str(read_mae.Item("mesta"))))
	                    writer.WriteElementString("StandardDeviation", iTRIM(Str(read_mae.Item("stadal"))))
	                    writer.WriteElementString("NoOfAnalyses", iTRIM(Str(read_mae.Item("maeling"))))
	                    'writer.WriteElementString("Minimum", iTRIM(Str(read_mae.Item("MINNSTA"))))
	                    Try
	                        writer.WriteElementString("QualityIndex", iTRIM(Str(read_mae.Item("gaedakodi"))))
	                    Catch ex As Exception
	                        writer.WriteElementString("QualityIndex", "")  ' ATH ATH ATH
	                    End Try

	                    Try
	                        writer.WriteElementString("Remarks", read_mae.Item("ath"))
	                    Catch ex As Exception
	                        writer.WriteElementString("Remarks", "")
	                    End Try

	                    writer.WriteStartElement("References")

	                    writer.WriteStartElement("ValueReference")
	                    writer.WriteAttributeString("publicationtype", Nothing, "")
	                    writer.WriteAttributeString("acquisitiontype", Nothing, "")
	                    writer.WriteAttributeString("link", Nothing, "")
	                    writer.WriteEndElement() 'ValueReference

	                    writer.WriteStartElement("MethodReference")
	                    writer.WriteAttributeString("publicationtype", Nothing, "")
	                    writer.WriteAttributeString("acquisitiontype", Nothing, "")
	                    writer.WriteAttributeString("link", Nothing, "")
	                    writer.WriteEndElement() 'MethodReference

	                    writer.WriteEndElement() 'References

	                    writer.WriteEndElement() 'Value
	                    writer.WriteEndElement() 'Values

	                    writer.WriteEndElement() 'Component
	                End While

	                writer.WriteEndElement() 'Components
	                writer.WriteEndElement() 'Food
	            End While
	            writer.WriteEndElement() 'Foods

	            ' Write the XML to file and close the writer.
	            writer.WriteEndElement()
	            writer.Flush()
	            writer.Close()
	        End Using

	        Dim xdoc As System.Xml.XmlDocument = New System.Xml.XmlDocument()
	        isdbxml.Seek(0, IO.SeekOrigin.Begin)
	        xdoc.Load(isdbxml)

	        Return xdoc

	    End Function

	    Private Sub InitializeComponent()

	    End Sub
	    Private Function iTRIM(ByVal s As String) As String
	        If s = "Null" Then
	            iTRIM = ""
	        Else
	            iTRIM = Trim(s)
	        End If
	    End Function*/
}
