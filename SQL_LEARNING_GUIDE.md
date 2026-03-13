# SQL Learning Guide - Payroll Reporting Lab

## Introduction

This guide teaches SQL concepts using the Payroll Reporting Lab database. All queries can be run in the H2 Console at `http://localhost:8080/h2-console`.

## Database Connection

```
JDBC URL: jdbc:h2:mem:payrolldb
Username: sa
Password: (leave empty)
```

## Level 1: Basic Queries

### 1.1 Select All Records
```sql
-- View all employees
SELECT * FROM employees;

-- View all payroll runs
SELECT * FROM payroll_runs;
```

### 1.2 Select Specific Columns
```sql
-- Get employee names and countries
SELECT employee_name, country FROM employees;

-- Get payroll amounts
SELECT gross_salary, net_salary FROM payroll_results;
```

### 1.3 Filtering with WHERE
```sql
-- Employees in Kenya
SELECT * FROM employees WHERE country = 'Kenya';

-- Employees in Engineering department
SELECT * FROM employees WHERE department = 'Engineering';

-- Payroll for January 2025
SELECT * FROM payroll_runs WHERE payroll_month = '2025-01';
```

### 1.4 Multiple Conditions
```sql
-- Employees in Kenya AND Engineering
SELECT * FROM employees 
WHERE country = 'Kenya' AND department = 'Engineering';

-- Employees in Kenya OR UK
SELECT * FROM employees 
WHERE country = 'Kenya' OR country = 'UK';

-- Employees in multiple countries
SELECT * FROM employees 
WHERE country IN ('Kenya', 'UK', 'Germany');
```

## Level 2: Sorting and Limiting

### 2.1 ORDER BY
```sql
-- Employees sorted by name
SELECT * FROM employees ORDER BY employee_name;

-- Employees sorted by hire date (newest first)
SELECT * FROM employees ORDER BY hire_date DESC;

-- Sort by multiple columns
SELECT * FROM employees 
ORDER BY country, department, employee_name;
```

### 2.2 LIMIT
```sql
-- First 10 employees
SELECT * FROM employees LIMIT 10;

-- Top 10 highest salaries
SELECT * FROM payroll_results 
ORDER BY gross_salary DESC 
LIMIT 10;
```

## Level 3: Aggregation Functions

### 3.1 COUNT
```sql
-- Total number of employees
SELECT COUNT(*) FROM employees;

-- Employees per country
SELECT country, COUNT(*) as employee_count 
FROM employees 
GROUP BY country;

-- Employees per department
SELECT department, COUNT(*) as employee_count 
FROM employees 
GROUP BY department;
```

### 3.2 SUM
```sql
-- Total payroll cost
SELECT SUM(net_salary) as total_payroll 
FROM payroll_results;

-- Total payroll by country (requires JOIN - see Level 4)
```

### 3.3 AVG, MIN, MAX
```sql
-- Average salary
SELECT AVG(gross_salary) as avg_salary FROM payroll_results;

-- Minimum and maximum salary
SELECT 
    MIN(gross_salary) as min_salary,
    MAX(gross_salary) as max_salary
FROM payroll_results;

-- Salary statistics
SELECT 
    COUNT(*) as total_records,
    AVG(gross_salary) as avg_salary,
    MIN(gross_salary) as min_salary,
    MAX(gross_salary) as max_salary,
    SUM(gross_salary) as total_salary
FROM payroll_results;
```

## Level 4: Joins

### 4.1 INNER JOIN (Most Common)
```sql
-- Employee names with their payroll results
SELECT 
    e.employee_name,
    e.country,
    pr.gross_salary,
    pr.net_salary
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id;

-- Add payroll run information
SELECT 
    e.employee_name,
    e.country,
    run.payroll_month,
    pr.gross_salary,
    pr.net_salary
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id;
```

### 4.2 Multiple Joins
```sql
-- Complete payroll report
SELECT 
    e.employee_name,
    e.country,
    e.department,
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

### 4.3 LEFT JOIN
```sql
-- All employees, even those without deductions
SELECT 
    e.employee_name,
    d.deduction_type,
    d.amount
