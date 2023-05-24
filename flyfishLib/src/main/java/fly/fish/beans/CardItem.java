package fly.fish.beans;

public class CardItem {
	private String pay_id = "";
	private String remark = "";
	private String money = "";
	
	
	
	public CardItem(String pay_id, String remark,String money) {
		this.pay_id = pay_id;
		this.remark = remark;
		this.money = money;
	}
	public String getPay_id() {
		return pay_id;
	}
	public void setPay_id(String pay_id) {
		this.pay_id = pay_id;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	
}
