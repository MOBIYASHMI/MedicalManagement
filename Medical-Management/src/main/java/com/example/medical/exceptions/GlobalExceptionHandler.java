package com.example.medical.exceptions;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppointmentNotFoundException.class)
    public String handleAppointmentNotFoundException(AppointmentNotFoundException exception, Model model){
        model.addAttribute("error",exception.getMessage());
        return "appointments-view";
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UsernameNotFoundException exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("error",exception.getMessage());
        return "redirect:/patients/view-details";
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public String handleDoctorNotFoundException(DoctorNotFoundException exception,Model model){
        model.addAttribute("error",exception.getMessage());
        return "doctor-appointments";
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public String handlePatientNotFoundException(PatientNotFoundException exception,Model model){
        model.addAttribute("error",exception.getMessage());
        return "appointments-view";
    }

    @ExceptionHandler(MedicationNotFoundException.class)
    public String handleMedicationNotFoundException(MedicationNotFoundException exception,RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("error",exception.getMessage());
        return "redirect:/appointments/view";
    }

}