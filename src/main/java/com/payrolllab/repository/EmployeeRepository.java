package com.payrolllab.repository;

import com.payrolllab.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByCountry(String country);
    List<Employee> findByDepartment(String department);
    
    @Query("SELECT DISTINCT e.country FROM Employee e ORDER BY e.country")
    List<String> findDistinctCountries();
    
    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department")
    List<String> findDistinctDepartments();
    
    // JPQL Version (Entity Form) - Recommended
    @Query("""
        SELECT new map(
            e.id as id,
            e.employeeName as employeeName,
            SUM(pr.grossSalary) as totalGross
        )
        FROM PayrollResult pr
        JOIN pr.employee e
        GROUP BY e.id, e.employeeName
        ORDER BY SUM(pr.grossSalary) DESC
        """)
    List<Map<String, Object>> getEmployeeTotalGrossSalaryJpql();
    
   
}
