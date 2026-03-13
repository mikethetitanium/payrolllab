# SQL Server Setup Guide - Payroll Reporting Lab

## Overview

The Payroll Reporting Lab supports SQL Server out of the box. The MS SQL Server JDBC driver is already included in `pom.xml`.

## Prerequisites

### 1. SQL Server Installation

You need SQL Server installed and running. Options:

#### Option A: SQL Server Express (Free)
- Download: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
- Edition: SQL Server Express (free, 10GB limit)
- Includes: SQL Server Management Studio (SSMS)

#### Option B: SQL Server Developer Edition (Free)
- Download: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
- Edition: Developer Edition (free for development)
- Full features, no production use

#### Option C: Docker (Easiest)
```bash
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=YourPassword123!" \
  -p 1433:1433 \
  -d mcr.microsoft.com/mssql/server:2022-latest
```

### 2. SQL Server Management Studio (SSMS)
- Download: https://learn.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms
- Used to verify database and run queries

## Configuration Steps

### Step 1: Update application.properties

Edit `src/main/resources/application.properties`:

```properties
# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:payrolldb
# spring.datasource.driverClassName=org.h2.Driver
# spring.datasource.username=sa
# spring.datasource.password=
# spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
# spring.h2.console.enabled=true

# Uncomment SQL Server configuration
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=YourPassword123!
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

### Step 2: Verify SQL Server is Running

#### On Windows:
```cmd
sqlcmd -S localhost -U sa -P YourPassword123!
```

#### On Mac/Linux:
```bash
sqlcmd -S localhost -U sa -P YourPassword123!
```

If successful, you'll see the `1>` prompt.

### Step 3: Run the Application

```bash
./mvnw spring-boot:run
```

The application will:
1. Connect to SQL Server
2. Create the `payrolldb` database (if it doesn't exist)
3. Create all tables
4. Initialize sample data
5. Start the web server

### Step 4: Verify in SSMS

1. Open SQL Server Management Studio
2. Connect to: `localhost` (or `localhost,1433`)
3. Login: `sa` / `YourPassword123!`
4. Expand Databases
5. You should see `payrolldb` database
6. Expand Tables to see all payroll tables

## Configuration Details

### Connection String Breakdown

```
jdbc:sqlserver://localhost:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=true
```

- `localhost` - Server address
- `1433` - Default SQL Server port
- `payrolldb` - Database name
- `encrypt=true` - Use encryption
- `trustServerCertificate=true` - Trust self-signed certificates (dev only)

### Hibernate Dialect

```properties
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

This tells Hibernate to generate SQL Server-compatible SQL.

## Common Issues and Solutions

### Issue 1: Connection Refused

**Error**: `com.microsoft.sqlserver.jdbc.SQLServerException: The TCP/IP connection to the host localhost, port 1433 has failed`

**Solution**:
1. Verify SQL Server is running
2. Check port 1433 is open
3. Verify username/password
4. Check firewall settings

### Issue 2: Authentication Failed

**Error**: `Login failed for user 'sa'`

**Solution**:
1. Verify password is correct
2. Ensure SQL Server is in Mixed Authentication mode
3. Reset SA password:
   ```sql
   ALTER LOGIN sa WITH PASSWORD = 'NewPassword123!'
   ```

### Issue 3: Database Already Exists

**Error**: `Cannot create database 'payrolldb' because it already exists`

**Solution**:
1. Drop existing database in SSMS:
   ```sql
   DROP DATABASE payrolldb;
   ```
2. Or change database name in connection string:
   ```properties
   databaseName=payrolldb_v2
   ```

### Issue 4: Encryption Issues

**Error**: `The certificate chain was issued by an authority that is not trusted`

**Solution**: Already handled in connection string with `trustServerCertificate=true`

## Switching Between Databases

### From H2 to SQL Server

1. Edit `application.properties`
2. Comment out H2 config
3. Uncomment SQL Server config
4. Restart application

### From SQL Server to H2

1. Edit `application.properties`
2. Comment out SQL Server config
3. Uncomment H2 config
4. Restart application

### From SQL Server to PostgreSQL

1. Edit `application.properties`
2. Comment out SQL Server config
3. Uncomment PostgreSQL config
4. Restart application

## Verifying Data in SQL Server

### Using SSMS

