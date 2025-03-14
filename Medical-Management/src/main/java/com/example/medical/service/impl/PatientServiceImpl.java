package com.example.medical.service.impl;

import com.example.medical.dto.PatientDto;
import com.example.medical.entity.Patient;
import com.example.medical.entity.User;
import com.example.medical.repository.PatientRepository;
import com.example.medical.repository.UserRepository;
import com.example.medical.service.PatientService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PatientDto savePatientDetails(PatientDto patientDto) {

        Optional<Patient> existingPatient=patientRepository.findByUserId(patientDto.getUserId());
        Patient patient;
        if(existingPatient.isPresent()){
            patient=existingPatient.get();
        }else {
            patient=new Patient();
            patient = modelMapper.map(patientDto, Patient.class);
            User user = userRepository.findById(patientDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + patientDto.getUserId()));
            patient.setUser(user);
        }

        patient.setAge(patientDto.getAge());
        patient.setGender(patientDto.getGender());
        patient.setMedicalHistory(patientDto.getMedicalHistory());

        // Save patient details
        Patient savedPatient = patientRepository.saveAndFlush(patient);

        return modelMapper.map(savedPatient, PatientDto.class);
    }

    @Override
    public PatientDto getPatientDetails(Long userId) {
        Optional<Patient> patient = patientRepository.findByUserId(userId);
        return patient.map(value -> modelMapper.map(value, PatientDto.class)).orElse(null);
    }


//    @Override
//    public AppointmentDto bookAppointment(AppointmentDto appointmentDto) {
//        Appointment appointment=modelMapper.map(appointmentDto,Appointment.class);
//        Appointment savedAppointment=appointmentRepository.save(appointment);
//        return modelMapper.map(savedAppointment,AppointmentDto.class);
//    }
//
//    @Override
//    public List<AppointmentDto> viewAppointments(Long patientId) {
//        Patient patient=patientRepository.findById(patientId)
//                .orElseThrow(()-> new PatientNotFoundException("Patient not found"));
//        List<Appointment> appointments=appointmentRepository.findByPatient(patient);
//        if(appointments.isEmpty()){
//            return null;
//        }
//        return appointments.stream().map(appointment -> modelMapper.map(appointment,AppointmentDto.class)).collect(Collectors.toList());
//    }
//
//    @Override
//    public void cancelAppointment(Long appointmentId) {
//        Appointment appointment=appointmentRepository.findById(appointmentId)
//                .orElseThrow(()->new AppointmentNotFoundException("Appointment not found"));
//        appointmentRepository.delete(appointment);
//    }
//
//    @Override
//    public List<MedicationDto> viewMedications(Long appointmentId) {
//        return medicationRepository.findByAppointmentId(appointmentId)
//                .stream().map(medication -> modelMapper.map(medication, MedicationDto.class))
//                .collect(Collectors.toList());
//    }
}
