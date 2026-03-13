# Quick Start Guide - Payroll Reporting Lab

## 5-Minute Setup

### Step 1: Run the Application

```bash
# On Mac/Linux
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

Wait for the message: `Data initialization complete!`

### Step 2: Open Your Browser

Navigate to: `http://localhost:8080`

You'll see the dashboard with:
- Total payroll cost
- Employee headcount
- Department and country breakdowns

### Step 3: Explore Reports

1. Click **"Reports"** in the navigation
2. Click on **"payroll-summary"**
3. Try filtering by:
   - Country: Kenya
   - Payroll Month: 2025-01
4. Click **"Apply Filters"**

### Step 4: Export Data

1. From any report view
2. Click **"Export to Excel"** or **"Export to CSV"**
3. Open the downloaded file

### Step 5: Validate Reports

1. Go to **Reports** page
2. Scroll to **"Report Validation"** section
3. Enter:
   - Payroll Month: `2025-01`
   - Country: `Kenya`
4. Click **"Validate Report"**
5. See validation results (all should pass ✓)

### Step 6: Learning Mode

1. Click **"Learning Mode"** in navigation
2. Explore:
   - **SQL Queries**: See the actual SQL used
   - **Report Metadata**: Understand dynamic configuration
   - **Data Model**: View database schema
   - **Report Flow**: See how reports are generated

### Step 7: Database Console

1. Click **"H2 Console"** in navigation
2. Use these credentials:
   - JDBC URL: `jdbc:h2:mem:payrolldb`
   - Username: `sa`
   - Password: (leave empty)
3. Click **"Connect"**
4. Try sample queries:

```sql
-- See all employees
SELECT * FROM employees LIMIT 10;

-- Get payroll for Kenya in January
SELECT 
    e.employee_name,
    pr.gross_salary,
    pr.net_salary
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE run.country = 'Kenya' 
  AND run.payroll_month = '2025-01'
LIMIT 10;

-- Total payroll by country
SELECT 
    e.country,
    COUNT(*) as employees,
    SUM(pr.net_salary) as total_payroll
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country;
```

## What You've Learned

In 5 minutes, you've:
- ✅ Run a Spring Boot application
- ✅ Viewed a payroll dashboard
- ✅ Generated parameterized reports
- ✅ Exported data to Excel/CSV
- ✅ Validated report accuracy
- ✅ Explored SQL queries
- ✅ Queried the database directly

## Next Steps

### Practice SQL Reporting
1. Write custom queries in H2 console
2. Compare results with report outputs
3. Understand JOINs and aggregations

### Create a Custom Report
1. In H2 console, insert into `report_sources`:
```sql
INSERT INTO report_sources (rpt_name, description, base_query) 
VALUES ('my-custom-report', 'My Custom Report', 
'SELECT e.employee_name, e.country, pr.net_salary 
 FROM payroll_results pr 
 JOIN employees e ON pr.employee_id = e.id');
```

2. Navigate to: `http://localhost:8080/reports/my-custom-report`

### Explore the Code
1. Open `src/main/java/com/payrolllab/service/ReportBuilderService.java`
2. See how SQL queries are built dynamically
3. Understand the metadata-driven approach

### Modify Sample Data
1. Edit `DataInitializationService.java`
2. Add more countries, departments, or employees
3. Restart the application
4. See your changes in the dashboard

## Common Tasks

### View All Available Reports
```
http://localhost:8080/reports
```

### Access a Specific Report
```
http://localhost:8080/reports/{report-name}
```

### Filter a Report
```
http://localhost:8080/reports/payroll-summary?country=Kenya&payrollMonth=2025-01
```

### Export to Excel
```
http://localhost:8080/reports/payroll-summary/export/excel?country=Kenya
```

### Validate Payroll
```
http://localhost:8080/reports/validate?payrollMonth=2025-01&country=Kenya
```

## Troubleshooting

### Port 8080 Already in Use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Application Won't Start
- Check Java version: `java -version` (need 17+)
- Check Maven: `mvn -version`
- Delete `target` folder and rebuild

### No Data Showing
- Check console logs for "Data initialization complete!"
- Verify H2 console shows tables with data
- Restart the application

## Tips for Learning

1. **Start with the Dashboard**: Get familiar with the data
2. **Use Learning Mode**: Understand concepts before diving into code
3. **Query the Database**: Practice SQL in H2 console
4. **Read the Code**: Start with controllers, then services
5. **Modify and Experiment**: Change data, add reports, break things!

## Resources

- Full documentation: `README.md`
- H2 Console: `http://localhost:8080/h2-console`
- Learning Mode: `http://localhost:8080/learning`
- Source code: `src/main/java/com/payrolllab/`

---

**You're ready to explore!** 🚀
