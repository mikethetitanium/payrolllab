# Complete Study Guide - Payroll Reporting Lab

## 🎯 Your Learning Journey

This guide provides a structured path to understand the entire project from beginner to advanced.

---

## Phase 1: Get It Running (30 minutes)

### Step 1: Start the Application
```bash
./mvnw clean spring-boot:run
```

### Step 2: Explore the Dashboard
- Open `http://localhost:8080`
- You'll see:
  - Total payroll cost
  - Employee count by country
  - Payroll by department
  - Payroll by country

### Step 3: View Sample Reports
- Click "Reports" in the navigation
- You'll see 4 pre-configured reports:
  1. **Payroll Summary** - All payroll data
  2. **Employee Payslip** - Individual employee records
  3. **Department Cost** - Aggregated by department
  4. **Country Totals** - Aggregated by country

### Step 4: Try Filtering
- Select a report
- Apply filters (country, month, department)
- See how the data changes

### Step 5: Export Data
- Click "Export to Excel" or "Export to CSV"
- Download and open the file

---

## Phase 2: Understand the Data (1 hour)

### Step 1: Access H2 Console
- Open `http://localhost:8080/h2-console`
- Connection details:
  - JDBC URL: `jdbc:h2:mem:payrolldb`
  - Username: `sa`
  - Password: (leave empty)

### Step 2: Explore Tables
Run these queries to understand the data:

```sql
-- See all tables
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES;

-- Count records in each table
SELECT 'employees' as table_name, COUNT(*) as count FROM employees
UNION ALL
SELECT 'payroll_runs', COUNT(*) FROM payroll_runs
UNION ALL
SELECT 'payroll_results', COUNT(*) FROM payroll_results
UNION ALL
SELECT 'deductions', COUNT(*) FROM deductions;
```

### Step 3: Explore Payroll Tables

```sql
-- See employee structure
SELECT * FROM employees LIMIT 5;

-- See payroll runs
SELECT * FROM payroll_runs LIMIT 5;

-- See payroll results
SELECT * FROM payroll_results LIMIT 5;

-- See deductions
SELECT * FROM deductions LIMIT 5;
```

### Step 4: Explore Metadata Tables

```sql
-- See report definitions
SELECT * FROM report_sources;

-- See report columns for payroll-summary
SELECT * FROM report_columns 
WHERE rpt_src_id = (SELECT rpt_src_id FROM report_sources WHERE rpt_name = 'payroll-summary');

-- See report parameters
SELECT * FROM report_parameters;
```

### Step 5: Run Sample Queries

```sql
-- Get all employees in Kenya
SELECT * FROM employees WHERE country = 'Kenya';

-- Get payroll for January 2025
SELECT * FROM payroll_runs WHERE payroll_month = '2025-01';

-- Get total payroll by country
SELECT 
    e.country,
    COUNT(DISTINCT e.id) as employee_count,
    SUM(pr.gross_salary) as total_gross,
    SUM(pr.net_salary) as total_net
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country;
```

---

## Phase 3: Understand the Code Structure (1.5 hours)

### Step 1: Read the Entity Classes
These represent your database tables:

**File**: `src/main/java/com/payrolllab/entity/Employee.java`
- Represents an employee
- Has fields: id, employee_name, country, department, hire_date
- Mapped to `employees` table

**File**: `src/main/java/com/payrolllab/entity/PayrollRun.java`
- Represents a payroll run (monthly)
- Has fields: id, payroll_month, country, run_date
- Mapped to `payroll_runs` table

**File**: `src/main/java/com/payrolllab/entity/PayrollResult.java`
- Represents individual payroll calculation
- Has fields: id, employee_id, payroll_run_id, gross_salary, tax_amount, pension, net_salary
- Mapped to `payroll_results` table

**File**: `src/main/java/com/payrolllab/entity/ReportSource.java`
- Represents a report definition
- Has fields: rpt_src_id, rpt_name, description, base_query
- Mapped to `report_sources` table

### Step 2: Read the Repository Classes
These handle database queries:

**File**: `src/main/java/com/payrolllab/repository/EmployeeRepository.java`
- Methods to query employees
- Example: `findByCountry(String country)`

**File**: `src/main/java/com/payrolllab/repository/PayrollResultRepository.java`
- Methods to query payroll results
- Used by services to fetch data

### Step 3: Read the Service Classes
These contain business logic:

**File**: `src/main/java/com/payrolllab/service/DashboardService.java`
- Calculates dashboard metrics
- Methods: `getTotalPayroll()`, `getEmployeeCountByCountry()`, etc.
- Called by HomeController

