package ru.kudinov.service;

import org.springframework.stereotype.Service;
import ru.kudinov.model.Employee;
import ru.kudinov.repository.EmployeeRepository;

import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> allEmployees() {
        return employeeRepository.findAllByIsActive(true);
    }

    public List<Employee> getNonActiveEmployees() {
        return employeeRepository.findAllByIsActive(false);
    }

    public Employee findById(Long employeeId) {
        if (employeeRepository.findById(employeeId).isPresent()) {
            return employeeRepository.findById(employeeId).get();
        }
        return null;
    }

    public boolean saveEmployee(Employee employee) {
        if (!isDataCorrectly(employee)) return false;
        employee.setActive();
        employeeRepository.save(employee);
        return true;

    }

    public boolean deleteEmployee(Employee employee) {
        if (employeeRepository.findById(employee.getId()).isPresent()) {
            employee.setNonActive();
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    public boolean isDataCorrectly(Employee employee) {
        return employee.getName() != null && !Objects.equals(employee.getName(), "")
                && employee.getSurname() != null && !Objects.equals(employee.getSurname(), "")
                && employee.getPatronymic() != null && !Objects.equals(employee.getPatronymic(), "")
                && employee.getPhoneNumber() != null && !Objects.equals(employee.getPhoneNumber(), "")
                && employee.getAddress() != null && !Objects.equals(employee.getAddress(), "")
                && employee.getBirthdate() != null
                && employee.getPost() != null
                && employee.getWorkExperience() != null
                && employee.getEmployeeWorkSchedule() != null
                && employee.getSalary() != null
                && employee.getSeniorityAllowance() != null
                && employee.getWages() != null;
    }
}
