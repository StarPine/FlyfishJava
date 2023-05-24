package fly.fish.beans;

/**
 * 充值页面模型
 * 
 * @author kete
 * 
 */
public class GridViewModel {
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String title;
	private String imgTag;
	private String remark;

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public GridViewModel() {

	}

	public GridViewModel(String id, String title, String imgTag) {
		super();
		this.id = id;
		this.title = title;
		this.imgTag = imgTag;
	}

	public GridViewModel(String id, String title, String imgTag, String remark) {
		super();
		this.id = id;
		this.title = title;
		this.imgTag = imgTag;
		this.remark = remark;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgTag() {
		return imgTag;
	}

	public void setImgTag(String imgTag) {
		this.imgTag = imgTag;
	}

}
