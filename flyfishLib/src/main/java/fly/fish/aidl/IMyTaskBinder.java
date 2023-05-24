/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\workspace\\Asdk2\\src\\fly\\fish\\aidl\\IMyTaskBinder.aidl
 */
package fly.fish.aidl;

import android.os.RemoteException;

public interface IMyTaskBinder extends android.os.IInterface {
	/** Local-side IPC implementation stub class. */
	public static abstract class Stub extends android.os.Binder implements IMyTaskBinder {
		private static final String DESCRIPTOR = "fly.fish.aidl.IMyTaskBinder";

		/** Construct the stub at attach it to the interface. */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an fly.fish.aidl.IMyTaskBinder interface,
		 * generating a proxy if needed.
		 */
		public static IMyTaskBinder asInterface(android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof IMyTaskBinder))) {
				return ((IMyTaskBinder) iin);
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
			case TRANSACTION_init: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				String _arg2;
				_arg2 = data.readString();
				String _arg3;
				_arg3 = data.readString();
				this.init(_arg0, _arg1, _arg2, _arg3);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_login: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				this.login(_arg0, _arg1);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_pay: {
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
				this.pay(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_query: {
				data.enforceInterface(DESCRIPTOR);
				String _arg0;
				_arg0 = data.readString();
				String _arg1;
				_arg1 = data.readString();
				String _arg2;
				_arg2 = data.readString();
				this.query(_arg0, _arg1, _arg2);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_registerCallBack: {
				data.enforceInterface(DESCRIPTOR);
				ITestListener _arg0;
				_arg0 = ITestListener.Stub.asInterface(data.readStrongBinder());
				String _arg1;
				_arg1 = data.readString();
				this.registerCallBack(_arg0, _arg1);
				reply.writeNoException();
				return true;
			}
			case TRANSACTION_quit: {
				data.enforceInterface(DESCRIPTOR);
				this.quit();
				reply.writeNoException();
				return true;
			}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements IMyTaskBinder {
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
			public void init(String cpid, String gameid, String key, String name) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(cpid);
					_data.writeString(gameid);
					_data.writeString(key);
					_data.writeString(name);
					mRemote.transact(Stub.TRANSACTION_init, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void login(String self, String key) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(self);
					_data.writeString(key);
					mRemote.transact(Stub.TRANSACTION_login, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void pay(String order, String url, String sum, String desc, String self, String key) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(order);
					_data.writeString(url);
					_data.writeString(sum);
					_data.writeString(desc);
					_data.writeString(self);
					_data.writeString(key);
					mRemote.transact(Stub.TRANSACTION_pay, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void query(String order, String self, String key) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(order);
					_data.writeString(self);
					_data.writeString(key);
					mRemote.transact(Stub.TRANSACTION_query, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void registerCallBack(ITestListener listener, String key) throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeStrongBinder((((listener != null)) ? (listener.asBinder()) : (null)));
					_data.writeString(key);
					mRemote.transact(Stub.TRANSACTION_registerCallBack, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

			@Override
			public void quit() throws RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					mRemote.transact(Stub.TRANSACTION_quit, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}

		}

		static final int TRANSACTION_init = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
		static final int TRANSACTION_login = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
		static final int TRANSACTION_pay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
		static final int TRANSACTION_query = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
		static final int TRANSACTION_registerCallBack = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
		static final int TRANSACTION_quit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
	}

	public void init(String cpid, String gameid, String key, String name) throws RemoteException;

	public void login(String self, String key) throws RemoteException;

	public void pay(String order, String url, String sum, String desc, String self, String key) throws RemoteException;

	public void query(String order, String self, String key) throws RemoteException;

	public void registerCallBack(ITestListener listener, String key) throws RemoteException;

	public void quit() throws RemoteException;

}
