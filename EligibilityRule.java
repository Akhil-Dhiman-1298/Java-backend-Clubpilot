public interface EligibilityRule {
    boolean isEligible(Event event, String user);
}