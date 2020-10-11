package com.example.lock.core;

import org.redisson.api.RLock;

public class RedisLock {

	private RLock rLock;

	private boolean lockSuccess = false;

	public RedisLock(){}

	public RedisLock(RLock rLock, boolean lockSuccess){
		this.rLock = rLock;
		this.lockSuccess = lockSuccess;
	}

	public void unlock(){
		//如果锁被当前线程持有，则释放
		if(rLock !=null && rLock.isHeldByCurrentThread()){
			rLock.unlock();
		}
	}

	public boolean isLockSuccess() {
		return lockSuccess;
	}

	public void setLockSuccess(boolean lockSuccess) {
		this.lockSuccess = lockSuccess;
	}

}
