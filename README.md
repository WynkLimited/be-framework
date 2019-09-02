**Overview**

This is core automation framework that is extended in LOB frameworks for API automation. API automation frameworks for LOBs are wrapper over this framework and leveraging TestNg for automation & validations. The framework has all important functionality for accessing APIs, validating APIs, comparing JSONs & pulling data from different databases.

Project extending this framework should have the mentioned package structure:

controller/helper - All methods hitting specific API, validating API functionality are included as part of these package.
dto - Request or response pojo, if required should be put as part of this package.
config - All configuration should be put as part of this package.
test - Test classes are mentioned in src/test/java. No business or validation logic should be put in here. All test should be light weight and use methods mentioned in controller or helper classes for validating test case. *TestClasses should alway extend ReportHelper for automated report logging.*

**Use cases solved using the framework**

- *Comparison b/w 2 JSON on same structure:* For discovery APIs or GET API, comparison b/w 2 JSON is used across all LOBs.
- *Comparison b/w 2 JSON on different structure:* For certain use cases, validation of API needs to be carried out with child API or downstream API. For those use cases, this methodology is used.
- *Schema and response code validation:* Basic schema & response code validation that should be done for all API is easily integratable using the framework
- *Data validation with different database:* For transaction APIs, data validations are done with downstreams like Mongo, Oracle, Aerospike etc. Ready to use methods for fetching data from these DB can be wrapped and used for validation.


**How to include**

Add the following dependency in your LOB framework

		<dependency>
			<groupId>com.airtel.api-automation</groupId>
			<artifactId>api-automation-framework</artifactId>
			<version>1.4.20</version>
		</dependency>

**Important Functionality**

*1. API related functionalities*

ApiHelper.java contains all important functions w.r.t accessing API, converting DTO to objects & vice-versa, reading configurations and fetching values at jPath.

*2. API Validations*

ApiResponseValidator.java contains methods for validations of API like schema validations and Json comparator. GenericComparator.java is used for comparing 2 different structured JSON by leveraging Json Paths. JsonComparator is a wrapper on zsonpatch and helps in providing difference b/w 2 JSON of same object. 

*3. Report Helpers*

ReportHelper.java has all method related to reporting. Functionality like automated reporting for accessing API, validation failures in comparison and method for logging additional information are leveraged from this class.

*4. Data Helpers*

The package for data helpers have functionality w.r.t fetching data from multiple data sources like MongoDB, OracleSQL, Aerospike and Excel. It also has TestDataHelper that can be used with @DataProvider method of TestNg for providing multiple data to a test case.
