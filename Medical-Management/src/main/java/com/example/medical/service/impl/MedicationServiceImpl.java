package com.example.medical.service.impl;

import com.example.medical.dto.MedicationDto;
import com.example.medical.entity.Appointment;
import com.example.medical.entity.Doctor;
import com.example.medical.entity.Medication;
import com.example.medical.exceptions.AppointmentNotFoundException;
import com.example.medical.exceptions.DoctorNotFoundException;
import com.example.medical.exceptions.MedicationNotFoundException;
import com.example.medical.repository.AppointmentRepository;
import com.example.medical.repository.DoctorRepository;
import com.example.medical.repository.MedicationRepository;
import com.example.medical.service.MedicationService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private MedicationRepository medicationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MedicationDto addMedication(MedicationDto medicationDto) {
        // Fetch appointment to validate its existence
        Appointment appointment = appointmentRepository.findById(medicationDto.getAppointmentId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + medicationDto.getAppointmentId()));

        // Map and save the medication entity
        Medication medication = modelMapper.map(medicationDto, Medication.class);
        medication.setAppointment(appointment);
        Medication savedMedication = medicationRepository.save(medication);
        appointment.setMedicationAdded(true);
        appointmentRepository.save(appointment);
        return modelMapper.map(savedMedication, MedicationDto.class);
    }

    @Override
    public MedicationDto getMedicationsByAppointment(Long appointmentId) {
        Medication medication=medicationRepository.findByAppointmentId(appointmentId).orElse(null);
        if (medication==null){
            return null;
        }
        // Fetch medications associated with the appointment
        return modelMapper.map(medication,MedicationDto.class);
    }

    @Override
    public void updateMedication(MedicationDto medicationDto) {
        Medication existingMedication = medicationRepository.findByAppointmentId(medicationDto.getAppointmentId())
                .orElseThrow(()->new MedicationNotFoundException("Medication not found for this ID: " + medicationDto.getAppointmentId()));

        // Update medication properties
        existingMedication.setMedicineName(medicationDto.getMedicineName());
        existingMedication.setDosage(medicationDto.getDosage());
        existingMedication.setInstruction(medicationDto.getInstruction());

        medicationRepository.save(existingMedication);
    }

    @Override
    public void deleteMedication(Long appointmentId) {
        Optional<Medication> optionalMedication = medicationRepository.findByAppointmentId(appointmentId);
        if(optionalMedication.isEmpty()){
            throw new MedicationNotFoundException("Medication not found with Appointment ID: " + appointmentId);
        }
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: "+appointmentId));
        Medication medication=optionalMedication.get();
        medicationRepository.delete(medication);
        appointment.setMedicationAdded(false);
        appointmentRepository.save(appointment);
    }

    @Override
        public List<MedicationDto> viewPrescribedMedications(Long doctorId) {
            Doctor doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new DoctorNotFoundException("Doctor not found with ID: " + doctorId));

            // Fetch all appointments for the doctor and extract medications
            return doctor.getAppointments().stream()
                    .flatMap(appointment -> appointment.getMedications().stream())
                    .map(medication -> modelMapper.map(medication, MedicationDto.class))
                    .collect(Collectors.toList());
    }
}

