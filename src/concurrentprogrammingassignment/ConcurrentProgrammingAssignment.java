package concurrentprogrammingassignment;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Thread.sleep;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

//  @author Shah
public class ConcurrentProgrammingAssignment {
    public static void main(String[] args) {
//        Patient patientArr[] = new Patient[35];
        CommonWaitingList commonWaitingList = new CommonWaitingList();
        Patient patientArr[] = new Patient[150];

        Doctor doctorArr[] = { new Doctor("Doctor 1"), new Doctor("Doctor 2"), new Doctor("Doctor 3"),
//                        new Doctor("Doctor 4"), new Doctor("Doctor 5"), new Doctor("Doctor 6"), new Doctor("Doctor 7"),
//                        new Doctor("Doctor 8"), new Doctor("Doctor 9"), new Doctor("Doctor 10") 
        };

        try {
            File file = new File("input.txt");
            Scanner sc = new Scanner(file);
            int counter = 0;

            sc.nextLine(); // skip line 1
            sc.nextLine(); // skip line 2
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String data[] = line.split(" ");
                patientArr[counter] = new Patient(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                counter++;
            }
            sc.close();
        } catch (FileNotFoundException | NumberFormatException e) {
            System.out.println(e);
        }

        Reception reception = new Reception(doctorArr, commonWaitingList);
        
        ExecutorService executor1 = Executors.newFixedThreadPool(10);
        for(Doctor doctor : doctorArr){
            executor1.execute(new StartConsultation(doctor, reception));
        }
        
        ExecutorService executor2 = Executors.newFixedThreadPool(10);
        for(Patient patient : patientArr){
            executor2.execute(new AssignPatientToDoctor(reception, patient));
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
            reception.AssignPatientToDoctor(patient);
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