FROM employees e
LEFT JOIN deductions d ON e.id = d.employee_id;
```

## Level 5: GROUP BY with Joins

### 5.1 Aggregation by Country
```sql
-- Total payroll by country
SELECT 
    e.country,
    COUNT(DISTINCT e.id) as employee_count,
    SUM(pr.gross_salary) as total_gross,
    SUM(pr.net_salary) as total_net
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country
ORDER BY total_net DESC;
```

### 5.2 Aggregation by Department
```sql
-- Payroll cost by department
SELECT 
    e.department,
    COUNT(DISTINCT e.id) as employee_count,
    AVG(pr.gross_salary) as avg_salary,
    SUM(pr.net_salary) as total_cost
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.department
ORDER BY total_cost DESC;
```

### 5.3 Aggregation by Month
```sql
-- Monthly payroll trends
SELECT 
    run.payroll_month,
    COUNT(DISTINCT pr.employee_id) as employees_paid,
    SUM(pr.gross_salary) as total_gross,
    SUM(pr.tax_amount) as total_tax,
    SUM(pr.net_salary) as total_net
FROM payroll_results pr
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
GROUP BY run.payroll_month
ORDER BY run.payroll_month;
```

## Level 6: Advanced Filtering

### 6.1 HAVING Clause
```sql
-- Countries with more than 40 employees
SELECT 
    country,
    COUNT(*) as employee_count
FROM employees
GROUP BY country
HAVING COUNT(*) > 40;

-- Departments with average salary > 5000
SELECT 
    e.department,
    AVG(pr.gross_salary) as avg_salary
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.department
HAVING AVG(pr.gross_salary) > 5000;
```

### 6.2 Subqueries
```sql
-- Employees earning above average
SELECT 
    e.employee_name,
    pr.gross_salary
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
WHERE pr.gross_salary > (SELECT AVG(gross_salary) FROM payroll_results);

-- Countries with highest payroll
SELECT country, total_payroll
FROM (
    SELECT 
        e.country,
        SUM(pr.net_salary) as total_payroll
    FROM payroll_results pr
    INNER JOIN employees e ON pr.employee_id = e.id
    GROUP BY e.country
) country_totals
WHERE total_payroll > 1000000;
```

## Level 7: Payroll-Specific Queries

### 7.1 Payroll Validation
```sql
-- Verify gross = net + tax + pension
SELECT 
    e.employee_name,
    pr.gross_salary,
    pr.net_salary + pr.tax_amount + pr.pension as calculated_gross,
    pr.gross_salary - (pr.net_salary + pr.tax_amount + pr.pension) as difference
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
WHERE ABS(pr.gross_salary - (pr.net_salary + pr.tax_amount + pr.pension)) > 0.01;
```

### 7.2 Tax Analysis
```sql
-- Tax rates by country
SELECT 
    e.country,
    AVG(pr.tax_amount / pr.gross_salary * 100) as avg_tax_rate,
    MIN(pr.tax_amount / pr.gross_salary * 100) as min_tax_rate,
    MAX(pr.tax_amount / pr.gross_salary * 100) as max_tax_rate
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.country;
```

### 7.3 Employee Payslip
```sql
-- Individual employee payslip
SELECT 
    e.employee_name,
    e.country,
    e.department,
    run.payroll_month,
    pr.gross_salary,
    pr.tax_amount,
    pr.pension,
    pr.net_salary,
    ROUND(pr.tax_amount / pr.gross_salary * 100, 2) as tax_rate_percent
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE e.employee_name LIKE '%Smith%'
ORDER BY run.payroll_month;
```

### 7.4 Year-to-Date Totals
```sql
-- YTD payroll by employee
SELECT 
    e.employee_name,
    e.country,
    COUNT(*) as months_paid,
    SUM(pr.gross_salary) as ytd_gross,
    SUM(pr.tax_amount) as ytd_tax,
    SUM(pr.net_salary) as ytd_net
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE run.payroll_month LIKE '2025%'
GROUP BY e.employee_name, e.country
ORDER BY ytd_net DESC
LIMIT 20;
```

## Level 8: Complex Reporting Queries

### 8.1 Department Comparison
```sql
-- Compare departments across countries
SELECT 
    e.department,
    e.country,
    COUNT(DISTINCT e.id) as headcount,
    AVG(pr.gross_salary) as avg_salary,
    SUM(pr.net_salary) as total_cost
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
GROUP BY e.department, e.country
ORDER BY e.department, total_cost DESC;
```

### 8.2 Payroll Growth Analysis
```sql
-- Month-over-month payroll growth
SELECT 
    run.payroll_month,
    SUM(pr.net_salary) as total_payroll,
    LAG(SUM(pr.net_salary)) OVER (ORDER BY run.payroll_month) as prev_month_payroll,
    SUM(pr.net_salary) - LAG(SUM(pr.net_salary)) OVER (ORDER BY run.payroll_month) as growth
