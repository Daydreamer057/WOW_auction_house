package dto;

public class SelectDTO {
    private String name;

    private long profit;

    private int itemId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getProfit() {
        return profit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
