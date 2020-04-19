# SpringReactiveMongoWebApplication

Getting Started
Reference Documentation

For further reference, please consider the following sections:

    Official Apache Maven documentation
    Spring Boot Maven Plugin Reference Guide
    Spring Boot DevTools
    Spring Data Reactive MongoDB



# For TeamCity and SonarQube configuration 

Set environment variable 
As SONAR_QUBE=E:\Education\Software\Education\java vedio tutorial\sonarqube\sonarqube
and PATH = %SONAR_QUBE%\bin\windows-x86-64

Go to command prompt and enter command StartSonar.bat
Sonar will run on http://localhost:9000


Integration Sonar Qube with Teamcity
Install Sonar Runner plugin in teamcity.

Add new build step in project build configuration

Caused by: Please provide compiled classes of your project with sonar.java.binaries property
To Resolve above issue please add one configuration parameter sonar.java.binaries=target and in sonar build step replace 
Sources location: value with %sonar.java.binaries%
