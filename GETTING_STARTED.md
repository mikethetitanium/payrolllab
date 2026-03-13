# Getting Started with Payroll Reporting Lab

Welcome! This guide will help you get the most out of the Payroll Reporting Lab.

## What You'll Learn

This system teaches you how enterprise payroll SaaS platforms work by demonstrating:

✅ **SQL Reporting**: Write complex queries with JOINs and aggregations  
✅ **Data Modeling**: Understand payroll database design  
✅ **Metadata-Driven Systems**: Configure reports without code changes  
✅ **Report Validation**: Ensure data accuracy  
✅ **Data Export**: Generate Excel and CSV files  
✅ **Spring Boot**: Build full-stack Java applications  
✅ **Thymeleaf**: Create dynamic web templates  

## Prerequisites

- Java 17 or higher
- Maven 3.6+ (included via wrapper)
- A web browser
- A text editor or IDE (optional)

## Installation

### Step 1: Verify Java
```bash
java -version
```
You should see Java 17 or higher.

### Step 2: Run the Application
```bash
# Mac/Linux
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Step 3: Wait for Startup
Look for this message in the console:
```
Data initialization complete!
```

### Step 4: Open Browser
Navigate to: `http://localhost:8080`

## Your First 30 Minutes

### Minutes 1-5: Explore the Dashboard
1. View total payroll cost
2. See employee distribution by country
3. Check department costs

### Minutes 6-10: Run Your First Report
1. Click "Reports" in navigation
2. Click "payroll-summary"
3. Apply filters:
   - Country: Kenya
   - Payroll Month: 2025-01
4. View the results

### Minutes 11-15: Export Data
1. From the report view
2. Click "Export to Excel"
3. Open the downloaded file
4. See formatted payroll data

### Minutes 16-20: Validate Reports
1. Go back to Reports page
2. Scroll to "Report Validation"
3. Enter:
   - Payroll Month: 2025-01
   - Country: Kenya
4. Click "Validate Report"
5. See all checks pass ✓

### Minutes 21-25: Query the Database
1. Click "H2 Console" in navigation
2. Connect with:
   - JDBC URL: `jdbc:h2:mem:payrolldb`
   - Username: `sa`
   - Password: (empty)
3. Run this query:
```sql
SELECT 
    e.employee_name,
    e.country,
    pr.gross_salary,
    pr.net_salary
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
LIMIT 10;
```

### Minutes 26-30: Learning Mode
1. Click "Learning Mode"
2. Explore:
   - SQL Queries
   - Report Metadata
   - Data Model
   - Report Flow

## Learning Paths

Choose your path based on your goals:

### Path 1: SQL Mastery
**Goal**: Become proficient in SQL reporting

1. Read `SQL_LEARNING_GUIDE.md`
2. Practice queries in H2 Console
3. Start with Level 1 (Basic Queries)
4. Progress through all 8 levels
5. Complete practice exercises
6. Create your own queries

**Time**: 4-6 hours

### Path 2: System Architecture
**Goal**: Understand how reporting systems work

1. Read `ARCHITECTURE.md`
2. Study the component diagram
3. Trace the data flow
4. Explore the code:
   - Start with `ReportController`
   - Then `ReportBuilderService`
   - Finally `DataInitializationService`
5. Understand metadata-driven design

**Time**: 2-3 hours

### Path 3: Full-Stack Development
**Goal**: Learn Spring Boot and Thymeleaf

1. Read `README.md`
2. Explore the project structure
3. Study the entities and repositories
4. Understand service layer patterns
5. Examine Thymeleaf templates
6. Modify and extend the code

**Time**: 6-8 hours

### Path 4: Quick Practical Skills
**Goal**: Get hands-on experience fast

1. Read `QUICKSTART.md`
2. Run all the examples
3. Create a custom report
4. Modify sample data
5. Export reports
6. Validate data

**Time**: 1-2 hours

## Key Concepts to Master

### 1. Payroll Data Model
```
Employee → has many → Payroll Results
Payroll Run → contains many → Payroll Results
Employee → has many → Deductions
```

### 2. Report Metadata
Reports are defined in database tables:
- `report_sources`: Report definitions
- `report_columns`: Column configurations
- `report_parameters`: Filter parameters

