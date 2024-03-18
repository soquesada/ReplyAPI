# ReplyAPI Instructions
I chose a feature-based file structure, hence the `payments/` and `user/` directories.

```
ReplyAPI
└───src/
    ├───main/
    │   └───java/
    │       └───com/
    │           └───example/
    │               └───replyapi/
    │                   ├───payment/
    │                   │   ├───Payment.java
    │                   │   ├───PaymentController.java
    │                   │   └───PaymentService.java
    │                   ├───user/
    │                   │   ├───User.java
    │                   │   ├───UserController.java
    │                   │   └───UserService.java
    │                   └───ReplyAPIApplication.java
    └───test/
        └───java/
            └───com/
                └───example/
                    └───replyapi/
                        ├───payment/
                        │   └───PaymentServiceTest.java
                        └───user/
                            └───UserServiceTest.java
```

## Running Application
### From the Command Line
Assuming Maven is installed:
1. Navigate to the ReplyAPI directory on the command line.
2. Run the command `mvn spring-boot:run`.
### From IntelliJ
1. Open the ReplyAPI directory as a project on IntelliJ.
2. Locate the `ReplyApiApplication.java` file in `src/main/java/com/example/replyapi/ReplyApiApplication.java`.
3. Run the file.
## Running the Unit Tests
### IntelliJ
All unit tests can be found in `src/test/java/com/example/replyapi/`. `PaymentServiceTest.java` and `UserServiceTest.java` are the unit tests, testing my PaymentService and UserService classes.
1. On IntelliJ, navigate to these files and run the tests.