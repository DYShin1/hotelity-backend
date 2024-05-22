package org.iot.hotelitybackend.employee.service;

import lombok.extern.slf4j.Slf4j;
import org.iot.hotelitybackend.employee.aggregate.*;
import org.iot.hotelitybackend.employee.dto.EmployeeDTO;
import org.iot.hotelitybackend.employee.repository.*;
import org.iot.hotelitybackend.employee.vo.RequestEmployee;
import org.iot.hotelitybackend.hotelmanagement.aggregate.BranchEntity;
import org.iot.hotelitybackend.hotelmanagement.repository.BranchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.iot.hotelitybackend.common.constant.Constant.*;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final ModelMapper mapper;
    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;
    private final PositionRepository positionRepository;
    private final RankRepository rankRepository;
    private final DepartmentRepository departmentRepository;
    private final BranchRepository branchRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public EmployeeServiceImpl(
            ModelMapper mapper,
            EmployeeRepository employeeRepository,
            PermissionRepository permissionRepository,
            PositionRepository positionRepository,
            RankRepository rankRepository,
            DepartmentRepository departmentRepository,
            BranchRepository branchRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.mapper = mapper;
        this.employeeRepository = employeeRepository;
        this.permissionRepository = permissionRepository;
        this.positionRepository = positionRepository;
        this.rankRepository = rankRepository;
        this.departmentRepository = departmentRepository;
        this.branchRepository = branchRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

        /* custom mapping */
        this.mapper.typeMap(EmployeeEntity.class, EmployeeDTO.class).addMappings(modelMapper -> {
            modelMapper.map(EmployeeEntity::getPermissionName, EmployeeDTO::setNameOfPermission);
            modelMapper.map(EmployeeEntity::getPositionName, EmployeeDTO::setNameOfPosition);
            modelMapper.map(EmployeeEntity::getRankName, EmployeeDTO::setNameOfRank);
            modelMapper.map(EmployeeEntity::getBranchName, EmployeeDTO::setNameOfBranch);
            modelMapper.map(EmployeeEntity::getDepartmentName, EmployeeDTO::setNameOfDepartment);

            modelMapper.map(EmployeeEntity::getPermissionId, EmployeeDTO::setDepartmentCodeFk);
            modelMapper.map(EmployeeEntity::getPositionId, EmployeeDTO::setPositionCodeFk);
            modelMapper.map(EmployeeEntity::getBranchId, EmployeeDTO::setBranchCodeFk);
            modelMapper.map(EmployeeEntity::getDepartmentId, EmployeeDTO::setDepartmentCodeFk);
            modelMapper.map(EmployeeEntity::getPositionId, EmployeeDTO::setPositionCodeFk);
        });
    }

    @Override
    public Map<String, Object> selectEmployeesList(
            int pageNum, String branchCode, Integer departmentCode, String employeeName
    ) {
        Specification<EmployeeEntity> spec = (root, query, criteriaBuilder) -> null;

        if (branchCode != null && !branchCode.isEmpty()) {
            spec = spec.and(EmploySpecification.equalsBranch(branchCode));
        }

        if (departmentCode != null) {
            spec = spec.and(EmploySpecification.equalsDepartment(departmentCode));
        }

        if (employeeName != null && !employeeName.isEmpty()) {
            spec = spec.and(EmploySpecification.containsEmployeeName(employeeName));
        }

        Pageable pageable = PageRequest.of(pageNum, PAGE_SIZE);
        Page<EmployeeEntity> employeePage = employeeRepository.findAll(spec, pageable);

        List<EmployeeDTO> employeeDTOList = employeePage
                .stream()
                .map(employeeEntity -> mapper.map(employeeEntity, EmployeeDTO.class))
                .toList();

        int totalPagesCount = employeePage.getTotalPages();
        int currentPageIndex = employeePage.getNumber();

        Map<String, Object> employeePageInfo = new HashMap<>();

        employeePageInfo.put(KEY_TOTAL_PAGES_COUNT, totalPagesCount);
        employeePageInfo.put(KEY_CURRENT_PAGE_INDEX, currentPageIndex);
        employeePageInfo.put(KEY_CONTENT, employeeDTOList);

        return employeePageInfo;
    }

    @Override
    public EmployeeDTO selectEmployeeByEmployeeCodePk(int employCode) {

        EmployeeEntity employeeEntity =
                employeeRepository.findById(employCode).orElse(null);

        if (employeeEntity != null) {
            return mapper.map(employeeEntity, EmployeeDTO.class);
        }

        return null;
    }

    @Transactional
    @Override
    public EmployeeDTO registEmployee(EmployeeDTO newEmployee) {
        boolean isExist = employeeRepository.existsByEmployeeNameAndEmployeePhoneNumberAndEmployeeEmail(
                newEmployee.getEmployeeName(), newEmployee.getEmployeePhoneNumber(), newEmployee.getEmployeeEmail()
        );

        if (isExist) {
            return null;
        }

        PermissionEntity permission =
                permissionRepository.findById(newEmployee.getPermissionCodeFk()).orElse(null);
        DepartmentEntity department =
                departmentRepository.findById(newEmployee.getDepartmentCodeFk()).orElse(null);
        PositionEntity position = positionRepository.findById(newEmployee.getPositionCodeFk()).orElse(null);
        BranchEntity branch = branchRepository.findById(newEmployee.getBranchCodeFk()).orElse(null);
        RankEntity rank = rankRepository.findById(newEmployee.getRankCodeFk()).orElse(null);

        String encodedPassword = bCryptPasswordEncoder.encode(newEmployee.getEmployeeSystemPassword());

        EmployeeEntity employeeEntity = EmployeeEntity.builder()
                .employeeName(newEmployee.getEmployeeName())
                .employeeAddress(newEmployee.getEmployeeAddress())
                .employeePhoneNumber(newEmployee.getEmployeePhoneNumber())
                .employeeOfficePhoneNumber(newEmployee.getEmployeeOfficePhoneNumber())
                .employeeEmail(newEmployee.getEmployeeEmail())
                .employeeSystemPassword(encodedPassword)
                .employeeResignStatus("N")
                .employeeProfileImageLink(newEmployee.getEmployeeProfileImageLink())
                .employeePermission(permission)
                .employeePosition(position)
                .employeeRank(rank)
                .employeeDepartment(department)
                .employeeBranch(branch)
                .build();

        EmployeeEntity createdEmployeeEntity = employeeRepository.save(employeeEntity);

        return mapper.map(createdEmployeeEntity, EmployeeDTO.class);
    }

    @Transactional
    @Override
    public EmployeeDTO modifyEmployeeByEmployeeCodePk(int employCode, RequestEmployee modifiedEmployInfo) {
        EmployeeEntity employeeEntity = employeeRepository.findById(employCode).orElse(null);

        if (employeeEntity != null) {
            PermissionEntity permission =
                    permissionRepository.findById(modifiedEmployInfo.getPermissionCodeFk()).orElse(null);
            DepartmentEntity department =
                    departmentRepository.findById(modifiedEmployInfo.getDepartmentCodeFk()).orElse(null);
            PositionEntity position = positionRepository.findById(modifiedEmployInfo.getPositionCodeFk()).orElse(null);
            BranchEntity branch = branchRepository.findById(modifiedEmployInfo.getBranchCodeFk()).orElse(null);
            RankEntity rank = rankRepository.findById(modifiedEmployInfo.getRankCodeFk()).orElse(null);

            EmployeeEntity modifiedEmployee = EmployeeEntity.builder()
                    .employeeCodePk(employeeEntity.getEmployeeCodePk())
                    .employeeName(modifiedEmployInfo.getEmployeeName())
                    .employeeAddress(modifiedEmployInfo.getEmployeeAddress())
                    .employeePhoneNumber(modifiedEmployInfo.getEmployeePhoneNumber())
                    .employeeOfficePhoneNumber(modifiedEmployInfo.getEmployeeOfficePhoneNumber())
                    .employeeEmail(modifiedEmployInfo.getEmployeeEmail())
                    .employeeSystemPassword(modifiedEmployInfo.getEmployeeSystemPassword())
                    .employeeResignStatus(modifiedEmployInfo.getEmployeeResignStatus())
                    .employeeProfileImageLink(modifiedEmployInfo.getEmployeeProfileImageLink())
                    .employeePermission(permission)
                    .employeePosition(position)
                    .employeeRank(rank)
                    .employeeDepartment(department)
                    .employeeBranch(branch)
                    .stayList(employeeEntity.getStayList())
                    .build();

            return mapper.map(employeeRepository.save(modifiedEmployee), EmployeeDTO.class);
        }

        return null;
    }

    @Override
    public int deleteEmployeeByEmployeeCodePk(int employCode) {
        /* -1: exception, 0: not exist, 1: success */
        try {
            if (employeeRepository.existsById(employCode)) {
                employeeRepository.deleteById(employCode);
                return 1;
            }

            return 0;
        } catch (Exception e) {
            log.warn("deleteEmployeeException: " + e);
            return -1;
        }
    }
}
