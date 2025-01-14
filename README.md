# Jenkins_SharedLibrary

# Groovy concepts 
# Groovy Concepts for Jenkins Shared Libraries

## **1. Basic Syntax and Data Types**

### Variables, Constants, and Basic Data Types
```groovy
// Variable declaration
def name = "Jenkins"    // String
def age = 10            // Integer
def isActive = true     // Boolean

// Constants (by convention, use uppercase names)
final VERSION = "1.0.0" // Cannot be changed

// Printing values
println "Name: $name, Age: $age, Active: $isActive, Version: $VERSION"
```

### Collections: Lists, Maps, Ranges
```groovy
// Lists
def fruits = ["Apple", "Banana", "Cherry"]
fruits.each { println it }  // Iterating over the list

// Maps
def user = [name: "John", age: 30]
println "User Name: ${user.name}, Age: ${user.age}"

// Ranges
def numbers = 1..5
println "Range: $numbers"
```

---

## **2. Closures**

### Understanding Closures
Closures are blocks of code that can be assigned to variables or passed as arguments.
```groovy
def greeting = { name -> println "Hello, $name!" }
greeting("World") // Output: Hello, World!
```

### Closures in Pipelines
```groovy
def executeStep(String stepName, Closure action) {
    println "Starting $stepName..."
    action()
    println "$stepName Completed!"
}

executeStep("Build") {
    println "Building the application..."
}
```

---

## **3. Object-Oriented Programming (OOP)**

### Classes and Objects
```groovy
class User {
    String name
    int age

    // Constructor
    User(String name, int age) {
        this.name = name
        this.age = age
    }

    // Method
    String getDetails() {
        return "Name: $name, Age: $age"
    }
}

def user = new User("Alice", 25)
println user.getDetails()
```

### Inheritance, Polymorphism, and Encapsulation
```groovy
class Animal {
    String sound() { return "Some sound" }
}

class Dog extends Animal {
    @Override
    String sound() { return "Bark" }
}

def dog = new Dog()
println dog.sound() // Output: Bark
```

---

## **4. Dynamic Typing and Duck Typing**

### Dynamic Typing
```groovy
def value = 10  // Initially an Integer
value = "Now a String" // Now it's a String
println value
```

### Duck Typing
```groovy
def printDetails(obj) {
    if (obj.metaClass.respondsTo(obj, "getDetails")) {
        println obj.getDetails()
    } else {
        println "No details available"
    }
}

class Car {
    String getDetails() { return "This is a Car" }
}

printDetails(new Car()) // Output: This is a Car
```

---

## **5. Builders and DSLs**

### XML Builder Example
```groovy
import groovy.xml.MarkupBuilder

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)

xml.root {
    node1("Value 1")
    node2("Value 2")
}

println writer.toString()
// Output:
// <root>
//     <node1>Value 1</node1>
//     <node2>Value 2</node2>
// </root>
```

### Custom DSL
```groovy
def pipeline = {
    stage("Build") {
        println "Building..."
    }
    stage("Test") {
        println "Testing..."
    }
}

def stage(String name, Closure action) {
    println "Stage: $name"
    action()
}

pipeline()
```

---

## **6. Script vs. Class**

### Script Example
```groovy
// Script: Directly executable
println "Running a Groovy Script"
```

### Class Example
```groovy
class MyClass {
    String sayHello(String name) {
        return "Hello, $name"
    }
}

def myClass = new MyClass()
println myClass.sayHello("Jenkins")
```

---

## **7. Exception Handling**

### Try-Catch Example
```groovy
try {
    int result = 10 / 0
} catch (ArithmeticException e) {
    println "Error: Division by zero"
} finally {
    println "Cleanup if needed"
}
```

---

## **8. Annotations**

### Using `@NonCPS`
```groovy
@NonCPS
def calculate() {
    // Non-serializable logic
    return (1..100).sum()
}

def result = calculate()
println "Sum: $result"
```

- Use `@NonCPS` for performance-heavy tasks that don't need Jenkins pipeline CPS context.

---

These Groovy concepts form the foundation of scripting and shared library development in Jenkins pipelines.

