package concurrentprogrammingassignment;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

//  @author Shah
public class ConcurrentProgrammingAssignment {
    public static void main(String[] args) {
        CommonWaitingList commonWaitingList = new CommonWaitingList();
        ArrayList<Patient> patientList = new ArrayList<>();
        ArrayList<Doctor> doctorList = new ArrayList<>();

        try {
            File file = new File("input.txt");
            Scanner sc = new Scanner(file);
            
            String doctorsFromInputFile = sc.nextLine(); 
            String doctorsData[] = doctorsFromInputFile.split(" ");
            for(String doctor : doctorsData){
                doctorList.add(new Doctor("Doctor "+doctor));
            }
            
            sc.nextLine(); // skip line 2
            
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String data[] = line.split(" ");
                patientList.add(new Patient(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
            sc.close();
        } catch (FileNotFoundException | NumberFormatException e) {
            System.out.println(e);
        }

        Doctor doctorArr[] = new Doctor[doctorList.size()];
        doctorArr = doctorList.toArray(doctorArr);
        Reception reception = new Reception(doctorArr, commonWaitingList);
        
        ExecutorService executor1 = Executors.newFixedThreadPool(10);
        doctorList.forEach(doctor -> {
            executor1.execute(new StartConsultation(doctor, reception));
        });
        
        ExecutorService executor2 = Executors.newFixedThreadPool(10);
        patientList.forEach(patient -> {
            executor2.execute(new AssignPatientToDoctor(reception, patient));
        });
        executor2.execute(new CloseReception(reception));
        
        executor1.shutdown();
        executor2.shutdown();
    }
}

class CloseReception implements Runnable{
    Reception reception;
    public CloseReception(Reception r) {
        reception = r;
    }
    
    @Override
    synchronized public void run(){
        try {
            sleep(3000);
            reception.closeReception();
        } catch (InterruptedException ex) {
            Logger.getLogger(CloseReception.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class AssignPatientToDoctor implements Runnable {
    Reception reception;
    Patient patient;

    public AssignPatientToDoctor(Reception reception, Patient patient) {
            this.reception = reception;
            this.patient = patient;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(patient.arrivalTime);
            reception.assignPatientToDoctor(patient);
        } catch (InterruptedException ex) {
            Logger.getLogger(AssignPatientToDoctor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class StartConsultation extends Thread {
    Doctor doctor;
    Reception reception;
    public StartConsultation(Doctor doctor, Reception reception) {
        this.doctor = doctor;
        this.reception = reception;
    }

    @Override
    public void run() {
        reception.startConsultation(doctor);
    }
}

