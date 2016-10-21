[![Build Status](https://travis-ci.org/forcedotcom/ApexUnit.svg?branch=master)](https://travis-ci.org/forcedotcom/ApexUnit)
ApexUnit
========

## What is ApexUnit?
ApexUnit is a continuous integration tool for the Force.com platform that executes Apex tests and fetches code coverage results.

ApexUnit comprises of two major components:
- An xUnit based testing framework for the Force.com platform 
- Extensive code coverage metrics with actionable detail for specified Apex classes

## Key Features of ApexUnit
Please refer https://github.com/forcedotcom/ApexUnit/wiki to learn more about the key features of ApexUnit and its usage

## Pre-Requisites
- Java 1.6 or later 
  - http://www.oracle.com/technetwork/java/javase/downloads/index.html 
- Maven 3.0.3 or later (latest version is recommended)
  - Download link : https://maven.apache.org/download.cgi 
  - Installation instructions : https://maven.apache.org/install.html
  - Configuring maven : https://maven.apache.org/configure.html
  
## How to build and execute ApexUnit
- Clone the project onto your local system using the command:
```shell
 git clone https://github.com/forcedotcom/ApexUnit.git 
``` 
This would create a local copy of the project for you.
- (Optional) Open the project in an IDE (Eclipse, IntelliJ, etc.) 
-  There are two ways you can select test classes to execute and select classes you wish to examine the code coverage of
  - regex - identify and provide regex for the test class names that you want to execute in the "-regex.tests" parameter. Example: if you want to execute the tests: My_Apex_controller_Test, My_Apex_builder_Test and My_Apex_validator_Test, identify the regex as "My_Apex_\*_Test". Pass the parameter "-regex.tests My_Apex_\*_Test" in the mvn command.
    - Similarly, you can provide regex for the classes for which you want to examine the code coverage of by using "-regex.classes My_Apex_\*_Class" in the mvn command.
  - Manifest files - Lists of tests can be read from Manifest files. Create a manifest file such as ManifestFile_Unit_Tests.txt in the "src/main/resources" directory of your project. Add test class names to execute in the manifest file. Specify this manifest file in the mvn command like "-manifest.tests". 
    - Similarly, add the class names for which you want to exercise code coverage in a manifest file such as ClasssManifestFile.txt in "src/main/resources" directory of your project and specify this manifest file in the mvn command like "-manifest.classes ClassManifestFile.txt". 
  - Note that multiple regexes and manifest files can be specified using comma seperation(without spaces). Example: "-regex.tests This_Is_Regex1\*,\*Few_Test,Another_\*_regex -manifest.classes ClassManifestFile.txt,MoreClassesManifestFile.txt"
- Go to your project directory (the directory containing pom.xml) in your commandline and execute the following command:
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner"
-Dexec.args="-org.username $username 
			 -org.password $password
			 -sandbox $sandbox
			 -threshold.org $Org_Wide_Code_Coverage_Percentage_Threshold 
			 -threshold.team $team_Code_Coverage_Percentage_Threshold 
			 -regex.classes 
				   $regex_For_Apex_Classes_To_Compute_Code_Coverage 
			 -regex.tests $regex_For_Apex_Test_Classes_To_Execute 
			 -manifest.tests   
				   $manifest_Files_For_Apex_Test_Classes_To_Execute 
			 -manifest.classes 
				   $manifest_Files_For_Apex_Source_Classes_to_compute_code_coverage
			 -threshold.maxtime 
				   $max_time_threshold_for_test_execution_to_abort"
			 -proxy.host
				   $prox_host
			 -proxy.port
				   $proxy_port

``` 
*Please replace all $xyz with the values specific to your environment/project*

Required parameters: 
- -org.username : Username for the org
- -org.password  : Password corresponding to the username for the org

Optional Parameters: 
- -threshold.org (default value: 75) : Org wide minimum code coverage required to meet the code coverage standards
- -threshold.team (default value: 75) : Team wide minimum code coverage required to meet the code coverage standards
- -regex.classes : The source regex used by the team for the apex source classes. All classes beginning with this parameter in the org will be used to compute team code coverage
- -regex.tests  : The test regex used by the team for the apex test classes. All tests beginning with this parameter in the org will be selected to run
- -manifest.tests : Manifest files containing the list of test classes to be executed
- -manifest.classes : Manifest files containing the list of Apex classes for which code coverage is to be computed
- -maxtime : Maximum execution time(in minutes) for a test before it gets aborted
- -proxy.host : Proxy host for external access
- -proxy.port : Proxy port for external access
- -sandbox : set to true to authenticate using test.salesforce.com, false if connecting to login.salesforce.com [default is false]
- -help : Displays options available for running this application

Note: You must provide either of the (-regex.classes OR -manifest.classes) AND either of  -(regex.tests OR -manifest.tests)

Sample command: 
```java
mvn compile exec:java -Dexec.mainClass="com.sforce.cd.apexUnit.ApexUnitRunner" -Dexec.args=" -org.username yourusername@salesforce.com -org.password yourpassword -threshold.org 75  -threshold.team 80 -regex.tests your_regular_exp1_for_test_classes,your_regular_exp2_for_test_classes -regex.classes your_regular_exp1_for_source_classes,your_regular_exp2_for_source_classes -manifest.tests ManifestFile.txt -manifest.classes ClassManifestFile.txt -threshold.maxtime 10 -proxy.host your.proxy-if-required.net -proxy.port 8080"
```
Note: Multiple comma separated manifest files and regexes can be provided. Please do not include spaces while providing multiple regex or manifest files.

On successful completion - the command should indicate that your build succeeded and should generate two report files - **ApexUnitReport.xml** (This is the test report in JUnit format) and **Report/ApexUnitReport.html** (This is the code coverage report in html format)

# Using Manifest files and Regexes

You can populate class names in the Manifest file and/or provide regular expressions(regexes) 
Please refer https://github.com/forcedotcom/ApexUnit/wiki/Manifest-file-vs-regex for the usecases where manifest file(s) and regex(es) option can be used

#Addional options

Use src/main/resources/config.properties to set the below parameters.

1. API_VERSION(Default value: 36.0) : The Partner API version in use for the org. 

2. MAX_TIME_OUT_IN_MS(Default value : 1200000 ==> 20 minutes) : Time out setting for the session, Once timeout occurs, session renewer module is invoked which renews the session. Helpful when you face a connection exception during query executions. 

## Integrating with CI pipeline
CI engines like Jenkins(https://jenkins-ci.org/) can be used to seamlessly integrate ApexUnit with CI pipelines.
Please find the details here: https://github.com/forcedotcom/ApexUnit/wiki/Integrating-with-CI-pipeline

## How to contribute or track Bug/Issue for ApexUnit?
https://github.com/forcedotcom/ApexUnit/wiki/Contribution-and-Bug-Issue-tracking

## Questions/Feedback?
https://github.com/forcedotcom/ApexUnit/wiki/Contact-info