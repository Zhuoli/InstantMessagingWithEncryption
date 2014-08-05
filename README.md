Encrypted Instant Messaging App. 
======================================
Requirements: JDK 1.6+

User log in the client application, client will send user name and hashed password to server to authenticate user. Here we use mutual authentication for both server side and client side. 

##Make tutorial:
  * Compile:     make
  * Run server:  make server
  * Run client:  make client

##Application comands:
### server:
  * 'ls' or 'list': show all the users
  *  'password NAME PASSWORD': change or create new user along with password
### client:
  * 'ls': show active users
  * 'send USER CONTENT': send content to the user

