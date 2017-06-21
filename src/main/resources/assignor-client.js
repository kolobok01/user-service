var grpc = require('grpc');

var user_service = grpc.load('user_service.proto');


var client = new user_service.core.UserService('localhost:8076',
    grpc.credentials.createInsecure());


function printResponse(error, response) {
    if (error)
        console.log('Error: ', error);
    else
        console.log(response);
}

function getUser(id) {
    console.log(id)
    client.getUser({id: parseInt(id)},
        function (err, assignor) {
            printResponse(err, assignor);
        });
}

var command = process.argv.shift();

if (command == 'get')
    getUser(process.argv[0]);
client.cancel();