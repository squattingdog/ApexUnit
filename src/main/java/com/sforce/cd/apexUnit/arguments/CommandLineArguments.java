/* 
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

/*
 * CommandLineArguments class used JCommander tool for accepting, validating and assigning to the 
 * command line arguments for the ApexUnit tool
 * The class exposes getter methods for global access of the command line arguments in the tool
 * 
 * @author adarsh.ramakrishna@salesforce.com
 */ 
 
package com.sforce.cd.apexUnit.arguments;

import org.apache.commons.lang.BooleanUtils;

import com.beust.jcommander.Parameter;

public class CommandLineArguments {
	/*
	 * Static variables that define the command line options
	 */
	public static final String ORG_USERNAME = "-org.username";
	public static final String ORG_PASSWORD = "-org.password";
	public static final String MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE = "-manifest.tests";
	public static final String MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION = "-manifest.classes";
	public static final String REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE = "-regex.tests";
	public static final String REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION = "-regex.classes";
	public static final String ORG_WIDE_CODE_COVERAGE_THRESHOLD = "-threshold.org";
	public static final String TEAM_CODE_COVERAGE_THRESHOLD = "-threshold.team";
	public static final String MAX_TEST_EXECUTION_TIME_THRESHOLD = "-threshold.maxtime";
	public static final String PROXY_HOST = "-proxy.host";
	public static final String PROXY_PORT = "-proxy.port";	
	public static final String SANDBOX = "-sandbox";
	
	public static final String HELP = "-help";

	/*
	 * Define Parameters using JCommander framework
	 */
	@Parameter(names = ORG_USERNAME, description = "Username for the org", required = true)
	static private String username = System.getProperty("SERVER_USERNAME_PARAMETER");
	@Parameter(names = ORG_PASSWORD, description = "Password corresponding to the username for the org", required = true)
	static private String password = System.getProperty("SERVER_PASSWORD_PARAMETER");
	@Parameter(names = MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE, description = "Manifest files containing the list of test classes to be executed", variableArity = true)
	static private String testManifestFiles = null;
	@Parameter(names = MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION, description = "Manifest files containing the list of Apex classes for which code coverage"
			+ " is to be computed", variableArity = true)
	static private String classManifestFiles = null;
	@Parameter(names = REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE, description = "The test regex used by the team for the apex test classes. "
			+ "All tests beginning with this parameter in the org will be selected to run", variableArity = true)
	static private String testRegex;
	@Parameter(names = REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION, description = "The source regex used by the team for the apex source classes. "
			+ "All classes beginning with this parameter in the org will be used to compute team code coverage", variableArity = true)
	static private String sourceRegex;
	@Parameter(names = ORG_WIDE_CODE_COVERAGE_THRESHOLD, description = "Org wide minimum code coverage required to meet the code coverage standards", validateWith = PercentageInputValidator.class, variableArity = true)
	static private Integer orgWideCodeCoverageThreshold = 75;
	@Parameter(names = TEAM_CODE_COVERAGE_THRESHOLD, description = "Team wide minimum code coverage required to meet the code coverage standards", validateWith = PercentageInputValidator.class, variableArity = true)
	static private Integer teamCodeCoverageThreshold = 75;
	@Parameter(names = MAX_TEST_EXECUTION_TIME_THRESHOLD, description = "Maximum execution time(in minutes) for a test before it gets aborted", validateWith = PositiveIntegerValidator.class, variableArity = true)
	static private Integer maxTestExecTimeThreshold;
	@Parameter(names = PROXY_HOST, description = "Proxy host if required for access.", required = false)
	static private String proxyHost;
	@Parameter(names = PROXY_PORT, description = "Proxy port if required for access.", validateWith = PositiveIntegerValidator.class, required = false)
	static private Integer proxyPort;
	@Parameter(names = HELP, help = true, description = "Displays options available for running this application")
	static private boolean help;
	@Parameter(names = SANDBOX, description = "Default is false.  Used to determine login endpoint for authentication.", required = false)
	static private String sandbox = "";

	/*
	 * Static getter methods for each of the CLI parameter
	 */
	public static String getOrgUrl() {
		if(isSandbox())
			return "https://test.salesforce.com";
		else
			return "https://login.salesforce.com";
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getTestManifestFiles() {
		return testManifestFiles;
	}

	public static String getClassManifestFiles() { return classManifestFiles; }

	public static String getTestRegex() {
		return testRegex;
	}

	public static String getSourceRegex() {
		return sourceRegex;
	}

	public static Integer getOrgWideCodeCoverageThreshold() {
		return orgWideCodeCoverageThreshold;
	}

	public static Integer getTeamCodeCoverageThreshold() {
		return teamCodeCoverageThreshold;
	}

	public static Integer getMaxTestExecTimeThreshold() {
		return maxTestExecTimeThreshold;
	}
	
	public static String getProxyHost() {
		return proxyHost;
	}

	public static Integer getProxyPort() {
		return proxyPort;
	}
	
	public static boolean isSandbox() {
		return BooleanUtils.toBoolean(sandbox);
	}
	
	public static boolean isHelp() {
		return help;
	}
}

