# Architecture Documentation - Payroll Reporting Lab

## System Overview

The Payroll Reporting Lab is a metadata-driven reporting system that demonstrates how enterprise payroll SaaS platforms generate, validate, and export reports.

## Architecture Principles

### 1. Metadata-Driven Design
Reports are defined in database tables rather than hardcoded, enabling:
- Dynamic report creation without code changes
- Business user configuration
- Version-controlled report definitions
- Flexible report modifications

### 2. Separation of Concerns
Clear boundaries between layers:
- **Presentation Layer**: Thymeleaf templates
- **Controller Layer**: HTTP request handling
- **Service Layer**: Business logic
- **Repository Layer**: Data access
- **Entity Layer**: Domain models

### 3. Convention Over Configuration
Spring Boot auto-configuration minimizes boilerplate while maintaining flexibility.

## Component Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Browser (Client)                      │
└────────────────────┬────────────────────────────────────┘
                     │ HTTP
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Spring MVC Controllers                      │
│  ┌──────────────┬──────────────┬──────────────┐        │
│  │HomeController│ReportController│LearningCtrl │        │
│  └──────────────┴──────────────┴──────────────┘        │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  Service Layer                           │
│  ┌──────────────────┬──────────────────┬──────────────┐│
│  │ReportBuilder     │Dashboard         │Validation    ││
│  │Service           │Service           │Service       ││
│  ├──────────────────┼──────────────────┼──────────────┤│
│  │Export            │ReportAutomation  │              ││
│  │Service           │Service           │              ││
│  └──────────────────┴──────────────────┴──────────────┘│
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Repository Layer (Spring Data JPA)          │
│  ┌──────────────┬──────────────┬──────────────┐        │
│  │Employee      │PayrollRun    │PayrollResult │        │
│  │Repository    │Repository    │Repository    │        │
│  └──────────────┴──────────────┴──────────────┘        │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  H2 Database                             │
│  ┌──────────────┬──────────────┬──────────────┐        │
│  │Payroll Tables│Metadata      │               │        │
│  │              │Tables        │               │        │
│  └──────────────┴──────────────┴──────────────┘        │
└─────────────────────────────────────────────────────────┘
```

## Data Flow

### Report Generation Flow

```
1. User Request
   └─> GET /reports/payroll-summary?country=Kenya
   
2. Controller (ReportController)
   └─> Extract parameters
   └─> Call ReportBuilderService.buildReport()
   
3. ReportBuilderService
   └─> Query report_sources for metadata
   └─> Query report_columns for column definitions
   └─> Build dynamic SQL query
   └─> Execute via JdbcTemplate
   └─> Return ReportData DTO
   
4. Controller
   └─> Add ReportData to Model
   └─> Return view name "reports/view"
   
5. Thymeleaf
   └─> Render HTML using ReportData
   └─> Display table with data
   
6. Browser
   └─> Display rendered HTML to user
```

### Export Flow

```
1. User clicks "Export to Excel"
   └─> GET /reports/payroll-summary/export/excel?country=Kenya
   
2. ReportController
   └─> Build report using ReportBuilderService
   └─> Call ExportService.exportToExcel()
   
3. ExportService
   └─> Create Excel workbook using Apache POI
   └─> Add headers and data rows
   └─> Return byte array
   
4. Controller
   └─> Set Content-Disposition header
   └─> Return ResponseEntity with Excel data
   
5. Browser
   └─> Download Excel file
```

## Database Schema

### Payroll Tables

```sql
employees
├── id (PK)
├── employee_name
├── country
├── department
└── hire_date

payroll_runs
├── id (PK)
├── payroll_month
├── country
└── run_date

payroll_results
├── id (PK)
├── employee_id (FK → employees)
├── payroll_run_id (FK → payroll_runs)
├── gross_salary
├── tax_amount
├── pension
└── net_salary

deductions
├── id (PK)
├── employee_id (FK → employees)
├── deduction_type
└── amount
```

### Metadata Tables

```sql
report_sources
├── rpt_src_id (PK)
├── rpt_name (unique)
├── description
└── base_query

report_columns
├── id (PK)
├── rpt_src_id (FK → report_sources)
├── rpt_col_name
├── rpt_col_alias
├── rpt_col_order
└── rpt_col_data_type

