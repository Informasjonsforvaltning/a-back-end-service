#Test users 

For contract tests on protected resources, the request has to supply a valid jwt token. To set this up two steps are neccessary. 
 - Provide a **JWK** (keystore with public key) to the application (JwkUtils.kt and application-test.properties)
 - Generate **JWT** token based on public key containing the correct access rights and audience (see JwkToken.kt)

##Audience 

Audience is supplied in the `"aud"` field and each service has its own entry


*User with access to resources in the services a-backend-service, fdk-harvest-admin and organization-catalouge:*
```
"aud": [
          "a-backend-service",
          "fdk-harvest-admin",
          "organization-catalogue",
        ]
```

##Authorities
Authorities is supplied in the `"authorities"` field and has three values;

Read: `"publisher:910244132:read"`

Write: `"publisher:910244132:admin"`

System administrator: `"publisher:910244132:"system:root:admin"`

##Other custom fields
User names

`"user_name": "23076102252"` : String of numbers
`"preferred_username": "23076102252"`: String of numbers or email

Name of user
```
"name": "MAUD GULLIKSEN"`,
"given_name": "MAUD"`,
"family_name": "GULLIKSEN"
```
  
