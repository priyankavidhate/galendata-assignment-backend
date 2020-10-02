# galendata-assignment-backend

## About
● Created a dynamic web project using eclipse.

● Designed Event Scraper using Java.

● Used MultiThreading to scrape data from given websites.

● This Event Scraper can be extended to run a daily cron job and update the event details
database.

● Designed a Rest API to get event details.

● Used Tomcat server 8.5 for application.

● Handled cors mechanism.

## Steps to run the backend application.

● Run Event scrapper as a java application. This step will get data from the website and store data
in the database.

● Now right-click on the project, select run on server option.

● Now the tomcat server will start running on port 8080.

● The rest URL for to get event details will be
http://localhost:8080/GalenAssignment/rest/events?limit=10;offset=0
