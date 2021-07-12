# SpringAuthApi

As the name implies, this is an attempt to create an authorisation API using Spring.

## API Structure

The client sends a POST request to one of the defined endpoints. The response is a JSON object with
two fields: `errorCode` and `errorMessage`.

`errorCode` zero means the request succeeded. Any other value signals an error, which is explained
in a human-readable form in the field `errorMessage`.

Example responses:

```json
{
  "errorCode": 0,
  "errorMessage": "All is well"
}
```

```json
{
  "errorCode": 3,
  "errorMessage": "Something went wrong"
}
```

## Endpoints

### /register

Example request body:

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "johnny@example.com",
  "phoneNumber": "+78005553535",
  "password": "johnnyiscool123"
}
```

Error codes:

- 0 - the user has been successfully registered and can now log in.
- 1 - the specified password is invalid.
  See [UserService](src/main/java/com/nonk/spring_auth_api/user/UserService.java).
- 2 - the given credentials are invalid.
- 3 - the email is already in use.

### /login

This endpoint doesn't do much besides checking if a user with the specified credentials exists.

Example request body:

```json
{
  "email": "johnny@example.com",
  "password": "johnnyiscool123"
}
```

Error codes:

- 0 - the user exists and can log in.
- 1 - the user doesn't exist, or the email is incorrect.
- 2 - the given password doesn't match the user's true one.

### /restore

In case a user loses their password, they can reset it using a restoration code. Performing this
request generates such code and sends it to the user's email address.

For security reasons, the restoration code becomes invalid 30 minutes after it's generated.

Example request body:

```
johnny@example.com
```

Error codes:

- 0 - the code has been generated and sent.
- 1 - the specified email doesn't associate with any user.

### /restore/submit

By submitting the generated restoration code, a user can reset their password.

Example request body:

```json
{
  "email": "johnny@example.com",
  "restorationCode": "069420",
  "newPassword": "johnnyistoocool123"
}
```

Error codes:

- 0 - the password has been successfully reset.
- 1 - the user doesn't exist.
- 2 - the specified password is not valid.
- 3 - the restoration code is older than 30 minutes.
- 4 - the specified restoration code doesn't match the generated one.
