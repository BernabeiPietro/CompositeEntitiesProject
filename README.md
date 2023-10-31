# CompositeEntitiesProject
A university exam project about the usage of the FIWARE Framework and ScorpioBroker 

## How to run
'''
cd CompositeEntitiesProject/CompositeEntitesProject
mvn clean package
docker compose up
'''
## Service
Service | Ports | Base URL |
--- | --- | --- |
ScorpioBroker | 9090 | http://172.17.0.1:9090/ngsi-ld/v1/|
Manager | 8080 | http://172.17.0.1:8080/machine/|
Machine | 8090| http://172.17.0.1:8090/notification/|


 
## REST API
### Manager
#### get all machine
'''
GET 
http://172.17.0.1:8080/machine/
'''
#### get all possible operation of a machine
'''
GET
http://172.17.0.1:8080/machine/{id}/operation
'''
### save a new machine
Examples of machine to save could be found on [Machine Manufactoring Folder]()
'''
POST
http://172.17.0.1:8080/machine/
'''

### create a new operation
'''
POST
http://172.17.0.1:8080/machine/{id}/operation
Body:
{
  


}
'''

### delete machine
'''
DELETE
http://172.17.0.1:8080/machine/{id}
'''

