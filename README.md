# Simple spring boot project with JWT and MongoDB
A simple spring boot REST API project that save/retrieve note in cloud mongodb.
## Endpoint
### User endpoints
* Sign-Up: `/auth/register` Method: `POST`
* Login: `/auth/loging` Method: `POST`
* Refresh Tokens: `/auth/refresh` Method: `POST`

### Note endpoints
* Create Note: `/notes` Method: `POST`
* Retrieve Notes: `/notes` Method: `GET`
* Delete Note: `/notes/{id}` Method: `DELETE`