FROM payroll_results pr
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
GROUP BY run.payroll_month
ORDER BY run.payroll_month;
```

### 8.3 Statutory Reporting
```sql
-- Country statutory report (tax summary)
SELECT 
    e.country,
    run.payroll_month,
    COUNT(DISTINCT e.id) as employees,
    SUM(pr.gross_salary) as total_gross,
    SUM(pr.tax_amount) as total_tax_withheld,
    SUM(pr.pension) as total_pension,
    SUM(pr.net_salary) as total_net_paid
FROM payroll_results pr
INNER JOIN employees e ON pr.employee_id = e.id
INNER JOIN payroll_runs run ON pr.payroll_run_id = run.id
WHERE run.payroll_month = '2025-01'
GROUP BY e.country, run.payroll_month
ORDER BY e.country;
```

## Practice Exercises

### Exercise 1: Basic Queries
1. Find all employees hired in 2024
2. List all payroll runs for Germany
3. Find employees in the Sales department

### Exercise 2: Aggregation
1. Calculate total deductions by type
2. Find the average salary by department
3. Count employees per country and department

### Exercise 3: Joins
1. List all employees with their total YTD earnings
2. Find employees with deductions and show deduction details
3. Create a complete payroll report for UK in February 2025

### Exercise 4: Advanced
1. Find employees earning in the top 10% of their country
2. Calculate the tax burden by country (total tax / total gross)
3. Identify employees with missing payroll records for any month

## Solutions to Exercises

### Exercise 1 Solutions
```sql
-- 1. Employees hired in 2024
SELECT * FROM employees 
WHERE hire_date >= '2024-01-01' AND hire_date < '2025-01-01';

-- 2. Payroll runs for Germany
SELECT * FROM payroll_runs WHERE country = 'Germany';

-- 3. Sales department employees
SELECT * FROM employees WHERE department = 'Sales';
```

### Exercise 2 Solutions
```sql
-- 1. Total deductions by type
SELECT deduction_type, SUM(amount) as total
FROM deductions
GROUP BY deduction_type;

-- 2. Average salary by department
SELECT 
    e.department,
    AVG(pr.gross_salary) as avg_salary
FROM payroll_results pr
JOIN employees e ON pr.employee_id = e.id
GROUP BY e.department;

-- 3. Employees per country and department
SELECT country, department, COUNT(*) as count
FROM employees
GROUP BY country, department
ORDER BY country, department;
```

## Tips for Writing Good SQL

1. **Use meaningful aliases**: `e` for employees, `pr` for payroll_results
2. **Format for readability**: Indent and align your queries
3. **Comment complex logic**: Explain what the query does
4. **Test incrementally**: Build complex queries step by step
5. **Use EXPLAIN**: Understand query performance
6. **Avoid SELECT ***: Specify only needed columns in production

## Common Mistakes to Avoid

1. **Forgetting JOIN conditions**: Results in Cartesian product
2. **Using WHERE instead of HAVING**: For aggregate filtering
3. **Not handling NULLs**: Use COALESCE or IS NULL checks
4. **Inefficient subqueries**: Consider JOINs instead
5. **Missing GROUP BY columns**: All non-aggregated columns must be in GROUP BY

## Next Steps

1. Practice all queries in H2 Console
2. Modify queries to answer your own questions
3. Create new reports using the metadata system
4. Study the application's SQL in `ReportBuilderService`
5. Experiment with window functions and CTEs

---

**Happy Querying!** 📊
