/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\workspace\\Asdk2\\src\\fly\\fish\\aidl\\ITestListener.aidl
 */
package fly.fish.aidl;

import android.os.RemoteException;

public interface ITestListener extends android.os.IInterface {
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements ITestListener {
		private static final String DESCRIPTOR = "fly.fish.aidl.ITestListener";

		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an fly.fish.aidl.ITestListener interface,
		 * generating a proxy if needed.
		 */
		public static ITestListener asInterface(android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof ITestListener))) {
				return ((ITestListener) iin);
			}
			return new Proxy(obj);
		}

		@Override
		public android.os.IBinder asBinder() {
			return this;
		}

		@Override
		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws RemoteException {
			switch (code) {
			case INTERFACE_TRANSACTION: {
				reply.writeString(DESCRIPTOR);
				return true;
			}
			case TRANSACTION_initback: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				this.initback(_arg0);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_loginback: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				String _arg2;
				_arg2 = data.readString();
				String _arg3;
				_arg3 = data.readString();
				this.loginback(_arg0, _arg1, _arg2, _arg3);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_payback: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				String _arg2;
				_arg2 = data.readString();
				String _arg3;
				_arg3 = data.readString();
				String _arg4;
				_arg4 = data.readString();
				String _arg5;
				_arg5 = data.readString();
				this.payback(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_queryback: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				String _arg2;
				_arg2 = data.readString();
				String _arg3;
				_arg3 = data.readString();
				this.queryback(_arg0, _arg1, _arg2, _arg3);
				reply.writeNoException();
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements ITestListener {
			private android.os.IBinder mRemote;

			Proxy(android.os.IBinder remote) {
				mRemote = remote;
			}

			@Override
			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			@Override
			public void initback(String status) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(status);
					mRemote.transact(Stub.TRANSACTION_initback, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void loginback(String sessionid, String accountid, String status, String customstring) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(sessionid);
					_data.writeString(accountid);
					_data.writeString(status);
					_data.writeString(customstring);
					mRemote.transact(Stub.TRANSACTION_loginback, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void payback(String msg, String status, String sum, String chargetype, String customorderid, String customstring) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(msg);
					_data.writeString(status);
					_data.writeString(sum);
					_data.writeString(chargetype);
					_data.writeString(customorderid);
					_data.writeString(customstring);
					mRemote.transact(Stub.TRANSACTION_payback, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void queryback(String status, String sum, String chargetype, String customstring) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(status);
					_data.writeString(sum);
					_data.writeString(chargetype);
					_data.writeString(customstring);
					mRemote.transact(Stub.TRANSACTION_queryback, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

		}

		static final int TRANSACTION_initback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_loginback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_payback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
		static final int TRANSACTION_queryback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
	}

	public void initback(String status) throws RemoteException;

	public void loginback(String sessionid, String accountid, String status, String customstring) throws RemoteException;

	public void payback(String msg, String status, String sum, String chargetype, String customorderid, String customstring) throws RemoteException;

	public void queryback(String status, String sum, String chargetype, String customstring) throws RemoteException;
}
