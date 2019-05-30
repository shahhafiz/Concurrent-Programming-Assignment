package concurrentprogrammingassignment;
// @author Shah

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class Reception {
    
    //Added Colours
    public static final String ANSI_RESET = "\u001B[0m";
public static final String ANSI_BLACK = "\u001B[30m";
public static final String ANSI_RED = "\u001B[31m";
public static final String ANSI_GREEN = "\u001B[32m";
public static final String ANSI_YELLOW = "\u001B[33m";
public static final String ANSI_BLUE = "\u001B[34m";
public static final String ANSI_PURPLE = "\u001B[35m";
public static final String ANSI_CYAN = "\u001B[36m";
public static final String ANSI_WHITE = "\u001B[37m";
    //End of Added Colours
    

    Doctor doctors[];
    CommonWaitingList cwl;
    ReentrantLock lock = new ReentrantLock();
    Condition ListNotEmpty = lock.newCondition();

    public Reception(Doctor d[], CommonWaitingList cwl) {
        this.doctors = d;
        this.cwl = cwl;
    }

    synchronized public void assignPatientToDoctor(Patient patient) {
        try {
            Doctor docWithLeastPatient = findDoctorWithLeastPatient();
            if (docWithLeastPatient.waitingList.getSize() == 3 || cwl.getSize() != 0) {
                cwl.addPatient(patient);
                //Added ANSI_RED
                System.out.print(ANSI_RED+"\nAll doctors are occupied. Adding " + patient.name + " to common waiting list -->" +ANSI_RESET);
                //Added cwl.printPatients
                cwl.printPatients();
                
               
            } else {
                //Added ANSI_BLACK
               System.out.println(ANSI_BLACK+patient.name+" arrived, assigned to "+docWithLeastPatient.name);
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
    
    volatile boolean stopWorking = false;
    public void startConsultation(Doctor d){
        while(!stopWorking){
            while(d.waitingList.getSize() == 0 && stopWorking == false){
//                System.out.println(d.name+": No patient in waiting list");
                ListNotEmptyAwait();
            }
            if(stopWorking) { 
                break;
            }
            Patient patient = d.selectPatient();
            // System.out.println(d.name + " meeting " + patient.name +" - "+System.currentTimeMillis());
            
            //Added ANSI_BLUE
            System.out.println(ANSI_BLUE+d.name + " meeting " + patient.name);
            waittt(patient.consultationTime);
            d.patientMetCounter++;
            d.waitingList.removePatient(patient);
            // System.out.println(d.name+" and "+patient.name+" session has ended - "+System.currentTimeMillis());
            
            //Added ANSI_GREEN
            System.out.println(ANSI_GREEN+(d.name+" and "+patient.name+" session has ended,("+d.patientMetCounter+")"));
            if(d.patientMetCounter == 8) doctorTakesBreak(d);
            doctorListHasVacancy(d);
        }
    }
    
    public void doctorListHasVacancy(Doctor d){
        if(cwl.getSize() != 0){
            Patient patient = cwl.list.remove(0);
            // System.out.println(patient.name+" from cwl, assigned to "+docWithLeastPatient.name +" - "+System.currentTimeMillis());
            cwl.removePatient(patient);
            d.addPatient(patient);
            
            //Added ANSI_PURPLE
            System.out.println(ANSI_PURPLE+patient.name+" from cwl, assigned to "+d.name);
        } else {
//            System.out.println("Common waiting list is empty");
        }
    }
    
    synchronized public void doctorTakesBreak(Doctor d){
        try {
            //Added ANSI_PURPLE and Take a break notification
            System.out.println(ANSI_PURPLE+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println(ANSI_PURPLE+"### "+d.name + " take 15 minutes break ###");
            System.out.println(ANSI_PURPLE+"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            //End of Added ANSI_PURPLE and Take a break notification
            wait(15);
        } catch (InterruptedException ex) {}
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
    
   public void closeReception(){
       
        System.out.println(ANSI_RED+"Close reception");
        stopWorking = true;
        ListNotEmptySignall();
        
        System.out.println("-------------------------------------------------------------------------Hospital Report---------------------------------------------------------------------------");

        int totalNumberOfPatient = 0;
        for(Doctor d: doctors){
            totalNumberOfPatient += d.apl.getSize();
        }
        
        System.out.println("Number of doctor today : "+doctors.length);
        System.out.println("The number of patient visited : "+totalNumberOfPatient);
        System.out.println("");
        int overallWaitingTime=0;
        int overallConsultaionTime=0;
        for(Doctor doctor : doctors){
            int totalConsultationTime=0;
            int totalWaitingTime=0;
            int waitingTime;
            Patient previousPatient=new Patient("dummy",0,0);
            int previousPatientFinishTime;
            int currentPatientFinishTime;
            int previousPatientWaitingTime=0;
            int countPatient=1;
            int doctorBreak;
            for(Patient patient : doctor.apl.getPatients())
            {
                waitingTime=0;
                
                if(countPatient==9){
                    doctorBreak=15;
                }
                else{
                    doctorBreak=0;
                }
                previousPatientFinishTime=previousPatient.arrivalTime+previousPatientWaitingTime+previousPatient.consultationTime+doctorBreak;
                if(previousPatientFinishTime<=patient.arrivalTime){
                    waitingTime=0+doctorBreak;
                } else {
                    waitingTime=previousPatient.arrivalTime+previousPatient.consultationTime+previousPatientWaitingTime-patient.arrivalTime+doctorBreak;
                }
                
                totalConsultationTime+=patient.consultationTime;
                currentPatientFinishTime = patient.arrivalTime + waitingTime + patient.consultationTime+doctorBreak; 
//                System.out.println("count : "+countPatient+" break : "+doctorBreak);
//                System.out.println(patient.name+ " waiting time: "+waitingTime+" "+patient.arrivalTime+" "+patient.consultationTime + "("+currentPatientFinishTime+")");
//                System.out.println("total consultaton time "+totalConsultationTime);
                totalWaitingTime += waitingTime;
                previousPatient=patient;
                previousPatientWaitingTime = waitingTime;
                countPatient++;
            }
            overallWaitingTime+=totalWaitingTime;
            overallConsultaionTime+=totalConsultationTime;
            System.out.println("Doctor name : "+doctor.name);
            System.out.println("Total number of patient seen by doctor : "+doctor.apl.getSize());
            System.out.println("Total waiting time : "+totalWaitingTime);
            System.out.println("Average patient waiting time :"+totalWaitingTime/doctor.apl.getSize());
            System.out.println("Total consultation time : "+totalConsultationTime);
            System.out.println("Average consultaion time: "+totalConsultationTime/doctor.apl.getSize()+"\n");

        }
        System.out.println("Overall waiting time: "+overallWaitingTime);
        System.out.println("Overall consultation time: "+overallConsultaionTime);
    }
}

