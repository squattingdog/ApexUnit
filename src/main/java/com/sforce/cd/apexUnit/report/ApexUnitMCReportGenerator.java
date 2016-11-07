package com.sforce.cd.apexUnit.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.remote.JMXServiceURL;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exacttarget.fuelsdk.ETClient;
import com.exacttarget.fuelsdk.ETDataExtension;
import com.exacttarget.fuelsdk.ETDataExtensionRow;
import com.exacttarget.fuelsdk.ETResponse;
import com.exacttarget.fuelsdk.ETResult;
import com.exacttarget.fuelsdk.ETResult.Status;
import com.exacttarget.fuelsdk.ETSdkException;
import com.exacttarget.fuelsdk.ETSoapConnection;
import com.exacttarget.fuelsdk.ETSubscriber;
import com.exacttarget.fuelsdk.ETTriggeredEmail;
import com.exacttarget.fuelsdk.internal.APIProperty;
import com.exacttarget.fuelsdk.internal.Attribute;
import com.exacttarget.fuelsdk.internal.CreateOptions;
import com.exacttarget.fuelsdk.internal.CreateRequest;
import com.exacttarget.fuelsdk.internal.CreateResponse;
import com.exacttarget.fuelsdk.internal.DataExtensionObject;
import com.exacttarget.fuelsdk.internal.ObjectExtension.Properties;
import com.exacttarget.fuelsdk.internal.Soap;
import com.exacttarget.fuelsdk.internal.Subscriber;
import com.exacttarget.fuelsdk.internal.TriggeredSend;
import com.exacttarget.fuelsdk.internal.TriggeredSendDefinition;
import com.sforce.cd.apexUnit.ApexUnitUtils;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;
import com.sforce.cd.apexUnit.client.connection.MCClientConnectorConfig;
import com.sforce.cd.apexUnit.client.testEngine.TestStatusPollerAndResultHandler;

public class ApexUnitMCReportGenerator {
	private static Logger LOG = LoggerFactory.getLogger(ApexUnitTestReportGenerator.class);

	/**
	 * Populates the Marketing Cloud objects and triggers an email.
	 * @param apexClassCodeCoverageBeans the class level test result data
	 */
	public static void sendTestResults(ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans) {
		LOG.info(String.format("sending %1$d results to Marketing Cloud.", apexClassCodeCoverageBeans.length));
		// generate the unique test run identifier.
		UUID testRunId = UUID.randomUUID();
		
		//add the data to MarketingCloud DataExtension
		saveResultsToDataExtension(testRunId, apexClassCodeCoverageBeans, "ApexUnitTestDetail");
		
		//setup subscriber
		Subscriber triggeredSendSubscriber = getSubscriber(testRunId);		
		
		//send the email.
		sendTriggeredEmail(triggeredSendSubscriber);
	}
	
