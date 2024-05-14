package org.iot.hotelitybackend.employee.repository;

import org.iot.hotelitybackend.employee.aggregate.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    Page<EmployeeEntity> findAll(Specification<EmployeeEntity> spec, Pageable pageable);
}
