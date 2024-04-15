
Docker compose - Creates postgres database container and pgadmin4

To run Docker :
   docker compose up 

After running container to access pg-admin: Go to port 5050
 (default email - admin@pgadmin.com , password - admin)

 
For Acessing Postgres : Port 5432
  - Database Name - kotlin_app_db username: root password: root


if you face authentication issues then go to pgconf inside the
docker volume and uncomment the port there and set it to 5432.