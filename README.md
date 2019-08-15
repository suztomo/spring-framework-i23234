# Servlet Versions in Spring-framework

bcloze says:

> in Spring Framework modules, we're using a various combination of Servlet versions. While the
> Framework itself is compatible with Servlet 3.1+, some modules might build specific support for
> Servlet 4 features. Reading the versions of those optional dependencies is just misleading.

Picking examples, spring-web has dependency to servlet-api 3.1.0:

```
dependencies {
   ...
	optional("javax.servlet:javax.servlet-api:3.1.0")
```

and spring-webflux has dependency to servlet-api 4.0.1 to support Servlet 4 features:

```
dependencies {
  ...
	compile(project(":spring-web"))
  ...
	optional("javax.servlet:javax.servlet-api:4.0.1")
```

## My Opinion

The optional dependencies above of (1) spring-web depending on Servlet 3.1 and (2) spring-webflux on
Servlet 4, reflect your explanation below quite well:

> While the Framework itself is compatible with Servlet 3.1+, some modules might build specific
> support for Servlet 4 features.

I don't see these optional dependencies misleading.

# Servlet and clashes

bcloze says:

> supporting various servlet containers means that we need to have several of those on classpath.
> We have to introduce dependency exclusions to avoid clashes (especially for API specs). Again,
> those are misleading because a Spring application should not have to do that.

For example spring-webflux has following in build.gradle:

```
	optional("org.apache.tomcat:tomcat-websocket:${tomcatVersion}") {
		exclude group: "org.apache.tomcat", module: "tomcat-websocket-api"
		exclude group: "org.apache.tomcat", module: "tomcat-servlet-api"
	}
	optional("org.eclipse.jetty.websocket:websocket-server") {
		exclude group: "javax.servlet", module: "javax.servlet"
	}
	optional("io.undertow:undertow-websockets-jsr:${undertowVersion}") {
		exclude group: "org.jboss.spec.javax.websocket", module: "jboss-websocket-api_1.1_spec"
	}
```

## My Opinion

Optional dependencies do not cause clashes. Maven adds these optional dependencies to a class path
only when it is project's direct dependencies.
For example spring-web's pom.xml declaring optional dependency to servlet-api 3.1.0 (as it does in
[5.1.9.RELEASE](https://search.maven.org/artifact/org.springframework/spring-web/5.1.9.RELEASE/jar))
does not have any effect in a Spring application's project. Users do not need to write exclusions
for servlet-api.



# javax specs

> now about the javax specs: they do not list the dependencies you need for a specific feature (you
> need the actual implementations, without our custom exclusions). The versions we're using do not
> show necessarily the minimum version supported by Spring, nor the advised one.

I need to figure out what is javax specs this comment is referring.

# "-all.jar" 

> Spring Framework is sometimes depending on "-all.jar" variants for historical reasons, and doing
> so in an application or library is not advised. Arguably we should not do that in our build and
> this is something we should fix. But in the meantime this is incorrect/misleading information
> we're publishing

spring-web's build.gradle has:
```
	optional("io.netty:netty-all")
```

Root build.gradle has hamcrest-all, but `testCompile` clause is not my interest of pom.xml in Maven
Central:

```
		testCompile("org.hamcrest:hamcrest-all:1.3")
```


