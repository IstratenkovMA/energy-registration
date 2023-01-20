package com.istratenkov.energyregistration.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * Entity that can be represented by analogy with user of electricity or gas.
 * Also store meterId, for that task we assume that one profile has only one meter.
 */
@Entity
@Table(schema = "energy")
@NoArgsConstructor
@Setter
@Getter
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String meterId;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    private List<Fraction> fractions;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "profile")
    private List<MeterMeasurement> measurements;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;
        return Objects.equals(name, profile.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
