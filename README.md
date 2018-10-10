# Technical-Work

It is an Technical Exam -work in Japan-
Here I develop simple search application.

Setup:
1. Create a database according to provided schema.
2. Change the connection string in model class constractor.

General Guide:
The main page of app has no front-end. You will just see a statement that your app is working.
To browse Developer UI: http://example.com/?developer_ui

NB: change example.com accordingly.

API Guide:
API supports three types of request GET, PUT and DELETE.

Response Type: all response will be in JSON string.

NB: all API request must be sent to main domain with required parameter.

Add Developer: send PUT request with action, api_key, email, plang(Programming Language), and lang (Language).
curl command: curl -X "PUT" http://example.com -d action=create_developer -d api_key=123456 -d eamil=email@example.com -d plang=Java -d lang=jp

NB: use ',' for multiple Language and Programming language.

Delete Developer: send DELETE request with action, api_key, and id ( Developer ID: Change developer_id accordingly ).
curl command: curl -X "DELETE" http://example.com -d action=delete_developer -d api_key=123456 -d id=3

Retrive Developer: send GET request with action, api_key, and count (Number of Developer to retrieve), lang (optional), plang (optional)
curl command: curl "http://example.com/?action=retrieve_developer&api_key=123456&count=5&lang=jp&plang=PHP"

Add Interview: send PUT request with action, api_key, id (Developer ID), score, comment
curl command: curl command: curl -X "PUT" http://example.com -d action=add_interview -d api_key=123456 -d score=5 -d comment="Sample Comment"

Delete Interview: send DELETE request with action, api_key, id (Interview ID)
curl command: curl -X "DELETE" http://example.com -d action=delete_interview -d api_key=123456 -d id=3

Retrieve Interview: send GET request with action, api_key, count, id (Developer ID), sort (optional : ASC or DESC)
curl command: curl command: curl "http://example.com/?action=retrieve_interviews&api_key=123456&count=5&id=2&sort=ASC"

In case of any error you will get descriptive error message.
