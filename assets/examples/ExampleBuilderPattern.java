public class Employee {
    private String uuid;
    private String fullName;
    private int previousDebt;

    public Employee(String uuid, String fullName, int previousDebt) {
        this.uuid = uuid;
        this.fullName = fullName;
        this.previousDebt = previousDebt;
    }

    public static void main(String[] args) {
        Builder builder = new Builder()
                .setName("My Example")
                .setUuid("this_is_a_uuid")
                .addToDebt(25000)
                .addToDebt(5000)
                .addToDebt(5000);
        Employee employee = builder.build();
    }

    public static class Builder {

        private String uuid;
        private String fullName;
        private int studentDebt = 0;

        /**
         * Set the student uuid.
         *
         * @param uuid the student uuid
         * @return this
         */
        public Builder setUuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        /**
         * Set the full name of the student.
         *
         * @param fullName the full name of the student
         * @return
         */
        public Builder setName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * Add to the current debt.
         *
         * @param amount the amount to add to the debt
         * @return this
         */
        public Builder addToDebt(int amount) {
            studentDebt += amount;
            return this;
        }

        public Employee build() {
            return new Employee(uuid, fullName, studentDebt);
        }
    }
}