### 3. Dynamic SQL Building
```java
SELECT columns FROM base_query WHERE parameters
```

### 4. Report Validation
Ensure data accuracy:
- Gross = Net + Tax + Pension
- All employees have records
- Totals match expectations

## Common Tasks

### View All Reports
```
http://localhost:8080/reports
```

### Run a Specific Report
```
http://localhost:8080/reports/payroll-summary
```

### Filter a Report
```
http://localhost:8080/reports/payroll-summary?country=Kenya&payrollMonth=2025-01
```

### Validate Payroll
```
http://localhost:8080/reports/validate?payrollMonth=2025-01&country=Kenya
```

### Access Database Console
```
http://localhost:8080/h2-console
```

## Customization Ideas

### Beginner Level
1. Change the countries in sample data
2. Add more departments
3. Modify salary ranges
4. Change tax rates

### Intermediate Level
1. Create a new report using metadata
2. Add a new validation check
3. Implement PDF export
4. Add report scheduling

### Advanced Level
1. Switch to PostgreSQL
2. Add user authentication
3. Implement role-based access
4. Create a REST API
5. Add real-time dashboards

## Troubleshooting

### Application Won't Start
```bash
# Check Java version
java -version

# Clean and rebuild
./mvnw clean install

# Check port availability
# Change port in application.properties if needed
server.port=8081
```

### No Data Showing
- Check console for "Data initialization complete!"
- Verify H2 console shows tables
- Restart the application

### Reports Not Loading
- Check report name in URL
- Verify report exists in database
- Check browser console for errors

### Export Not Working
- Check file download settings
- Verify report has data
- Check console logs for errors

## Resources

### Documentation
- `README.md` - Complete system documentation
- `QUICKSTART.md` - 5-minute quick start
- `ARCHITECTURE.md` - System architecture
- `SQL_LEARNING_GUIDE.md` - SQL tutorial

### Code
- `src/main/java/com/payrolllab/` - Java source code
- `src/main/resources/templates/` - Thymeleaf templates
- `src/main/resources/application.properties` - Configuration

### Tools
- H2 Console: `http://localhost:8080/h2-console`
- Dashboard: `http://localhost:8080`
- Reports: `http://localhost:8080/reports`
- Learning Mode: `http://localhost:8080/learning`

## Getting Help

### Check the Logs
The console shows detailed information about:
- SQL queries being executed
- Data initialization
- Errors and exceptions

### Use H2 Console
Query the database directly to:
- Verify data exists
- Test SQL queries
- Understand table structure

### Read the Code
The code is well-commented and structured:
- Start with controllers
- Move to services
- Examine repositories

### Experiment
The best way to learn:
- Modify the code
- Break things
- Fix them
- Understand why

## Next Steps

After completing this guide:

1. **Practice SQL**: Work through `SQL_LEARNING_GUIDE.md`
2. **Study Architecture**: Read `ARCHITECTURE.md`
3. **Modify Code**: Add features and enhancements
4. **Build Something**: Create your own reporting system
5. **Share**: Teach others what you've learned

## Success Metrics

You'll know you've mastered the system when you can:

✅ Write complex SQL queries with multiple JOINs  
✅ Explain how metadata-driven reports work  
✅ Create a new report without modifying code  
✅ Validate report accuracy  
✅ Export data in multiple formats  
✅ Understand Spring Boot architecture  
✅ Build a similar system from scratch  

## Community

This is a learning project. Feel free to:
- Extend the functionality
- Add new features
- Improve the documentation
- Share your learnings
- Help others learn

## Final Tips

1. **Take Your Time**: Don't rush through the material
2. **Practice**: Run queries, modify code, experiment
3. **Ask Questions**: Use the code and docs to find answers
4. **Build Projects**: Apply what you learn to real problems
5. **Teach Others**: The best way to solidify knowledge

---

**Ready to start?** Open `http://localhost:8080` and begin your journey! 🚀

**Questions?** Check the documentation files or explore the code.

**Stuck?** Review the logs, query the database, and experiment.

**Enjoying it?** Share what you've learned with others!
