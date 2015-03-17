package com.matis.eurofir.webservices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.matis.eurofir.webservices.Ws.PseudoResult;

public class Test {

	static Map<String,Set<String>>	tableColumnMap = new HashMap<String,Set<String>>();
	static {
		Set<String>	foodSet = new HashSet<String>();
		
		foodSet.add( "OriginalFoodCode" );
		foodSet.add( "OriginalFoodName" );
		foodSet.add( "EnglishFoodName" );
		foodSet.add( "ScientificFoodName" );
		foodSet.add( "OtherFoodNames" );
		foodSet.add( "OriginalFoodGroupCode" );
		foodSet.add( "FoodGroupIS1" );
		foodSet.add( "FoodGroupIS2" );
		foodSet.add( "FoodGroupIS3" );
		foodSet.add( "CodexFoodStandards" );
		foodSet.add( "ArticleNumber" );
		foodSet.add( "E_number" );
		foodSet.add( "INS_code" );
		foodSet.add( "LangualCodes" );
		foodSet.add( "CODEXAdditives" );
		foodSet.add( "CODEXFood" );
		foodSet.add( "CODEXContaminants" );
		foodSet.add( "FAOBalanceSheet" );
		foodSet.add( "CIAAFood" );
		foodSet.add( "EuroCode2" );
		foodSet.add( "AgriculturalConditions" );
		foodSet.add( "Cuisine" );
		foodSet.add( "EdiblePortion" );
		foodSet.add( "WastePortion" );
		foodSet.add( "NatureofEdiblePortion" );
		foodSet.add( "NatureofWaste" );
		foodSet.add( "TypicalServingSize" );
		foodSet.add( "TypicalWeightperPiece" );
		foodSet.add( "Colour" );
		foodSet.add( "FinalPreparation" );
		foodSet.add( "SpecificGravity" );
		foodSet.add( "NitrogenProteinFactor" );
		foodSet.add( "FattyAcidFactor" );
		foodSet.add( "GenericImage" );
		foodSet.add( "SpecificImage" );
		foodSet.add( "Producer" );
		foodSet.add( "Distributor" );
		foodSet.add( "Retailer" );
		foodSet.add( "AreaOfOrigin" );
		foodSet.add( "AreaOfProcessing" );
		foodSet.add( "AreaofConsumption" );
		foodSet.add( "WebPublishReady" );
		foodSet.add( "ListOfIngredients" );
		foodSet.add( "Remarks" );
		foodSet.add( "DateOfGeneration" );
		foodSet.add( "GeneratedBy" );
		foodSet.add( "DateOfUpdate" );
		foodSet.add( "UpdatedBy" );
		
		Set<String>	componentSet = new HashSet<String>();
		
		componentSet.add( "EuroFIRComponentIdentifier" );
		componentSet.add( "OriginalComponentCode" );
		componentSet.add( "OriginalComponentName" );
		componentSet.add( "EnglishComponentName" );
		componentSet.add( "Algorithm" );
		componentSet.add( "Unit" );
		componentSet.add( "GrunnefniIS" );
		componentSet.add( "YflIS" );
		componentSet.add( "UflIS" );
		componentSet.add( "InndratturIS" );
		componentSet.add( "FAgroupIS" );
		componentSet.add( "WebPublishReady" );
		componentSet.add( "Remarks" );
		componentSet.add( "DateofGeneration" );
		componentSet.add( "GeneratedBy" );
		componentSet.add( "DateOfUpdate" );
		componentSet.add( "UpdatedBy" );
		
		Set<String>	componentValueSet = new HashSet<String>();
		
		componentValueSet.add( "OriginalFoodCode" );
		componentValueSet.add( "OriginalComponentCode" );
		componentValueSet.add( "SelectedValue" );
		componentValueSet.add( "Unit" );
		componentValueSet.add( "Matrixunit" );
		componentValueSet.add( "ValueType" );
		componentValueSet.add( "AcquisitionType" );
		componentValueSet.add( "DateofEvaluation" );
		componentValueSet.add( "DateofEvaluationDisp" );
		componentValueSet.add( "N" );
		componentValueSet.add( "AnalyticalPortionSize" );
		componentValueSet.add( "NoofAnalyticalPortionReplicates" );
		componentValueSet.add( "Mean" );
		componentValueSet.add( "Median" );
		componentValueSet.add( "Minimum" );
		componentValueSet.add( "Maximum" );
		componentValueSet.add( "StandardDeviation" );
		componentValueSet.add( "StandardError" );
		componentValueSet.add( "MethodType" );
		componentValueSet.add( "MethodIndicator" );
		componentValueSet.add( "MethodParameter" );
		componentValueSet.add( "MethodFK" );
		componentValueSet.add( "NoofPrimarySampleUnits" );
		componentValueSet.add( "SampleFK" );
		componentValueSet.add( "OriginalReferenceCode" );
		componentValueSet.add( "QI_Eurofir" );
		componentValueSet.add( "QualityAssessmentFK" );
		componentValueSet.add( "SamplingStrategy" );
		componentValueSet.add( "DateOfAnalysis" );
		componentValueSet.add( "DateOfAnalysisDisp" );
		componentValueSet.add( "Remarks" );
		componentValueSet.add( "DateofGeneration" );
		componentValueSet.add( "GeneratedBy" );
		componentValueSet.add( "DateOfUpdate" );
		componentValueSet.add( "UpdatedBy" );
		
		Set<String>	referenceSet = new HashSet<String>();
		
		referenceSet.add( "OriginalReferenceCode" );
		referenceSet.add( "StandardReferenceCode" );
		referenceSet.add( "AcquisitionType" );
		referenceSet.add( "ReferenceType" );
		referenceSet.add( "OrgCitation" );
		referenceSet.add( "Title" );
		referenceSet.add( "Authors" );
		referenceSet.add( "PublicationDate" );
		referenceSet.add( "Version" );
		referenceSet.add( "OriginalLanguage" );
		referenceSet.add( "ISBN" );
		referenceSet.add( "FirstEditionDate" );
		referenceSet.add( "EditionNumber" );
		referenceSet.add( "NumberofPages" );
		referenceSet.add( "BookTitle" );
		referenceSet.add( "Editors" );
		referenceSet.add( "LongJournalName" );
		referenceSet.add( "AbbreviatedJournalName" );
		referenceSet.add( "ISSN" );
		referenceSet.add( "Volume" );
		referenceSet.add( "Issue" );
		referenceSet.add( "Pages" );
		referenceSet.add( "SeriesName" );
		referenceSet.add( "SeriesNumber" );
		referenceSet.add( "ReportTitle" );
		referenceSet.add( "FileFormat" );
		referenceSet.add( "WWW" );
		referenceSet.add( "DOI" );
		referenceSet.add( "PublicationMedium" );
		referenceSet.add( "OperatingSystem" );
		referenceSet.add( "Validfrom" );
		referenceSet.add( "Remarks" );
		referenceSet.add( "DateOfGeneration" );
		referenceSet.add( "GeneratedBy" );
		referenceSet.add( "DateOfUpdate" );
		referenceSet.add( "UpdatedBy" );
		referenceSet.add( "Citation" );
		
		tableColumnMap.put( "Food", foodSet );
		tableColumnMap.put( "Component", componentSet );
		tableColumnMap.put( "ComponentValue", componentValueSet );
		tableColumnMap.put( "Reference", referenceSet );
	};
	
	public Test() {
		InputStream stream = this.getClass().getResourceAsStream("/testrequest4.xml");
		/*try {
			stream = new FileInputStream( "/u0/matis/eurofir/src/testrequest4.xml" );
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}*/
		
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
					public void init(String fdql) {
						try {
							String sql = FDQL.fdqlToSql( new ByteArrayInputStream( fdql.getBytes() ), tableColumnMap );
							sql += " order by OriginalFoodCode asc, OriginalComponentCode asc";
							ps = connection.prepareStatement(sql);
							rs = ps.executeQuery();
						} catch (SQLException | ParserConfigurationException | SAXException | IOException e) {
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
				
				ByteArrayOutputStream	baos = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter( baos );
				EuroFIRWebService.parseStream( rs, stream, pw );
				pw.flush();
				pw.close();
				
				baos.close();
				
				String userhome = System.getProperty("user.home");
				Files.write( Paths.get( new File(userhome+"/barbara.xml").toURI() ), baos.toByteArray() );
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
