This is core automation framework that is extended in LOB frameworks for API automation.

- **How to include**

Add the following dependency in your LOB framework

		<dependency>
			<groupId>com.airtel.api-automation</groupId>
			<artifactId>api-automation-framework</artifactId>
			<version>1.4.20</version>
		</dependency>

Important Functionality

1. API related functionalities

ApiHelper.java contains all important functions w.r.t accessing API, converting DTO to objects & vice-versa, reading configurations and fetching values at jPath.

2. API Validations

ApiResponseValidator.java contains methods for validations of API like schema validations and Json comparator. GenericComparator.java is used for comparing 2 different structured JSON by leveraging Json Paths.

