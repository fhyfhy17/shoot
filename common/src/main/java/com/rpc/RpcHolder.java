package com.rpc;

import co.paralleluniverse.strands.SettableFuture;
import com.enums.TypeEnum;
import com.manager.ServerInfoManager;
import com.util.ProtoUtil;
import com.util.ProtostuffUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RpcHolder{
	private ConcurrentHashMap<String,SettableFuture<RPCResponse>> requestContext = new ConcurrentHashMap<>();
	
	public SettableFuture<RPCResponse> sendRequest(RpcRequest rpcRequest,Object hashkey,TypeEnum.ServerTypeEnum serverType,long uid) {
		String requestId = rpcRequest.getId();
		SettableFuture<RPCResponse> future = new SettableFuture<>();
		requestContext.put(requestId, future);
		Assert.notNull(requestId, "requestId 不能为空");
		String s=ServerInfoManager.hashChooseServer(hashkey,serverType);
		ServerInfoManager.sendMessage(s,ProtoUtil.buildRpcRequstMessage(ProtostuffUtil.serializeObject(rpcRequest,RpcRequest.class),uid,null));
		return future;
	}
	
	public void receiveResponse(RPCResponse rpcResponse){
		String requestId = rpcResponse.getRequestId();
		SettableFuture<RPCResponse> rpcResponseFuture = requestContext.get(requestId);
		requestContext.remove(requestId);
		rpcResponseFuture.set(rpcResponse);
	}
}
