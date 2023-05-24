/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\XiaoAiOp\\Desktop\\����\\�ĵ�\\zs_sdk\\flyfishsdk\\src\\fly\\fish\\aidl\\IGhostWindowService.aidl
 */
package fly.fish.aidl;
public interface IGhostWindowService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IGhostWindowService
{
private static final String DESCRIPTOR = "fly.fish.aidl.IGhostWindowService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an fly.fish.aidl.IGhostWindowService interface,
 * generating a proxy if needed.
 */
public static IGhostWindowService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IGhostWindowService))) {
return ((IGhostWindowService)iin);
}
return new Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_showGhostWindow:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.showGhostWindow(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_hideGhostWindow:
{
data.enforceInterface(DESCRIPTOR);
this.hideGhostWindow();
reply.writeNoException();
return true;
}
case TRANSACTION_showChatWindow:
{
data.enforceInterface(DESCRIPTOR);
this.showChatWindow();
reply.writeNoException();
return true;
}
case TRANSACTION_initGhostWindow:
{
data.enforceInterface(DESCRIPTOR);
this.initGhostWindow();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements IGhostWindowService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void showGhostWindow(String packageName, boolean isLandscape) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packageName);
_data.writeInt(((isLandscape)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_showGhostWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void hideGhostWindow() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hideGhostWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void showChatWindow() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_showChatWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void initGhostWindow() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_initGhostWindow, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_showGhostWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_hideGhostWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_showChatWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_initGhostWindow = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void showGhostWindow(String packageName, boolean isLandscape) throws android.os.RemoteException;
public void hideGhostWindow() throws android.os.RemoteException;
public void showChatWindow() throws android.os.RemoteException;
public void initGhostWindow() throws android.os.RemoteException;
}
