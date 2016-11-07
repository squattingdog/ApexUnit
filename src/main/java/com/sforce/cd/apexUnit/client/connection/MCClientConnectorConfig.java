package com.sforce.cd.apexUnit.client.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exacttarget.fuelsdk.ETClient;
import com.exacttarget.fuelsdk.ETConfiguration;
import com.exacttarget.fuelsdk.ETSdkException;
import com.sforce.cd.apexUnit.arguments.CommandLineArguments;

public class MCClientConnectorConfig {
	private static Logger LOG = LoggerFactory.getLogger(CommonConnectorConfig.class);
	
	private static MCClientConnectorConfig mcClientConnectorConfig = null;
	private static ETClient client = null;
	
	public static MCClientConnectorConfig instance() {
		if(mcClientConnectorConfig == null)
			mcClientConnectorConfig = new MCClientConnectorConfig();
		return mcClientConnectorConfig;
	}
	
	public ETClient getETClient() {
		if(client == null) {
			try {
				LOG.info(String.format("\n\n\tusername: %1$s\n\tpassword: %2$s\n\tsoapEndpoint: %3$s\n\n", CommandLineArguments.getMCUsername(), CommandLineArguments.getMCPassword(), CommandLineArguments.getMCSoapEndpoint()));
				ETConfiguration config = new ETConfiguration();
				config.set("username", CommandLineArguments.getMCUsername());
				config.set("password", CommandLineArguments.getMCPassword());
				config.set("soapEndpoint", CommandLineArguments.getMCSoapEndpoint());
				
				client = new ETClient(config);
			} catch(ETSdkException ex) {
				LOG.error("exception getting connection to MC account.  " + ex.getMessage());
			}
		}
		
		return client;
	}
}
