# Setting Up Backend Environment

## (Backend) Migrating the Accounts App (sqlite3 )

Before proceeding with any other backend operations, it's important to migrate the accounts app to avoid potential issues with migrations.

```bash
python manage.py makemigrations accounts
python manage.py migrate accounts
```
finally run 
```bash
python manage.py makemigrations
python manage.py migrate 
```


## For Deployment Only (postgres)

Docker compose - Creates postgres database container and pgadmin4

To run Docker : Inside root directory run.
   docker compose up 

After running container to access pg-admin: Port 5050
 (default email - admin@pgadmin.com , password - password)

 
For Acessing Postgres : Port 5432
  - Database Name - kotlin_app_db username: root password: root


if you face authentication issues then go to pgconf inside the
docker volume and uncomment the port there and set it to 5432.