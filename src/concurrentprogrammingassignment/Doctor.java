package concurrentprogrammingassignment;
// @author Shah

public class Doctor {
    String name;
    Boolean stopWorking = false;
    WaitingList waitingList = new WaitingList();
    AllPatientList apl = new AllPatientList();
    int patientMetCounter = 0;

    public Doctor(String name) {
        this.name = name;
    }

    public void addPatient(Patient p) {
        waitingList.addPatient(p);
        apl.addPatient(p);
    }

    public Patient selectPatient(){
        Patient patient = new Patient("dummy", 1000, 0);
        for (Patient p : waitingList.getPatients()) {
                if (p.arrivalTime < patient.arrivalTime) {
                        patient = p;
                }
        }
        return patient;
    }
}
