package com.powerblock.inspiriappbeta;

public interface WishlistObservable {
	public void add(OnWishlistChangeListener listener);
	public void remove(OnWishlistChangeListener listener);
}