	/**
	 * Populates and returns a subscriber object.
	 * 
	 * @param dataRunId - the unique identifier for the test run.
	 * 
	 */
	private static Subscriber getSubscriber(UUID testRunId) {
		Subscriber sub = new Subscriber();
		sub.setEmailAddress(CommandLineArguments.getEmailAddress());
		sub.setSubscriberKey(CommandLineArguments.getEmailAddress());
		
		//DataRunId Attribute
		Attribute attrTestRunId = new Attribute();
		attrTestRunId.setName("TestRunId");
		attrTestRunId.setValue(String.valueOf(testRunId));		
		sub.getAttributes().add(attrTestRunId);
		
		//TimeStamp Attribute
		Attribute attrTimeStamp = new Attribute();
		attrTimeStamp.setName("TimeStamp");
		attrTimeStamp.setValue(DateTime.now().toString());
		sub.getAttributes().add(attrTimeStamp);

		//OrgPercentage Attribute
		Attribute attrOrgPercentage = new Attribute();
		attrOrgPercentage.setName("OrgPercentage");
		attrOrgPercentage.setValue(String.valueOf(new DecimalFormat("#.##").format(ApexUnitCodeCoverageResults.orgWideCodeCoverage)));
		sub.getAttributes().add(attrOrgPercentage);
		
		//TeamPercentage Attribute
		Attribute attrTeamPercentage = new Attribute();
		attrTeamPercentage.setName("TeamPercentage");
		attrTeamPercentage.setValue(String.valueOf(new DecimalFormat("#.##").format(ApexUnitCodeCoverageResults.teamCodeCoverage)));
		sub.getAttributes().add(attrTeamPercentage);
		
		//TotalTestClassesInRun Attribute
		Attribute attrNumTotalTestClasses = new Attribute();
		attrNumTotalTestClasses.setName("TotalTestClassesInRun");
		attrNumTotalTestClasses.setValue(String.valueOf(TestStatusPollerAndResultHandler.totalTestClasses));
		sub.getAttributes().add(attrNumTotalTestClasses);
		
		//TotalTestMethodsInRun Attribute
		Attribute attrNumTotalTestMethods = new Attribute();
		attrNumTotalTestMethods.setName("TotalTestMethodsInRun");
		attrNumTotalTestMethods.setValue(String.valueOf(TestStatusPollerAndResultHandler.totalTestMethodsExecuted));
		sub.getAttributes().add(attrNumTotalTestMethods);
		
		//TestMethodsPassed Attribute
		Attribute attrNumTestMethodsPassed = new Attribute();
		attrNumTestMethodsPassed.setName("TestMethodsPassed");
		attrNumTestMethodsPassed.setValue(String.valueOf(TestStatusPollerAndResultHandler.getNumOfPassedTestMethods()));
		sub.getAttributes().add(attrNumTestMethodsPassed);
		
		//TestMethodsFailed Attribute
		Attribute attrNumTestMethodsFailded = new Attribute();
		attrNumTestMethodsFailded.setName("TestMethodsFailed");
		attrNumTestMethodsFailded.setValue(String.valueOf(TestStatusPollerAndResultHandler.getNumOfFailedTestMethods()));
		sub.getAttributes().add(attrNumTestMethodsFailded);	
		
		//OrgThreshold Attribute
		Attribute attrOrgThreshold = new Attribute();
		attrOrgThreshold.setName("OrgThreshold");
		attrOrgThreshold.setValue(String.valueOf(CommandLineArguments.getOrgWideCodeCoverageThreshold()));
		sub.getAttributes().add(attrOrgThreshold);
		
		//TeamThreshold Attribute
		Attribute attrTeamThreshold = new Attribute();
		attrTeamThreshold.setName("TeamThreshold");
		attrTeamThreshold.setValue(String.valueOf(CommandLineArguments.getTeamCodeCoverageThreshold()));
		sub.getAttributes().add(attrTeamThreshold);
		
		//TeamName Attribute
		Attribute attrTeamName = new Attribute();
		attrTeamName.setName("TeamName");
		attrTeamName.setValue(String.valueOf(CommandLineArguments.getTeamName()));
		sub.getAttributes().add(attrTeamName);
		
		//Jenkins Job Name
		Attribute attrJenkinsJobName = new Attribute();
		attrJenkinsJobName.setName("JenkinsJobName");
		attrJenkinsJobName.setValue(String.valueOf(CommandLineArguments.getJenkinsJobName()));
		sub.getAttributes().add(attrJenkinsJobName);
		
		//Jenkins Build Number
		Attribute attrJenkinsBuildNumber = new Attribute();
		attrJenkinsBuildNumber.setName("JenkinsBuildNumber");
		attrJenkinsBuildNumber.setValue(String.valueOf(CommandLineArguments.getJenkinsBuildNumber()));
		sub.getAttributes().add(attrJenkinsBuildNumber);
		
		//Jenkins Build URL
		Attribute attrJenkinsBuildUrl = new Attribute();
		attrJenkinsBuildUrl.setName("JenkinsBuildURL");
		attrJenkinsBuildUrl.setValue(String.valueOf(CommandLineArguments.getJenkinsBuildUrl()));
		sub.getAttributes().add(attrJenkinsBuildUrl);
		
		return sub;
	}
	
