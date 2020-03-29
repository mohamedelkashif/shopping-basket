Project for creating shopping basket shopping basket service that is implemented using [Lagom](https://www.lightbend.com/lagom).


#### Initial setup
1. Clone the project to any directory in your computer, open it with any IDE, I recommend using [Intellij](https://www.jetbrains.com/idea/).
2. Install and setup [lombok](https://projectlombok.org/) plugin , you can use this [link](https://projectlombok.org/setup/intellij) for help. 

#### Lombok
Lombok is being used to automatically generate PoJo's.
In IntelliJ you need to install a [plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) to make this work.

# Important notes

1. The application uses [Cassandra](http://cassandra.apache.org/) as an embedded database
2. You don't have to install Cassandra on your system.
3. Each time you will restart the server the data will be deleted as it embedded database.
4. You can view the data in database using Cassandra client like [TablePlus](https://tableplus.com/).
5. The end-point of updating is not finished but the main logic is done besides it is not from the requirement.
6. I am not sure why it is required to make the adding of an item to the shopping basket as PUT request instead of POST request.

#### Running
The project can be started with the following command in terminal or run the project through the IDE
```bash
mvn lagom:runAll
```


## End-Points 

It's possible to interact with the shopping basket service using the following end points:

##### Note: you can use POSTMAN or cURL to interact with the endpoints 

Create a new shopping basket
```bash
curl -X POST 'http://localhost:9000/api/basket' \
 -d '{"userUuid": "1"}'
```

_This returns the uuid of the shopping basket_

Get the shopping basket
```bash
curl -X GET 'http://localhost:9000/api/basket/<UUID>'
```

Get the items of a shopping basket
```bash
curl -X GET 'http://localhost:9000/api/basket/<UUID>'
```

Add an item to the shopping basket ==> 
```bash
curl -X PUT 'http://localhost:9000/api/basket/<UUID>' \
 -d '{"initialAmount": 1, "price":4}'
```


## Author
[Mohamed Elkashif](mailto:mohamedelkashif922@gmail.com
)

## License
[MIT](https://choosealicense.com/licenses/mit/)