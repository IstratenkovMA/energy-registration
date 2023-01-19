package com.istratenkov.energyregistration.model.entity;

import com.istratenkov.energyregistration.model.entity.enumeration.Month;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(schema = "energy")
@Setter
@Getter
public class MeterMeasurement {
    @Id
    @GeneratedValue
    private Long id;
    private Integer value;
    private Month month;
    private Integer year;

    @ManyToOne
    @JoinColumn(name="profile_id", nullable=false)
    private Profile profile;
}