report_parameters
├── id (PK)
├── rpt_src_id (FK → report_sources)
├── parameter_name
├── parameter_type
└── default_value
```

## Key Design Patterns

### 1. Repository Pattern
Spring Data JPA repositories abstract data access:
```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByCountry(String country);
}
```

### 2. Service Layer Pattern
Business logic encapsulated in services:
```java
@Service
public class ReportBuilderService {
    public ReportData buildReport(String reportName, Map<String, Object> params) {
        // Build and execute report
    }
}
```

### 3. DTO Pattern
Data transfer objects separate domain models from API contracts:
```java
@Data
public class ReportData {
    private String reportName;
    private List<String> columns;
    private List<Map<String, Object>> rows;
}
```

### 4. Template Method Pattern
Export service provides template for different formats:
```java
public byte[] exportToExcel(ReportData data) { ... }
public String exportToCsv(ReportData data) { ... }
```

### 5. Strategy Pattern
Different validation strategies for different checks:
```java
private ValidationResult validateGrossCalculation() { ... }
private ValidationResult validateTotalPayroll() { ... }
```

## Technology Choices

### Why H2 Database?
- Zero configuration for learning
- In-memory for fast startup
- Built-in web console
- Easy to switch to production databases

### Why JdbcTemplate for Reports?
- Dynamic SQL generation
- Direct result mapping
- Performance for read-heavy operations
- Flexibility for complex queries

### Why Thymeleaf?
- Natural templating (valid HTML)
- Server-side rendering
- Spring Boot integration
- Easy to learn

### Why Apache POI?
- Industry standard for Excel
- Rich formatting capabilities
- Wide adoption

## Scalability Considerations

### Current Design (Learning/Demo)
- In-memory database
- Synchronous processing
- Single server

### Production Enhancements

#### 1. Database
```
H2 → PostgreSQL/MS SQL Server
- Persistent storage
- Connection pooling
- Read replicas for reports
```

#### 2. Caching
```java
@Cacheable("reports")
public ReportData buildReport(String name, Map params) { ... }
```

#### 3. Async Processing
```java
@Async
public CompletableFuture<ReportData> buildReportAsync() { ... }
```

#### 4. Message Queue
```
Report Request → Queue → Worker → S3 → Notification
```

#### 5. Microservices
```
Report Service
├── Report Builder Service
├── Export Service
└── Validation Service
```

## Security Considerations

### Current Implementation
- No authentication (learning environment)
- No authorization
- No input sanitization

### Production Requirements

#### 1. Authentication
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // JWT or OAuth2 configuration
}
```

#### 2. Authorization
```java
@PreAuthorize("hasRole('PAYROLL_ADMIN')")
public ReportData buildReport() { ... }
```

#### 3. SQL Injection Prevention
```java
// Use parameterized queries
jdbcTemplate.query(sql, params, rowMapper);
```

#### 4. Data Encryption
- Encrypt sensitive payroll data at rest
- Use HTTPS for data in transit
- Encrypt exports

## Performance Optimization

### Current Bottlenecks
1. All data loaded into memory
2. No pagination
3. No query optimization
4. Synchronous exports

### Optimization Strategies

#### 1. Pagination
```java
public Page<Map<String, Object>> buildReport(Pageable pageable) {
    // Return paginated results
}
```

#### 2. Lazy Loading
```java
@OneToMany(fetch = FetchType.LAZY)
private List<PayrollResult> results;
```

#### 3. Query Optimization
```sql
-- Add indexes
CREATE INDEX idx_payroll_results_employee ON payroll_results(employee_id);
CREATE INDEX idx_payroll_results_run ON payroll_results(payroll_run_id);
```

#### 4. Async Exports
```java
@Async
public CompletableFuture<byte[]> exportToExcelAsync(ReportData data) {
    // Generate export in background
}
```

## Testing Strategy

### Unit Tests
```java
@Test
void testReportBuilder() {
    ReportData report = reportBuilderService.buildReport("test", params);
    assertNotNull(report);
    assertEquals(10, report.getRows().size());
}
```

### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {
    @Test
    void testReportEndpoint() {
        mockMvc.perform(get("/reports/payroll-summary"))
               .andExpect(status().isOk());
    }
}
```

### Validation Tests
```java
@Test
void testPayrollValidation() {
    List<ValidationResult> results = validationService.validate("2025-01", "Kenya");
    assertTrue(results.stream().allMatch(ValidationResult::isPassed));
}
```

## Monitoring and Observability

### Logging
```java
@Slf4j
public class ReportBuilderService {
    public ReportData buildReport() {
        log.info("Building report: {}", reportName);
        log.debug("Query: {}", sqlQuery);
    }
}
```

### Metrics (Production)
```java
@Timed("report.generation")
public ReportData buildReport() { ... }
```

### Health Checks
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    public Health health() {
        // Check database connectivity
    }
}
```

## Deployment

### Development
```bash
./mvnw spring-boot:run
```

### Production Build
```bash
./mvnw clean package
java -jar target/payroll-reporting-lab-0.0.1-SNAPSHOT.jar
```

### Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Kubernetes
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: payroll-reporting-lab
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: app
        image: payroll-reporting-lab:latest
```

## Future Enhancements

### Phase 1: Core Features
- [ ] User authentication
- [ ] Role-based access control
- [ ] Report scheduling UI
- [ ] Email delivery

### Phase 2: Advanced Features
- [ ] Custom report builder UI
- [ ] Report versioning
- [ ] Audit logging
- [ ] Data masking

### Phase 3: Enterprise Features
- [ ] Multi-tenancy
- [ ] API for external systems
- [ ] Real-time dashboards
- [ ] Machine learning insights

## Conclusion

This architecture demonstrates:
- Metadata-driven design for flexibility
- Clean separation of concerns
- Scalable patterns for growth
- Educational clarity for learning

The system serves as both a functional application and a learning tool for understanding enterprise reporting systems.
