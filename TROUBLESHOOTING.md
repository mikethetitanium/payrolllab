# Troubleshooting Guide

## Common Issues and Solutions

### Issue 1: Parameter Name Error
**Error**: `Name for argument of type [java.lang.String] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag.`

**Solution**:
1. Run: `./mvnw clean`
2. Run: `./mvnw spring-boot:run`
3. See: [COMPILER_FIX.md](COMPILER_FIX.md)

---

### Issue 2: SQL Grammar Error
**Error**: `ERROR: column "department" does not exist` or `SELECT  FROM (...)`

**Solution**:
1. Verify you're using the latest code
2. Check that report columns are being loaded
3. See: [BUG_FIX_REPORT.md](BUG_FIX_REPORT.md)

---

### Issue 3: Connection Refused
**Error**: `The TCP/IP connection to the host localhost, port 1433 has failed` (SQL Server)

**Solution**:
1. Verify SQL Server is running
2. Check port 1433 is open
3. Verify connection string in `application.properties`
4. See: [SQL_SERVER_SETUP.md](SQL_SERVER_SETUP.md)

---

### Issue 4: No Data Showing
**Error**: Dashboard is empty, reports show no data

**Solution**:
1. Check console for: `Data initialization complete!`
2. If not present, data didn't initialize
3. Verify database connection
4. Check H2 Console: `http://localhost:8080/h2-console`
5. Run: `SELECT COUNT(*) FROM employees;`
6. Should return: 200

---

### Issue 5: Port Already in Use
**Error**: `Address already in use: bind`

**Solution**:
1. Change port in `application.properties`:
   ```properties
   server.port=8081
   ```
2. Or kill the process using port 8080:
   - Windows: `netstat -ano | findstr :8080`
   - Mac/Linux: `lsof -i :8080`

---

### Issue 6: Database Already Exists
**Error**: `Cannot create database 'payrolldb' because it already exists`

**Solution**:
1. Drop the database (H2 Console or SSMS)
2. Or change database name in connection string
3. Or use `spring.jpa.hibernate.ddl-auto=update` instead of `create-drop`

---

### Issue 7: Slow Application Startup
**Error**: Application takes > 30 seconds to start

**Solution**:
1. This is normal for first startup
2. Subsequent startups are faster
3. If consistently slow:
   - Check disk I/O
   - Check system resources
   - Check database performance

---

### Issue 8: Export Not Working
**Error**: Export button doesn't download file

**Solution**:
1. Check browser download settings
2. Try different browser
3. Check console for errors
4. Verify report has data before exporting
5. Check file size (should be > 1KB)

---

### Issue 9: Validation Fails
**Error**: Validation checks show ✗ FAIL

**Solution**:
1. This indicates data integrity issue
2. Check the specific validation message
3. Verify payroll calculations
4. Check if all employees have records
5. See: [SQL_LEARNING_GUIDE.md](SQL_LEARNING_GUIDE.md) for validation queries

---

### Issue 10: H2 Console Won't Connect
**Error**: Can't connect to H2 Console

**Solution**:
1. Verify H2 is enabled in `application.properties`:
   ```properties
   spring.h2.console.enabled=true
   ```
2. Verify you're using H2 database (not SQL Server)
3. Check URL: `http://localhost:8080/h2-console`
4. Use credentials:
   - JDBC URL: `jdbc:h2:mem:payrolldb`
   - Username: `sa`
   - Password: (empty)

---

## Diagnostic Steps

### Step 1: Check Console Logs
Look for these messages:

✅ Good:
```
Data initialization complete!
Executing query: SELECT employee_name, country, ...
```

❌ Bad:
```
ERROR: column "department" does not exist
Exception in thread "main"
```

### Step 2: Verify Database Connection
```bash
# For H2
curl http://localhost:8080/h2-console

# For SQL Server
sqlcmd -S localhost -U sa -P YourPassword123!
```

### Step 3: Check Application Health
```bash
curl http://localhost:8080/
```

Should return HTML (dashboard page)

### Step 4: Test Report Endpoint
```bash
curl http://localhost:8080/reports
```

Should return HTML (report list page)