**File**: `src/main/java/com/payrolllab/service/ReportBuilderService.java`
- **Most important file for understanding reports**
- Builds dynamic SQL queries
- Methods: `buildReport()`, `buildSqlQuery()`
- Key concept: Metadata-driven report generation

**File**: `src/main/java/com/payrolllab/service/ExportService.java`
- Exports reports to Excel and CSV
- Methods: `exportToExcel()`, `exportToCsv()`

**File**: `src/main/java/com/payrolllab/service/ValidationService.java`
- Validates report accuracy
- Methods: `validateGrossCalculation()`, `validateTotalPayroll()`

### Step 4: Read the Controller Classes
These handle HTTP requests:

**File**: `src/main/java/com/payrolllab/controller/HomeController.java`
- Handles dashboard requests
- Maps to `/` and `/dashboard`
- Calls DashboardService

**File**: `src/main/java/com/payrolllab/controller/ReportController.java`
- Handles report requests
- Maps to `/reports/*`
- Calls ReportBuilderService, ExportService, ValidationService

**File**: `src/main/java/com/payrolllab/controller/LearningController.java`
- Handles learning mode requests
- Maps to `/learning/*`
- Returns educational pages

### Step 5: Read the Template Files
These render the HTML:

**File**: `src/main/resources/templates/index.html`
- Dashboard page
- Shows metrics and charts

**File**: `src/main/resources/templates/reports/view.html`
- Report display page
- Shows table with data
- Has filter and export buttons

**File**: `src/main/resources/templates/learning/index.html`
- Learning mode home page
- Links to educational content

---

## Phase 4: Deep Dive - How Reports Work (2 hours)

### Understanding the Report Generation Flow

#### Step 1: User Request
```
User clicks: /reports/payroll-summary?country=Kenya
```

#### Step 2: Controller Receives Request
**File**: `ReportController.java`
```java
@GetMapping("/{reportName}")
public String viewReport(@PathVariable String reportName, 
                         @RequestParam Map<String, Object> parameters,
                         Model model) {
    // Extract parameters from URL
    // Call ReportBuilderService
}
```

#### Step 3: Service Builds Report
**File**: `ReportBuilderService.java`
```java
public ReportData buildReport(String reportName, Map<String, Object> parameters) {
    // 1. Query report_sources to get base query
    // 2. Query report_columns to get column definitions
    // 3. Build dynamic SQL query
    // 4. Execute query
    // 5. Return ReportData object
}
```

#### Step 4: Dynamic SQL Building
The key method is `buildSqlQuery()`:

```java
private String buildSqlQuery(ReportSource reportSource, 
                             List<ReportColumn> columns, 
                             Map<String, Object> parameters) {
    // For aggregated queries (with GROUP BY):
    // - Inject WHERE clause BEFORE GROUP BY
    // - Use table aliases (e.department, pr.payroll_month)
    // - Wrap in subquery to apply column aliases
    
    // For simple queries:
    // - Wrap base query in subquery
    // - Add WHERE clause after
    // - Apply column aliases
}
```

#### Step 5: Template Renders Data
**File**: `reports/view.html`
```html
<table>
  <thead>
    <tr>
      <th th:each="col : ${reportData.columns}" th:text="${col}"></th>
    </tr>
  </thead>
  <tbody>
    <tr th:each="row : ${reportData.rows}">
      <td th:each="col : ${reportData.columns}" 
          th:text="${row[col]}"></td>
    </tr>
  </tbody>
</table>
```

### Key Concepts to Understand

#### 1. Metadata-Driven Design
- Reports are defined in database tables, not code
- `report_sources` table stores the base SQL query
- `report_columns` table stores column definitions
- This allows non-developers to create reports

#### 2. Dynamic SQL Generation
- SQL queries are built at runtime
- Parameters are injected into WHERE clauses
- Column aliases are applied from metadata
- Aggregated queries need special handling (WHERE before GROUP BY)

#### 3. Separation of Concerns
- **Controller**: Handles HTTP requests
- **Service**: Builds reports and exports
- **Repository**: Queries database
- **Template**: Renders HTML

---

## Phase 5: SQL Deep Dive (2 hours)

### Understanding the Report Queries

#### Query 1: Payroll Summary (Simple Query)
```sql
SELECT 
    e.employee_name,
    e.country,
    pr.payroll_month,
    pres.gross_salary,
    pres.tax_amount,
    pres.pension,
    pres.net_salary
FROM payroll_results pres
JOIN employees e ON pres.employee_id = e.id
JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
```

