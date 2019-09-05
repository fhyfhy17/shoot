package com.rpc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RPCResponse
{
	private String requestId;
	private Object data;
}