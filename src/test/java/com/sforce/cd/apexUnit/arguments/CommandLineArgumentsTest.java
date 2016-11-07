/*
 * Copyright (c) 2016, salesforce.com, inc.
 * All rights reserved.
 * Licensed under the BSD 3-Clause license.
 * For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.sforce.cd.apexUnit.arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.beust.jcommander.JCommander;

public class CommandLineArgumentsTest {
	private static Logger logs = LoggerFactory.getLogger(CommandLineArgumentsTest.class);
	CommandLineArguments cmdLineArgs = new CommandLineArguments();
	// initialize test parameter values.
	// Change the below values to execute various positive/negative test cases

	private String SERVER_ORG_LOGIN_URL_PARAMETER = null;
	private String ORG_USERNAME_PARAMETER = null;
	private String ORG_PASSWORD_PARAMETER = null;
	private String MANIFEST_FILE_PARAMETER = null;
	private String CLASS_MANIFEST_FILE_PARAMETER = null;
	private String TEST_PREFIX_PARAMETER = null;
	private String ORG_WIDE_CC_THRESHOLD_PARAMETER = null;
	private String TEAM_CC_THRESHOLD_PARAMETER = null;
	private String CLASS_PREFIX_PARAMETER = null;
	private String MAX_TEST_EXEC_TIME_THRESHOLD = null;
	private String SANDBOX = null;

	public CommandLineArgumentsTest() {
		//CommandLineArguments.ORG_USERNAME set this in your environment
		//CommandLineArguments.ORG_PASSWORD set this in your environment
		System.setProperty(CommandLineArguments.MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION
					, "./src/main/resources/ManifestFile_For_Unit_Test_Classes.txt");
		System.setProperty(CommandLineArguments.MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE
					, "./src/main/resources/ClassManifestFile.txt");
		System.setProperty(CommandLineArguments.ORG_WIDE_CODE_COVERAGE_THRESHOLD, "0");
		System.setProperty(CommandLineArguments.TEAM_CODE_COVERAGE_THRESHOLD, "0");
		System.setProperty(CommandLineArguments.MAX_TEST_EXECUTION_TIME_THRESHOLD, "1200000");
		System.setProperty(CommandLineArguments.SANDBOX, "false");
	}
	
	@BeforeTest
	public void setup() {
		SERVER_ORG_LOGIN_URL_PARAMETER = CommandLineArguments.getOrgUrl();
		ORG_USERNAME_PARAMETER = System.getenv(CommandLineArguments.ORG_USERNAME);  //pulled from environment variable
		ORG_PASSWORD_PARAMETER = System.getenv(CommandLineArguments.ORG_PASSWORD);  //pulled from environment variable
		MANIFEST_FILE_PARAMETER = System
				.getProperty(CommandLineArguments.MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE);
		CLASS_MANIFEST_FILE_PARAMETER = System
				.getProperty(CommandLineArguments.MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION);
		TEST_PREFIX_PARAMETER = System
				.getProperty(CommandLineArguments.REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE);
		ORG_WIDE_CC_THRESHOLD_PARAMETER = System
				.getProperty(CommandLineArguments.ORG_WIDE_CODE_COVERAGE_THRESHOLD);
		TEAM_CC_THRESHOLD_PARAMETER = System
				.getProperty(CommandLineArguments.TEAM_CODE_COVERAGE_THRESHOLD);
		CLASS_PREFIX_PARAMETER = System
				.getProperty(CommandLineArguments.REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION);
		MAX_TEST_EXEC_TIME_THRESHOLD = System
				.getProperty(CommandLineArguments.MAX_TEST_EXECUTION_TIME_THRESHOLD);
		SANDBOX = System
				.getProperty(CommandLineArguments.SANDBOX);
		
		StringBuffer arguments = new StringBuffer();

		arguments.append(CommandLineArguments.SANDBOX);
		arguments.append(appendSpaces("false"));
		arguments.append(CommandLineArguments.ORG_USERNAME);
		arguments.append(appendSpaces(ORG_USERNAME_PARAMETER));
		arguments.append(CommandLineArguments.ORG_PASSWORD);
		arguments.append(appendSpaces(ORG_PASSWORD_PARAMETER));
		arguments.append(CommandLineArguments.MANIFEST_FILES_WITH_TEST_CLASS_NAMES_TO_EXECUTE);
		arguments.append(appendSpaces(MANIFEST_FILE_PARAMETER));
		arguments.append(CommandLineArguments.MANIFEST_FILES_WITH_SOURCE_CLASS_NAMES_FOR_CODE_COVERAGE_COMPUTATION);
		arguments.append(appendSpaces(CLASS_MANIFEST_FILE_PARAMETER));
		arguments.append(CommandLineArguments.REGEX_FOR_SELECTING_TEST_CLASSES_TO_EXECUTE);
		arguments.append(appendSpaces(TEST_PREFIX_PARAMETER));
		arguments.append(CommandLineArguments.ORG_WIDE_CODE_COVERAGE_THRESHOLD);
		arguments.append(appendSpaces(ORG_WIDE_CC_THRESHOLD_PARAMETER));
		arguments.append(CommandLineArguments.TEAM_CODE_COVERAGE_THRESHOLD);
		arguments.append(appendSpaces(TEAM_CC_THRESHOLD_PARAMETER));
		arguments.append(CommandLineArguments.REGEX_FOR_SELECTING_SOURCE_CLASSES_FOR_CODE_COVERAGE_COMPUTATION);
		arguments.append(appendSpaces(CLASS_PREFIX_PARAMETER));
		arguments.append(CommandLineArguments.MAX_TEST_EXECUTION_TIME_THRESHOLD);
		arguments.append(appendSpaces(MAX_TEST_EXEC_TIME_THRESHOLD));
		String[] args = arguments.toString().split(" ");

		System.out.println(CommandLineArguments.ORG_USERNAME + ": " + ORG_USERNAME_PARAMETER);
		System.out.println(CommandLineArguments.ORG_PASSWORD + ": " + ORG_PASSWORD_PARAMETER);
		JCommander jcommander = new JCommander(cmdLineArgs, args);
	}

	@Test
	public void getOrgWideCodeCoverageThreshold() {
		Assert.assertEquals(CommandLineArguments.getOrgWideCodeCoverageThreshold().intValue(),
				Integer.parseInt(ORG_WIDE_CC_THRESHOLD_PARAMETER));
	}

	@Test
	public void getManifestFileLoc() {
		Assert.assertEquals(CommandLineArguments.getTestManifestFiles(), MANIFEST_FILE_PARAMETER);
	}

	@Test
	public void getClassManifestFileLoc() {
		Assert.assertEquals(CommandLineArguments.getClassManifestFiles(), CLASS_MANIFEST_FILE_PARAMETER);
	}

	public void getPassword() {
		Assert.assertEquals(CommandLineArguments.getPassword(), ORG_PASSWORD_PARAMETER);

	}

	@Test
	public void getTeamCodeCoverageThreshold() {
		Assert.assertEquals(CommandLineArguments.getTeamCodeCoverageThreshold().intValue(),
				Integer.parseInt(TEAM_CC_THRESHOLD_PARAMETER));
	}

	@Test
	public void getTestPrefix() {
		Assert.assertEquals(CommandLineArguments.getTestRegex(), TEST_PREFIX_PARAMETER);
	}

	@Test
	public void getUrl() {
		Assert.assertEquals(CommandLineArguments.getOrgUrl(), SERVER_ORG_LOGIN_URL_PARAMETER);
	}

	@Test
	public void getUsername() {
		Assert.assertEquals(CommandLineArguments.getUsername(), ORG_USERNAME_PARAMETER);
	}

	@Test
	public void getClassPrefix() {
		Assert.assertEquals(CommandLineArguments.getSourceRegex(), CLASS_PREFIX_PARAMETER);
	}
	
	String appendSpaces(String input) {
		if (input != null && !input.isEmpty()) {
			return " " + input.toString() + " ";
		}
		return " ";
	}

}
