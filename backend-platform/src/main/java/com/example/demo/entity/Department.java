package com.example.demo.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "yuan_xi")
@Data
public class Department {
    @Id
    @Value("Department")
    private Integer department;
    @Value("Department_name")
    private String departmentName;
}
