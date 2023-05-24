package fly.fish.beans;

public class PayBackModel {
	String key;
	String customorderid;
	String msg;
	String sum;
	String chargetype;
	String customstring;

	public PayBackModel() {
	}

	public PayBackModel(String customorderid, String msg, String sum, String chargetype, String customstring, String key) {
		super();
		this.customorderid = customorderid;
		this.msg = msg;
		this.sum = sum;
		this.chargetype = chargetype;
		this.customstring = customstring;
		this.key = key;
	}

	public String getCustomorderid() {
		return customorderid;
	}

	public void setCustomorderid(String customorderid) {
		this.customorderid = customorderid;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSum() {
		return sum;
	}

	public void setSum(String sum) {
		this.sum = sum;
	}

	public String getChargetype() {
		return chargetype;
	}

	public void setChargetype(String chargetype) {
		this.chargetype = chargetype;
	}

	public String getCustomstring() {
		return customstring;
	}

	public void setCustomstring(String customstring) {
		this.customstring = customstring;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
