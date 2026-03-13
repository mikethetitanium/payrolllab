# SQL Server Quick Start - 5 Minutes

## TL;DR - Get Running with SQL Server

### Step 1: Have SQL Server Running
```bash
# If using Docker (easiest)
docker run -e "ACCEPT_EULA=Y" -e "SA_PASSWORD=YourPassword123!" \
  -p 1433:1433 -d mcr.microsoft.com/mssql/server:2022-latest
```

### Step 2: Update Configuration
Edit `src/main/resources/application.properties`:

Find this section:
```properties
# --- H2 Database (Default - In-Memory) ---
spring.datasource.url=jdbc:h2:mem:payrolldb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

Replace with:
```properties
# --- SQL Server Configuration ---
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=payrolldb;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=YourPassword123!
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

### Step 3: Run Application
```bash
./mvnw spring-boot:run
```

### Step 4: Open Browser
```
http://localhost:8080
```

**Done!** Your payroll system is now using SQL Server. 🎉

---

## Verify It's Working

### Check in Application
1. Open `http://localhost:8080`
2. Dashboard should show data
3. Click "Reports" to see payroll data

### Check in SQL Server Management Studio
1. Open SSMS
2. Connect to: `localhost`
3. Login: `sa` / `YourPassword123!`
4. Expand Databases → `payrolldb`
5. Expand Tables → See all payroll tables

### Check via Command Line
```bash
sqlcmd -S localhost -U sa -P YourPassword123! -d payrolldb
```

Then run:
```sql
SELECT COUNT(*) FROM employees;
SELECT COUNT(*) FROM payroll_results;
```

---

## Common Issues

### "Connection refused"
- Verify SQL Server is running: `docker ps` (if using Docker)
- Check port 1433 is open
- Verify password is correct

### "Login failed"
- Check username is `sa`
- Check password matches
- Verify SQL Server is in Mixed Authentication mode

### "Database already exists"
- Drop it: `DROP DATABASE payrolldb;` in SSMS
- Or change name in connection string

---

## Next Steps

1. ✅ SQL Server running
2. ✅ Application configured
3. ✅ Data initialized
4. → Explore reports
5. → Export data
6. → Learn SQL in H2 Console

---

## Full Documentation

- **Database Setup**: [DATABASE_CONFIGURATION.md](DATABASE_CONFIGURATION.md)
- **SQL Server Details**: [SQL_SERVER_SETUP.md](SQL_SERVER_SETUP.md)
- **Main Guide**: [README.md](README.md)
- **Quick Start**: [QUICKSTART.md](QUICKSTART.md)

---

**You're ready to go!** 🚀
