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

# Proper Usage

Proper usage is using `NettyDataBufferFactory` via Reactor Netty. 

> This is used for managing DataBuffer instances with Reactor Netty as a server.

## Reactor Netty

https://github.com/reactor/reactor-netty says:

> Reactor Netty offers non-blocking and backpressure-ready TCP/HTTP/UDP clients & servers based on
> Netty framework.

Maven coordinates: `io.projectreactor.netty:reactor-netty:0.9.0.M3`

> spring-webflux use case

Maven coordinates: `org.springframework:spring-webflux:5.2.0.M3`.

# Demo: Spring Webflux

Followed https://spring.io/guides/gs/reactive-rest-service/.


## Spring Webflux

[spring-webflux 5.2.0.M3](
https://repo.spring.io/libs-milestone-local/org/springframework/spring-webflux/5.2.0.M3/spring-webflux-5.2.0.M3.pom)
has following dependencies:

```
<dependencies>
  <dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
    <version>3.3.0.M2</version>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>5.2.0.M3</version>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-core</artifactId>
    <version>5.2.0.M3</version>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-web</artifactId>
    <version>5.2.0.M3</version>
    <scope>compile</scope>
  </dependency>
</dependencies>
```

This still does not have Netty.

[reactor-core 3.3.0.M3](
https://repo.spring.io/libs-milestone-local/io/projectreactor/reactor-core/3.3.0.M3/reactor-core-3.3.0.M3.pom)
has following dependencies:

```
<dependencies>
  <dependency>
    <groupId>com.google.code.findbugs</groupId>
    <artifactId>jsr305</artifactId>
    <version>3.0.2</version>
    <scope>compile</scope>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
    <version>RELEASE</version>
    <scope>compile</scope>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>io.projectreactor.tools</groupId>
    <artifactId>blockhound</artifactId>
    <version>1.0.0.M4</version>
    <scope>compile</scope>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib</artifactId>
    <version>1.3.31</version>
    <scope>compile</scope>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>org.reactivestreams</groupId>
    <artifactId>reactive-streams</artifactId>
    <version>1.0.2</version>
    <scope>compile</scope>
  </dependency>
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.12</version>
    <scope>compile</scope>
    <optional>true</optional>
  </dependency>
</dependencies>
```
