# Examination Management System

Minor Project NSP - Java Swing + JDBC + MySQL

**Developed by:** Khyati Arora (PIET25CS079)

## Easy way (Mac)
Double-click `run.command`. It creates the database the first time and opens the login screen.

## How to run in IntelliJ
1. Open MySQL Workbench, open `database.sql` and run it (creates database `examdb`, tables and sample data).
2. (Only if your MySQL root has a password) write it in `PASSWORD` in `DBConnection.java`.
3. In IntelliJ: File > Project Structure > Libraries > + > Java > select `lib/mysql-connector-j-8.4.0.jar`.
4. Run `DBConnection.java` once. It should print "Connection successful!".
5. Run `LoginPage.java`.

## Login details
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Admin |
| user | user123 | User |