1. Open SQL Server Management Studio
2. Connect to localhost
3. Expand `payrolldb` database
4. Right-click `employees` table
5. Select "Select Top 1000 Rows"

### Using sqlcmd

```bash
sqlcmd -S localhost -U sa -P YourPassword123! -d payrolldb
```

Then run queries:
```sql
SELECT COUNT(*) FROM employees;
SELECT COUNT(*) FROM payroll_results;
SELECT * FROM employees LIMIT 10;
```

## Performance Considerations

### SQL Server vs H2

| Aspect | H2 | SQL Server |
|--------|----|----|
| Startup | Fast (in-memory) | Slower (disk I/O) |
| Data Persistence | No (lost on restart) | Yes (persistent) |
| Scalability | Limited | Excellent |
| Production Ready | No | Yes |
| Learning | Good | Better |

### Optimization Tips

1. **Add Indexes** (for large datasets):
```sql
CREATE INDEX idx_payroll_results_employee ON payroll_results(employee_id);
CREATE INDEX idx_payroll_results_run ON payroll_results(payroll_run_id);
CREATE INDEX idx_employees_country ON employees(country);
```

2. **Connection Pooling** (already configured by Spring Boot):
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

3. **Query Optimization**:
- Use JdbcTemplate for complex queries (already done)
- Add WHERE clauses to filter early
- Use aggregation at database level

## Backup and Restore

### Backup Database

In SSMS:
1. Right-click `payrolldb`
2. Select "Tasks" → "Back Up..."
3. Choose backup location
4. Click "OK"

Or via sqlcmd:
```sql
BACKUP DATABASE payrolldb 
TO DISK = 'C:\Backups\payrolldb.bak';
```

### Restore Database

In SSMS:
1. Right-click "Databases"
2. Select "Restore Database..."
3. Choose backup file
4. Click "OK"

## Production Deployment

### For Production Use

1. **Use Strong Passwords**:
```properties
spring.datasource.password=ComplexPassword123!@#$%
```

2. **Enable Encryption**:
```properties
spring.datasource.url=jdbc:sqlserver://server:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=false
```

3. **Use Connection Pooling**:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.connection-timeout=30000
```

4. **Enable Logging**:
```properties
logging.level.com.microsoft.sqlserver.jdbc=DEBUG
```

5. **Regular Backups**:
- Schedule automated backups
- Test restore procedures
- Store backups securely

## Troubleshooting Queries

### Check Database Size
```sql
SELECT 
    DB_NAME() as DatabaseName,
    CAST(SUM(size) * 8.0 / 1024 AS DECIMAL(10,2)) as SizeMB
FROM sys.master_files
WHERE DB_NAME(database_id) = 'payrolldb'
GROUP BY database_id;
```

### Check Table Row Counts
```sql
SELECT 
    TABLE_NAME,
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = t.TABLE_NAME) as RowCount
FROM INFORMATION_SCHEMA.TABLES t
WHERE TABLE_SCHEMA = 'dbo';
```

### Check Active Connections
```sql
SELECT 
    DB_NAME(database_id) as DatabaseName,
    COUNT(*) as ConnectionCount
FROM sys.dm_exec_sessions
WHERE database_id > 0
GROUP BY database_id;
```

## Sample SQL Server Queries

### Get Payroll Summary
```sql
SELECT 
    e.country,
    COUNT(DISTINCT e.id) as employees,
    SUM(pr.gross_salary) as total_gross,
    SUM(pr.net_salary) as total_net
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country
ORDER BY total_net DESC;
```

### Get Employee Payslip
```sql
SELECT 
    e.employee_name,
    e.country,
    run.payroll_month,
    pr.gross_salary,
    pr.tax_amount,
    pr.pension,
    pr.net_salary
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE run.payroll_month = '2025-01'
ORDER BY e.employee_name;
```

## Next Steps

1. Configure SQL Server connection
2. Run the application
3. Verify data in SSMS
4. Run reports
5. Export data
6. Explore learning mode

## Support

For SQL Server issues:
- Check SQL Server error logs
- Use SSMS to verify database
- Review application logs
- Test connection with sqlcmd

For application issues:
- Check application logs
- Verify database connection
- Review ARCHITECTURE.md
- Check troubleshooting guides

---

**SQL Server is now ready to use with Payroll Reporting Lab!** 🚀
