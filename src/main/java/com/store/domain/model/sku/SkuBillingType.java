package com.store.domain.model.sku;

/**
 * The billing types are obvious in intent, except for the one called
 * CHiLD_SPECIFIC. That case represents that the bundle associated with a
 * particular Sku will delegate the billing accountabe values to his inner Skus
 */
public enum SkuBillingType {
	HOURLY, DAILY, WEEKLY, CHILD_SPECIFIC
}