**What it does:**
- Joins 3 tables
- Gets individual payroll records
- Can be filtered by country, month, department

**How it's wrapped:**
```sql
SELECT 
    employee_name AS "Employee Name",
    country AS "Country",
    payroll_month AS "Month",
    gross_salary AS "Gross",
    tax_amount AS "Tax",
    pension AS "Pension",
    net_salary AS "Net"
FROM (
    -- Base query above
) base_data
WHERE 1=1 
  AND base_data.country = 'Kenya'
  AND base_data.payroll_month = '2025-01'
```

#### Query 2: Department Cost (Aggregated Query)
```sql
SELECT 
    e.department,
    e.country,
    COUNT(DISTINCT e.id) as employee_count,
    SUM(pres.gross_salary) as total_gross,
    SUM(pres.net_salary) as total_net
FROM payroll_results pres
JOIN employees e ON pres.employee_id = e.id
JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
GROUP BY e.department, e.country
```

**What it does:**
- Groups by department and country
- Counts employees
- Sums salaries

**How it's wrapped:**
```sql
SELECT 
    department AS "Department",
    country AS "Country",
    employee_count AS "Employees",
    total_gross AS "Total Gross",
    total_net AS "Total Net"
FROM (
    -- Base query with WHERE injected BEFORE GROUP BY
    SELECT 
        e.department,
        e.country,
        COUNT(DISTINCT e.id) as employee_count,
        SUM(pres.gross_salary) as total_gross,
        SUM(pres.net_salary) as total_net
    FROM payroll_results pres
    JOIN employees e ON pres.employee_id = e.id
    JOIN payroll_runs pr ON pres.payroll_run_id = pr.id
    WHERE 1=1 
      AND e.country = 'Kenya'
    GROUP BY e.department, e.country
) base_data
```

**Important**: WHERE clause is BEFORE GROUP BY for aggregated queries!

### SQL Concepts Used

1. **SELECT** - Choose columns
2. **FROM** - Specify table
3. **JOIN** - Combine tables
4. **WHERE** - Filter rows
5. **GROUP BY** - Aggregate rows
6. **ORDER BY** - Sort results
7. **LIMIT** - Limit results
8. **Aggregation Functions** - COUNT, SUM, AVG, MIN, MAX
9. **Aliases** - Rename columns and tables
10. **Subqueries** - Queries within queries

---

## Phase 6: Hands-On Practice (2 hours)

### Exercise 1: Write Your First Query
**Goal**: Get all employees in Engineering department

```sql
-- Your answer:
SELECT * FROM employees WHERE department = 'Engineering';
```

### Exercise 2: Aggregate Data
**Goal**: Count employees by country

```sql
-- Your answer:
SELECT country, COUNT(*) as count FROM employees GROUP BY country;
```

### Exercise 3: Join Tables
**Goal**: Get employee names with their gross salary

```sql
-- Your answer:
SELECT e.employee_name, pr.gross_salary
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id;
```

### Exercise 4: Complex Query
**Goal**: Get total payroll by department for Kenya

```sql
-- Your answer:
SELECT 
    e.department,
    SUM(pr.gross_salary) as total_gross
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
WHERE e.country = 'Kenya'
GROUP BY e.department;
```

### Exercise 5: Create a Custom Report
**Goal**: Create a report showing employees by department and country

```sql
-- Step 1: Create the base query
SELECT 
    e.department,
    e.country,
    COUNT(*) as employee_count
FROM employees e
GROUP BY e.department, e.country;

-- Step 2: Insert into report_sources
INSERT INTO report_sources (rpt_name, description, base_query)
VALUES ('employee-by-dept-country', 'Employees by Department and Country', 
'SELECT e.department, e.country, COUNT(*) as employee_count FROM employees e GROUP BY e.department, e.country');

-- Step 3: Get the report ID
SELECT rpt_src_id FROM report_sources WHERE rpt_name = 'employee-by-dept-country';

-- Step 4: Add columns (replace {ID} with the ID from step 3)
INSERT INTO report_columns (rpt_src_id, rpt_col_name, rpt_col_alias, rpt_col_order, rpt_col_data_type)
VALUES 
({ID}, 'department', 'Department', 1, 'VARCHAR'),
({ID}, 'country', 'Country', 2, 'VARCHAR'),
({ID}, 'employee_count', 'Count', 3, 'INTEGER');

-- Step 5: Access your report
-- Open: http://localhost:8080/reports/employee-by-dept-country
```

---

## Phase 7: Modify and Extend (2 hours)

### Modification 1: Change Sample Data
**File**: `src/main/java/com/payrolllab/service/DataInitializationService.java`

