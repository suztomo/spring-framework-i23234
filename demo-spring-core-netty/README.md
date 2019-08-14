# Background

bclozel says:

> spring-core depends on netty-buffer, but a Spring application should not depend on Netty directly,
> but rather on Reactor Netty. This is used for managing DataBuffer instances with Reactor Netty as
> a server. So this tells us more about a particular spring-webflux use case, rather than something
> that is actually linked with spring-core itself

Spring-core's netty usage:

```
src/main/java/org/springframework/core/io/buffer/DataBuffer.java (interface)
src/main/java/org/springframework/core/io/buffer/NettyDataBufferFactory.java
src/main/java/org/springframework/core/io/buffer/NettyDataBuffer.java (implementation via Netty)
```

# Demo: AppNetty

suztomo.AppNetty tries to instantiate `NettyDataBufferFactory`:

```
new NettyDataBufferFactory(null);
```

This project fails to compile, because it's missing dependency on Netty:

```
suztomo@suxtomo24:~/spring-framework-i23234/demo-spring-core-netty$ mvn compile
[INFO] -------------------------------------------------------------
[ERROR] COMPILATION ERROR : 
[INFO] -------------------------------------------------------------
[ERROR] /usr/local/google/home/suztomo/spring-framework-i23234/demo-spring-core-netty/src/main/java/suztomo/AppNetty.java:[9,8] cannot access io.netty.buffer.ByteBufAllocator
  class file for io.netty.buffer.ByteBufAllocator not found
[INFO] 1 error
```

This is what "Spring application should not depend on Netty directly" means.
Spring-core's `org.springframework.io.buffer.NettyDataBufferFactory` class is not supposed to touch
in this manner.

# Proper Usage

Proper usage is using `NettyDataBufferFactory` via Reactor Netty. 

> This is used for managing DataBuffer instances with Reactor Netty as a server.

## Reactor Netty

https://github.com/reactor/reactor-netty says:

> Reactor Netty offers non-blocking and backpressure-ready TCP/HTTP/UDP clients & servers based on
> Netty framework.

Maven coordinates: `io.projectreactor.netty:reactor-netty:0.9.0.M3`

> spring-webflux use case

Maven coordinates: `org.springframework:spring-webflux:5.2.0.M3`. This does not have dependency
to netty-buffer either.

# Demo: Spring Webflux

Followed https://spring.io/guides/gs/reactive-rest-service/.

Dependency tree to netty

```
[INFO] --- maven-dependency-plugin:3.1.1:tree (default-cli) @ demo-spring-webflux ---
[INFO] Verbose not supported since maven-dependency-plugin 3.0
[INFO] org.springframework.boot:demo-spring-webflux:jar:1.0.0-SNAPSHOT
[INFO] +- org.springframework.boot:spring-boot-starter-webflux:jar:2.2.0.M5:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter:jar:2.2.0.M5:compile
[INFO] |  |  ...
[INFO] |  +- org.springframework.boot:spring-boot-starter-reactor-netty:jar:2.2.0.M5:compile
[INFO] |  |  +- io.projectreactor.netty:reactor-netty:jar:0.9.0.M3:compile
[INFO] |  |  |  +- io.netty:netty-codec-http:jar:4.1.38.Final:compile
[INFO] |  |  |  |  +- io.netty:netty-common:jar:4.1.38.Final:compile
[INFO] |  |  |  |  +- io.netty:netty-buffer:jar:4.1.38.Final:compile
[INFO] |  |  |  |  +- io.netty:netty-transport:jar:4.1.38.Final:compile
[INFO] |  |  |  |  |  \- io.netty:netty-resolver:jar:4.1.38.Final:compile
[INFO] |  |  |  |  \- io.netty:netty-codec:jar:4.1.38.Final:compile
[INFO] |  |  |  +- io.netty:netty-codec-http2:jar:4.1.38.Final:compile
[INFO] |  |  |  +- io.netty:netty-handler:jar:4.1.38.Final:compile
[INFO] |  |  |  +- io.netty:netty-handler-proxy:jar:4.1.38.Final:compile
[INFO] |  |  |  |  \- io.netty:netty-codec-socks:jar:4.1.38.Final:compile
[INFO] |  |  |  +- io.netty:netty-transport-native-epoll:jar:linux-x86_64:4.1.38.Final:compile
[INFO] |  |  |  |  \- io.netty:netty-transport-native-unix-common:jar:4.1.38.Final:compile
[INFO] |  |  |  \- io.projectreactor.addons:reactor-pool:jar:0.0.1.M3:compile
[INFO] |  |  ...
```

Spring-webflux does not declare netty dependency but spring-boot-starter-webflux has transitive
dependency to netty-buffer:

```
[INFO] +- org.springframework.boot:spring-boot-starter-webflux:jar:2.2.0.M5:compile
[INFO] |  +- org.springframework.boot:spring-boot-starter-reactor-netty:jar:2.2.0.M5:compile
[INFO] |  |  +- io.projectreactor.netty:reactor-netty:jar:0.9.0.M3:compile
[INFO] |  |  |  +- io.netty:netty-codec-http:jar:4.1.38.Final:compile
[INFO] |  |  |  |  +- io.netty:netty-buffer:jar:4.1.38.Final:compile
```

Without netty-buffer, the application fails to run:

```
$ mvn clean package
$ java -jar target/demo-spring-webflux-1.0.0-SNAPSHOT.jar
2019-08-14 10:46:17.798 ERROR 246875 --- [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'reactorClientHttpConnector' defined in class path resource [org/springframework/boot/autoconfigure/web/reactive/function/client/ClientHttpConnectorConfiguration$ReactorNetty.class]: Post-processing of merged bean definition failed; nested exception is java.lang.IllegalStateException: Failed to introspect Class [org.springframework.http.client.reactive.ReactorClientHttpConnector] from ClassLoader [org.springframework.boot.loader.LaunchedURLClassLoader@533ddba]
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:572) ~[spring-beans-5.2.0.RC1.jar!/:5.2.0.RC1]
...
Caused by: java.lang.ClassNotFoundException: io.netty.buffer.ByteBufAllocator
```

> This is used for managing DataBuffer instances with Reactor Netty as
> a server. So this tells us more about a particular spring-webflux use case

Following classes with reference to Netty classes are supposed to run with
spring-boot-starter-webflux:

- Spring-core's `org.springframework.io.buffer.NettyDataBufferFactory`
- Spring-web's `org.springframework.http.client.reactive.ReactorClientHttpConnector`

# Other libraries than Netty  

> spring-core depends on a few reactive libraries, including Kotlin coroutines, reactor, RxJava 1
> and 2. Those are not dependencies that you should necessarily have on classpath or as direct
> dependencies. In fact, a library or a driver can bring/use of one of those and spring-core
> provides an adapter for bridging between various reactive-streams implementations. So again, not a
> spring-core concern nor a dependency that you should worry about at that level


There is a _library A_ (eg., netty-buffer). Spring-core is built with library A to support a certain
functionality X.
Normal usages of spring-core do not require functionality X or library A, and thus spring-core does
not declare dependency to library A.
Users are not expected to directly use the functionality X in spring-core.

```
spring-core
+- library A (only present at compile time)
```

There is a _spring-XXX_ (e.g., spring-boot-starter-webflux) that leverages spring-core's
functionality X.
The spring-XXX has transitive dependency to library A.
Users can depends on spring-XXX which includes both spring-core and library A.
The class path has required dependencies.
They do not encounter `ClassNotFoundException`s.

```
spring-XXX
+- spring-core
+- library A (available at runtime)
```
