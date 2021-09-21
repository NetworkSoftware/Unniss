package pro.network.unniss.app;

public class PreferenceBean {
    private static PreferenceBean _instance;

    boolean enableBooking;

    int discount, startTime, endTime, deliveryHour, shippingCost, minimumOrder, invoicePercent;
    int width, height;

    public static void set_instance(PreferenceBean _instanceV) {
        _instance = _instanceV;
    }

    public static PreferenceBean getInstance() {
        if (_instance == null) {
            _instance = new PreferenceBean();
        }
        return _instance;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getDeliveryHour() {
        return deliveryHour;
    }

    public void setDeliveryHour(int deliveryHour) {
        this.deliveryHour = deliveryHour;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    public boolean isEnableBooking() {
        return enableBooking;
    }

    public void setEnableBooking(boolean enableBooking) {
        this.enableBooking = enableBooking;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getMinimumOrder() {
        return minimumOrder;
    }

    public void setMinimumOrder(int minimumOrder) {
        this.minimumOrder = minimumOrder;
    }

    public int getInvoicePercent() {
        return invoicePercent;
    }

    public void setInvoicePercent(int invoicePercent) {
        this.invoicePercent = invoicePercent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