	/**
	 * Creates a TriggeredSend (sends an email by "queing a triggered send").
	 * 
	 * @param sub - the subscriber to whom the email will be sent.
	 */
	private static void sendTriggeredEmail(Subscriber sub) {
		//populate the triggeredSendDefinition to use for the send.
		TriggeredSendDefinition tsd = new TriggeredSendDefinition();
		tsd.setCustomerKey("ITAppsApexUnitResults");
		
		//setup the triggeredSend
		TriggeredSend ts = new TriggeredSend();
		//specify the triggered send definition to use.
		ts.setTriggeredSendDefinition(tsd);
		//add subscriber to send
		ts.getSubscribers().add(sub);
		
		//trigger the send
		CreateRequest request = new CreateRequest();
		request.setOptions(new CreateOptions());
		request.getObjects().add(ts);
		try{
			CreateResponse response = MCClientConnectorConfig.instance().getETClient().getSoapConnection().getSoap().create(request);
			if(response.getOverallStatus().equals("OK")) {
				LOG.info("TriggeredSend Created.");
			} else {
				LOG.error("Failed to create TriggeredSend.");
				ApexUnitUtils.shutDownWithErrMsg(response.getResults().get(0).getStatusMessage());
			}
		}catch(Exception ex) {
			ApexUnitUtils.shutDownWithDebugLog(ex, "Exception creating TriggeredSend.");
		}
	}
	/**
	 * Get the Data Extension filtering by the Key (also named CustomerKey and ExternalKey)
	 * 
	 * @param key - the key of the DE to retrieve.
	 * @return - An ETDataExtension object.
	 */
	private static ETDataExtension retrieveDataExtensionByKey(String key) {
		ETDataExtension detailDE = null;
		try{		
			ETClient client = MCClientConnectorConfig.instance().getETClient();
			detailDE = client.retrieveObject(ETDataExtension.class, "CustomerKey = " + key);
			if(detailDE == null)
				ApexUnitUtils.shutDownWithErrMsg("Unable to retrieve DE: ApexUnitTestDetail");
			LOG.info("DetailDE info: " + detailDE.toString());
			detailDE.setClient(client);
		} catch(ETSdkException ex) {
			ApexUnitUtils.shutDownWithErrMsg("Exception while retrieving DataExtension by key: " + key + ".  " + ex.getMessage());
		}
		
		return detailDE;		
	}
	
	/**
	 * stores data about a test run into the specified dataExtension.
	 * 
	 * @param dataRunId - the unique Id of the test run.
	 * @param apexClassCodeCoverageBeans - the results of the test run.
	 * @param DEKey - the dataExtension CustomerKey (ExternalKey) of the DE to populate.
	 */
	public static void saveResultsToETDataExtension(UUID testRunId, ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans, String DEKey) {
		try{
			//get the dataExtension
			ETDataExtension detailsDE = retrieveDataExtensionByKey(DEKey);
			detailsDE.setClient(MCClientConnectorConfig.instance().getETClient());
			
			List<ETDataExtensionRow> rows = new ArrayList<ETDataExtensionRow>();
			//populate the rows
			for(int i = 0; i < apexClassCodeCoverageBeans.length; i++){
				ApexClassCodeCoverageBean item = apexClassCodeCoverageBeans[i];
				ETDataExtensionRow row = new ETDataExtensionRow();
				row.setClient(MCClientConnectorConfig.instance().getETClient());
				row.setDataExtensionKey(DEKey);
				row.setColumn("ClassName", item.getApexClassName());
				row.setColumn("LinesTotal", String.valueOf(item.getTotalLines()));
				row.setColumn("LinesCovered", String.valueOf(item.getNumLinesCovered()));
				row.setColumn("Percentage", String.valueOf(item.getCoveragePercentage()));
				row.setColumn("SortOrder", String.valueOf(i));
				row.setColumn("APIVersion", item.getApiVersion());
				row.setColumn("TestRunId", String.valueOf(testRunId));
				
				rows.add(row);		
				//LOG.info(String.format("added %1$s - %2$d / %3$d -- %4$.2f%", item.getApexClassName(), item.getNumLinesCovered(), item.getTotalLines(), item.getCoveragePercentage()));
			}
			//send API call to save test run data
			ETResponse<ETDataExtensionRow> response = detailsDE.insert(rows);
			for(ETResult<ETDataExtensionRow> result : response.getResults()){
				LOG.debug("Result: " + result.toString());
			}
			
		} catch(ETSdkException ex) {
			ApexUnitUtils.shutDownWithErrMsg("Exception saving detail data to DE: " + ex.getMessage());
		}
	}
	
