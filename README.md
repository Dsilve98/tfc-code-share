# Research Centers Manager

The project was designed to be used internally by the Interdisciplinary Research Center for Education and Development, which influenced some decisions throughout development.

### Application Features:

* ORCID authentication
* Synchronization with the Ciência Vitae
* Personal Data Query
* Scientific Activities Query
* Researcher profile management
* Researcher registration and removal
* All data export to Excel

### Application Entity-Relationship Diagram
![image](https://user-images.githubusercontent.com/43912987/180653265-714b16a8-2156-423b-aec7-7d237fa42454.png)

---
### Development Setup

#### bulma-calendar
```
> npm i bulma-calendar
```
#### bulma-switch
```
> npm i bulma-switch
```
#### Generate the CSS file
To compile the css via sass file (you need to make a change in the sass file to generate the css)
```
> npm start
```

### DB Setup

* Install MySQL and run it
* Connect Intellij to MySQL through the Database tab > New Data Source (MySQL)
* Create a schema XXX with the application name (ex: "gestao-tfc") and create a user XXX with the same name as the application and 
give him permissions on the schema created in the previous step

```
create database trabalho;

create user 'trabalho'@'localhost' identified by 'passwordtrabalho';

grant all privileges on trabalho.* to 'trabalho'@'localhost';
```

* Change src/resources/application.properties with the DB name, user name and pass
