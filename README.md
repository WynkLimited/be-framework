# Sentinel Support with Base Manager Implementation (Beta)

This is beta version that supports integration with sentinel logging and Base Manager.
Full version will be updated over this version only
The version uses below versions of sentinel libraries

| **Sentinel Component**  | **Version**  |
|-------------------------|--------------|
| Subscriber TestNg       |1.1.0         |
| Transformer optimus-java|1.1.1         |
| Sentinel models         |2.0.3         |
| Sentinel Log4j Appender |1.0.2         |

```
Latest Version: 1.4.29
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