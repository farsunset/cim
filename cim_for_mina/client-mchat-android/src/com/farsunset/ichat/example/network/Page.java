package com.farsunset.ichat.example.network;

import java.io.Serializable;

public class Page implements Serializable
{

	/**
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么）
	 *
	 * @since 1.0.0 
	 */
	private static final long serialVersionUID = 1L;
	public int count;
    public int size;
    public int currentPage;
    public int countPage;
}
