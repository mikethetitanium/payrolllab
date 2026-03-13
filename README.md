# Payroll Reporting Lab

A full-stack learning system that simulates the reporting environment of a global payroll SaaS platform.

## 🚀 Quick Start

```bash
# Run the application
./mvnw spring-boot:run

# Open your browser
http://localhost:8080
```

That's it! The system initializes with 200 employees, 12 months of payroll data, and 4 pre-configured reports.

## 📚 Documentation Quick Links

- **[QUICKSTART.md](QUICKSTART.md)** - Get running in 5 minutes
- **[GETTING_STARTED.md](GETTING_STARTED.md)** - Structured learning paths  
- **[SQL_LEARNING_GUIDE.md](SQL_LEARNING_GUIDE.md)** - 8-level SQL tutorial
- **[ARCHITECTURE.md](ARCHITECTURE.md)** - System architecture
- **[PROJECT_MAP.md](PROJECT_MAP.md)** - Navigate the project
- **[INDEX.md](INDEX.md)** - Complete index of everything

## Overview

This application teaches and demonstrates:
- Advanced SQL reporting
- Payroll data modeling
- Dynamic report configuration using metadata tables
- Thymeleaf-based report templates
- Multi-country payroll reporting
- Report exports (Excel, CSV)
- QA validation of reports
- Parameterized reports
- Dashboard-style payroll summaries

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.5.11
- Spring Data JPA
- H2 Database (in-memory, can be switched to PostgreSQL/MS SQL)
- JdbcTemplate for dynamic queries

### Frontend
- Thymeleaf
- HTML5
- Bootstrap 5

### Export Libraries
- Apache POI (Excel exports)
- OpenCSV (CSV exports)

## Features

### 1. Payroll Database
Sample tables with realistic data:
- **employees**: 200 employees across 4 countries
- **payroll_runs**: 12 months of payroll runs per country
- **payroll_results**: Individual payroll calculations
- **deductions**: Employee deductions

### 2. Report Metadata System
Dynamic report configuration using:
- **report_sources**: Report definitions
- **report_columns**: Column configurations
- **report_parameters**: Filter parameters

### 3. Pre-configured Reports
- Payroll Summary
- Employee Payslip Summary
- Department Payroll Cost
- Country Payroll Totals

### 4. Multi-Country Support
Sample data for:
- Kenya
- UK
- Germany
- UAE

### 5. Report Features
- Parameterized filtering (country, month, department)
- Excel export
- CSV export
- SQL query display for learning

### 6. QA Validation Module
Automated validation checks:
- Gross salary calculation verification
- Total payroll sum validation
- Employee coverage checks

### 7. Dashboard
Real-time metrics:
- Total payroll cost
- Employee headcount by country
- Payroll by department
- Payroll by country

### 8. Learning Mode
Educational pages explaining:
- SQL queries used in reports
- Report metadata configuration
- Payroll data model
- Report generation flow

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
./mvnw spring-boot:run
```

Or on Windows:
```cmd
mvnw.cmd spring-boot:run
```

4. Open your browser and navigate to:
```
http://localhost:8080
```

### H2 Database Console

Access the H2 console for direct database queries:
```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:payrolldb`
- Username: `sa`
- Password: (leave empty)

## Project Structure

```
src/main/java/com/payrolllab/
├── entity/              # JPA entities
│   ├── Employee.java
│   ├── PayrollRun.java
│   ├── PayrollResult.java
│   ├── Deduction.java
│   ├── ReportSource.java
│   ├── ReportColumn.java
│   └── ReportParameter.java
├── repository/          # Spring Data repositories
├── service/            # Business logic
│   ├── ReportBuilderService.java
│   ├── DashboardService.java
│   ├── ValidationService.java
│   ├── ExportService.java
│   └── DataInitializationService.java
├── controller/         # Web controllers
│   ├── HomeController.java
│   ├── ReportController.java
│   └── LearningController.java
└── dto/               # Data transfer objects

