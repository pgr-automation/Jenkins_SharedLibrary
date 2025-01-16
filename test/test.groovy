// Variables, Constants, and Basic Data Types
def name = "Jenkins"    // String
def age = 10            // Integer
def isActive = true     // Boolean

final VERSION = "1.0.0" // Constant

// Printing values
println "Name: $name, Age: $age, Active: $isActive, Version: $VERSION"

// Lists
def fruits = ["Apple", "Banana", "Cherry"]
println "Fruits:"
fruits.each { println it }  // Iterating over the list

// Maps
def user = [name: "John", age: 30]
println "User Name: ${user.name}, Age: ${user.age}"
println user.name
println user.age
// Ranges
def numbers = 1..5
println "Range: $numbers"


// Closure Example
def greeting = { name1 -> println "Hello, $name1!" }
greeting("World")

// Closure in a method
def executeStep(String stepName, Closure action) {
    println "Starting $stepName..."
    action()
    println "$stepName Completed!"
}

executeStep("Build") {
    println "Building the application..."
}


// Conditional Statements
def dockerFileMap = [
   ["Dockerfile1", "Dockerfile2"]
]
 
dockerFileMap.each { dockerFile ->
    def dockerFiles = dockerFile[0]
        println "Dockerfile: $dockerFiles"
    }

 
 
