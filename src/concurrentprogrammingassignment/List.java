package concurrentprogrammingassignment;
// @author Shah

import java.util.ArrayList;

public class List {
    ArrayList<Patient> list = new ArrayList<>();
    public void printPatients() {
        list.forEach((p) -> {
            System.out.println(p.name + ": " + p.consultationTime);
        });
    }

    public int getSize() {
        return list.size();
    }

    public Patient[] getPatients(){
        Patient patientArr[] = new Patient[list.size()];
        patientArr = list.toArray(patientArr);
        return patientArr;
    }

    public void removePatient(Patient p){
//        System.out.println("Remove "+p.name+" from waiting list");
        list.remove(p);
    }
}

class WaitingList extends List {
    final int CAPACITY = 3;

    public void addPatient(Patient p) {
        if (list.size() < CAPACITY) {
            list.add(p);
        }
    }
}

class CommonWaitingList extends List {
    public void addPatient(Patient p) {
        list.add(p);
    }
}

class AllPatientList extends List {
    public void addPatient(Patient p) {
        list.add(p);
    }
}

