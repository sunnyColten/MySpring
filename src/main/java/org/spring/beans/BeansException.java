package org.spring.beans;

/**
 * 规范抛出异常
 * @author zzf
 *
 */
public class BeansException extends RuntimeException {
	public BeansException(String msg) {
		super(msg);	}

	public BeansException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
