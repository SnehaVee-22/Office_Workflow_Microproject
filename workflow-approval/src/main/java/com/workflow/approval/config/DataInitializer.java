package com.workflow.approval.config;

import com.workflow.approval.entity.Department;
import com.workflow.approval.entity.RequestType;
import com.workflow.approval.entity.User;
import com.workflow.approval.repository.DepartmentRepository;
import com.workflow.approval.repository.RequestTypeRepository;
import com.workflow.approval.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner 
{

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private static final String DEPT_ADMINISTRATION = null;

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RequestTypeRepository requestTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           RequestTypeRepository requestTypeRepository,
                           PasswordEncoder passwordEncoder) 
    {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.requestTypeRepository = requestTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) 
    {
        seedAdmin();
        seedDepartments();
        seedRequestTypes();
        seedSampleUsersIfEmpty();
    }

    private void seedAdmin() 
    {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) 
        {
            User admin = new User();
            admin.setEmployeeId("ADM001");
            admin.setName("System Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setRole(User.Role.ADMIN);
            admin.setDepartment("Administration");
            admin.setActive(true);
            userRepository.save(admin);
            log.info("=== Admin user seeded: admin@gmail.com / Admin@123 ===");
        }
    }

    private void seedDepartments() {
        String[][] depts = {
            {"IT", "Information Technology"},
            {"HR", "Human Resources"},
            {"Finance", "Finance and Accounts"},
            {"Operations", "Operations"},
            {"Sales", "Sales and Marketing"},
            {DEPT_ADMINISTRATION, DEPT_ADMINISTRATION}
        };

        for (String[] d : depts) {
            if (!departmentRepository.existsByName(d[0])) {
                Department dept = new Department();
                dept.setName(d[0]);
                dept.setDescription(d[1]);
                departmentRepository.save(dept);
            }
        }

        log.info("Departments seeded.");
    }

    private void seedRequestTypes() 
    {
        String[][] types = {
            {"Leave Request", "For Attending Project Seminars"},
            {"Software Requirement", "Software tool for development"}
        };
        for (String[] t : types) 
        {
            if (!requestTypeRepository.existsByName(t[0])) 
            {
                RequestType rt = new RequestType();
                rt.setName(t[0]);
                rt.setDescription(t[1]);
                requestTypeRepository.save(rt);
            }
        }
        log.info("Request types seeded.");
    }

    private void seedSampleUsersIfEmpty() 
    {
        // Only seed if no non-admin users exist
        long nonAdminCount = userRepository.findAll().stream()
                .filter(u -> u.getRole() != User.Role.ADMIN).count();

        if (nonAdminCount > 0) return;

        // Seed sample manager
        User manager = new User();
        manager.setEmployeeId("MGR001");
        manager.setName("Veerakumar");
        manager.setEmail("veerakumar@gmail.com");
        manager.setPassword(passwordEncoder.encode("Manager@123"));
        manager.setRole(User.Role.MANAGER);
        manager.setDepartment("HR");
        manager.setActive(true);
        User savedManager = userRepository.save(manager);

        // Seed sample employee
        User employee = new User();
        employee.setEmployeeId("EMP001");
        employee.setName("Karthik");
        employee.setEmail("karthik@gmail.com");
        employee.setPassword(passwordEncoder.encode("Employee@123"));
        employee.setRole(User.Role.EMPLOYEE);
        employee.setDepartment("IT");
        employee.setActive(true);
        employee.setManager(savedManager);
        userRepository.save(employee);

        log.info("=== Sample users seeded ===");
        log.info("Manager : veerakumar@gmail.com   / Manager@123");
        log.info("Employee: karthik@gmail.com / Employee@123");
    }
}
