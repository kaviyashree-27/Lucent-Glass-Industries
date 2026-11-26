package lucentglassmanagement;

public class DispatchDashboard extends BillingDispatchDashboard {
    public DispatchDashboard(int userId) {
        // DB constraint stores dispatch staff as DISPATCH.
        super(userId, "DISPATCH");
    }
}

