# PTM-Project

## Overview

PTM-Project is a Java-based system implementing an event-driven architecture using agents, topics, and an HTTP server. The project includes various agents performing mathematical operations, a topic-based messaging system, and a multi-threaded HTTP server handling requests.

##  Project Structure

```
PTM-Project/
│── agents/                  # Contains agent classes (e.g., BinOpAgent, IncAgent, ParallelAgent, PlusAgent)
│── config/                  # Configuration files and classes (e.g., Config.java, GenericConfig.java)
│── graph/                   # Graph implementation for agent-topic relationships
│── http/                    # HTTP server implementation (e.g., MyHTTPServer, Servlet, RequestParser)
│── topics/                  # Message and topic management classes (e.g., Message.java, Topic.java, TopicManagerSingleton)
│── MainTrain.java            # Entry point for running and testing the project
│── bin/                      # Compiled Java files (ignored in Git)
│── .gitignore                # Files to be ignored by Git
│── README.md                 # Project documentation
```

## 🚀 How to Compile and Run

### **1️ Compile the project**

Ensure you have **Java 23** installed. Then, open a terminal inside the project directory and run:

```bash
javac -d bin $(find . -name "*.java")
```

### **2️ Run the project**

```bash
java -cp bin test.MainTrain
```

## ⚙ Features

- **Agent-based system**: Different agents perform operations and interact through topics.
- **Topic-based messaging**: Topics facilitate communication between agents.
- **Graph representation**: Relationships between agents and topics are stored in a graph.
- **Multi-threaded HTTP server**: Handles client requests efficiently.
- **Configuration-driven architecture**: Supports different configurations for agents.

##  Technologies Used

- **Java 23**
- **Multi-threading (ExecutorService)**
- **Event-driven messaging system**
- **HTTP server implementation**

## Future Improvements

- Add RESTful API support
- Implement persistence for messages and configurations
- Improve logging and debugging tools

##  License

This project is open-source and available under the MIT License.

---

 **Developed by Noamoscato** 

