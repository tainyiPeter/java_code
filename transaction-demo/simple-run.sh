#!/bin/bash
echo "开始运行Spring事务管理Demo..."
mvn clean compile exec:java -Dexec.mainClass="com.example.demo.TransactionDemoApplication"