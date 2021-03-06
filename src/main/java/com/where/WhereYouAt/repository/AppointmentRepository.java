package com.where.WhereYouAt.repository;

import com.where.WhereYouAt.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    List<Appointment> findByDate(String date);
}
