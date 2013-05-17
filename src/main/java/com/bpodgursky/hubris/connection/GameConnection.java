package com.bpodgursky.hubris.connection;


import com.bpodgursky.hubris.command.GameRequest;

public interface GameConnection {

	public abstract <R> R sendRequest(GameRequest<R> request) throws Exception;
	
}
