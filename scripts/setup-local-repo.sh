#!/bin/bash
set -euo pipefail
export JAVA_HOME=/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export PATH="/bin:/usr/bin:/opt/homebrew/bin:${JAVA_HOME}/bin"
ROOT="/Users/b0208627_1/Documents/automation-framework"
REPO="${ROOT}/.local-m2"
STUB="${REPO}/_build"
/bin/rm -rf "${REPO}"
/bin/mkdir -p "${STUB}/src/com/airtel/Models" "${STUB}/src/com/airtel/Utils" "${STUB}/classes" "${STUB}/empty"

/bin/cat > "${STUB}/src/com/airtel/Models/LogItemReq.java" <<'EOF'
package com.airtel.Models;
public class LogItemReq {
    public void setHeaders(String v){}
    public void setTargetApiUrl(String v){}
    public void setResStatusCode(int v){}
    public void setMessage(String v){}
    public void setRequest(String v){}
    public void setQueryParams(String v){}
}
EOF
/bin/cat > "${STUB}/src/com/airtel/Models/DebugLogItemReq.java" <<'EOF'
package com.airtel.Models;
public class DebugLogItemReq {
    public void setAssertStatus(String v){}
    public void setMessage(String v){}
}
EOF
/bin/cat > "${STUB}/src/com/airtel/Utils/PropertyParser.java" <<'EOF'
package com.airtel.Utils;
import java.io.InputStream;
import java.util.Properties;
public class PropertyParser {
    private final Properties props = new Properties();
    public static PropertyParser load(String resource) {
        PropertyParser p = new PropertyParser();
        try (InputStream in = PropertyParser.class.getClassLoader().getResourceAsStream(resource)) {
            if (in != null) p.props.load(in);
        } catch (Exception ignored) {}
        return p;
    }
    public String getProperty(String key){ return props.getProperty(key); }
}
EOF

javac -d "${STUB}/classes" "${STUB}/src/com/airtel/Models/"*.java "${STUB}/src/com/airtel/Utils/PropertyParser.java"
jar cf "${STUB}/sentinel-Models-1.1.1.jar" -C "${STUB}/classes" .
echo stub > "${STUB}/empty/README.txt"
jar cf "${STUB}/optimus-java-1.0.9.jar" -C "${STUB}/empty" .
/bin/cp /Users/b0208627_1/.m2/repository/com/airtel/sentinello4jappender/subcriber-log4j/1.0.3/subcriber-log4j-1.0.3.jar "${STUB}/subcriber-log4j-1.0.2.jar"
/bin/cp /Users/b0208627_1/.m2/repository/com/airtel/subTest-Ng/subscriber-testNg/1.0.7/subscriber-testNg-1.0.7.jar "${STUB}/subscriber-testNg-1.1.8.jar"

put() {
  g="$1"; a="$2"; v="$3"; f="$4"
  path="${REPO}/$(echo "$g" | /usr/bin/tr '.' '/')/${a}/${v}"
  /bin/mkdir -p "${path}"
  /bin/cp "${STUB}/${f}" "${path}/${a}-${v}.jar"
  /bin/cat > "${path}/${a}-${v}.pom" <<POM
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>${g}</groupId>
  <artifactId>${a}</artifactId>
  <version>${v}</version>
</project>
POM
}

put com.airtel.sentinelModels sentinel-Models 1.1.1 sentinel-Models-1.1.1.jar
put com.airtel.optimusJava optimus-java 1.0.9 optimus-java-1.0.9.jar
put com.airtel.sentinello4jappender subcriber-log4j 1.0.2 subcriber-log4j-1.0.2.jar
put com.airtel.subTest-Ng subscriber-testNg 1.1.8 subscriber-testNg-1.1.8.jar
echo "SETUP_OK"
