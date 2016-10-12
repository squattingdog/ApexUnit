/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * Class to generate OAuth token for the given org and login credentials
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */
package com.sforce.cd.apexUnit.client.codeCoverage;

import com.sforce.cd.apexUnit.client.connection.ConnectionHandler;

/*
 * Generates OAuth Token
 * Usage: the token can be used to invoke web services 
 * and leverage features provided by force.com platform like Tooling APIs 
 */
public class OAuthTokenGenerator {
	/*
	 * returns oauth org token
	 */
	public static String getOrgToken() {
		return ConnectionHandler.getConnectionHandlerInstance().getSessionIdFromConnectorConfig();
	}
}
