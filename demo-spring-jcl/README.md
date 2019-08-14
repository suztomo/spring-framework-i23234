# Background

bclozel says:

> spring-jcl has a couple of log4j and slf4j dependencies, but those are not the actual
> dependencies you should have to enable logging with a logging framework.
> Those are dependencies that we use to build the specific bridge implementations, but using a
> logging framework requires more than that.

A couple of log4j and slf4j dependencies:
https://github.com/spring-projects/spring-framework/blob/master/spring-jcl/spring-jcl.gradle

```
dependencies {
	optional("org.apache.logging.log4j:log4j-api:${log4jVersion}")
	optional("org.slf4j:slf4j-api:${slf4jVersion}")
}
```

Bridge implementation that use these dependencies is `org.apache.commons.logging.LogAdapter`. The
class checks the presence of the logging framework:

```
		else if (isPresent(SLF4J_SPI)) {
			// Full SLF4J SPI including location awareness support
			logApi = LogApi.SLF4J_LAL;
		}
```

Having slf4j in dependency is not sufficient to enable it.

# Demo

This demo has dependency to spring-jcl, slf4j and logback-classic.
This demo uses `org.apache.commons.logging.Log` to output a message.

Spring-jcl's `org.apache.commons.logging.LogAdapter` detects the presence of slf4j and redirects
the log to slf4j.

Running `suztomo.App` outputs two lines of log message:

```
09:01:03.988 [main] INFO suztomo.App - Hello, spring-jcl log!
09:01:03.990 [main] INFO suztomo.App - Hello, slf4j log!
```

When `logback-classic` dependency is omitted, no message is output:

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

This is what "using a logging framework requires more than that" means.

# My Opinion

However, I don't think declaring slf4j or log4j in pom.xml is wrong information.
Spring-jcl's logging bridge's role is to forward logs to log4j or slf4j.
Picking up logging backend or setting up logger configuration is under slf4j's usage, rather than
spring-jcl's usage. Spring-jcl does not need to declare logging backend in its pom.xml.

Therefore, spring-jcl's logging-bridge options in pom.xml (as it does in
[spring-jcl 5.1.9.RELEASE](https://search.maven.org/artifact/org.springframework/spring-jcl/5.1.9.RELEASE/jar))
is not wrong information.
