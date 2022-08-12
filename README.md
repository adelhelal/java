# Java

- OpenJDK - VM to translate source code to bytecode that can run machine code on CPU
- Static Java - produces a statically compiled native executable targeting fast startup
- Native Java - ahead-of-time compiles to machine code directly without JIT
  - GraalVM Native Image - ahead-of-time compiler (AOT)
    - Quarkus - Native Java support on Kubernetes
optimizing Java specifically for containers and enabling it to become an effective platform for serverless, cloud, and Kubernetes environments
    - Micronaut framework - building Cloud Native Java microservices using GraalVM
    - Helidon
    - configuration repository- dynamic features like reflection and proxies need "reachability metadata" to help Native Image discover at runtime 
  - Java Native Interface (JNI) library - invoke a library through JNI to use GPU
- TornadoVM - JIT compiler and runtime to run for GPU
- VM options
  - `-Xmx1024M`
  - `-Xms5g`
  - `-Xmx6g`
- 3rd party libraries
  - lombok - editor plugin for declarative getter/setter
- Garbage collection
  - G1 - default since Java 11
  - Serial GC
  - ZGC
  - Shenandoah

### Code

- wildcard generics
  - `ArrayList<? extends Number>` allows any object that extends Number to fill the ArrayList
  - `ArrayList<? super Integer>` allows any object that is the super class of `Integer` i.e. `Number` or `Object`
- streams
  - intermediate (deferred executions) and terminal (action)
  - method reference operator `stream.forEach(System.out::println)` instead of `stream.forEach(s -> System.out::println(s))`
- synchronized - can only be invoked on one thread at a time
```java
public synchronized void increment() {
    c++;
}
```
```java
private Object mutex = new Object();
synchronized (mutex) {
  // do stuff
}
```
- data-oriented
  - Records, sealed interfaces, pattern matching
