package com.payrolllab.repository;

import com.payrolllab.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByCountry(String country);
    List<Employee> findByDepartment(String department);
    
    @Query("SELECT DISTINCT e.country FROM Employee e ORDER BY e.country")
    List<String> findDistinctCountries();
    
    @Query("SELECT DISTINCT e.department FROM Employee e ORDER BY e.department")
    List<String> findDistinctDepartments();
}
