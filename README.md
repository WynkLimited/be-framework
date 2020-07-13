# Sentinel Support with Base Manager Implementation (Beta)

This is beta version that supports integration with sentinel logging and Base Manager.
Full version will be updated over this version only
The version uses below versions of sentinel libraries

| **Sentinel Component**  | **Version**  |
|-------------------------|--------------|
| Subscriber TestNg       |1.1.2         |
| Transformer optimus-java|1.1.3         |
| Sentinel models         |2.0.5         |
| Sentinel Log4j Appender |1.0.4         |

```
Latest Version: 1.4.44
```

- You can checkout this implementation using git tags:

```shell
git clone https://code.airtelworld.in:7999/api/automation-framework.git
git checkout v1.4.29
```

- To use or migrate 
Add the following dependency in your LOB framework
``` xml
	<dependency>
		<groupId>com.airtel.api-automation</groupId>
		<artifactId>api-automation-framework</artifactId>
		<version>1.4.40</version>
	</dependency>
```

- How to use base manager, can be found [here](https://code.airtelworld.in:7990/bitbucket/projects/API/repos/automation-framework/browse?at=refs%2Ftags%2Fv1.4.28)
- Sample project( wrt. same version of above sentinel libraries) using sentinel logging can be found  [here](https://code.airtelworld.in:7990/bitbucket/projects/API/repos/sentinel-sampleonboard/browse?at=refs%2Ftags%2Fv1.0.7)
- How to use Sentinel TestNg Subscriber can be found [here](https://code.airtelworld.in:7990/bitbucket/projects/API/repos/sentinel-subscriber-testng/browse?at=refs%2Ftags%2Fv1.0.7)

**Overview**

This is core automation framework that is extended in LOB frameworks for API automation. API automation frameworks for LOBs are wrapper over this framework and leverage TestNg for automation & validations. The framework has all important functionality for accessing APIs, validating APIs, comparing JSONs & pulling data from different databases.

Project extending this framework should have the mentioned package structure:

src/main/java/constants - Containing all LOB specific constansts.
src/main/java/controller - All methods hitting specific API, validating API functionality are included as part of these package.
src/main/java/helper - LOB specific helper methods can be added here. Example: starting and stopping a  specific process on the server.
src/main/java/dto - Request or response pojo, if required should be put as part of this package.
config - All configuration should be put as part of this package.
src/main/test - Test classes are mentioned in src/test/java. No business or validation logic should be put in here. All tests should be light weight and use methods mentioned in controller or helper classes for validating test case. *TestClasses should alway extend ReportHelper for automated report logging.*

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
Example: methods like ‘getResponseWithHeaders, postResponseWithHeaders, convertToJson’ and the like.

*2. API Validations*

ApiResponseValidator.java contains methods for validations of API like schema validations and Json comparator. GenericComparator.java is used for comparing 2 different structured JSON by leveraging Json Paths. JsonComparator is a wrapper on zsonpatch and helps in providing difference b/w 2 JSON of same object. 

*3. Report Helpers*

ReportHelper.java has all method related to reporting. Functionality like automated reporting for accessing API, validation failures in comparison and method for logging additional information are leveraged from this class.
This class has to be extended by each Test class. It initialises the report file at the beginning of each class and flushes it at the end of it.
The results of each suite/individual run are also pushed to InfluxDb for monitoring.

*4. Data Helpers*

The package for data helpers have functionality w.r.t fetching data from multiple data sources like MongoDB, OracleSQL, Aerospike and Excel. 
TestDataHelper.java class has multiple implementations of @DataProvider annotation of TestNg. It fetches data from different sources and feeds it into the DataProvider of TestNg.