### Step 5: Test with Filters
```bash
curl "http://localhost:8080/reports/payroll-summary?country=Kenya"
```

Should return HTML with filtered data

---

## Database Verification

### Using H2 Console
1. Open: `http://localhost:8080/h2-console`
2. Connect
3. Run these queries:

```sql
-- Check tables exist
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;

-- Check data
SELECT COUNT(*) FROM employees;
SELECT COUNT(*) FROM payroll_results;
SELECT COUNT(*) FROM report_sources;

-- Check report metadata
SELECT * FROM report_sources;
SELECT * FROM report_columns;
```

### Using SQL Server Management Studio
1. Connect to: `localhost`
2. Login: `sa` / `YourPassword123!`
3. Expand `payrolldb` database
4. Right-click table → "Select Top 1000 Rows"

---

## Performance Troubleshooting

### Slow Dashboard
- Check database query performance
- Add indexes to frequently queried columns
- Check system resources (CPU, RAM, disk)

### Slow Reports
- Check report query in logs
- Verify filters are working (should reduce data)
- Add database indexes
- Check network latency

### Slow Exports
- Check file size
- Verify data isn't too large
- Try exporting smaller dataset first

---

## Browser Issues

### JavaScript Errors
- Open browser console: F12
- Check for errors
- Try different browser
- Clear cache: Ctrl+Shift+Delete

### CSS Not Loading
- Check browser console for 404 errors
- Verify Bootstrap CDN is accessible
- Try refreshing page

### Forms Not Submitting
- Check browser console for errors
- Verify form fields have correct names
- Try different browser

---

## IDE Issues

### Maven Not Recognizing Changes
- Refresh Maven project
- Run: `./mvnw clean install`
- Restart IDE

### Compilation Errors
- Check Java version: `java -version` (need 17+)
- Run: `./mvnw clean compile`
- Check for syntax errors in code

### Debugging
- Set breakpoints in IDE
- Run: `./mvnw spring-boot:run`
- IDE should stop at breakpoints

---

## Getting Help

### Check These Resources
1. [README.md](README.md) - Complete documentation
2. [ARCHITECTURE.md](ARCHITECTURE.md) - System design
3. [SQL_LEARNING_GUIDE.md](SQL_LEARNING_GUIDE.md) - SQL help
4. [BUG_FIX_REPORT.md](BUG_FIX_REPORT.md) - Known issues
5. [COMPILER_FIX.md](COMPILER_FIX.md) - Build issues

### Check Logs
- Application console output
- Browser console (F12)
- Database logs
- IDE output

### Try These Steps
1. Restart application
2. Clear browser cache
3. Run `./mvnw clean`
4. Rebuild project
5. Restart IDE

---

## Still Stuck?

### Collect Information
1. Full error message
2. Console logs
3. Steps to reproduce
4. Database being used (H2/SQL Server/PostgreSQL)
5. Java version
6. Operating system

### Try Minimal Example
1. Start fresh: `./mvnw clean`
2. Run application: `./mvnw spring-boot:run`
3. Open dashboard: `http://localhost:8080`
4. If dashboard works, issue is specific to reports
5. If dashboard fails, issue is with database connection

### Reset Everything
```bash
# Clean build
./mvnw clean

# Remove target directory
rm -rf target

# Rebuild
./mvnw install

# Run
./mvnw spring-boot:run
```

---

## Quick Reference

| Issue | Command | File |
|-------|---------|------|
| Compiler error | `./mvnw clean` | [COMPILER_FIX.md](COMPILER_FIX.md) |
| SQL error | Check logs | [BUG_FIX_REPORT.md](BUG_FIX_REPORT.md) |
| Connection error | Verify database | [DATABASE_CONFIGURATION.md](DATABASE_CONFIGURATION.md) |
| SQL Server issue | Check setup | [SQL_SERVER_SETUP.md](SQL_SERVER_SETUP.md) |
| No data | Check initialization | [VERIFY_FIX.md](VERIFY_FIX.md) |
| Port in use | Change port | [README.md](README.md) |

---

**Still need help?** Review the documentation files or check the application logs for specific error messages.
