# CompositeEntitiesProject
A university exam project about the usage of the FIWARE Framework and ScorpioBroker 

## How to run
```
cd CompositeEntitiesProject/CompositeEntitesProject
mvn clean package
docker compose up
```
## Service
Service | Ports | Base URL |
--- | --- | --- |
ScorpioBroker | 9090 | http://172.17.0.1:9090/ngsi-ld/v1/|
Manager | 8080 | http://172.17.0.1:8080/machine/|
Machine | 8090| http://172.17.0.1:8090/notification/|


 
## REST API
### Manager
#### get all machine
```
GET 
http://172.17.0.1:8080/machine/
```
#### get all possible operation of a machine
```
GET
http://172.17.0.1:8080/machine/{id}/operation
```
### save a new machine
Examples of machine to save could be found on [Machine Manufactoring Folder](CompositeEntitiesProject/CompositeEntitiesManagerPrototype/src/main/resources/exampleOfContextInformation/MachineManufactoring)
```
POST
http://172.17.0.1:8080/machine/
```

### create a new operation
```
POST
http://172.17.0.1:8080/machine/{id}/operation
Body (optional):

  {
    "operationType": "process",
    "description": "Forward",
    "result": "ok",
    "plannedStartAt": "2016-08-22T10:18:16Z",
    "plannedEndAt": "2016-08-28T10:18:16Z",
    "status": "finished",
    "startedAt": "2016-08-22T10:18:16Z",
    "endedAt": "2016-08-28T10:18:16Z",
    "commandSequence": "Forward"
}
```

### delete machine
```
DELETE
http://172.17.0.1:8080/machine/{id}
```

