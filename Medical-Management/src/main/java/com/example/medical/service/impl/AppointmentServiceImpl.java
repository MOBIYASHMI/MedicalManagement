package com.example.medical.service.impl;

import com.example.medical.dto.AppointmentDto;
import com.example.medical.entity.Appointment;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.exceptions.AppointmentNotFoundException;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.PatientNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.AvailabilityRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.PatientRepository;
import com.example.medical.service.AppointmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    AvailabilityRepository availabilityRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AppointmentDto saveAppointment(AppointmentDto appointmentDto) {
        // Fetch the doctor and patient by ID to validate their existence
        Doctor doctor = doctorRepository.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + appointmentDto.getDoctorId()));
        Patient patient = patientRepository.findByUserId(appointmentDto.getPatientId())
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + appointmentDto.getPatientId()));

        // Map the DTO to an entity and set the associations
        appointmentDto.setMedicationAdded(false);
        Appointment appointment = modelMapper.map(appointmentDto, Appointment.class);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        // Save appointment and return the mapped DTO
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return modelMapper.map(savedAppointment, AppointmentDto.class);
    }

    @Transactional // Ensures a transaction is available
    public void deleteAvailability(Long doctorId, LocalDateTime selectedTime) {
        availabilityRepository.deleteByDoctorIdAndAvailableSlot(doctorId, selectedTime);
    }

    @Override
    public List<AppointmentDto> getAppointmentsByDoctor(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

//        User user=doctor.getUser();

        List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);

//        return appointments.stream()
//                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
//                .collect(Collectors.toList());

        return appointments.stream().map(appointment -> {
            AppointmentDto appointmentDto=new AppointmentDto();
            appointmentDto.setId(appointment.getId());
            appointmentDto.setAppointmentTime(appointment.getAppointmentTime());
            appointmentDto.setStatus(appointment.getStatus());
            appointmentDto.setDoctorId(appointment.getDoctor().getId());
            appointmentDto.setPatientId(appointment.getPatient().getId());
            User doctorUser=appointment.getDoctor().getUser();
            appointmentDto.setDoctorName(doctorUser.getFullname());
            User patientUser=appointment.getPatient().getUser();
            appointmentDto.setPatientName(patientUser.getFullname());
            appointmentDto.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
            return appointmentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDto> getAppointmentsByPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: " + patientId));

        List<Appointment> appointments = appointmentRepository.findByPatient(patient);

//        return appointments.stream()
//                .map(appointment -> modelMapper.map(appointment, AppointmentDto.class))
//                .collect(Collectors.toList());

        return appointments.stream().map(appointment -> {
            AppointmentDto appointmentDto=new AppointmentDto();
            appointmentDto.setId(appointment.getId());
            appointmentDto.setAppointmentTime(appointment.getAppointmentTime());
            appointmentDto.setStatus(appointment.getStatus());
            appointmentDto.setDoctorId(appointment.getDoctor().getId());
            appointmentDto.setPatientId(appointment.getPatient().getId());
            User doctorUser=appointment.getDoctor().getUser();
            appointmentDto.setDoctorName(doctorUser.getFullname());
            User patientUser=appointment.getPatient().getUser();
            appointmentDto.setPatientName(patientUser.getFullname());
            appointmentDto.setDoctorSpecialization(appointment.getDoctor().getSpecialization());
            appointmentDto.setMedicationAdded(appointment.isMedicationAdded());
            return appointmentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

        appointmentRepository.delete(appointment);
    }

    @Override
    public AppointmentDto getAppointmentDetails(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + appointmentId));

        return modelMapper.map(appointment, AppointmentDto.class);
    }
}
