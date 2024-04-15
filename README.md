
Docker compose - runs postgres database container and pgadmin4

To run Docker :
   docker compose up 

After running container to access pg-admin: Go to:
 localhost:5432
 (default email - admin@pgadmin.com , password - admin)

 To inspect postgres database using pgadmin:
  - create a new server connection using server provide name - postgres on port 5432
  - postgres database name - kotlin_app_db
  - username: admin password: admin


if you face authentication issues then go to pgconf inside the docker volume and uncomment the port ther