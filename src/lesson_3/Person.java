package lesson_3;

public class Person {
    private int phoneNumber;
    private String eMail;

    Person(int phoneNumber, String eMail) {
        this.phoneNumber = phoneNumber;
        this.eMail = eMail;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String geteMail() {
        return eMail;
    }

    @Override
    public String toString() {
        return "(" + phoneNumber + ", " + eMail + ")";
    }
}
