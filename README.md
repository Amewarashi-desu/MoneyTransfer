MoneyTransfer is a simple application made as an implementation for next requirements:

Design and implement a RESTful API (including data model and the backing implementation) for money transfers between accounts.

Explicit requirements:
1. You can use Java, Scala or Kotlin.
2. Keep it simple and to the point (e.g. no need to implement any authentication).
3. Assume the API is invoked by multiple systems and services on behalf of end users.
4. You can use frameworks/libraries if you like (except Spring), but don't forget about
requirement #2 – keep it simple and avoid heavy frameworks.
5. The datastore should run in-memory for the sake of this test.
6. The final result should be executable as a standalone program (should not require
a pre-installed container/server).
7. Demonstrate with tests that the API works as expected.

Implicit requirements:
1. The code produced by you is expected to be of high quality.
2. There are no detailed requirements, use common sense.

There are two entities: 

Account [Account_ID, Balance, CreatedDate]

Payment [Payment_ID, Amount, CreatedDate, Account_ID(FK, from), Account_ID(FK, to)]

Constraints:
Account balance can't be less than 0. Payment can't be between the same account.

Compile, package & run the application: 

`mvn clean package && java -jar target/MoneyTransfer-1.0-SNAPSHOT.jar`

Default port is 8181, but you can specify another port passing him as a first parameter.

API:

| Path          | Method | Request payload                                                  | Response                                                                                                        |
|---------------|--------|------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------|
| /account/{id} | GET    |                                                                  | JSON example:  {"balance":1218403, "created":{...verbose date...},  "id":899}                                   |
| /account/     | POST   | JSON example: {"initialBalance":100000}                          | HTTP 201 and Location of the newly created account (URL)                                                       |
| /payment/{id} | GET    |                                                                  | JSON example: {"amount":29, "created":{...verbose date...}, "rightAccountId":591, "id":1,  "leftAccountId":412} |
| /payment/     | POST   | JSON example: {"amount":10,"rightAccountId":2,"leftAccountId":1} | HTTP 201 and Location of the newly created payment (URL)                                                        |

Technology stack: Undertow, RESTEasy, Weld, h2.

There is a BlackBox test that simulates users working with application.