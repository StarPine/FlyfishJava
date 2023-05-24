package fly.fish.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class FileHeader implements Parcelable {
	public static final String HEAD = "XPKG";// 固定文件标识
	public static final String MYPKG = "xpkg.cra";// 统一文件结尾字符
	public static final String PATCH = "xpkg.patch";// 统一补丁结尾符
	public static String flag = null;// 文件标识
	public static int version = 0;// 文件版本（SV版本）
	public static int fileCount = 0;// 文件数量

	public String name = null;// 文件名
	public int size = 0;// 文件大小
	public int startoff = 0;// 起始偏移
	public int realsize = 0;// 文件原始大小（对压缩方式 而言）
	public String md5 = null;// md5校验

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeInt(size);
		dest.writeInt(startoff);
		dest.writeInt(realsize);
		dest.writeString(md5);
	}

	public static final Creator<FileHeader> CREATOR = new Creator<FileHeader>() {
		@Override
		public FileHeader createFromParcel(Parcel source) {
			FileHeader fileheader = new FileHeader();
			fileheader.name = source.readString();
			fileheader.size = source.readInt();
			fileheader.startoff = source.readInt();
			fileheader.realsize = source.readInt();
			fileheader.md5 = source.readString();
			return fileheader;
		}

		@Override
		public FileHeader[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FileHeader[size];
		}

	};
}
