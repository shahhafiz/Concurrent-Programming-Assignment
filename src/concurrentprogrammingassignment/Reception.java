package concurrentprogrammingassignment;
// @author Shah

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Reception {
    Doctor doctors[];
    CommonWaitingList cwl;
    ReentrantLock lock = new ReentrantLock();
    Condition ListNotEmpty = lock.newCondition();

    public Reception(Doctor d[], CommonWaitingList cwl) {
        this.doctors = d;
        this.cwl = cwl;
    }

    synchronized public void AssignPatientToDoctor(Patient patient) {
        try {
            Doctor docWithLeastPatient = findDoctorWithLeastPatient();
            if (docWithLeastPatient.waitingList.getSize() == 3 || cwl.getSize() != 0) {
                cwl.addPatient(patient);
                System.out.println("All doctors are occupied. Adding " + patient.name + " to common waiting list");
            } else {
                // System.out.println(patient.name+" arrived, assigned to "+docWithLeastPatient.name +" - "+System.currentTimeMillis());
               System.out.println(patient.name+" arrived, assigned to "+docWithLeastPatient.name);
               docWithLeastPatient.addPatient(patient);
               ListNotEmptySignall();
            }
        } catch (Exception e) {} 
    }
    
    public void ListNotEmptySignall(){
        try {
            lock.lock();
            ListNotEmpty.signalAll();
        } catch (Exception e) {
        } finally { lock.unlock();}
    }

    public Doctor findDoctorWithLeastPatient() {
        Doctor d;
        d = doctors[0];
        for (Doctor doctor : doctors) {
            if (doctor.apl.getSize() < d.apl.getSize()) {
                d = doctor;
            }
        }
        return d;
    }
    
    boolean stopWorking = false;
    public void startConsultation(Doctor d){
        while(!stopWorking){
            while(d.waitingList.getSize() == 0){
//                System.out.println(d.name+": No patient in waiting list");
                ListNotEmptyAwait();
            }
            Patient patient = d.selectPatient();
            // System.out.println(d.name + " meeting " + patient.name +" - "+System.currentTimeMillis());
            System.out.println(d.name + " meeting " + patient.name);
            waittt(patient.consultationTime);
            d.patientMetCounter++;
            d.waitingList.removePatient(patient);
            // System.out.println(d.name+" and "+patient.name+" session has ended - "+System.currentTimeMillis());
            System.out.println(d.name+" and "+patient.name+" session has ended,("+d.patientMetCounter+")");
            DoctorListHasVacancy(d);
        }
    }
    
    public void DoctorListHasVacancy(Doctor d){
        if(cwl.getSize() != 0){
            Patient patient = cwl.list.remove(0);
            // System.out.println(patient.name+" from cwl, assigned to "+docWithLeastPatient.name +" - "+System.currentTimeMillis());
            cwl.removePatient(patient);
            d.addPatient(patient);
            System.out.println(patient.name+" from cwl, assigned to "+d.name);
        } else {
            System.out.println("Common waiting list is empty");
        }
    }
    
    public void ListNotEmptyAwait(){
        try {
            lock.lock();
            ListNotEmpty.await();
        } catch (InterruptedException e) {
        } finally { lock.unlock();}
    }
    
    synchronized public void waittt(int t){
        try {
            wait(t);
        } catch (InterruptedException ex) {} 
    }
}

