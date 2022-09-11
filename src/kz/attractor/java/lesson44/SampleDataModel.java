//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kz.attractor.java.lesson44;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SampleDataModel {
    private User user = new User("Apache", "FreeMarker");
    private LocalDateTime currentDateTime = LocalDateTime.now();
    private List<User> customers = new ArrayList();

    public SampleDataModel() {
        this.customers.add(new User("Marco"));
        this.customers.add(new User("Winston", "Duarte"));
        this.customers.add(new User("Amos", "Burton", "'Timmy'"));
        ((User)this.customers.get(1)).setEmailConfirmed(true);
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCurrentDateTime() {
        return this.currentDateTime;
    }

    public void setCurrentDateTime(LocalDateTime currentDateTime) {
        this.currentDateTime = currentDateTime;
    }

    public List<User> getCustomers() {
        return this.customers;
    }

    public void setCustomers(List<User> customers) {
        this.customers = customers;
    }

    public static class User {
        private String firstName;
        private String lastName;
        private String middleName;
        private boolean emailConfirmed;
        private String email;

        public User(String firstName) {
            this(firstName, (String)null, (String)null);
        }

        public User(String firstName, String lastName) {
            this(firstName, lastName, (String)null);
        }

        public User(String firstName, String lastName, String middleName) {
            this.middleName = null;
            this.emailConfirmed = false;
            this.firstName = firstName;
            this.lastName = lastName;
            this.middleName = middleName;
            this.email = firstName + "@test.mail";
        }

        public String getFirstName() {
            return this.firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return this.lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getMiddleName() {
            return this.middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public boolean isEmailConfirmed() {
            return this.emailConfirmed;
        }

        public void setEmailConfirmed(boolean emailConfirmed) {
            this.emailConfirmed = emailConfirmed;
        }

        public String getEmail() {
            return this.email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
