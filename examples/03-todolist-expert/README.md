# ToDo List Expert
Based on [01-todolist](../01-todolist) with some more stuff.
* Configuration by implementing `EventStorePublisherConfig` and `EventStoreSubscriberConfig` [see](src/main/java/net/osomahe/todolist/EventStoreConfiguration.java)
* Using custom topic name e.g. [TodoCreatedEvent.java](src/main/java/net/osomahe/todolist/cmd/entity/TodoCreatedEvent.java) via parent event class 
[TodoEvent.java](src/main/java/net/osomahe/todolist/cmd/entity/TodoEvent.java)
[Apache Tamaya](http://tamaya.incubator.apache.org/) was added as an example of configuration provider.


## Operations
Possible operations are:

Commands:
* Create ToDo
    ```bash
    curl -H "Content-Type: application/json" -d '{"name": "learn event sourcing"}' http://localhost:9080/api/todo
    ```
* Complete ToDo
    ```bash
    curl -X PATCH http://localhost:9080/api/todo/081593f6-7d92-4955-8250-c305c892ebd2-1518294637885-0/complete
    ```
* Delete ToDo
    ```bash
    curl -X DELETE http://localhost:9080/api/todo/135e2e73-39ff-48f9-9f38-c2b0a6d55e18-1518294614322-0
    ```

Queries:
* Get all ToDos
    ```bash
    curl http://localhost:9080/api/todo
    ```