```java
// Change countries
String[] countries = {"Kenya", "UK", "Germany", "UAE", "India"};

// Change departments
String[] departments = {"Engineering", "Finance", "HR", "Sales", "Marketing"};

// Change employee count
int employeeCount = 300; // was 200
```

### Modification 2: Add a New Report
**File**: `src/main/java/com/payrolllab/service/DataInitializationService.java`

Add a new method:
```java
private void createMyCustomReport() {
    ReportSource report = new ReportSource();
    report.setRptName("my-custom-report");
    report.setDescription("My Custom Report");
    report.setBaseQuery("""
        SELECT e.employee_name, e.country, pr.gross_salary
        FROM payroll_results pr
        JOIN employees e ON pr.employee_id = e.id
        """);
    report = reportSourceRepository.save(report);
    
    // Add columns...
}
```

### Modification 3: Add Validation Logic
**File**: `src/main/java/com/payrolllab/service/ValidationService.java`

Add a new validation method:
```java
private ValidationResult validateEmployeeCount() {
    // Check that all employees have payroll records
    // Return pass/fail result
}
```

---

## Phase 8: Advanced Topics (3 hours)

### Topic 1: Spring Boot Concepts
- **@Entity**: Maps class to database table
- **@Repository**: Provides database access methods
- **@Service**: Contains business logic
- **@Controller**: Handles HTTP requests
- **@Autowired**: Dependency injection
- **@GetMapping**: Maps HTTP GET requests

### Topic 2: JPA Concepts
- **JpaRepository**: Base interface for database operations
- **@OneToMany**: One-to-many relationship
- **@ManyToOne**: Many-to-one relationship
- **@JoinColumn**: Specifies foreign key column

### Topic 3: Thymeleaf Concepts
- **th:each**: Loop through collections
- **th:text**: Set element text
- **th:if**: Conditional rendering
- **th:href**: Set link href
- **th:value**: Set input value

### Topic 4: Performance Optimization
- **Pagination**: Load data in chunks
- **Lazy Loading**: Load related data on demand
- **Caching**: Store frequently accessed data
- **Indexing**: Speed up database queries

---

## Phase 9: Testing (1 hour)

### Unit Test Example
```java
@Test
void testReportBuilder() {
    ReportData report = reportBuilderService.buildReport("payroll-summary", 
        Map.of("country", "Kenya"));
    
    assertNotNull(report);
    assertFalse(report.getRows().isEmpty());
    assertTrue(report.getColumns().contains("Employee Name"));
}
```

### Integration Test Example
```java
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    @Test
    void testReportEndpoint() {
        mockMvc.perform(get("/reports/payroll-summary"))
               .andExpect(status().isOk())
               .andExpect(view().name("reports/view"));
    }
}
```

---

## Phase 10: Deployment (1 hour)

### Build the Application
```bash
./mvnw clean package
```

### Run the JAR
```bash
java -jar target/payroll-reporting-lab-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## 📚 Documentation Reference

| Document | Purpose | Time |
|----------|---------|------|
| README.md | Complete overview | 30 min |
| QUICKSTART.md | Get running fast | 5 min |
| ARCHITECTURE.md | System design | 1 hour |
| SQL_LEARNING_GUIDE.md | SQL tutorial | 2 hours |
| PROJECT_MAP.md | Navigate code | 30 min |
| PROJECT_SUMMARY.md | Feature overview | 30 min |

---

## 🎯 Learning Checklist

- [ ] Phase 1: Application running
- [ ] Phase 2: Explored H2 console
- [ ] Phase 3: Read entity classes
- [ ] Phase 4: Understood report flow
- [ ] Phase 5: Learned SQL concepts
- [ ] Phase 6: Completed exercises
- [ ] Phase 7: Modified code
- [ ] Phase 8: Learned advanced topics
- [ ] Phase 9: Wrote tests
- [ ] Phase 10: Deployed application

---

## 🚀 Next Steps

1. **Complete all phases** - Follow the learning path
2. **Experiment** - Modify code and see what happens
3. **Create custom reports** - Build your own reports
4. **Add features** - Extend the application
5. **Deploy** - Run in production environment

---

## 💡 Tips for Success

1. **Run the application first** - See it working before reading code
2. **Use H2 console** - Practice SQL queries
3. **Read code comments** - They explain the logic
4. **Modify and test** - Learn by doing
5. **Ask questions** - Use the learning mode pages
6. **Take notes** - Write down key concepts
7. **Practice SQL** - It's the foundation
8. **Understand architecture** - It's the blueprint

---

**Happy Learning!** 🎓

