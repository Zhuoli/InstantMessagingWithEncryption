.PHONY: clean server client
default: 
	javac server/*.java
	javac client/*.java
server:
	python server.py
client:
	python client.py
clean:
	$(RM) server/*.class
	$(RM) client/*.class
