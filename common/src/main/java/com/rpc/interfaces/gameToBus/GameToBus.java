package com.rpc.interfaces.gameToBus;

import co.paralleluniverse.fibers.Suspendable;
import com.annotation.Rpc;

public interface GameToBus
{
	@Suspendable
	@Rpc(needResponse=true)
	public String needResponse(String a);
	
	@Suspendable
	@Rpc(needResponse=false)
	public void noNeedResponse(String a);
}