	/**
	 * Send the test result details to the Marketing Cloud dataExtension
	 * 
	 * @param dataRunId - unique Id of the test run.
	 * @param apexClassCodeCoverageBeans - The test result data.
	 * @param DEKey - The DataExtension CustomerKey where the data will be saved.
	 */
	private static void saveResultsToDataExtension(UUID testRunId, ApexClassCodeCoverageBean[] apexClassCodeCoverageBeans, String DEKey) {
		//populate the dataExtension detail data.
		List<DataExtensionObject> rows = new ArrayList<DataExtensionObject>();
		for(int i = 0; i < apexClassCodeCoverageBeans.length; i++){
			ApexClassCodeCoverageBean item = apexClassCodeCoverageBeans[i];
			Properties props = new Properties();
			DataExtensionObject row = new DataExtensionObject();
			row.setCustomerKey(DEKey);
			//set properties (the fields and their values).
			APIProperty prop1 = new APIProperty();
			prop1.setName("ClassName");
			prop1.setValue(item.getApexClassName());
			props.getProperty().add(prop1);
			
			APIProperty prop2 = new APIProperty();
			prop2.setName("LinesTotal");
			prop2.setValue(String.valueOf((int)item.getTotalLines()));
			props.getProperty().add(prop2);
			
			APIProperty prop3 = new APIProperty();
			prop3.setName("LinesCovered");
			prop3.setValue(String.valueOf(item.getNumLinesCovered()));
			props.getProperty().add(prop3);
			
			APIProperty prop4 = new APIProperty();
			prop4.setName("Percentage");
			prop4.setValue(String.valueOf(new DecimalFormat("#.##").format(item.getCoveragePercentage())));
			props.getProperty().add(prop4);
			
			APIProperty prop5 = new APIProperty();
			prop5.setName("SortOrder");
			prop5.setValue(String.valueOf(i));
			props.getProperty().add(prop5);
			
			APIProperty prop6 = new APIProperty();
			prop6.setName("APIVersion");
			prop6.setValue(item.getApiVersion());
			props.getProperty().add(prop6);
			
			APIProperty prop7 = new APIProperty();
			prop7.setName("TestRunId");
			prop7.setValue(String.valueOf(testRunId));
			props.getProperty().add(prop7);
			
			row.setProperties(props);
			rows.add(row);
			//LOG.info(String.format("added %1$s - %2$d / %3$d -- %4$.2f%", item.getApexClassName(), item.getNumLinesCovered(), item.getTotalLines(), item.getCoveragePercentage()));		
		}
		
		//build the request of the dataExtension detail.
		CreateRequest request = new CreateRequest();
		request.setOptions(new CreateOptions());
		LOG.debug("request: " + request);
		request.getObjects().addAll(rows);
		
		//setup soap endpoint and authToken
		Soap soap = MCClientConnectorConfig.instance().getETClient().getSoapConnection().getSoap();
		
		
		//Send the request to Marketing Cloud
		CreateResponse response = soap.create(request);
		
		//check the response results
		String overallStatus = response.getOverallStatus();
		if(overallStatus.equals("OK")) {
			LOG.debug("Test detailed data saved to MC DE.", response);
		} else {
			LOG.error("Error saving test detail data to MC DE.", response);			
			for(int i = 0; i < response.getResults().size(); i++) {
				LOG.error("Row Error: " + response.getResults().get(i).getStatusMessage());
			}
			ApexUnitUtils.shutDownWithErrMsg("Test detail data was not saved.");
		}
	}
}
