package org.keplerproject.luajava;

public class LuaSocket {
	private final static String LUANET_LIB = "luasocket";
	/**
	 * Opens the library containing the luanet API
	*/
    static
	{
	    //System.loadLibrary(LUANET_LIB);
	}
    
    public synchronized native void luasocket_open(CPtr cptr);
}