src/main/resources/
├── templates/         # Thymeleaf templates
│   ├── index.html
│   ├── reports/
│   └── learning/
└── application.properties
```

## Learning Paths

### 1. Understanding the Data Model
- Visit `/learning/data-model` to see the database schema
- Explore relationships between tables
- Use H2 console to run sample queries

### 2. Exploring SQL Queries
- Visit `/learning/sql-queries` to see report queries
- Understand how JOINs work in payroll reporting
- Learn aggregation techniques

### 3. Report Metadata System
- Visit `/learning/report-metadata` to understand dynamic reports
- See how reports are configured without code changes
- Learn the metadata-driven approach

### 4. Report Generation Flow
- Visit `/learning/report-flow` to see the complete process
- Understand how user requests become reports
- Learn the architecture of a reporting engine

### 5. Building Custom Reports
1. Insert a new record in `report_sources` table
2. Define columns in `report_columns` table
3. Add parameters in `report_parameters` table
4. Access your report at `/reports/{your-report-name}`

## Sample Queries

### Get all employees in Kenya
```sql
SELECT * FROM employees WHERE country = 'Kenya';
```

### Get payroll summary for January 2025
```sql
SELECT 
    e.employee_name,
    e.country,
    pr.gross_salary,
    pr.net_salary
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE run.payroll_month = '2025-01';
```

### Get total payroll cost by country
```sql
SELECT 
    e.country,
    SUM(pr.net_salary) as total_cost
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country;
```

## Validation Testing

Test report accuracy:
1. Navigate to `/reports`
2. Click "Validate Report"
3. Select payroll month and country
4. View validation results

The system checks:
- Gross = Net + Tax + Pension
- Total payroll matches sum of individual results
- All employees have payroll records

## Export Features

From any report view:
1. Apply filters (country, month, department)
2. Click "Export to Excel" or "Export to CSV"
3. File downloads automatically

## Customization

### Switching to PostgreSQL or MS SQL

Update `application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/payrolldb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# MS SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=payrolldb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
```

### Adding New Countries

Modify `DataInitializationService.java`:
```java
String[] countries = {"Kenya", "UK", "Germany", "UAE", "YourCountry"};
```

### Creating New Reports

1. Add report metadata in `DataInitializationService`
2. Or insert directly into database tables
3. Report automatically appears in the UI

## Architecture Highlights

### Metadata-Driven Reports
Reports are defined in database tables, not code. This allows:
- Non-developers to create reports
- Easy report modifications
- Version control of report definitions
- Dynamic report generation

### Dynamic SQL Building
The `ReportBuilderService` constructs SQL queries at runtime based on:
- Report metadata
- User-selected parameters
- Column definitions

### Separation of Concerns
- **Controllers**: Handle HTTP requests
- **Services**: Business logic and report building
- **Repositories**: Data access
- **DTOs**: Data transfer between layers

## Learning Objectives

After using this system, you should understand:
1. How payroll systems calculate and store data
2. How to write complex SQL queries with JOINs and aggregations
3. How metadata-driven reporting works
4. How to validate report accuracy
5. How to export data to multiple formats
6. How Spring Boot applications are structured
7. How Thymeleaf templates render dynamic data

## Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Check port 8080 is available
- Review console logs for errors

### No data showing
- Data is initialized on startup
- Check logs for "Data initialization complete!"
- Access H2 console to verify data

### Reports not loading
- Check report name matches database
- Verify report metadata exists
- Check browser console for errors

## Contributing

This is a learning project. Feel free to:
- Add new reports
- Enhance validation logic
- Improve UI/UX
- Add more export formats
- Extend the data model

## License

This project is for educational purposes.

## Support

For questions or issues:
1. Check the Learning Mode pages
2. Review the H2 console
3. Examine application logs
4. Review the code comments

---

**Happy Learning!** 🎓
